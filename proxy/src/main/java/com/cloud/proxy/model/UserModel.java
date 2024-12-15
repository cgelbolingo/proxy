package com.cloud.proxy.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UserModel {
    @NotBlank
    String firstName;

    @NotBlank
    String lastName;

    @NotBlank
    @Email
    String email;

}
