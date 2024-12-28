package com.example.demo.service.impl;

import java.security.SecureRandom;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.exception.authexception.UnauthorizedException;
import com.example.demo.exception.securityexception.PasswordInvalidException;
import com.example.demo.exception.userexception.UserNotFoundException;
import com.example.demo.model.dto.UserDto;
import com.example.demo.model.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.AuthService;
import com.example.demo.service.PasswordService;
import com.example.demo.service.RedisService;

@Service
public class AuthServiceImpl implements AuthService {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private PasswordService passwordService;
	
	@Autowired
	private RedisService redisService;
	
	
	@Override
	public UserDto login(String idNumber, String password) {
		
		// 尋找用戶
		
		User loginUser = userRepository.findByIdNumber(idNumber)
				                       .orElseThrow( ()->new UserNotFoundException("用戶不存在") );
		
		// 驗證密碼
		
		if(!passwordService.verifyPassword(password,loginUser.getHashPassword())){
			throw new PasswordInvalidException("密碼錯誤");
		}
		
		return modelMapper.map(loginUser,UserDto.class); 
		
	}
	
	
	
	@Override
	public UserDto adminLogin(String idNumber, String password) {
		
		// 驗證用戶
		User loginUser = userRepository.findByIdNumber(idNumber)
				                       .orElseThrow( ()->new UserNotFoundException("用戶不存在") );
		
		// 驗證密碼
		if(!passwordService.verifyPassword(password,loginUser.getHashPassword())){
			throw new PasswordInvalidException("密碼錯誤");
		}
		
		// 驗證權限
		if(!loginUser.getRole().equals("ROLE_ADMIN")) {
			
			throw new UnauthorizedException();  // 權限不足
		}
		
		return modelMapper.map(loginUser,UserDto.class); 
		
	}
	


	@Override
	public void generateToken() {
		
	}
	

	// 產生驗證碼 + 驗證 ( 忘記密碼 )

	@Override
	public String generateAuthCode() {
		
		SecureRandom sr = new SecureRandom(); 
		
		Integer authCode = sr.nextInt(900000) + 100000;  // 產生 [100000 ~ 1000000) 之間的隨機數
		
		redisService.saveData("AuthCode", String.valueOf(authCode));
		
		return String.valueOf(authCode);
	
	}


	@Override
	public Boolean checkAuthCode(String authCodeInput) {
		
		String authCode = redisService.getData("AuthCode");
		
		return authCode.equals(authCodeInput);
	}	
	
	
	
	
	
	
	
	
	
	

}
