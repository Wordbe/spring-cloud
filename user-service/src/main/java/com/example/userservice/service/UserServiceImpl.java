package com.example.userservice.service;

import com.example.userservice.client.OrderServiceClient;
import com.example.userservice.dto.UserDto;
import com.example.userservice.jpa.UserEntity;
import com.example.userservice.jpa.UserRepository;
import com.example.userservice.util.ModelMapperUtils;
import com.example.userservice.vo.ResponseOrder;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ModelMapperUtils modelMapperUtils;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final RestTemplate restTemplate;
    private final Environment env;
    private final OrderServiceClient orderServiceClient;
    private final CircuitBreakerFactory circuitBreakerFactory;

    @Override
    public UserDto createUser(UserDto userDto) {
        userDto.setUserId(UUID.randomUUID().toString());

        UserEntity userEntity = modelMapperUtils.mapper().map(userDto, UserEntity.class);
        userEntity.setEncryptedPwd(bCryptPasswordEncoder.encode(userDto.getPwd()));

        UserEntity savedUserEntity = userRepository.save(userEntity);

        return modelMapperUtils.mapper().map(savedUserEntity, UserDto.class);
    }

    @Override
    public UserDto getUserById(String userId) {
        UserEntity userEntity = userRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("해당 ID의 사용자를 찾을 수 없습니다."));
        UserDto userDto = new ModelMapper().map(userEntity, UserDto.class);

//        String orderUrl = String.format(env.getProperty("order_service.url"), userId);
//        ResponseEntity<List<ResponseOrder>> orderListResponse =
//                restTemplate.exchange(
//                        orderUrl,
//                        HttpMethod.GET,
//                        null,
//                        new ParameterizedTypeReference<>() {}
//                );
//        List<ResponseOrder> orders = orderListResponse.getBody();

        // Feign Exception Handling
//        List<ResponseOrder> orderList = null;
//        try {
//            orderList = orderServiceClient.getOrders(userId);
//        } catch (FeignException e) {
//            log.error(e.getMessage());
//        }

        // ErrorDecoder
//        List<ResponseOrder> orders = orderServiceClient.getOrders(userId);

        // CircuitBreaker
        log.info("order-service 호출 전...");
        CircuitBreaker circuitbreaker = circuitBreakerFactory.create("circuitbreaker");
        List<ResponseOrder> orders = circuitbreaker.run(() -> orderServiceClient.getOrders(userId),
                throwable -> new ArrayList<>());
        log.info("order-service 호출완료");

        userDto.setOrders(orders);

        return userDto;
    }

    @Override
    public Iterable<UserEntity> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public UserDto getUserDetailByEmail(String email) {
        UserEntity userEntity = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(email));

        return modelMapperUtils.mapper().map(userEntity, UserDto.class);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));

        return new User(userEntity.getEmail(), userEntity.getEncryptedPwd(), new ArrayList<>());
    }
}
