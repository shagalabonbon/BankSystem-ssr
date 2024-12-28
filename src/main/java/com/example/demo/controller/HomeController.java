package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.model.dto.ExchangeRate;
import com.example.demo.model.dto.UserDto;
import com.example.demo.model.entity.Account;
import com.example.demo.service.AccountService;
import com.example.demo.service.ExchangeRateService;

import jakarta.servlet.http.HttpSession;


@Controller
@RequestMapping("/bank")
public class HomeController {
	
	@Autowired
	private ExchangeRateService exchangeRateService;
	
	@Autowired
	private AccountService accountService;
	
	// ----------------------------------------------------------------
	
	@GetMapping("/index")
	public String indexPage(Model model) {
		
		ExchangeRate USexchangeRate = exchangeRateService.getTargetExchangeRate("USD");
		ExchangeRate EUexchangeRate = exchangeRateService.getTargetExchangeRate("EUR");
		ExchangeRate JPexchangeRate = exchangeRateService.getTargetExchangeRate("JPY");
		ExchangeRate CNexchangeRate = exchangeRateService.getTargetExchangeRate("CNY");
		
		model.addAttribute("USexchangeRate",USexchangeRate);
		model.addAttribute("EUexchangeRate",EUexchangeRate);
		model.addAttribute("JPexchangeRate",JPexchangeRate);
		model.addAttribute("CNexchangeRate",CNexchangeRate);
		
		return "index";  
	} 
	
	// ----------------------------------------------------------------
	
	
	@GetMapping("/home")
	public String homePage(HttpSession session, Model model) {
		
		UserDto loginUserDto = (UserDto) session.getAttribute("loginUserDto");   // 檢查有沒有登入時存入的 Dto 資料
		
		if( loginUserDto != null) {                           
			
			model.addAttribute("loginUserDto",loginUserDto);  // 將 Dto 傳遞到頁面
			
			// 尋找全部帳號，並用 model 傳遞
			
			List<Account> accounts = accountService.findAllUserAccounts(loginUserDto.getId());
			
			model.addAttribute("accounts",accounts);
			
			return "user_homepage";
		}
		
		return "redirect:/bank/login";  // 尚未登入，返回登入頁面
	}
	
	
	@GetMapping("/admin/home")
	public String adminHomePage(HttpSession session, Model model) {
		
		UserDto loginAdminDto = (UserDto) session.getAttribute("loginAdminDto");   // session 檢查登入時資料
		
		if( loginAdminDto != null) {                           
			
			model.addAttribute("loginAdminDto",loginAdminDto);  // 資料用 model 傳遞到頁面
			
			return "admin_homepage";
		}
		
		return "redirect:/bank/admin/login";  // 尚未登入，返回登入頁面
	}
	
	
	
	
	
	
}
