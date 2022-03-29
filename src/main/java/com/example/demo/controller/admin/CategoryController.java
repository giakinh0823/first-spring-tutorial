package com.example.demo.controller.admin;

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

import com.example.demo.domain.Category;
import com.example.demo.model.CategoryDto;
import com.example.demo.service.CategoryService;

@Controller
@RequestMapping("admin/categories")
public class CategoryController {

	@Autowired
	CategoryService categoryService;

	@GetMapping("")
	public String search(@RequestParam(name = "name", required = false) String name, ModelMap model,
			@RequestParam(name= "page") Optional<Integer> page, 
			@RequestParam(name= "size") Optional<Integer> size) {
		int pageSize = size.orElse(5);
		model.addAttribute("size", pageSize);
		int currentPage = page.orElse(1);
		Pageable pageable = PageRequest.of(currentPage-1, pageSize, Sort.by("name"));
		Page<Category> results = null;
		System.out.println(name);
		if (StringUtils.hasText(name)) {
			results = categoryService.findByNameContaining(name, pageable);
			model.addAttribute("name", name);
		} else {
			results = categoryService.findAll(pageable);
		}
		int totalPages = results.getTotalPages();
		if (totalPages > 0) {
			int start = Math.max(1, currentPage-2);
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
		return "admin/category/list";
	}

	@GetMapping("add")
	public String add(Model model) {
		model.addAttribute("category", new CategoryDto());
		return "admin/category/form";
	}

	@GetMapping("edit/{id}")
	public ModelAndView edit(@PathVariable("id") Long id, ModelMap model) {
		Optional<Category> optional = categoryService.findById(id);
		CategoryDto categoryDto = new CategoryDto();
		if (!optional.isEmpty()) {
			Category category = optional.get();
			BeanUtils.copyProperties(category, categoryDto);
			categoryDto.setEdit(true);
			model.addAttribute("category", categoryDto);
			return new ModelAndView("admin/category/form", model);
		}
		return new ModelAndView("redirect:admin/categories", model);
	}

	@PostMapping("save")
	public ModelAndView save(ModelMap model, @Valid @ModelAttribute("category") CategoryDto categoryDto,
			BindingResult result) {
		if (result.hasErrors()) {
			return new ModelAndView("admin/category/form");
		}
		Category category = new Category();
		BeanUtils.copyProperties(categoryDto, category);
		categoryService.save(category);
		model.addAttribute("success", "Category save success!");
		return new ModelAndView("redirect:/admin/categories", model);
	}

	@GetMapping("delete/{id}")
	public String delete(@PathVariable("id") Long id,
			@RequestHeader(value = "referer", required = false) String referer) {
		categoryService.deleteById(id);
		return "redirect:" + referer;
	}
}