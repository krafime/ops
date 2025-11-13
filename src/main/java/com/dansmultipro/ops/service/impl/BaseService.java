package com.dansmultipro.ops.service.impl;


import com.dansmultipro.ops.model.BaseModel;
import com.dansmultipro.ops.util.AuthUtil;
import com.dansmultipro.ops.util.UUIDUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.UUID;

public abstract class BaseService {

    private AuthUtil authUtil;

    @Autowired
    public void setAuthUtil(AuthUtil authUtil) {
        this.authUtil = authUtil;
    }


    protected <T extends BaseModel> T insert(T data) {
        data.setId(UUID.randomUUID());
        data.setCreatedAt(LocalDateTime.now());
        data.setCreatedBy(authUtil.getLoginId());
        data.setActive(true);
        return data;
    }

    protected <T extends BaseModel> T insert(T data, UUID systemId) {
        data.setId(UUID.randomUUID());
        data.setCreatedAt(LocalDateTime.now());
        data.setCreatedBy(systemId);
        data.setActive(false);
        return data;
    }

    protected <T extends BaseModel> T update(T data) {
        data.setUpdatedAt(LocalDateTime.now());
        data.setUpdatedBy(authUtil.getLoginId());
        return data;
    }

    protected <T extends BaseModel> T update(T data, UUID systemId) {
        data.setUpdatedAt(LocalDateTime.now());
        data.setUpdatedBy(systemId);
        return data;
    }

    protected <T extends BaseModel> T activate(T data) {
        data.setActive(true);
        data.setUpdatedAt(LocalDateTime.now());
        data.setUpdatedBy(authUtil.getLoginId());
        return data;
    }

    protected UUID toUUID(String id) {
        return UUIDUtil.toUUID(id);
    }
}
