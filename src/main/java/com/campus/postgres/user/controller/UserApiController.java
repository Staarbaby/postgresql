package com.campus.postgres.user.controller;

import com.campus.postgres.dto.request.RegistrationUserRequest;
import com.campus.postgres.dto.request.EditUserRequest;
import com.campus.postgres.dto.response.UserResponse;
import com.campus.postgres.user.entity.UserEntity;
import com.campus.postgres.user.exception.BedRequestException;
import com.campus.postgres.user.exception.UserAlreadyExistException;
import com.campus.postgres.user.exception.UserNotFoundException;
import com.campus.postgres.user.repository.UserRepository;
import com.campus.postgres.user.routes.UserRoutes;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class UserApiController {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${init.email}")
    private String initUser;
    @Value("${init.password}")
    private String initPassword;


    @GetMapping("/")
    public UserEntity root(){
        UserEntity user = UserEntity.builder()
                .firstName("Test")
                .lastName("Test")
                .build();

        user = userRepository.save(user);
        return user;
    }

    @GetMapping(UserRoutes.INIT)
    public UserResponse init(){
        Optional<UserEntity> check = userRepository.findByEmail(initUser);
        if (check.isPresent()) return UserResponse.of(check.get());

        UserEntity user = UserEntity.builder()
                .firstName("")
                .lastName("")
                .email(initUser)
                .password(passwordEncoder.encode(initPassword))
                .build();

        user = userRepository.save(user);
        return UserResponse.of(user);
    }

    @GetMapping(UserRoutes.TEST)
    public String test(){
        return HttpStatus.OK.name();
    }
    @PostMapping(UserRoutes.REGISTRATION)
    public UserResponse registration (@RequestBody RegistrationUserRequest request) throws BedRequestException, UserAlreadyExistException {
        request.validation();
        Optional<UserEntity> check = userRepository.findByEmail(request.getEmail());
        if (check.isPresent()) throw new UserAlreadyExistException();

        UserEntity user = UserEntity.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
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
    public UserResponse edit(Principal principal, @RequestBody EditUserRequest request) throws UserNotFoundException {
        UserEntity user = userRepository.findByEmail(principal.getName()).orElseThrow(UserNotFoundException::new);

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        userRepository.save(user);

        return UserResponse.of(user);
    }

    @DeleteMapping(UserRoutes.DELETE)
    public String delete(@PathVariable Long id){
        userRepository.deleteById(id);
        return HttpStatus.OK.name();
    }

}
