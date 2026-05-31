package com.example.Blink.role.mapper;

import com.example.Blink.role.dto.CreateRoleRequest;
import com.example.Blink.role.dto.RoleResponse;
import com.example.Blink.role.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    @Mapping(target = "roleId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Role toEntity(CreateRoleRequest request);

    RoleResponse toResponse(Role role);
}
