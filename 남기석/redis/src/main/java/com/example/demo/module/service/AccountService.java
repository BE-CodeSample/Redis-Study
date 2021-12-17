package com.example.demo.module.service;

import com.example.demo.infra.errors.ErrorCode;
import com.example.demo.infra.errors.exception.AuthenticationException;
import com.example.demo.infra.errors.exception.LogOutedException;
import com.example.demo.infra.errors.exception.PasswordNotMatchException;
import com.example.demo.infra.errors.exception.UserNotFoundException;
import com.example.demo.infra.jwt.JwtTokenProvider;
import com.example.demo.module.dto.Token;
import com.example.demo.module.dto.SignUpForm;
import com.example.demo.module.dto.response.UserResponse;
import com.example.demo.module.entity.Account;
import com.example.demo.module.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.concurrent.TimeUnit;

@Service
@Transactional
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final RedisTemplate redisTemplate;
    private final JwtTokenProvider provider;
    //validation은 나중에



    public Account signUp(SignUpForm form) {
        Account account = new Account();
        account.setUsername(form.getUsername());
        account.setPassword(passwordEncoder.encode(form.getPassword()));
        return accountRepository.save(account);
    }


    public Account login(SignUpForm form) {
        Account account = accountRepository.findByUsername(form.getUsername()).orElseThrow(() -> new UserNotFoundException(
                ErrorCode.USER_NOT_FOUND.getErrorMessage(), ErrorCode.USER_NOT_FOUND.getErrorCode()
        ));
        if (!passwordEncoder.matches(form.getPassword(), account.getPassword())) {
            throw new PasswordNotMatchException(ErrorCode.PASSWORD_NOT_MATCH.getErrorMessage(),
                    ErrorCode.PASSWORD_NOT_MATCH.getErrorCode());
        }
        return account;
    }

    public void saveRefreshToken(Account account, UserResponse.TokenInfo info) {
        redisTemplate.opsForValue().set("RT:" + account.getUsername(),
                info.getRefreshToken(), info.getRefreshTokenExpirationTime(), TimeUnit.MILLISECONDS);

        /**
         * RefreshToken 저장을 위해 사용한 set 메서드는 key, value 값 외에 long type의 timeout, T
         * imeUnit type의 unit을 인자로 받습니다.
         *
         * 해당 메서드의 용도는 'Set the value and expiration timeout for key.'
         * refresh token의 유효시간 정보를 함께 저장해서 토큰이 만료되었을 때
         * value 값을 자동으로 삭제하는 기능을 위해 사용하였습니다.
         * */
    }

    public ResponseEntity reissue(Token token) {
        if (!provider.validateToken(token.getAccessToken())) {
            throw new AuthenticationException(ErrorCode.Authenticate_INVALID_Exception.getErrorMessage(), ErrorCode.Authenticate_INVALID_Exception.getErrorCode());
        }
        Authentication auth = provider.getAuth(token.getAccessToken());

        // redis에서 username으로 refreshToken가져온다
        String refreshToken = (String) redisTemplate.opsForValue().get("RT:" + auth.getName());

        // logout된 유저라면 rt 또한 삭제되었을테니 검증
        if (ObjectUtils.isEmpty(refreshToken)) {
            throw new LogOutedException(ErrorCode.LOGOUT_ACCESS_TOKEN.getErrorMessage(),
                    ErrorCode.LOGOUT_ACCESS_TOKEN.getErrorCode());
        }

        if (!refreshToken.equals(token.getRefreshToken())) {
            throw new AuthenticationException(ErrorCode.Authenticate_INVALID_Exception.getErrorMessage(), ErrorCode.Authenticate_INVALID_Exception.getErrorCode());
        }
        // reissue = 즉 refreshToken을 통한 토큰 갱싱
        // 따라서 새로운 토큰 생성해서 재발급
        UserResponse.TokenInfo reissuedTokenInfo = provider.generateToken(auth.getName());

        //refreshToken Redis에서 다시 업데이트
        redisTemplate.opsForValue().set("RT" + auth.getName(), reissuedTokenInfo.getRefreshToken(),
                reissuedTokenInfo.getRefreshTokenExpirationTime(), TimeUnit.MILLISECONDS);
        return ResponseEntity.ok().body(reissuedTokenInfo);
    }

    public void logOut(Token token) {
        //validation
        if (!provider.validateToken(token.getAccessToken())) {
            throw new AuthenticationException(ErrorCode.Authenticate_INVALID_Exception.getErrorMessage(),
                    ErrorCode.Authenticate_INVALID_Exception.getErrorCode());
        }

        // accessToken으로부터 auth 가져온다
        Authentication auth = provider.getAuth(token.getAccessToken());
        // auth의 username 가져와서 redis에 저장된 refreshToken 삭제
        if (redisTemplate.opsForValue().get("RT:" + auth.getName()) != null) {
            redisTemplate.delete("RT:" + auth.getName());
        }

        // blackList에 해당 accessToken 추가
            //먼저 해당 accessToken남은 시간 계산하고
        Long expiration = provider.getExpiration(token.getAccessToken());
        //blackList에 추가
        redisTemplate.opsForValue()
                .set(token.getAccessToken(), "logout", expiration, TimeUnit.MILLISECONDS);
    }
}
