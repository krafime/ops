package com.dansmultipro.ops.service.impl;


import com.dansmultipro.ops.model.BaseModel;

import java.time.LocalDateTime;
import java.util.UUID;

public abstract class BaseService {
    protected <T extends BaseModel> T insert(T data) {
        data.setId(UUID.randomUUID());
        data.setCreatedAt(LocalDateTime.now());
        data.setActive(true);
        return data;
    }

    protected <T extends BaseModel> T update(T data) {
        data.setUpdatedAt(LocalDateTime.now());
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
