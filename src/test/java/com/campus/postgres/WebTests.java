package com.campus.postgres;

import com.campus.postgres.dto.request.CreateUserRequest;
import com.campus.postgres.dto.request.EditUserRequest;
import com.campus.postgres.user.entity.UserEntity;
import com.campus.postgres.user.repository.UserRepository;
import com.campus.postgres.user.routes.UserRoutes;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.AutoConfigureDataJdbc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;

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

    @Test
    void contextLoad() throws Exception {
        UserEntity user = UserEntity.builder()
                .firstName("test")
                .lastName("test")
                .build();

        user = userRepository.save(user);

        mockMvc.perform(
                         get(UserRoutes.BY_ID, user.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

    }

    @Test
    void createTest() throws Exception {
        CreateUserRequest request = CreateUserRequest.builder()
                .firstName("test")
                .lastName("test")
                .build();
        RequestBuilder post;
        mockMvc.perform(
                post(UserRoutes.CREATE)
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
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(content().string(containsString("findById")));
    }
    @Test
    void findById_notFound() throws Exception {
        mockMvc.perform(get(UserRoutes.BY_ID, "111111")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }
    @Test
    void updateTest() throws Exception{
        UserEntity user = UserEntity.builder()
                .firstName("findById")
                .lastName("findById")
                .build();
        user = userRepository.save(user);

        EditUserRequest request = EditUserRequest.builder()
                .id(user.getId())
                .firstName("update")
                .lastName("update")
                .build();

        mockMvc.perform(put(UserRoutes.BY_ID, request.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
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
                        .contentType(MediaType.APPLICATION_JSON))
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
