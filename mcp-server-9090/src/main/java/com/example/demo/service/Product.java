package com.example.demo.service;

//商品類別
public class Product {
	private String name; // 商品名稱
	private Integer price; // 商品價格
	private Integer stock; // 商品庫存
	
	public Product(String name, Integer price, Integer stock) {
		this.name = name;
		this.price = price;
		this.stock = stock;
	}
	
	public String getName() {
		return name;
	}
	
	public Integer getPrice() {
		return price;
	}
	
	public Integer getStock() {
		return stock;
	}
	
	// 扣庫存
	public void deductStock(int qty) {
		this.stock -= qty;
	}
	
	
}
