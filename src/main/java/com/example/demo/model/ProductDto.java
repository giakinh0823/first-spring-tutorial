package com.example.demo.model;

import java.util.Date;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDto {
	private Long id;
	@NotEmpty
	@Length(min=5)
	private String name;
	@Min(value=0)
	private int quantity;
	@Min(value=0)
	private double price;
	
	private String image;
	private MultipartFile imageFile;
	
	private String description;
	@Min(value=0)
	private double discount;
	private Date enteredDate;
	@NotNull
	private short status;
	@NotNull
	private Long categoryId;
	
	private boolean edit = false;
}
