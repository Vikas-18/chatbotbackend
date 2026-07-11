package com.hostelchatbot.hostelchatbot.DTO;

public record LoginResponse(String token, String tokenType, long expiresIn) {
}
