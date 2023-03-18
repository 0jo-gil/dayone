package com.example.stock.web;

import com.example.stock.model.Auth;
import com.example.stock.secutiry.TokenProvider;
import com.example.stock.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final MemberService memberService;
    private final TokenProvider tokenProvider;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(
            @RequestBody Auth.SignUp request
    ){
        // 회원 가입
        var result = this.memberService.register(request);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/signin")
    public ResponseEntity<?> signin(
            @RequestBody Auth.SignIn request
    ){
        // 로그인
        var member = this.memberService.authenticate(request);
        var token = this.tokenProvider.generateToken(member.getUsername(), member.getRoles());
        log.info("user login -> " + request.getUsername());

        return ResponseEntity.ok(token);
    }
}
