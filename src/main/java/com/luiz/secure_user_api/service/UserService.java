package com.luiz.secure_user_api.service;

import com.luiz.secure_user_api.dto.UserRequestDTO;
import com.luiz.secure_user_api.dto.UserResponseDTO;
import com.luiz.secure_user_api.dto.UserUpdateRequestDTO;
import com.luiz.secure_user_api.entity.Role;
import com.luiz.secure_user_api.entity.User;
import com.luiz.secure_user_api.exception.EmailAlreadyInUseException;
import com.luiz.secure_user_api.exception.UserNotFoundException;
import com.luiz.secure_user_api.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    private UserResponseDTO toResponseDTO(User user) {
        return new UserResponseDTO(user.getId(), user.getName(), user.getEmail(), user.getRole());
    }

    public UserResponseDTO createUser(UserRequestDTO request, Role role) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyInUseException(request.getEmail());
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(role);

        return toResponseDTO(userRepository.save(user));
    }

    public List<UserResponseDTO> findAll() {
        return userRepository.findAll().stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public UserResponseDTO findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        return toResponseDTO(user);
    }

    public UserResponseDTO update(Long id, UserUpdateRequestDTO request, boolean canChangeRole) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        user.setName(request.getName());
        user.setEmail(request.getEmail());
        if (canChangeRole && request.getRole() != null) {
            user.setRole(request.getRole());
        }

        return toResponseDTO(userRepository.save(user));
    }

    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException(id);
        }
        userRepository.deleteById(id);
    }

    public UserResponseDTO findByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
        return toResponseDTO(user);
    }

    public UserResponseDTO updateOwnProfile(String email, UserUpdateRequestDTO request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));

        user.setName(request.getName());
        user.setEmail(request.getEmail());

        return toResponseDTO(userRepository.save(user));
    }
}