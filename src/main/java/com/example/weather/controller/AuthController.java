package com.example.weather.controller;

import com.example.weather.dto.request.LoginRequest;
import com.example.weather.dto.response.LoginResponse;
import com.example.weather.dto.response.LogoutResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Value("${auth0.domain}")
    private String domain;

    @Value("${auth0.clientId}")
    private String clientId;

    @Value("${auth0.clientSecret}")
    private String clientSecret;

    @Value("${auth0.audience}")
    private String audience;

    @Value("${auth0.returnToUrl}")
    private String returnToUrl;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type", "password");
        map.add("client_id", clientId);
        map.add("client_secret", clientSecret);
        map.add("audience", audience);
        map.add("username", loginRequest.getUsername());
        map.add("password", loginRequest.getPassword());
        map.add("scope", "openid profile email");
        map.add("connection", "Username-Password-Authentication");

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    "https://" + domain + "/oauth/token",
                    HttpMethod.POST,
                    entity,
                    Map.class
            );

            String accessToken = (String) response.getBody().get("access_token");
            return ResponseEntity.ok(new LoginResponse(accessToken));

        } catch (HttpClientErrorException e) {
            logger.error("Auth0 Token Error: {}", e.getResponseBodyAsString());
            return new ResponseEntity<>(e.getResponseBodyAsString(), e.getStatusCode());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");

        // Revoke the token with Auth0
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("client_id", clientId);
        map.add("client_secret", clientSecret);
        map.add("token", token);

        try {
            restTemplate.postForEntity(
                    "https://" + domain + "/oauth/revoke",
                    new HttpEntity<>(map, headers),
                    String.class
            );

            String logoutUrl = "https://" + domain + "/v2/logout?" +
                    "client_id=" + clientId +
                    "&returnTo=" + returnToUrl;

            return ResponseEntity.ok(new LogoutResponse(
                    "Token revoked and logout successful",
                    logoutUrl
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to revoke token");
        }
    }
}