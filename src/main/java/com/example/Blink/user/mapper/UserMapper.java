package com.example.Blink.user.mapper;

import com.example.Blink.user.dto.CreateUserRequest;
import com.example.Blink.user.dto.UpdateUserRequest;
import com.example.Blink.user.dto.UserResponse;
import com.example.Blink.user.dto.UserSummaryResponse;
import com.example.Blink.user.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletesAt", ignore = true)
    @Mapping(target = "profilePictureUrl", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "verify", ignore = true)
    @Mapping(target = "role", ignore = true)
    User toEntity(CreateUserRequest createUserRequest);

    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletesAt", ignore = true)
    @Mapping(target = "profilePictureUrl", ignore = true)
    @Mapping(target = "verificationCode", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "verify", ignore = true)
    @Mapping(target = "role", ignore = true)
    User toUpdateEntity(UpdateUserRequest updateUserRequest);

    @Mapping(target = "roleName", source = "role.roleName")
    UserResponse toResponse(User user);

    @Mapping(target = "roleName", source = "role.roleName")
    UserSummaryResponse toSummaryResponse(User user);
}
