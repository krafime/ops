package com.dansmultipro.ops.service.impl;

import com.dansmultipro.ops.constant.RoleTypeConstant;
import com.dansmultipro.ops.dto.general.CommonResDTO;
import com.dansmultipro.ops.dto.general.InsertResDTO;
import com.dansmultipro.ops.dto.login.LoginResDTO;
import com.dansmultipro.ops.dto.user.ChangePasswordReqDTO;
import com.dansmultipro.ops.dto.user.UserInsertReqDTO;
import com.dansmultipro.ops.dto.user.UserResDTO;
import com.dansmultipro.ops.model.User;
import com.dansmultipro.ops.repo.RoleTypeRepo;
import com.dansmultipro.ops.repo.UserRepo;
import com.dansmultipro.ops.service.UserService;
import com.dansmultipro.ops.util.AuthUtil;
import com.dansmultipro.ops.util.JWTUtil;
import com.dansmultipro.ops.util.UUIDUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserServiceImpl extends BaseService implements UserService {

    private final UserRepo userRepo;
    private final RoleTypeRepo roleTypeRepo;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JWTUtil jwtUtil;
    private final AuthUtil authUtil;

    @Value("${gateway.secret.key}")
    private String gatewaySecretKey;
    @Value("${gateway.uuid}")
    private String gatewayUUID;

    public UserServiceImpl(UserRepo userRepo, RoleTypeRepo roleTypeRepo, BCryptPasswordEncoder bCryptPasswordEncoder, JWTUtil jwtUtil, AuthUtil authUtil) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.userRepo = userRepo;
        this.roleTypeRepo = roleTypeRepo;
        this.jwtUtil = jwtUtil;
        this.authUtil = authUtil;
    }


    @Override
    public LoginResDTO loginGateway(String secretKey) {
        if (!bCryptPasswordEncoder.matches(secretKey, gatewaySecretKey)) {
            throw new BadCredentialsException("Invalid secret key");
        }

        var expiration = Timestamp.valueOf(LocalDateTime.now().plusHours(1));
        var token = jwtUtil.generateToken(gatewayUUID, RoleTypeConstant.GTW.name(), expiration);

        return new LoginResDTO(
                token,
                "User Gateway",
                RoleTypeConstant.GTW.name()
        );
    }

    @Override
    public List<UserResDTO> getAllUsers(Boolean isActive) {
        var allUsers = userRepo.findAll().stream()
                .filter(user -> {
                    String roleCode = user.getRoleType().getRoleCode();
                    return !roleCode.equals(RoleTypeConstant.SA.name()) &&
                            !roleCode.equals(RoleTypeConstant.SYS.name());
                })
                .map(this::mapToDTO)
                .toList();

        if (isActive != null) {
            return allUsers.stream()
                    .filter(dto -> {
                        var user = userRepo.findById(UUIDUtil.toUUID(dto.getId())).orElse(null);
                        return user != null && user.getActive().equals(isActive);
                    })
                    .toList();
        }

        return allUsers;
    }

    @Override
    public UserResDTO getUserById(String id) {
        var userId = UUIDUtil.toUUID(id);
        var user = userRepo.findById(userId).orElseThrow(
                () -> new IllegalArgumentException("User not found")
        );
        return mapToDTO(user);
    }

    @Override
    public UserResDTO getUserByEmail(String email) {
        var user = userRepo.findByEmail(email).orElseThrow(
                () -> new IllegalArgumentException("User not found")
        );
        return mapToDTO(user);
    }

    @Override
    public InsertResDTO createUserCustomer(UserInsertReqDTO user) {
        var isEmailExist = userRepo.findByEmail(user.email()).isPresent();
        if (isEmailExist) {
            throw new IllegalArgumentException("Email already registered");
        }

        var systemUser = userRepo.findByRoleTypeRoleCode(RoleTypeConstant.SYS.name()).orElseThrow(
                () -> new IllegalArgumentException("System user not found")
        );

        var roleType = roleTypeRepo.findByRoleCode(RoleTypeConstant.CUST.name())
                .orElseThrow(() -> new IllegalArgumentException("Role not found"));

        var userData = new User();
        userData.setEmail(user.email());
        userData.setFullName(user.fullName());
        userData.setPassword(bCryptPasswordEncoder.encode(user.password()));
        userData.setRoleType(roleType);

        var res = userRepo.save(super.insert(userData, systemUser.getId()));

        return new InsertResDTO(
                res.getId(),
                "Registration successful"
        );
    }

    @Override
    public CommonResDTO activateBulkUser(List<String> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            throw new IllegalArgumentException("User ID list cannot be empty");
        }

        var users = userIds.stream()
                .map(userId -> {
                    var id = UUIDUtil.toUUID(userId);
                    return userRepo.findByIdAndIsActive(id, false)
                            .orElseThrow(() -> new IllegalArgumentException("User not found or already active: " + userId));
                })
                .toList();

        users.forEach(user -> userRepo.save(super.activate(user)));

        return new CommonResDTO(users.size() + " user(s) activated successfully");
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var user = userRepo.findByEmailAndIsActiveTrue(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));
        return new org.springframework.security.core.userdetails.User(
                username,
                user.getPassword(),
                List.of(new SimpleGrantedAuthority(user.getRoleType().getRoleCode()))
        );
    }

    private UserResDTO mapToDTO(User user) {
        return new UserResDTO(
                user.getId().toString(),
                user.getEmail(),
                user.getFullName(),
                user.getRoleType().getRoleCode(),
                user.getOptLock()
        );
    }

    @Override
    public CommonResDTO changePassword(ChangePasswordReqDTO changePasswordReq) {
        var userId = authUtil.getLoginId();
        if (userId == null) {
            throw new IllegalArgumentException("User not authenticated");
        }

        var user = userRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!bCryptPasswordEncoder.matches(changePasswordReq.oldPassword(), user.getPassword())) {
            throw new BadCredentialsException("Old password is incorrect");
        }

        var hashedNewPassword = bCryptPasswordEncoder.encode(changePasswordReq.newPassword());

        user.setPassword(hashedNewPassword);
        userRepo.save(super.update(user));

        return new CommonResDTO("Password changed successfully");
    }
}
