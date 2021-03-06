package com.example.demo.module.controller;

import com.example.demo.infra.jwt.JwtTokenProvider;
import com.example.demo.module.dto.Token;
import com.example.demo.module.dto.SignUpForm;
import com.example.demo.module.dto.response.UserResponse;
import com.example.demo.module.entity.Account;
import com.example.demo.module.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;
    private final JwtTokenProvider provider;
    // todo signUpForm validation

    @PostMapping("/signUp")
    public ResponseEntity signUp(@RequestBody SignUpForm form) {
        Account account = accountService.signUp(form);
        return ResponseEntity.ok().body(account);
    }

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody SignUpForm form) {
        Account account = accountService.login(form);
        UserResponse.TokenInfo info = provider.generateToken(account.getUsername());
        accountService.saveRefreshToken(account,info);
        return ResponseEntity.ok().body(info);
    }

    @PostMapping("/reissue")
    public ResponseEntity reissue(@RequestBody Token token) {
        // validation
        return accountService.reissue(token);
    }

    @PostMapping("/logOut")
    public ResponseEntity logOut(@RequestBody Token token) {
        accountService.logOut(token);
        return ResponseEntity.ok().body("로그아웃 완료");
    }
}
