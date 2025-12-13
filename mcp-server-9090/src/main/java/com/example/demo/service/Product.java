package com.example.demo.service;

//商品類別
public class Product {
	private String name; // 商品名稱
	private Integer price; // 商品價格
	
	public Product(String name, Integer price) {
		this.name = name;
		this.price = price;
	}
	
	public String getName() {
		return name;
	}
	
	public Integer getPrice() {
		return price;
	}
}
