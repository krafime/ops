package com.dansmultipro.ops.service.impl;


import com.dansmultipro.ops.model.BaseModel;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;
import java.util.UUID;

public abstract class BaseService {

    @Value("${system.admin.uuid}")
    private UUID systemAdminUUID;
//    private AuthUtil authUtil;

//    @Autowired
//    public void setAuthUtil(AuthUtil authUtil) {
//        this.authUtil = authUtil;
//    }


    protected <T extends BaseModel> T insert(T data) {
        data.setId(UUID.randomUUID());
        data.setCreatedAt(LocalDateTime.now());
        data.setCreatedBy(UUID.randomUUID()); // Placeholder for createdBy
//        data.setCreatedBy(authUtil.getLoginId());
        data.setActive(true);
        return data;
    }

    protected <T extends BaseModel> T createUser(T data) {
        data.setId(UUID.randomUUID());
        data.setCreatedAt(LocalDateTime.now());
        data.setCreatedBy(systemAdminUUID);
        data.setActive(false);
        return data;
    }

    protected <T extends BaseModel> T update(T data) {
        data.setUpdatedAt(LocalDateTime.now());
        data.setUpdatedBy(UUID.randomUUID()); // Placeholder for updatedBy
//        data.setUpdatedBy(authUtil.getLoginId());
        return data;
    }

    protected <T extends BaseModel> T delete(T data) {
        data.setActive(false);
        data.setDeletedAt(LocalDateTime.now());
        return data;
    }

    protected <T extends BaseModel> T reactive(T data) {
        data.setActive(true);
        data.setDeletedAt(null);
        return data;
    }
}
