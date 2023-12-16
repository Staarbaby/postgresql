package com.campus.postgres;

import com.campus.postgres.dto.request.RegistrationUserRequest;
import com.campus.postgres.dto.request.EditUserRequest;
import com.campus.postgres.user.entity.UserEntity;
import com.campus.postgres.user.repository.UserRepository;
import com.campus.postgres.user.routes.UserRoutes;
import com.fasterxml.jackson.databind.JsonSerializable;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class WebTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;


    @Value("${init.email}")
    private String initUser;
    @Value("${init.password}")
    private String initPassword;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @BeforeEach
    public void config(){
        Optional<UserEntity> check = userRepository.findByEmail(initUser);
        if(check.isPresent()) return;

        UserEntity user = UserEntity.builder()
                .email(initUser)
                .password(passwordEncoder.encode(initPassword))
                .build();
        userRepository.save(user);
    }

    public String authHeader(){
        return "Basic" + Base64.getEncoder().encodeToString((initUser + ":" + initPassword).getBytes());
    }
    @Test
    void contextLoad() throws Exception {
        UserEntity user = UserEntity.builder()
                .firstName("1")
                .lastName("1")
                .build();

        user = userRepository.save(user);

        mockMvc.perform(
                         get(UserRoutes.BY_ID, user.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, authHeader())
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void createTest() throws Exception {
        RegistrationUserRequest request = RegistrationUserRequest.builder()
                .firstName("test")
                .lastName("test")
                .email("reg@mail.ru")
                .password("1")
                .build();
        RequestBuilder post;
        mockMvc.perform(
                post(UserRoutes.REGISTRATION)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        )
                .andDo(print())
                .andExpect(content().string(containsString("test")));
    }

    @Test
    void findById() throws Exception{
        UserEntity user = UserEntity.builder()
                .firstName("findById")
                .lastName("findById")
                .build();
        user = userRepository.save(user);

        mockMvc.perform(get(UserRoutes.BY_ID, user.getId())
                .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, authHeader()))

                .andDo(print())
                .andExpect(content().string(containsString("findById")));
    }
    @Test
    void findById_notFound() throws Exception {
        mockMvc.perform(get(UserRoutes.BY_ID, "11111")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, authHeader()) )
                .andDo(print())
                .andExpect(status().isNotFound());
    }
    @Test
    void updateTest() throws Exception{
        EditUserRequest request = EditUserRequest.builder()
                .firstName("update")
                .lastName("update")
                .build();

        mockMvc.perform(put(UserRoutes.EDIT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header(HttpHeaders.AUTHORIZATION, authHeader()))
                .andDo(print())
                .andExpect(content().string(containsString("update")));
    }

    @Test
    void deleteTest() throws Exception{
        UserEntity user = UserEntity.builder()
                .firstName("delete")
                .lastName("delete")
                .build();
        user = userRepository.save(user);

        assert userRepository.findById(user.getId()).isPresent();

        mockMvc.perform(delete(UserRoutes.DELETE, user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, authHeader()))
                .andDo(print())
                .andExpect(status().isOk());

        assert userRepository.findById(user.getId()).isEmpty();
    }

    @Test
    void searchTest() throws Exception{
        List<UserEntity> users = new ArrayList<>();
            for (int i = 0; i<100;i++){
                users.add(
                        userRepository.save(
                                UserEntity.builder()
                                        .firstName("search" +i)
                                        .lastName("search" +i)
                                        .build()));
            }

            //users.get(0).setFirstName("ppp");

            mockMvc.perform(get(UserRoutes.SEARCH).param("size", "100").contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(content().json(objectMapper.writeValueAsString(users)));
        }
}
