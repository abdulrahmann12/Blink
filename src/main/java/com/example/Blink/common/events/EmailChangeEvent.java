package com.example.Blink.common.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailChangeEvent {
    private String oldEmail;
    private String newEmail;
    private String  username;
    private String code;
    private Instant timestamp;
}
