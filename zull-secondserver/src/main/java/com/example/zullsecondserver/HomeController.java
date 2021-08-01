package com.example.zullsecondserver;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping("/second-service/")
@RequiredArgsConstructor
public class HomeController {

    Environment env;

    @GetMapping("/home")
    public String home() {
        return "second home";
    }

    @GetMapping("/message")
    public String message(@RequestHeader("second-request") String header) {
        log.info(">>> header: " + header);
        return "message [second-service]";
    }

    @GetMapping("/check")
    public String check(HttpServletRequest request) {
        System.out.println("request.getServerPort() = " + request.getServerPort());

        return String.format("[check]: on port %s", env.getProperty("local.server.port"));
    }
}
