package com.campus.postgres.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CreateUserRequest {
    private String firstName;
    private String lastName;
}
