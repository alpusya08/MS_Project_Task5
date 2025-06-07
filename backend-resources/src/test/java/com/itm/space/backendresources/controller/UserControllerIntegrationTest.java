package com.itm.space.backendresources.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itm.space.backendresources.api.request.UserRequest;
import com.itm.space.backendresources.api.response.UserResponse;
import com.itm.space.backendresources.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createUser_shouldReturn200_withMockJwt() throws Exception {
        UserRequest userRequest = UserRequest.builder()
                .username("alpamys")
                .email("123@gmail.com")
                .password("1234")
                .firstName("Alpamys")
                .lastName("Paninov")
                .build();

        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .claim("realm_access", Map.of("roles", List.of("MODERATOR")))
                .claim("sub", "alpamys")
                .build();

        JwtAuthenticationToken token = new JwtAuthenticationToken(jwt, List.of(
                new SimpleGrantedAuthority("ROLE_MODERATOR")
        ));

        mockMvc.perform(post("/api/users")
                        .with(authentication(token))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isOk());
    }


    @Test
    @WithMockUser(roles = "MODERATOR")
    void getUserById_shouldReturnUserResponse() throws Exception {
        UUID id = UUID.randomUUID();

        UserResponse userResponse = UserResponse.builder()
                .firstName("Alpamys")
                .lastName("Paninov")
                .email("123@gmail.com")
                .roles(List.of("ROLE_USER"))
                .groups(List.of("GroupA"))
                .build();

        Mockito.when(userService.getUserById(id)).thenReturn(userResponse);

        mockMvc.perform(get("/api/users/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Alpamys"))
                .andExpect(jsonPath("$.lastName").value("Paninov"))
                .andExpect(jsonPath("$.email").value("123@gmail.com"))
                .andExpect(jsonPath("$.roles[0]").value("ROLE_USER"))
                .andExpect(jsonPath("$.groups[0]").value("GroupA"));
    }

    @Test
    @WithMockUser(username = "alpamys", roles = "MODERATOR")
    void hello_shouldReturnAuthenticatedUsername() throws Exception {
        mockMvc.perform(get("/api/users/hello"))
                .andExpect(status().isOk())
                .andExpect(content().string("alpamys"));
    }
}


