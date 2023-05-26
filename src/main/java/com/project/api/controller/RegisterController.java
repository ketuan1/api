package com.project.api.controller;

import java.security.SecureRandom;
import java.util.List;

import org.springframework.beans.BeanUtils;
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

import com.project.api.ResponseObject;
import com.project.api.dao.RegisterRepository;
import com.project.api.dto.RegisterDto;
import com.project.api.entity.Register;

@CrossOrigin(origins = "http://localhost:3000",allowedHeaders = "*", allowCredentials = "true")
@RestController
@RequestMapping("/api")
public class RegisterController {
    BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    @Autowired
    private RegisterRepository registerRepository;

    @PostMapping("/register")
    public ResponseObject Register(@RequestBody Register register) {
        register.setPassword(bCryptPasswordEncoder.encode(register.getPassword()));
        registerRepository.save(register);
        return new ResponseObject("success", null, "Đăng kí thành công");
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseObject> Login(@RequestBody Register register) {
        List<Register> listResult = registerRepository.findByEmail(register.getEmail());
        if (listResult.size() == 0) {
            return new ResponseEntity<>(new ResponseObject("fail", null, "Tên tài khoản hoặc mật khẩu không chính xác"),
                    HttpStatus.BAD_REQUEST);

        } else {
            Boolean check = bCryptPasswordEncoder.matches(register.getPassword(), listResult.get(0).getPassword());
            if (check == false) {
                return new ResponseEntity<>(
                        new ResponseObject("fail", null, "Tên tài khoản hoặc mật khẩu không chính xác"),
                        HttpStatus.BAD_REQUEST);

            } else {
                RegisterDto registerDto = new RegisterDto();
                registerDto.setId(listResult.get(0).getId());
                registerDto.setEmail(listResult.get(0).getEmail());
                registerDto.setFirstName(listResult.get(0).getFirstName());
                registerDto.setLastName(listResult.get(0).getLastName());
                registerDto.setPhone(listResult.get(0).getPhone());
                registerDto.setAddress(listResult.get(0).getAddress());
                registerDto.setRole(listResult.get(0).getRole());
                return new ResponseEntity<>(
                        new ResponseObject("success", registerDto, "Thành công"),
                        HttpStatus.OK);

            }
        }
    }
}
