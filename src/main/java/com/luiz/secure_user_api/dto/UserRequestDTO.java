package com.luiz.secure_user_api.dto;

import com.luiz.secure_user_api.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRequestDTO {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    // Importa apenas para o endpoint de criação de administrador.
    // O cadastro publico irá forçar o papel de CUSTOMER.
    private Role role;
}