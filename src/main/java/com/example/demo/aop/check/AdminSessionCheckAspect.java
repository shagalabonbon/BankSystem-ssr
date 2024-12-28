package com.example.demo.aop.check;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.demo.exception.authexception.UnauthorizedException;
import com.example.demo.model.dto.UserDto;

import jakarta.servlet.http.HttpSession;


@Aspect 
@Component
public class AdminSessionCheckAspect {

	@Autowired
	private HttpSession session;
	
	@Before("@annotation(com.example.demo.aop.check.CheckAdminSession)")      // Before 方法執行前執行 -  @annotation( 自定義標籤位置 ) 匹配使用到 @CheckUserSession 的方法
	public void checkAdminSession() {
		
		// 取得 Admin
		
		UserDto loginAdminDto = (UserDto)session.getAttribute("loginAdminDto");
		
		// 檢查登入狀態
		
		if( loginAdminDto == null ) {
			throw new UnauthorizedException();
		}
		
	}
	
}
