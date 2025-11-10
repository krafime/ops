package com.dansmultipro.ops.controller;

import com.dansmultipro.ops.dto.general.CommonResDTO;
import com.dansmultipro.ops.dto.login.GatewayLoginReqDTO;
import com.dansmultipro.ops.dto.login.LoginReqDTO;
import com.dansmultipro.ops.dto.login.LoginResDTO;
import com.dansmultipro.ops.dto.user.UserInsertReqDTO;
import com.dansmultipro.ops.dto.user.UserResDTO;
import com.dansmultipro.ops.service.UserService;
import com.dansmultipro.ops.util.JWTUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User Management", description = "API untuk mengelola pengguna (Super Admin, PIC, Customer)")
public class UserController {
    private final AuthenticationManager authenticationManager;
    private  final JWTUtil jwtUtil;
    private final UserService userService;

    public UserController(AuthenticationManager authenticationManager, JWTUtil jwtUtil,UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }

    @Operation(summary = "Login pengguna", description = "Melakukan autentikasi user dengan email dan password, mengembalikan informasi token, fullname, dan role code jika berhasil")
    @PostMapping("/login")
    public ResponseEntity<LoginResDTO> login(@Valid @RequestBody LoginReqDTO loginRequest) {
        var auth = new UsernamePasswordAuthenticationToken(
                loginRequest.email(),
                loginRequest.password()
        );

        var user = userService.getUserByEmail(loginRequest.email());
        authenticationManager.authenticate(auth);

        // Generate JWT token with expiration 1 hour from now
        var expiration = Timestamp.valueOf(LocalDateTime.now().plusHours(1));
        var token = jwtUtil.generateToken(user.getId(),user.getRoleCode(), expiration);

        var res = new LoginResDTO(
                token,
                user.getFullName(),
                user.getRoleCode()
        );
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PostMapping("/login/gateway")
    @Operation(summary = "Gateway login", description = "Login User Gateway menggunakan secret key, mengembalikan token, fullname, dan role code")
    public ResponseEntity<LoginResDTO> loginGateway(@Valid @RequestBody GatewayLoginReqDTO gatewayLoginRequest) {
        var response = userService.loginGateway(gatewayLoginRequest.secretKey());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/register")
    @Operation(summary = "Create Customer User", description = "Membuat user dengan role Customer")
    public ResponseEntity<CommonResDTO> createUserCustomer(@Valid @RequestBody UserInsertReqDTO user) {
        var response = userService.createUserCustomer(user);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/activate")
    @PreAuthorize("hasAuthority('SA')")
    @Operation(summary = "Activate single or multiple users", description = "Mengaktifkan satu atau lebih user sekaligus. User ID dalam format list string JSON")
    public ResponseEntity<CommonResDTO> activateBulkUser(@RequestBody List<String> userIds) {
        var response = userService.activateBulkUser(userIds);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('SA')")
    @Operation(summary = "Get all users", description = "Mengambil semua user (exclude Super Admin dan System Admin) dengan optional filter isActive. Query param: isActive=true/false")
    public ResponseEntity<List<UserResDTO>> getAllUsers(
            @RequestParam(required = false) Boolean isActive) {
        var response = userService.getAllUsers(isActive);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('SA')")
    @Operation(summary = "Get User By ID", description = "Mengambil detail pengguna berdasarkan ID")
    public ResponseEntity<UserResDTO> getUserById(@PathVariable String id) {
        UserResDTO user = userService.getUserById(id);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }


}
