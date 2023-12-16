package com.campus.postgres.dto.request;

import com.campus.postgres.user.exception.BedRequestException;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EditUserRequest {
    private String firstName;
    private String lastName;
}
