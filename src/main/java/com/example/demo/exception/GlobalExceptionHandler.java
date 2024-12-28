package com.example.demo.exception;


import java.net.http.HttpRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.config.MessageConfig;
import com.example.demo.exception.accountexception.InsufficientFundsException;
import com.example.demo.exception.authexception.UnauthorizedException;
import com.example.demo.exception.securityexception.PasswordInvalidException;
import com.example.demo.exception.userexception.UserAlreadyExistException;
import com.example.demo.exception.userexception.UserNotFoundException;

import jakarta.servlet.http.HttpServletRequest;

// 自訂例外可決定是否繼承受檢例外 ( ex.Exception )
// 也可繼承非受檢例外 ( ex.RuntimrException )，可簡潔代碼，減少顯式撰寫 throws，但拋出錯誤時仍需要處裡

// 當例外未處理時最終會由 JVM 進行錯誤堆疊 ( Stack Trace )

@ControllerAdvice
public class GlobalExceptionHandler {
	
	@Autowired
	private MessageConfig message;
	
	
	Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
	
	// 記錄錯誤
	public void logError(Exception ex) {
		
		String errorMessage = String.format(
			    "Exception: %s\nOccurred in: %s.%s (line %d)",
			    ex.getClass().getName(),                                  // 錯誤的類型
			    ex.getStackTrace()[0].getClassName(),                     // 發生錯誤的類別名稱
			    ex.getStackTrace()[0].getMethodName(),                    // 發生錯誤的方法名稱
			    ex.getStackTrace()[0].getLineNumber()                     // 發生錯誤的行號
			    );
		
		logger.error(errorMessage);
		
		/*  輸出如下
		 	Exception: java.lang.NullPointerException
		 	Occurred in: com.example.service.UserService.getUserById (line 42)
		 	Message: Cannot invoke "Object.toString()" because "object" is null
		*/
	}
	
	
	@ExceptionHandler({PasswordInvalidException.class,
		               UserNotFoundException.class,
		               UserAlreadyExistException.class,
		               InsufficientFundsException.class,
		               UnauthorizedException.class,
		               })
    public String handleException(Exception ex,RedirectAttributes redirectAttributes,HttpServletRequest request) {
        
		logError(ex);
		
		if( ex instanceof PasswordInvalidException ){			
			redirectAttributes.addFlashAttribute("errorMessage", message.getPasswordInvalid());    // 將錯誤消息添加到 model
		}
		
		if( ex instanceof UserNotFoundException ){			
			redirectAttributes.addFlashAttribute("errorMessage", message.getUserNotFound());  
		}
		
		if( ex instanceof UserAlreadyExistException ){			
			redirectAttributes.addFlashAttribute("errorMessage", message.getUserAlreadyExist());  
		}
		
		if( ex instanceof InsufficientFundsException ){			
			redirectAttributes.addFlashAttribute("errorMessage", message.getInsufficientFunds());  
		}
		
		if( ex instanceof UnauthorizedException ){			
			
			return "redirect:/bank/login";  // 未授權回首頁
		}
		
		// 重導回上一頁
		
		String previousURL = request.getRequestURL().toString();   // 取得當前發生錯誤的請求 URL
		
		return "redirect:"+ previousURL ;                          // 返回原本的請求頁面
		
    }	
	
}
