package com.campus.postgres.user.controller;

import com.campus.postgres.dto.request.CreateUserRequest;
import com.campus.postgres.dto.request.EditUserRequest;
import com.campus.postgres.dto.response.UserResponse;
import com.campus.postgres.user.entity.UserEntity;
import com.campus.postgres.user.exception.UserNotFoundException;
import com.campus.postgres.user.repository.UserRepository;
import com.campus.postgres.user.routes.UserRoutes;
import lombok.AllArgsConstructor;
import org.apache.catalina.User;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
public class UserApiController {
    private final UserRepository userRepository;

    @GetMapping("/")
    public UserEntity test(){
        UserEntity user = UserEntity.builder()
                .firstName("Test")
                .lastName("Test")
                .build();

        user = userRepository.save(user);
        return user;
    }
    @PostMapping(UserRoutes.CREATE)
    public UserResponse create (@RequestBody CreateUserRequest request){
        UserEntity user = UserEntity.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .build();

        user = userRepository.save(user);
        return UserResponse.of(user);

    }

    @GetMapping(UserRoutes.BY_ID)
    public UserResponse byId(@PathVariable Long id) throws UserNotFoundException {
        UserEntity user = userRepository
                .findById(id)
                .orElseThrow(UserNotFoundException::new);
        return UserResponse.of(user);

    }

    @GetMapping(UserRoutes.SEARCH)
    public List<UserResponse> search(
            @RequestParam(defaultValue = "") String query,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "0") Integer page){
        Pageable pageable = PageRequest.of(page, size);

        ExampleMatcher exampleMatcher = ExampleMatcher.matchingAny()
                .withMatcher("firstName", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
                .withMatcher("lastName", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase());

        Example<UserEntity> example = Example.of(
                UserEntity.builder().firstName(query).lastName(query).build(),
                exampleMatcher
        );

        return userRepository
                .findAll(example, pageable)
                .stream()
                .map(UserResponse::of)
                .collect(Collectors.toList());
    }

    @PutMapping(UserRoutes.EDIT)
    public UserResponse edit(@PathVariable Long id, @RequestBody EditUserRequest request) throws UserNotFoundException {
        UserEntity user = userRepository.findById(request.getId()).orElseThrow(UserNotFoundException::new);

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        userRepository.save(user);

        return UserResponse.of(user);
    }

}
