package com.example.userservice.vo;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
public class RequestLogin {
    @NotNull(message = "이메일이 필요합니다.")
    @Size(min = 2, message = "이메일은 두 글자 이상이어야 합니다.")
    private String email;

    @NotNull(message = "비밀번호가 필요합니다.")
    @Size(min = 8, message = "비밀번호는 8자 이상이어야 합니다.")
    private String password;
}
