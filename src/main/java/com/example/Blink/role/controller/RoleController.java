package com.example.Blink.role.controller;

import com.example.Blink.common.dto.BaseResponse;
import com.example.Blink.common.messages.Messages;
import com.example.Blink.common.messages.SwaggerMessages;
import com.example.Blink.role.dto.CreateRoleRequest;
import com.example.Blink.role.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = SwaggerMessages.TAG_ROLE, description = SwaggerMessages.TAG_ROLE_DESC)
public class RoleController {
    private final RoleService roleService;

    @Operation(summary = SwaggerMessages.CREATE_ROLE, description = SwaggerMessages.CREATE_ROLE_DESC)
    @PostMapping
    public ResponseEntity<BaseResponse> createRole(@Valid @RequestBody CreateRoleRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new BaseResponse(Messages.ROLE_CREATED, roleService.createRole(request)));
    }

    @Operation(summary = SwaggerMessages.UPDATE_ROLE, description = SwaggerMessages.UPDATE_ROLE_DESC)
    @PutMapping("/{roleId}")
    public ResponseEntity<BaseResponse> updateRole(@PathVariable Long roleId, @Valid @RequestBody CreateRoleRequest request) {
        return ResponseEntity.ok(new BaseResponse(Messages.ROLE_UPDATED, roleService.updateRole(roleId, request)));
    }

    @Operation(summary = SwaggerMessages.GET_ALL_ROLES, description = SwaggerMessages.GET_ALL_ROLES_DESC)
    @GetMapping
    public ResponseEntity<BaseResponse> getAllRoles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(new BaseResponse(Messages.ROLES_FETCHED, roleService.getAllRoles(page, size)));
    }

    @Operation(summary = SwaggerMessages.GET_ROLE_BY_ID, description = SwaggerMessages.GET_ROLE_BY_ID_DESC)
    @GetMapping("/{roleId}")
    public ResponseEntity<BaseResponse> getRoleById(@PathVariable Long roleId) {
        return ResponseEntity.ok(new BaseResponse(Messages.ROLE_FETCHED, roleService.getRoleById(roleId)));
    }

    @Operation(summary = SwaggerMessages.GET_ROLE_BY_NAME, description = SwaggerMessages.GET_ROLE_BY_NAME_DESC)
    @GetMapping("/name/{roleName}")
    public ResponseEntity<BaseResponse> getRoleByName(@PathVariable String roleName) {
        return ResponseEntity.ok(new BaseResponse(Messages.ROLE_FETCHED, roleService.getRoleByName(roleName)));
    }

    @Operation(summary = SwaggerMessages.DELETE_ROLE, description = SwaggerMessages.DELETE_ROLE_DESC)
    @DeleteMapping("/{roleId}")
    public ResponseEntity<BaseResponse> deleteRole(@PathVariable Long roleId) {
        roleService.deleteRole(roleId);
        return ResponseEntity.ok(new BaseResponse(Messages.ROLE_DELETED));
    }
}
