package com.example.demo.service;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class ShoppingService {
	
	
	// 商品資訊:商品名稱 -> 商品物件
	private static Map<String, Product> productCatalog = new LinkedHashMap<>();
	static {
		productCatalog.put("蘋果", new Product("蘋果", 30));
		
	}
	
	
	
	

}
