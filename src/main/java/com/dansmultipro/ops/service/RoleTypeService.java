package com.dansmultipro.ops.service;

import com.dansmultipro.ops.dto.roletype.RoleTypeResDTO;

import java.util.List;

public interface RoleTypeService {
    List<RoleTypeResDTO> getAllRoleTypes(Boolean isActive);

    RoleTypeResDTO getRoleTypeById(String id);

    RoleTypeResDTO getRoleTypeByCode(String roleCode);
}
