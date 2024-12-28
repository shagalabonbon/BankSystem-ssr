package com.example.demo.aop.check;

import java.io.IOException;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.demo.exception.authexception.UnauthorizedException;
import com.example.demo.model.dto.UserDto;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/* @Before、@After 只能執行邏輯，無法直接阻止目標方法執行
 * 
 * 使用 @Around 執行全面控制
 * 
 * */ 

@Aspect 
@Component
public class UserSessionCheckAspect {

	@Autowired
	private HttpSession session;
	
	@Autowired
    private HttpServletResponse response; // 用於執行重導向操作
	
	@Around("@annotation(com.example.demo.aop.check.CheckUserSession)")      // Before 方法執行前執行 -  @annotation( 自定義標籤位置 ) 匹配使用到 @CheckUserSession 的方法
	public Object checkUserSession(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
		
		// 取得 User
		
		UserDto loginUserDto = (UserDto)session.getAttribute("loginUserDto");
		
		// 檢查登入狀態
		
		if( loginUserDto == null ) {
			response.sendRedirect("/bank/login");   // 若未登入，直接重導到登入頁面
			   
		}
		
		return null;

	}
	
}
