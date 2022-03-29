package com.example.demo.model;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Customer {
	private Long id;
	private String name;
	private String email;
	private String phone;
	private String password;
	private Date registered;
	private short status;
}
