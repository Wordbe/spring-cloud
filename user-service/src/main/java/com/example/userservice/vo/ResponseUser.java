package com.example.userservice.vo;

import lombok.Getter;
import lombok.Setter;

@Getter // json mapper: dto -> json
@Setter // object mapper: entity -> dto
public class ResponseUser {
    private String email;
    private String name;
    private String userId;
}
