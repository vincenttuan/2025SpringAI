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
		productCatalog.put("蘋果", new Product("蘋果", 30, 5));
		productCatalog.put("香蕉", new Product("香蕉", 20, 5));
		productCatalog.put("橘子", new Product("橘子", 25, 5));
		productCatalog.put("牛奶", new Product("牛奶", 60, 5));
		productCatalog.put("麵包", new Product("麵包", 45, 5));
	}
	
	/** 購物車:商品名稱 -> 數量 */
	private Map<String, Integer> cart = new LinkedHashMap<>();
	
	/** 加入商品到購物車 */
	@Tool(name = "addToCart", description = "將指定商品加入購物車")
	public String addToCart(
			@ToolParam(description = "商品名稱") String name, 
			@ToolParam(description = "買進數量") Integer quantity) {
		// 檢查商品是否存在 ?
		if(!productCatalog.containsKey(name)) {
			return "無此商品: " + name;
		}
		
		// 檢查庫存
		Product product = productCatalog.get(name);
		if(product.getStock() < quantity) {
			return String.format("庫存不足 ! %s 僅剩 %d 個, 無法購買 %d 個%n", 
					name, product.getStock(), quantity);
		}
		
		// 扣抵庫存
		product.deductStock(quantity);
		
		// 加入購物車
		cart.put(name, cart.getOrDefault(name, 0) + quantity);
		System.out.printf("呼叫 addToCart(%s, %d)%n", name, quantity);
		System.out.printf("%s 已加入購物車, 數量: %d (剩餘庫存: %d)%n", name, cart.get(name), product.getStock());
		return String.format("%s 已加入購物車, 數量: %d (剩餘庫存: %d)%n", name, cart.get(name), product.getStock()); 
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
			String msg = String.format("商品名稱:%s 單價:%d 數量:%d (剩餘庫存: %d)%n", 
					name, product.getPrice(), qty, product.getStock());
			sb.append(msg);
		});
		return sb.toString();
	}
	
	/** 結帳並清空購物車 */
	@Tool(name = "checkout", description = "購物車結帳")
	public String checkout() {
		System.out.println("呼叫 checkout()");
		if(cart.isEmpty()) {
			System.out.println("購物車是空的無法結帳");
			return "購物車是空的無法結帳";
		}
		int total = cart.entrySet().stream()
				.mapToInt(entry -> {
					Product product = productCatalog.get(entry.getKey());
					return product.getPrice() * entry.getValue();
				})
				.sum();
		cart.clear();
		return String.format("結帳成功! 總金額:%d%n", total);
	}
	
	/** 查詢目前所有商品的庫存 */
	@Tool(name = "checkInventory", description = "查詢目前所有商品的庫存")
	public String checkInventory() {
		System.out.println("呼叫 checkInventory()");
		StringBuilder sb = new StringBuilder("目前庫存狀態:\n");
		productCatalog.forEach((name, product) -> {
			sb.append(String.format("商品名稱:%s 庫存:%d%n", name, product.getStock()));
		});
		
		return sb.toString();
	}
 	
}
