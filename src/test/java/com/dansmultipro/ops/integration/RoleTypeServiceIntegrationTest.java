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
        // Ambil semua role types, ambil yang pertama untuk test
        List<RoleTypeResDTO> roleTypes = roleTypeService.getAllRoleTypes(null);

        assertThat(roleTypes).isNotNull();
        assertThat(roleTypes.size()).isGreaterThan(0);

        UUID roleTypeId = roleTypes.get(0).id();
        RoleTypeResDTO roleTypeFound = roleTypeService.getRoleTypeById(roleTypeId.toString());

        assertThat(roleTypeFound).isNotNull();
        assertThat(roleTypeFound.id()).isEqualTo(roleTypeId);
        assertThat(roleTypeFound.roleCode()).isNotBlank();
        assertThat(roleTypeFound.roleName()).isNotBlank();
    }

    @Test
    void getRoleTypeByIdNotFoundTest() {
        assertThatThrownBy(() -> roleTypeService.getRoleTypeById(UUID.randomUUID().toString()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Role Type not found");  // ← Sesuaikan dengan actual message
    }

    @Test
    void getRoleTypeByCodeTest() {
        // Ambil semua role types, ambil yang pertama untuk test
        List<RoleTypeResDTO> roleTypes = roleTypeService.getAllRoleTypes(null);

        assertThat(roleTypes).isNotNull();
        assertThat(roleTypes.size()).isGreaterThan(0);

        String roleCode = roleTypes.get(0).roleCode();
        RoleTypeResDTO roleTypeFound = roleTypeService.getRoleTypeByCode(roleCode);

        assertThat(roleTypeFound).isNotNull();
        assertThat(roleTypeFound.roleCode()).isEqualTo(roleCode);
        assertThat(roleTypeFound.roleName()).isNotBlank();
    }

    @Test
    void getRoleTypeByCodeNotFoundTest() {
        assertThatThrownBy(() -> roleTypeService.getRoleTypeByCode("INVALID_CODE_XYZ"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
