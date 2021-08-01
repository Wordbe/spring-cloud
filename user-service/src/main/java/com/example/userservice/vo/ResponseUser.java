package com.example.userservice.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter // json mapper: dto -> json
@Setter // object mapper: entity -> dto
@JsonInclude(JsonInclude.Include.NON_NULL) // null 값을 가진 필드는 json 에 포함시키지 않는다.
public class ResponseUser {
    private String email;
    private String name;
    private String userId;

    private List<ResponseOrder> orders;
}
