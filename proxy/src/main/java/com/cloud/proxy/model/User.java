package com.cloud.proxy.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class User {
    String id;

    @NotBlank
    String firstName;

    @NotBlank
    String lastName;

    @NotBlank
    @Email
    String email;

}
