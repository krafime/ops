package com.dansmultipro.ops.integration;

import com.dansmultipro.ops.constant.RoleTypeConstant;
import com.dansmultipro.ops.dto.roletype.RoleTypeResDTO;
import com.dansmultipro.ops.model.RoleType;
import com.dansmultipro.ops.repo.RoleTypeRepo;
import com.dansmultipro.ops.service.RoleTypeService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest
@Transactional // Rollback after each test
public class RoleTypeServiceIntegrationTest {

    private static final UUID SYSTEM_ADMIN_UUID = UUID.fromString("792b3990-9315-405a-ba5a-da07770c1edf");
    @Autowired
    private RoleTypeRepo roleTypeRepo;
    @Autowired
    private RoleTypeService roleTypeService;

    @BeforeEach
    void setUp() {
        // Clear existing data
        roleTypeRepo.deleteAll();

        // Create role types sesuai dengan database-script.sql menggunakan RoleTypeConstant
        // 1. SA (Super Admin)
        RoleType roleTypeSA = new RoleType();
        roleTypeSA.setId(UUID.randomUUID());
        roleTypeSA.setRoleCode(RoleTypeConstant.SA.name());
        roleTypeSA.setRoleName(RoleTypeConstant.SA.getRoleName());
        roleTypeSA.setActive(true);
        roleTypeSA.setCreatedAt(LocalDateTime.now());
        roleTypeSA.setCreatedBy(SYSTEM_ADMIN_UUID);
        roleTypeRepo.save(roleTypeSA);

        // 2. GTW (User Gateway)
        RoleType roleTypeGTW = new RoleType();
        roleTypeGTW.setId(UUID.randomUUID());
        roleTypeGTW.setRoleCode(RoleTypeConstant.GTW.name());
        roleTypeGTW.setRoleName(RoleTypeConstant.GTW.getRoleName());
        roleTypeGTW.setActive(true);
        roleTypeGTW.setCreatedAt(LocalDateTime.now());
        roleTypeGTW.setCreatedBy(SYSTEM_ADMIN_UUID);
        roleTypeRepo.save(roleTypeGTW);

        // 3. SYS (System Admin)
        RoleType roleTypeSYS = new RoleType();
        roleTypeSYS.setId(UUID.randomUUID());
        roleTypeSYS.setRoleCode(RoleTypeConstant.SYS.name());
        roleTypeSYS.setRoleName(RoleTypeConstant.SYS.getRoleName());
        roleTypeSYS.setActive(true);
        roleTypeSYS.setCreatedAt(LocalDateTime.now());
        roleTypeSYS.setCreatedBy(SYSTEM_ADMIN_UUID);
        roleTypeRepo.save(roleTypeSYS);

        // 4. CUST (Customer)
        RoleType roleTypeCUST = new RoleType();
        roleTypeCUST.setId(UUID.randomUUID());
        roleTypeCUST.setRoleCode(RoleTypeConstant.CUST.name());
        roleTypeCUST.setRoleName(RoleTypeConstant.CUST.getRoleName());
        roleTypeCUST.setActive(true);
        roleTypeCUST.setCreatedAt(LocalDateTime.now());
        roleTypeCUST.setCreatedBy(SYSTEM_ADMIN_UUID);
        roleTypeRepo.save(roleTypeCUST);
    }

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
