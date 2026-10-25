package com.dms.service.impl;

import com.dms.config.JwtTokenProvider;
import com.dms.dto.AuthRequest;
import com.dms.dto.AuthResponse;
import com.dms.dto.RegisterRequest;
import com.dms.exception.DuplicateResourceException;
import com.dms.exception.ResourceNotFoundException;
import com.dms.exception.UnauthorizedOperationException;
import com.dms.model.*;
import com.dms.model.enums.UserRole;
import com.dms.repository.UserRepository;
import com.dms.service.ActivityLogService;
import com.dms.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementation demonstrating OOP Polymorphism and Factory-like instantiation
 * of specialized User subclasses (Administrator, Victim, Volunteer).
 */
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private ActivityLogService activityLogService;

    @Override
    public AuthResponse login(AuthRequest request) {
        User user = userRepository.findByUsernameOrEmail(request.getUsername(), request.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Invalid username or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UnauthorizedOperationException("Invalid username or password");
        }

        String token = tokenProvider.generateToken(user.getUsername(), user.getRole().name());

        activityLogService.logActivity(
                user.getUsername(),
                user.getRole().name(),
                "USER_LOGIN",
                "User",
                user.getId(),
                "User logged into the Disaster Management System"
        );

        return new AuthResponse(token, user.getId(), user.getUsername(), user.getEmail(), user.getFullName(), user.getRole());
    }

    @Override
    @Transactional
    public User register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("Username '" + request.getUsername() + "' is already registered.");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email '" + request.getEmail() + "' is already registered.");
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());
        UserRole role = request.getRole() != null ? request.getRole() : UserRole.VICTIM;

        // OOP Polymorphic entity instantiation based on role hierarchy
        User user;
        switch (role) {
            case ADMIN -> {
                Administrator admin = new Administrator();
                admin.setUsername(request.getUsername());
                admin.setEmail(request.getEmail());
                admin.setPassword(encodedPassword);
                admin.setFullName(request.getFullName());
                admin.setPhone(request.getPhone());
                admin.setAddress(request.getAddress());
                admin.setDepartment(request.getDepartment() != null ? request.getDepartment() : "Emergency Coordination");
                user = admin;
            }
            case VOLUNTEER -> {
                Volunteer volunteer = new Volunteer();
                volunteer.setUsername(request.getUsername());
                volunteer.setEmail(request.getEmail());
                volunteer.setPassword(encodedPassword);
                volunteer.setFullName(request.getFullName());
                volunteer.setPhone(request.getPhone());
                volunteer.setAddress(request.getAddress());
                volunteer.setSkills(request.getSkills() != null ? request.getSkills() : "First Aid, General Relief");
                volunteer.setLatitude(request.getLatitude() != null ? request.getLatitude() : 28.6139);
                volunteer.setLongitude(request.getLongitude() != null ? request.getLongitude() : 77.2090);
                volunteer.setApprovedByAdmin(true); // Auto-approve for demo convenience
                user = volunteer;
            }
            default -> { // VICTIM
                Victim victim = new Victim();
                victim.setUsername(request.getUsername());
                victim.setEmail(request.getEmail());
                victim.setPassword(encodedPassword);
                victim.setFullName(request.getFullName());
                victim.setPhone(request.getPhone());
                victim.setAddress(request.getAddress());
                victim.setEmergencyContact(request.getEmergencyContact());
                victim.setFamilyMembersCount(request.getFamilyMembersCount() != null ? request.getFamilyMembersCount() : 1);
                victim.setMedicalNeeds(request.getMedicalNeeds());
                user = victim;
            }
        }

        User savedUser = userRepository.save(user);

        activityLogService.logActivity(
                savedUser.getUsername(),
                savedUser.getRole().name(),
                "USER_REGISTERED",
                "User",
                savedUser.getId(),
                "Registered new " + savedUser.getRole() + " account: " + savedUser.getUsername()
        );

        return savedUser;
    }

    @Override
    public User getCurrentUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
    }
}
