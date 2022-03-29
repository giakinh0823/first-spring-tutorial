package com.example.demo.controller.admin;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.validation.Valid;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.example.demo.domain.Category;
import com.example.demo.domain.Product;
import com.example.demo.model.CategoryDto;
import com.example.demo.model.ProductDto;
import com.example.demo.service.CategoryService;
import com.example.demo.service.ProductService;
import com.example.demo.service.StorageService;

@Controller
@RequestMapping("admin/products")
public class ProductController {
	@Autowired
	ProductService productService;
	
	@Autowired
	CategoryService categoryService;
	
	@Autowired
	StorageService storageService;
	
	@ModelAttribute("categories")
	public List<CategoryDto> getCategories(){
		return categoryService.findAll().stream().map((item) -> {
			CategoryDto categoryDto= new CategoryDto();
			BeanUtils.copyProperties(item, categoryDto);
			return categoryDto;
		}).toList();
	}
	
	// đọc nội dung file sau đó trả về nội dung của file đó
	@GetMapping("/images/{filename:.+}")
	@ResponseBody
	public ResponseEntity<Resource> serverFile(@PathVariable String filename){
		Resource file = storageService.loadAsResource(filename);
		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename\""+file.getFilename()+"\"").body(file);
	}
	
	
	@GetMapping("")
	public String search(@RequestParam(name = "name", required = false) String name, ModelMap model,
			@RequestParam(name= "page") Optional<Integer> page, 
			@RequestParam(name= "size") Optional<Integer> size) {
		int pageSize = size.orElse(5);
		model.addAttribute("size", pageSize);
		int currentPage = page.orElse(1);
		Pageable pageable = PageRequest.of(currentPage-1, pageSize, Sort.by("name"));
		Page<Product> results = null;
		System.out.println(name);
		if (StringUtils.hasText(name)) {
			results = productService.findByNameContaining(name, pageable);
			model.addAttribute("name", name);
		} else {
			results = productService.findAll(pageable);
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
		return "admin/product/list";
	}
	
	@PostMapping("save")
	public ModelAndView save(ModelMap model, @Valid @ModelAttribute("product") ProductDto productDto,
			BindingResult result) {
		if (result.hasErrors()) {
			return new ModelAndView("admin/product/form");
		}
		Product product = new Product();
		BeanUtils.copyProperties(productDto, product);
		
		Category category = new Category();
		category.setId(productDto.getCategoryId());
		product.setCategory(category);
		
		if(productDto.getImageFile()!=null && !productDto.getImageFile().isEmpty()) {
			UUID uuid = UUID.randomUUID();
			String uuString = uuid.toString();
			product.setImage(storageService.getStoredFileName(productDto.getImageFile(), uuString));
			try {
				storageService.store(productDto.getImageFile(), product.getImage());
			} catch (Exception e) {
				// TODO: handle exception
				model.addAttribute("error", "Cant not save file");
				System.out.println("Cant not save file");
				return new ModelAndView("admin/product/form");
			}
		}

		productService.save(product);
		model.addAttribute("success", "Product save success!");
		return new ModelAndView("redirect:/admin/products", model);
	}
	
	@GetMapping("edit/{id}")
	public ModelAndView edit(@PathVariable("id") Long id, ModelMap model) {
		Optional<Product> optional = productService.findById(id);
		ProductDto productDto = new ProductDto();
		if (!optional.isEmpty()) {
			Product product = optional.get();
			BeanUtils.copyProperties(product, productDto);
			productDto.setEdit(true);
			model.addAttribute("product", productDto);
			return new ModelAndView("admin/product/form", model);
		}
		return new ModelAndView("redirect:admin/products", model);
	}
	
	@GetMapping("add")
	public String add(Model model) {
		model.addAttribute("product", new ProductDto());
		return "admin/product/form";
	}
}
