package com.dansmultipro.ops.controller;

import com.dansmultipro.ops.dto.roletype.RoleTypeResDTO;
import com.dansmultipro.ops.service.RoleTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/role-types")
@Tag(name = "Role Type Management", description = "API untuk mengelola tipe peran dalam sistem")
public class RoleTypeController {
    private final RoleTypeService roleTypeService;

    public RoleTypeController(RoleTypeService roleTypeService) {
        this.roleTypeService = roleTypeService;
    }

    @GetMapping
    @Operation(summary = "Get All Role Types", description = "Mengambil daftar semua tipe peran yang tersedia dalam sistem")
    public ResponseEntity<List<RoleTypeResDTO>> getAllRoleTypes(@RequestParam(required = false) Boolean isActive) {
        List<RoleTypeResDTO> roleTypes = roleTypeService.getAllRoleTypes(isActive);
        return new ResponseEntity<>(roleTypes, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Role Type By ID", description = "Mengambil detail tipe peran berdasarkan ID")
    public ResponseEntity<RoleTypeResDTO> getRoleTypeById(@PathVariable String id) {
        RoleTypeResDTO roleType = roleTypeService.getRoleTypeById(id);
        return new ResponseEntity<>(roleType, HttpStatus.OK);
    }

    @GetMapping("/code/{code}")
    @Operation(summary = "Get Role Type By Code", description = "Mengambil detail tipe peran berdasarkan kode")
    public ResponseEntity<RoleTypeResDTO> getRoleTypeByCode(@PathVariable String code) {
        RoleTypeResDTO roleType = roleTypeService.getRoleTypeByCode(code);
        return new ResponseEntity<>(roleType, HttpStatus.OK);
    }

}
