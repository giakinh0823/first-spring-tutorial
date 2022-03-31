package com.example.demo.controller.admin;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.example.demo.domain.Account;
import com.example.demo.model.AdminLoginDto;
import com.example.demo.service.AccountService;

@Controller
@RequestMapping("/auth/admin")
public class AdminLoginController {
	
	@Autowired
	private AccountService accountService;
	
	@Autowired
	private HttpSession session;
	
	@GetMapping("login")
	public ModelAndView login(ModelMap model) {
		AdminLoginDto admin = new AdminLoginDto();
		model.addAttribute("admin", admin);
		return new ModelAndView("admin/auth/login", model);
	}
	
	@PostMapping("login")
	public ModelAndView login(ModelMap model,
			@Valid @ModelAttribute("admin") AdminLoginDto adminLoginDto,
			BindingResult result) {
		if (result.hasErrors()) {
			return new ModelAndView("admin/auth/login");
		}
		Account account = accountService.login(adminLoginDto.getUsername(), adminLoginDto.getPassword());
		if(account==null) {
			model.addAttribute("error", "Invalid username or email");
			return new ModelAndView("admin/auth/login", model);
		}
		session.setAttribute("account", account);
		Object redirectUri= session.getAttribute("redirect-uri");
		if(redirectUri!=null) {
			session.removeAttribute("redirect-uri");
			return new ModelAndView("redirect:"+redirectUri);
		}
		
		return new ModelAndView("redirect:/admin");
	}
}
