package com.dansmultipro.ops.integration;

import com.dansmultipro.ops.constant.RoleTypeConstant;
import com.dansmultipro.ops.dto.general.CommonResDTO;
import com.dansmultipro.ops.dto.general.InsertResDTO;
import com.dansmultipro.ops.dto.login.LoginResDTO;
import com.dansmultipro.ops.dto.user.ChangePasswordReqDTO;
import com.dansmultipro.ops.dto.user.ForgotPasswordReqDTO;
import com.dansmultipro.ops.dto.user.UserInsertReqDTO;
import com.dansmultipro.ops.dto.user.UserResDTO;
import com.dansmultipro.ops.model.User;
import com.dansmultipro.ops.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

public class UserServiceIntegrationTest extends AbstractServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Test
    void getAllUsersTest() {
        List<UserResDTO> users = userService.getAllUsers(null);

        assertThat(users).isNotNull();
        assertThat(users.size()).isEqualTo(2); // customerUser, gatewayUser (SA and SYS are filtered out)
    }

    @Test
    void getAllUsersActiveOnlyTest() {
        List<UserResDTO> activeUsers = userService.getAllUsers(true);

        assertThat(activeUsers).isNotNull();
        assertThat(activeUsers.size()).isGreaterThan(0);

        // Verify all users are active
        activeUsers.forEach(user ->
                assertThat(userRepo.findById(UUID.fromString(user.getId()))).isPresent()
        );
    }

    @Test
    void getUserByIdTest() {
        // Test with system user
        UserResDTO user = userService.getUserById(systemUser.getId().toString());

        assertThat(user).isNotNull();
        assertThat(user.getId()).isEqualTo(systemUser.getId().toString());
        assertThat(user.getEmail()).isEqualTo("system@ops.test");
        assertThat(user.getFullName()).isEqualTo("System");
        assertThat(user.getRoleCode()).isEqualTo("SYS");
    }

    @Test
    void getUserByIdNotFoundTest() {
        assertThatThrownBy(() -> userService.getUserById(UUID.randomUUID().toString()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("User not found");
    }

    @Test
    void getUserByEmailTest() {
        // Test with customer user email
        UserResDTO user = userService.getUserByEmail("customer@ops.local");

        assertThat(user).isNotNull();
        assertThat(user.getEmail()).isEqualTo("customer@ops.local");
        assertThat(user.getFullName()).isEqualTo("Customer User");
        assertThat(user.getRoleCode()).isEqualTo("CUST");
    }

    @Test
    void getUserByEmailNotFoundTest() {
        assertThatThrownBy(() -> userService.getUserByEmail("nonexistent@example.com"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("User not found");
    }

    @Test
    void createUserCustomerTest() {
        UserInsertReqDTO newUser = new UserInsertReqDTO(
                "newcustomer@test.com",
                "password123",
                "New Customer"
        );

        InsertResDTO result = userService.createUserCustomer(newUser);

        assertThat(result).isNotNull();
        assertThat(result.id()).isNotNull();
        assertThat(result.message()).isEqualTo("Registration successful");

        // Verify user was created
        UserResDTO createdUser = userService.getUserById(result.id().toString());
        assertThat(createdUser).isNotNull();
        assertThat(createdUser.getEmail()).isEqualTo("newcustomer@test.com");
        assertThat(createdUser.getFullName()).isEqualTo("New Customer");
        assertThat(createdUser.getRoleCode()).isEqualTo("CUST");
    }

    @Test
    void createUserCustomerDuplicateEmailTest() {
        UserInsertReqDTO duplicateUser = new UserInsertReqDTO(
                "customer@ops.local", // Already exists
                "password123",
                "Duplicate Customer"
        );

        assertThatThrownBy(() -> userService.createUserCustomer(duplicateUser))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void activateBulkUserTest() {
        // First create an inactive user
        User inactiveUser = createUser("Inactive User", "inactive@test.com", customerRole, false);
        assertThat(inactiveUser.getActive()).isFalse();

        // Activate the user
        CommonResDTO result = userService.activateBulkUser(List.of(inactiveUser.getId().toString()));

        assertThat(result).isNotNull();
        assertThat(result.message()).isEqualTo("1 user(s) activated successfully");

        // Verify user is now active
        UserResDTO activatedUser = userService.getUserById(inactiveUser.getId().toString());
        assertThat(activatedUser).isNotNull();
    }

    @Test
    void loginGatewayTest() {
        // Test with gateway secret key from application.properties
        LoginResDTO result = userService.loginGateway("gateway.secret.key");

        assertThat(result).isNotNull();
        assertThat(result.accessToken()).isNotNull();
        assertThat(result.fullName()).isEqualTo("User Gateway");
        assertThat(result.roleCode()).isEqualTo("GTW");
    }

    @Test
    void loginGatewayInvalidSecretTest() {
        assertThatThrownBy(() -> userService.loginGateway("invalid.secret"))
                .isInstanceOf(BadCredentialsException.class);
    }

    @Test
    void changePasswordTest() {
        ChangePasswordReqDTO changePasswordReq = new ChangePasswordReqDTO(
                "password", // old password
                "newpassword123" // new password
        );

        // Mock the authentication context to return the customer user ID
        Mockito.when(authUtil.getLoginId()).thenReturn(customerUser.getId());

        CommonResDTO result = userService.changePassword(changePasswordReq);

        assertThat(result).isNotNull();
        assertThat(result.message()).isEqualTo("Password changed successfully");

        // Verify password was actually changed
        User updatedUser = userRepo.findById(customerUser.getId()).orElse(null);
        assertThat(updatedUser).isNotNull();
        assertThat(passwordEncoder.matches("newpassword123", updatedUser.getPassword())).isTrue();
    }

    @Test
    void forgotPasswordTest() {
        ForgotPasswordReqDTO forgotPasswordReq = new ForgotPasswordReqDTO("customer@ops.local");

        CommonResDTO result = userService.forgotPassword(forgotPasswordReq);

        assertThat(result).isNotNull();
        assertThat(result.message()).isEqualTo("A temporary password has been sent to your email. Please login with it and change your password immediately.");
    }

    @Test
    void forgotPasswordNonExistentEmailTest() {
        ForgotPasswordReqDTO forgotPasswordReq = new ForgotPasswordReqDTO("nonexistent@example.com");

        assertThatThrownBy(() -> userService.forgotPassword(forgotPasswordReq))
                .isInstanceOf(IllegalArgumentException.class);
    }


    @Test
    void loadUserByUsernameShouldReturnDetails() {
        UserDetails details = userService.loadUserByUsername(customerUser.getEmail());

        assertThat(details.getUsername()).isEqualTo(customerUser.getEmail());
        assertThat(details.getAuthorities().iterator().next().getAuthority()).isEqualTo(RoleTypeConstant.CUST.name());
    }

    @Test
    void loadUserByUsernameShouldThrowWhenUnknown() {
        assertThatThrownBy(() -> userService.loadUserByUsername("missing@ops.local"))
                .isInstanceOf(UsernameNotFoundException.class);
    }
}
