package com.example.userservice.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class RequestUser {

    @NotNull(message = "이메일은 null 일 수 없습니다.")
    @Size(min = 2, message = "이메일은 2자 이상이어야 합니다.")
    private String email;

    @NotNull(message = "이름은 null 일 수 없습니다.")
    @Size(min = 2, message = "이름은 2자 이상이어야 합니다.")
    private String pwd;

    @NotNull(message = "비밀번호는 null 일 수 없습니다.")
    @Size(min = 8, message = "비밀번호는 8자 이상이어야 합니다.")
    private String name;
}
