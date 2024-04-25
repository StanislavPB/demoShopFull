package org.demoshop.controllers;


import org.demoshop.dto.NewUserDto;
import org.demoshop.exceptions.EmailAlreadyExistException;
import org.demoshop.models.ConfirmationCode;
import org.demoshop.models.User;
import org.demoshop.repositories.ConfirmationCodesRepository;
import org.demoshop.repositories.UsersRepository;
import org.demoshop.services.UsersService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.yml")
public class SecurityTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ConfirmationCodesRepository confirmationCodesRepository;

    @Autowired
    private UsersRepository usersRepository;

    @MockBean
    private UsersService usersService;

    @BeforeEach
    void setUp() {
        User testUser = new User();
        testUser.setFirstName("user1");
        testUser.setLastName("user1");
        testUser.setEmail("user1@gmail.com");
        testUser.setHashPassword("Qwerty007!");
        testUser.setRole(User.Role.USER);
        testUser.setState(User.State.NOT_CONFIRMED);
        User savedUser = usersRepository.save(testUser);

        ConfirmationCode code = new ConfirmationCode();
        code.setCode("someConfirmationCode");
        code.setUser(savedUser);
        code.setExpiredDateTime(LocalDateTime.now().plusDays(1));
        confirmationCodesRepository.save(code);
    }

    @AfterEach
    void drop() {
        confirmationCodesRepository.deleteAll();
        usersRepository.deleteAll();
    }

    @Test
    public void whenNoAuthenticationThenReturn401() throws Exception {
        mockMvc.perform(get("/api/admin/users")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void whenNotAuthorizeRoleThenReturn403() throws Exception {
        mockMvc.perform(get("/api/admin/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void whenDuplicateEmailThenReturn409() throws Exception {

        doThrow(new EmailAlreadyExistException("Email is already exist")).when(usersService).register(any());

        assertThrows(EmailAlreadyExistException.class, () -> {
            usersService.register(new NewUserDto("user1@gmail.com", "Qwerty007!","user1", "user1"));
        });

    }
}
