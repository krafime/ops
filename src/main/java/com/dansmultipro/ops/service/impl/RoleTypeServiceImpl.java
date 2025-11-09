package com.dansmultipro.ops.service.impl;

import com.dansmultipro.ops.dto.roletype.RoleTypeResDTO;
import com.dansmultipro.ops.model.RoleType;
import com.dansmultipro.ops.repo.RoleTypeRepo;
import com.dansmultipro.ops.service.RoleTypeService;
import com.dansmultipro.ops.util.UUIDUtil;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleTypeServiceImpl extends BaseService implements RoleTypeService {

    private final RoleTypeRepo roleTypeRepo;

    public RoleTypeServiceImpl(RoleTypeRepo roleTypeRepo) {
        this.roleTypeRepo = roleTypeRepo;
    }

    @Override
    public List<RoleTypeResDTO> getAllRoleTypes(Boolean isActive) {
        var roleTypes = isActive == null ?
                roleTypeRepo.findAll() :
                roleTypeRepo.findAllByIsActive(isActive);

        return roleTypes.stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    public RoleTypeResDTO getRoleTypeById(String id) {
        var roleId = UUIDUtil.toUUID(id);
        var roleType = roleTypeRepo.findById(roleId)
                .orElseThrow(() -> new IllegalArgumentException("Role Type not found"));
        return mapToDTO(roleType);
    }

    @Override
    public RoleTypeResDTO getRoleTypeByCode(String roleCode) {
        var roleType = roleTypeRepo.findByRoleCode(roleCode)
                .orElseThrow(() -> new IllegalArgumentException("Role Type not found"));
        return mapToDTO(roleType);
    }

    private RoleTypeResDTO mapToDTO(RoleType roleType) {
        return new RoleTypeResDTO(
                roleType.getId(),
                roleType.getRoleCode(),
                roleType.getRoleName()
        );
    }
}
