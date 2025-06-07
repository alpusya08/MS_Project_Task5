package com.itm.space.backendresources.service;

import com.itm.space.backendresources.api.response.UserResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;

@SpringBootTest
public class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Test
    void shouldReturnUserById() {
        UserResponse user = userService.getUserById(UUID.fromString("b9faa7fe-5383-4f58-9f7e-8697e84ccb54"));
        assertNotNull(user);
    }
}
