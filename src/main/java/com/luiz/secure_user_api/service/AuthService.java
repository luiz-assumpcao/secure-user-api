package com.luiz.secure_user_api.service;

import com.luiz.secure_user_api.dto.LoginRequestDTO;
import com.luiz.secure_user_api.dto.LoginResponseDTO;
import com.luiz.secure_user_api.dto.UserRequestDTO;
import com.luiz.secure_user_api.dto.UserResponseDTO;
import com.luiz.secure_user_api.entity.Role;
import com.luiz.secure_user_api.entity.User;
import com.luiz.secure_user_api.exception.InvalidCredentialsException;
import com.luiz.secure_user_api.repository.UserRepository;
import com.luiz.secure_user_api.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, UserService userService,
                       PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public UserResponseDTO register(UserRequestDTO request) {
        // Public registration always creates a CUSTOMER, regardless of what is sent in the body.
        return userService.createUser(request, Role.CUSTOMER);
    }

    public LoginResponseDTO login(LoginRequestDTO request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException();
        }

        String token = jwtService.generateToken(user.getEmail(), user.getId(), user.getRole().name());
        return new LoginResponseDTO(token);
    }
}