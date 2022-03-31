package com.example.demo.controller.admin;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.validation.Valid;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.domain.Account;
import com.example.demo.domain.Category;
import com.example.demo.model.AccountDto;
import com.example.demo.model.CategoryDto;
import com.example.demo.service.AccountService;

@Controller
@RequestMapping("admin/accounts")
public class AccountController {
	@Autowired
	AccountService accountService;

	@GetMapping("")
	public String search(@RequestParam(name = "name", required = false) String username, ModelMap model,
			@RequestParam(name = "page") Optional<Integer> page, @RequestParam(name = "size") Optional<Integer> size) {
		int pageSize = size.orElse(5);
		model.addAttribute("size", pageSize);
		int currentPage = page.orElse(1);
		Pageable pageable = PageRequest.of(currentPage - 1, pageSize, Sort.by("username"));
		Page<Account> results = null;
		System.out.println(username);
		if (StringUtils.hasText(username)) {
			results = accountService.findByUsernameContaining(username, pageable);
			model.addAttribute("username", username);
		} else {
			results = accountService.findAll(pageable);
		}
		int totalPages = results.getTotalPages();
		if (totalPages > 0) {
			int start = Math.max(1, currentPage - 2);
			int end = Math.min(currentPage + 2, totalPages);
			if (totalPages > 5) {
				if (end == totalPages)
					start = end - 5;
				else if (start == 1)
					end = start + 5;
			}
			List<Integer> pages = IntStream.rangeClosed(start, end).boxed().collect(Collectors.toList());
			model.addAttribute("pages", pages);
		}
		model.addAttribute("results", results);
		return "admin/account/list";
	}

	@GetMapping("add")
	public String add(Model model) {
		model.addAttribute("account", new AccountDto());
		return "admin/account/form";
	}

	@GetMapping("edit/{username}")
	public ModelAndView edit(@PathVariable("username") String username, ModelMap model) {
		Optional<Account> optional = accountService.findById(username);
		AccountDto accountDto = new AccountDto();
		if (!optional.isEmpty()) {
			Account account = optional.get();
			BeanUtils.copyProperties(account, accountDto);
			accountDto.setPassword("");
			accountDto.setEdit(true);
			model.addAttribute("account", accountDto);
			return new ModelAndView("admin/account/form", model);
		}
		return new ModelAndView("redirect:admin/accounts", model);
	}

	@PostMapping("save")
	public ModelAndView save(ModelMap model, @Valid @ModelAttribute("account") AccountDto accountDto,
			BindingResult result, final RedirectAttributes redirectAttributes) {
		if (result.hasErrors()) {
			return new ModelAndView("admin/account/form");
		}
		Account account = new Account();
		BeanUtils.copyProperties(accountDto, account);
		accountService.save(account);
		redirectAttributes.addFlashAttribute("success", "Account save success!");
		return new ModelAndView("redirect:/admin/accounts");
	}

	@GetMapping("delete/{username}")
	public ModelAndView delete(@PathVariable("username") String username,
			@RequestHeader(value = "referer", required = false) String referer, ModelMap model,
			final RedirectAttributes redirectAttributes) throws IOException {
		Optional<Account> optional = accountService.findById(username);
		if (!optional.isEmpty()) {
			accountService.delete(optional.get());
			redirectAttributes.addFlashAttribute("success", "Account delete success!");
			return new ModelAndView("redirect:/admin/accounts");
		}
		redirectAttributes.addFlashAttribute("error", "Account delete failed!");
		return new ModelAndView("redirect:/admin/accounts");
	}
}
