package com.example.demo.module.service;

import com.example.demo.infra.errors.ErrorCode;
import com.example.demo.infra.errors.exception.PasswordNotMatchException;
import com.example.demo.infra.errors.exception.UserNotFoundException;
import com.example.demo.module.dto.SignUpForm;
import com.example.demo.module.entity.Account;
import com.example.demo.module.entity.UserAccount;
import com.example.demo.module.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class AccountService implements UserDetailsService {
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    //validation은 나중에

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountRepository.findByUsername(username).orElseThrow(() ->
                new UsernameNotFoundException(username));
        return new UserAccount(account);
    }

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

        //todo 레디스를 활용하여 refreshToken을 레디스에 저장하기
        return account;
    }
}
