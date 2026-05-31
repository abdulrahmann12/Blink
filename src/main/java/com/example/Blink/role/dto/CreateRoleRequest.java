package com.example.Blink.role.dto;

import com.example.Blink.common.messages.ValidationMessages;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateRoleRequest {

    @NotBlank(message = ValidationMessages.ROLE_NAME_NOT_BLANK)
    private String roleName;
}
