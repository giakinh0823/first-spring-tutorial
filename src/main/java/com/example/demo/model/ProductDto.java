package com.example.demo.model;

import java.util.Date;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDto {
	private Long id;
	private String name;
	private int quantity;
	private double price;
	
	private String image;
	private MultipartFile imageFile;
	
	private String description;
	private double discount;
	private Date enteredDate;
	private short status;
	private int categoryId;
	
	private boolean edit = false;
}
