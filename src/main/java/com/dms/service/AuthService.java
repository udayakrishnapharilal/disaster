package com.dms.service;

import com.dms.dto.AuthRequest;
import com.dms.dto.AuthResponse;
import com.dms.dto.RegisterRequest;
import com.dms.model.User;

public interface AuthService {
    AuthResponse login(AuthRequest request);
    User register(RegisterRequest request);
    User getCurrentUser(String username);
}
