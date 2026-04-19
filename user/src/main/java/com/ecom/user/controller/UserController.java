package com.ecom.user.controller;

import com.ecom.user.dto.UserRequest;
import com.ecom.user.dto.UserResponse;
import com.ecom.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
//@Slf4j
public class UserController {

    private final UserService userService;
//    private static Logger logger = LoggerFactory.getLogger(UserController.class);


    @PostMapping("/createUser")
    private ResponseEntity<String> createUser(@RequestBody UserRequest request) {
        userService.createUser(request);
        return ResponseEntity.ok("User created");
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable String userId){
//        log.info("Request received for user: {}", userId);
//        log.warn("Request received for user: {}", userId);
//        log.error("Request received for user: {}", userId);
//        log.trace("Request received for user: {}", userId);
//        log.debug("Request received for user: {}", userId);
        return userService.getUserById(userId);
    }

    @GetMapping("/test")
    public ResponseEntity<String> test(){
        return ResponseEntity.ok("Hello World");
    }


}
