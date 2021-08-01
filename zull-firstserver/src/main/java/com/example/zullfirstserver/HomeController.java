package com.example.zullfirstserver;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/first-service/")
public class HomeController {

    @GetMapping("/home")
    public String home() {  
        return "first home";
    }

    @GetMapping("/message")
    public String message(@RequestHeader("first-request") String header) {
        log.info(">>> header: " + header);
        return "message [first-service]";
    }
}
