package com.dansmultipro.ops.service;

import com.dansmultipro.ops.dto.general.CommonResDTO;
import com.dansmultipro.ops.dto.general.InsertResDTO;
import com.dansmultipro.ops.dto.login.LoginResDTO;
import com.dansmultipro.ops.dto.user.UserInsertReqDTO;
import com.dansmultipro.ops.dto.user.UserResDTO;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {
    List<UserResDTO> getAllUsers(Boolean isActive);

    UserResDTO getUserById(String id);

    UserResDTO getUserByEmail(String email);

    InsertResDTO createUserCustomer(UserInsertReqDTO user);

    CommonResDTO activateBulkUser(List<String> userId);

    LoginResDTO loginGateway(String secretKey);
}
