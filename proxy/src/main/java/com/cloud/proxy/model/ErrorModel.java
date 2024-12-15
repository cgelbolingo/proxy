package com.cloud.proxy.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ErrorModel {
    @NotBlank
    String title;

    @NotNull
    int status;

    @NotNull
    String[] errors;
}
