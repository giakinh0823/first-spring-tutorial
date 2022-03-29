package com.example.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetail {
	private Long id;
	private Long orderId;
	private Long productId;
	private int quantity;
	private double unitPrice;
}	
