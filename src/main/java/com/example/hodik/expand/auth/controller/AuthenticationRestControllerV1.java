package com.example.hodik.expand.auth.controller;

import com.example.hodik.expand.auth.controller.dto.AuthenticationRequestDTO;
import com.example.hodik.expand.auth.jwt.JwtTokenProvider;
import com.example.hodik.expand.model.User;
import com.example.hodik.expand.repository.UserRepository;
import com.example.hodik.expand.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class AuthenticationRestControllerV1 {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthenticationRestControllerV1(AuthenticationManager authenticationManager, UserRepository userRepository, UserService userService, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/add")
    public ResponseEntity<String> userRegister(@RequestBody AuthenticationRequestDTO request) {

        Optional<User> userByUsername = userService.findUserByUsername(request.getUsername());

        if (userByUsername.isPresent()) {
            return new ResponseEntity<>("Username is taken !! ", HttpStatus.BAD_REQUEST);
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        userService.save(user);
        return new ResponseEntity<>("User Register successful !!! ", HttpStatus.CREATED);
    }

    @PostMapping(value = "/authenticate")
    public ResponseEntity<?> authenticate(@RequestBody AuthenticationRequestDTO request) {
        try {
            String userName = request.getUsername();
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userName, request.getPassword()));
            User user = userRepository.findUserByUsername(userName)
                    .orElseThrow(() -> new UsernameNotFoundException("User doesn't exists"));
            String accessToken = jwtTokenProvider.createToken(userName, false);
            String refreshToken = jwtTokenProvider.createToken(userName, true);
            Map<Object, Object> response = new HashMap<>();
            response.put("username", userName);
            response.put("access_token", accessToken);
            response.put("refresh_token", refreshToken);
            return ResponseEntity.ok(response);
        } catch (AuthenticationException e) {
            return new ResponseEntity<>("Invalid email/password combination", HttpStatus.FORBIDDEN);
        }
    }

    @PostMapping("/logout")
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        SecurityContextLogoutHandler securityContextLogoutHandler = new SecurityContextLogoutHandler();
        securityContextLogoutHandler.logout(request, response, null);
    }

    @GetMapping("/refreshToken")
    public ResponseEntity<?> refreshToken(HttpServletRequest request) {
        String refreshToken = jwtTokenProvider.resolveToken(request);
        String userName = jwtTokenProvider.getUsername(refreshToken);
        User user = userRepository.findUserByUsername(userName)
                .orElseThrow(() -> new UsernameNotFoundException("User doesn't exists"));
        String accessToken = jwtTokenProvider.createToken(user.getUsername(), false);
        Map<Object, Object> response = new HashMap<>();
        response.put("username", user.getUsername());
        response.put("access_token", accessToken);
        response.put("refresh_token", refreshToken);
        return ResponseEntity.ok().body(response);
    }
}
