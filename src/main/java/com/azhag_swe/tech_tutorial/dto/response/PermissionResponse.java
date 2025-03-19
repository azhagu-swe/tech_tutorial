package com.azhag_swe.tech_tutorial.dto.response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PermissionResponse {
    private Long id;
    private String name;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String modifiedBy;
}