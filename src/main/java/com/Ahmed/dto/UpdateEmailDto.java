package com.Ahmed.dto;

import com.Ahmed.annotation.ValidEmail;
import com.Ahmed.annotation.ValidPassword;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEmailDto {
    @ValidEmail
    private String email;

    @ValidPassword
    private String password;
}
