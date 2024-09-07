package com.example.flim.services;

import com.example.flim.entities.User;
import com.example.flim.repositories.UserRepository;
import com.example.flim.request.UserRequset;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public User save(UserRequset userRequest) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);

        String pass = passwordEncoder.encode(userRequest.getPassword());
        User user = User.builder()
                .username(userRequest.getUsername())
                .email(userRequest.getEmail())
                .password(pass)  // Consider hashing the password before saving it
                .role(User.Role.ADMIN)
                .build();
        return userRepository.save(user);
    }

    @Transactional
    public User update(int id, UserRequset userRequest) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id " + id));

        existingUser.setUsername(userRequest.getUsername());
        existingUser.setEmail(userRequest.getEmail());
        existingUser.setPassword(userRequest.getPassword());  // Consider hashing the password before saving it


        return userRepository.save(existingUser);
    }

    @Transactional
    public void delete(int id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found with id " + id);
        }
        userRepository.deleteById(id);
    }
}
