package com.example.demo.service;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

@Service
public class ShoppingService {
	
	/** 商品資訊:商品名稱 -> 商品物件 */
	private static Map<String, Product> productCatalog = new LinkedHashMap<>();
	static {
		productCatalog.put("蘋果", new Product("蘋果", 30));
		productCatalog.put("香蕉", new Product("香蕉", 20));
		productCatalog.put("橘子", new Product("橘子", 25));
		productCatalog.put("牛奶", new Product("牛奶", 60));
		productCatalog.put("麵包", new Product("麵包", 45));
	}
	
	/** 購物車:商品名稱 -> 數量 */
	private Map<String, Integer> cart = new LinkedHashMap<>();
	
	/** 加入商品到購物車 */
	@Tool(name = "addToCart", description = "將指定商品加入購物車")
	public String addToCart(
			@ToolParam(description = "商品名稱") String name, 
			@ToolParam(description = "買進數量") int quantity) {
		if(!productCatalog.containsKey(name)) {
			return "無此商品: " + name;
		}
		cart.put(name, cart.getOrDefault(name, 0) + quantity);
		System.out.printf("呼叫 addToCart(%s, %d)%n", name, quantity);
		System.out.printf("%s 已加入購物車, 數量: %d%n", name, cart.get(name));
		return String.format("%s 已加入購物車, 數量: %d%n", name, cart.get(name)); 
	}
	
	/** 查看購物車內容 */
	@Tool(name = "viewCart", description = "查看購物車目前所有商品")
	public String viewCart() {
		System.out.println("呼叫 viewCart()");
		if(cart.isEmpty()) {
			return "目前購物車是空的";
		}
		StringBuilder sb = new StringBuilder("購物車內容:\n");
		cart.forEach((name, qty) -> {
			Product product = productCatalog.get(name);
			String msg = String.format("商品名稱:%s 單價:%d 數量:%d%n", name, product.getName(), qty);
			sb.append(msg);
		});
		return sb.toString();
	}
	
}
