package com.campus.postgres.dto.request;

import lombok.Getter;

@Getter
public class EditUserRequest {
    private Long id;
    private String firstName;
    private String lastName;
}
