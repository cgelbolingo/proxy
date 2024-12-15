package com.cloud.proxy.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ErrorModel {
    @NotBlank
    String title;

    @NotNull
    int status;

    @NotNull
    String[] errors;
}
