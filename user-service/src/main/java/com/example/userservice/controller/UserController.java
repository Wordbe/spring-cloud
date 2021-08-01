package com.example.userservice.controller;

import com.example.userservice.dto.UserDto;
import com.example.userservice.jpa.UserEntity;
import com.example.userservice.service.UserService;
import com.example.userservice.util.ModelMapperUtils;
import com.example.userservice.vo.RequestUser;
import com.example.userservice.vo.ResponseUser;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RequestMapping("/")
@RestController
public class UserController {
    private final Environment env;
    private final UserService userService;
    private final ModelMapperUtils modelMapperUtils;

    @GetMapping("/health_check")
    @Timed(value = "users.status", longTask = true)
    public String status() {
        return String.format("[UserController.status] It's working!\n" +
                "port: %s", env.getProperty("local.server.port"));
    }

    @PostMapping("/users")
    public ResponseEntity<ResponseUser> createUser(@RequestBody RequestUser user) {
        UserDto userDto = modelMapperUtils.mapper().map(user, UserDto.class);
        userService.createUser(userDto);

        ResponseUser responseUser = modelMapperUtils.mapper().map(userDto, ResponseUser.class);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseUser);
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<ResponseUser> getUser(@PathVariable("userId") String userId) {
        UserDto userDto = userService.getUserById(userId);
        ResponseUser responseUser = modelMapperUtils.mapper().map(userDto, ResponseUser.class);
        return ResponseEntity.status(HttpStatus.OK).body(responseUser);
    }

    @GetMapping("/users")
    public ResponseEntity<List<ResponseUser>> getUsers() {
        List<UserEntity> allUsers = (List<UserEntity>) userService.getAllUsers();
        return ResponseEntity.status(HttpStatus.OK).body(
                allUsers.stream().map(userEntity ->
                        modelMapperUtils.mapper().map(userEntity, ResponseUser.class)
                ).collect(Collectors.toList()));
    }
}
