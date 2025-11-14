package com.dansmultipro.ops.integration;

import com.dansmultipro.ops.dto.roletype.RoleTypeResDTO;
import com.dansmultipro.ops.service.RoleTypeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

public class RoleTypeServiceIntegrationTest extends AbstractServiceIntegrationTest {

    @Autowired
    private RoleTypeService roleTypeService;

    @Test
    void getAllRoleTypesTest() {

        List<RoleTypeResDTO> roleTypes = roleTypeService.getAllRoleTypes(null);

        assertThat(roleTypes).isNotNull();
        assertThat(roleTypes.size()).isGreaterThan(0);
    }

    @Test
    void getAllRoleTypesActiveOnlyTest() {
        List<RoleTypeResDTO> activeRoleTypes = roleTypeService.getAllRoleTypes(true);

        assertThat(activeRoleTypes).isNotNull();
        // Verify semua roleType yang diambil adalah active
        activeRoleTypes.forEach(roleType ->
                assertThat(roleTypeRepo.findById(roleType.id())).isPresent()
        );
    }

    @Test
    void getRoleTypeByIdTest() {
        RoleTypeResDTO roleTypeFound = roleTypeService.getRoleTypeById(customerRole.getId().toString());

        assertThat(roleTypeFound).isNotNull();
        assertThat(roleTypeFound.id()).isEqualTo(customerRole.getId());
        assertThat(roleTypeFound.roleCode()).isNotBlank();
        assertThat(roleTypeFound.roleName()).isNotBlank();
    }

    @Test
    void getRoleTypeByIdNotFoundTest() {
        assertThatThrownBy(() -> roleTypeService.getRoleTypeById(UUID.randomUUID().toString()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Role Type not found");
    }

    @Test
    void getRoleTypeByCodeTest() {
        RoleTypeResDTO roleTypeFound = roleTypeService.getRoleTypeByCode(customerRole.getRoleCode());

        assertThat(roleTypeFound).isNotNull();
        assertThat(roleTypeFound.roleCode()).isEqualTo(customerRole.getRoleCode());
        assertThat(roleTypeFound.roleName()).isNotBlank();
    }

    @Test
    void getRoleTypeByCodeNotFoundTest() {
        assertThatThrownBy(() -> roleTypeService.getRoleTypeByCode("INVALID_CODE_XYZ"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
