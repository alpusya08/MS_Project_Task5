package com.itm.space.backendresources.controller;

import com.itm.space.backendresources.BaseIntegrationTest;
import com.itm.space.backendresources.api.request.UserRequest;
import com.itm.space.backendresources.api.response.UserResponse;
import com.itm.space.backendresources.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerIntegrationTest extends BaseIntegrationTest {

    @MockBean
    private UserService userService;

    @Test
    void createUser_shouldReturn200_withMockJwt() throws Exception {
        UserRequest userRequest = UserRequest.builder()
                .username("alpamys")
                .email("123@gmail.com")
                .password("1234")
                .firstName("Alpamys")
                .lastName("Paninov")
                .build();

        mvc.perform(requestWithContent(post("/api/users"), userRequest)).andExpect(status().isOk());
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
                .groups(List.of("Moderators"))
                .build();

        Mockito.when(userService.getUserById(id)).thenReturn(userResponse);

        mvc.perform(get("/api/users/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Alpamys"))
                .andExpect(jsonPath("$.lastName").value("Paninov"))
                .andExpect(jsonPath("$.email").value("123@gmail.com"))
                .andExpect(jsonPath("$.roles[0]").value("ROLE_USER"))
                .andExpect(jsonPath("$.groups[0]").value("Moderators"));
    }

    @Test
    @WithMockUser(username = "alpamys", roles = "MODERATOR")
    void hello_shouldReturnAuthenticatedUsername() throws Exception {
        mvc.perform(get("/api/users/hello"))
                .andExpect(status().isOk())
                .andExpect(content().string("alpamys"));
    }
}


