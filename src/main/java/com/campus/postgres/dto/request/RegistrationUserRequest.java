package com.campus.postgres.dto.request;

import com.campus.postgres.user.exception.BedRequestException;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RegistrationUserRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String password;

    public void validation() throws BedRequestException {
        if (email == null || email.isEmpty()) throw new BedRequestException();
        if(password == null || password.isEmpty()) throw new BedRequestException();
    }
}
