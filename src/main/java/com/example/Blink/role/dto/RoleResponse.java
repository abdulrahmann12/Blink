package com.example.Blink.role.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleResponse {

    private Long roleId;
    private String roleName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
