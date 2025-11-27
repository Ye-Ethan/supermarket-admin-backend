package org.yaojiu.supermarket.service;

import org.yaojiu.supermarket.entity.UserDTO;

import java.util.Map;

public interface AuthService {
    public Map<String, String> login(UserDTO userDTO);
    public void logout(String token);
    public Map<String, String> refreshToken(String refreshToken);
}
