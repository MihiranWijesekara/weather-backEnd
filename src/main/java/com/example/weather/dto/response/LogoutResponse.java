package com.example.weather.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record LogoutResponse(
        String message,
        String logoutUrl
) {

    public LogoutResponse {
        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("Message cannot be null or blank");
        }
        if (logoutUrl == null || logoutUrl.isBlank()) {
            throw new IllegalArgumentException("Logout URL cannot be null or blank");
        }
    }

    public static LogoutResponse success(String logoutUrl) {
        return new LogoutResponse(
                "Logout successful. Please clear your client-side token and redirect to the logout URL.",
                logoutUrl
        );
    }

    public static LogoutResponse of(String message, String logoutUrl) {
        return new LogoutResponse(message, logoutUrl);
    }
}