package com.project.api.controller;

import java.security.SecureRandom;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.api.dao.RegisterRepository;
import com.project.api.entity.Register;

@RestController
@CrossOrigin("*")
@RequestMapping("/api")
public class RegisterController {
    BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    @Autowired
    private RegisterRepository registerRepository;

    @PostMapping("/register")
    public ResponseEntity<String> Register(@RequestBody Register register) {
        register.setPassword(bCryptPasswordEncoder.encode(register.getPassword()));
        registerRepository.save(register);
        return new ResponseEntity<>("Đăng kí thành công", HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<String> Login(@RequestBody Register register) {
        List<Register> listResult = registerRepository.findByFirstName(register.getFirstName());
        if (listResult.size() == 0) {
            return new ResponseEntity<>("Tên tài khoản hoặc password không chính xác", HttpStatus.CREATED);
        } else {
            Boolean check = bCryptPasswordEncoder.matches(register.getPassword(), listResult.get(0).getPassword());
            if (check == false) {
                return new ResponseEntity<>("Tên tài khoản hoặc password không chính xác", HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>("Đăng nhập thành công", HttpStatus.CREATED);
            }
        }
    }
}
