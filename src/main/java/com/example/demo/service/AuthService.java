package com.example.demo.service;

import com.example.demo.model.dto.UserDto;

public interface AuthService {
	
	UserDto login(String idNumber, String password) ;
	
	UserDto adminLogin(String idNumber, String password);
	
    void generateToken();  // 未做 登入後獲取令牌
    
    // 產生驗證碼
    String generateAuthCode();
    
    // 核對驗證碼
    Boolean checkAuthCode(String authCodeInput);
    
    
    
    
}
