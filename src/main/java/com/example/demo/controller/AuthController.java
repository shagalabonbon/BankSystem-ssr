package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.config.MessageConfig;
import com.example.demo.model.dto.UserDto;
import com.example.demo.model.entity.User;
import com.example.demo.service.AuthService;
import com.example.demo.service.GmailService;
import com.example.demo.service.UserService;
import com.google.api.services.gmail.Gmail;

import jakarta.servlet.http.HttpSession;

// 使用 Post/Redirect/Get (PRG) 模式： Post 請求搭配 Redirect 再由 GET 獲取頁面 ( 避免由 post 直接導向頁面 ) 

@Controller
@RequestMapping("/bank")
public class AuthController {

	@Autowired
	private AuthService authService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private GmailService gmailService;
		
	@Autowired
	private MessageConfig message;
	

	@GetMapping("/login")
	public String loginPage() {
		
		return "login";  
	}
	
	@PostMapping("/login")
    public String login( @RequestParam String idNumber , @RequestParam String password , HttpSession session , Model model ) {
		
		// 登入服務
		UserDto loginUserDto = authService.login(idNumber,password);
		      
		session.setAttribute("loginUserDto", loginUserDto);  // 將登入者的 DTO 設入 session ( 可改為憑證 )
					
		return "redirect:/bank/home";  
	}
	
	
	@PostMapping("/logout")                       
	public String logout(HttpSession session) {
		
		session.invalidate();
		
		return "redirect:/bank/index";  
		
	}
	
	
	@GetMapping("/admin/login")
	private String adminLoginPage() {
		
		return "admin_login";
	}
	
	
	@PostMapping("/admin/login")
	private String adminLogin(@RequestParam String idNumber,@RequestParam String password , HttpSession session,Model model) {
		
		UserDto loginAdminDto = authService.adminLogin(idNumber,password);
		
		session.setAttribute("loginAdminDto",loginAdminDto);  // 資料存入 session 
		
		return "redirect:/bank/admin/home" ;   // 引導到 HomeController
		
	}
	
		
	// 忘記密碼 -----------------------------------------------------------
	
	@GetMapping("/user/recovery")
	private String recoveryPage() {
		
		return "user_recovery";
	}
	
	@GetMapping("/user/recovery/continue")
	private String checkCodePage() {
		
		return "user_recovery_verify";
	}
	
	@GetMapping("/user/recovery/reset")
	private String resetPage() {
		
		return "user_recovery_reset";
	}
	
	
	@GetMapping("/user/recovery/result")
	private String recoveryResult() {
		
		return "user_recovery_result";
	}
	
	
	@PostMapping("/user/recovery")
	public String sendRecoveryMail(@RequestParam String email,RedirectAttributes redirectAttributes,HttpSession session) {
		
		UserDto userDto = userService.getUserByEmail(email);  // 找不到會拋出 null
		
		if( userDto==null ) {
			
			redirectAttributes.addAttribute("errorMessage","Email 不存在");
			
			return "redirect:/bank/user/recovery";
		}
		
		// 傳遞使用者資料
		
		session.setAttribute("userDto", userDto); 
		
		// 發送驗證碼郵件
		
		try {
			Gmail service = gmailService.getGmailService();
	    	gmailService.sendMessage(service, "me", gmailService.createEmail(email,message.getResetGmailHead(),message.getResetGmailBody()+authService.generateAuthCode() ));  // 使用 @Value 帶入的屬性
		} catch (Exception e) {
	        e.printStackTrace();
	    }
		
		return "redirect:/bank/user/recovery/continue";
	}
	
	
	@PostMapping("/user/recovery/continue")
	public String checkAuthCode(@RequestParam String authCode,RedirectAttributes redirectAttributes,HttpSession session) {
		
		if( !authService.checkAuthCode(authCode) ) {
			
			redirectAttributes.addAttribute("errorMessage","驗證碼輸入錯誤");
			
			return "redirect：/bank/user/recovery/continue";	
		}
			
		return "redirect:/bank/user/recovery/reset" ;
	}
	
	
	@PostMapping("/user/recovery/reset")
	public String resetPassword(@RequestParam String newPassword,HttpSession session) {
		
		UserDto userDto = (UserDto)session.getAttribute("userDto");
		
		userService.updatePassword(userDto.getId(),newPassword);
		
		return "redirect:/bank/user/recovery/result";	
	}
		
	

	
	
}
