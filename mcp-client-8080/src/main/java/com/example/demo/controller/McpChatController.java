package com.example.demo.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mcp")
public class McpChatController {
	
	private final ChatClient chatClient;
	
	public McpChatController(ChatClient.Builder chatClientBuilder, 
			SyncMcpToolCallbackProvider toolCallbackProvider) {
		// 建立 ChatClient 並註冊 MCP 工具
		this.chatClient = chatClientBuilder
				.defaultTools(toolCallbackProvider)
				.build();
	}
	
	@GetMapping("/default")
	public String defaultAction() {
		String defaultPrompt1 = "2橘子,1牛奶";
		String defaultPrompt2 = "請將2個橘子與1瓶牛奶放到購物車中, 最後顯示購物車的內容";
		// 加強 prompt (強調工具名稱與順序)
		String defaultPrompt3 = """
					你有兩個可用工具：
					addToCart(name, quantity)：將指定商品加入購物車。
					viewCart()：查看購物車目前所有商品。
					請依照以下步驟操作：
					將 2 個「橘子」和 1 個「牛奶」加入購物車，加入時必須呼叫 addToCart 工具，商品名稱必須使用「橘子」「牛奶」這兩個中文字。
					所有需要新增商品的動作都必須透過 addToCart 工具完成，不可以只用文字假裝已經加入。
					全部加入後，呼叫一次 viewCart 工具，取得購物車內容並顯示。
					最後請只輸出 viewCart 的結果文字，不要再額外解釋。
				""";
		String defaultPrompt4 = """
				你是一個專門負責「線上購物流程自動化」的專業 AI 代理人，具備嚴謹的工具使用習慣。
				目前系統提供下列兩個工具：
				addToCart(name, quantity)：將指定商品加入購物車。
				viewCart()：查看購物車目前所有商品。
				商品目錄中已知的商品名稱為：蘋果、香蕉、橘子、牛奶、麵包。
				在任何情況下，都不得自行創造商品名稱或假設有其他商品存在。
				請嚴格依照以下要求執行任務：
				必須透過 addToCart(name, quantity) 工具，將 2 個「橘子」與 1 瓶（1 個）「牛奶」加入購物車。
				每一種商品都要各自呼叫一次 addToCart，例如：
				對「橘子」呼叫一次，數量為 2。
				對「牛奶」呼叫一次，數量為 1。
				所有「加入購物車」的行為都必須透過工具完成，不可以只用文字描述「已加入」而實際未使用工具。
				如果工具回傳錯誤訊息（例如找不到商品），你必須停止操作並在回應中清楚說明問題，不可以假裝成功。
				完成所有商品加入後：
				必須呼叫一次 viewCart() 工具，以取得目前購物車的實際內容。
				最終回應時，請以 viewCart() 回傳的文字內容為主，可以在其前後加上一行簡短說明，但不得自行改寫或杜撰購物車的資料。
				請將整個流程視為一個「專業下單任務」：
				你要像專業交易員一樣，精確執行每一步操作。
				嚴格區分「推理」與「使用工具」，不要在沒有工具結果的情況下編造狀態。
				所有涉及購物車狀態的資訊，都應以工具回傳結果為唯一可信來源。
				非常重要：最終回答時，你只能輸出 viewCart() 工具回傳的字串本身，不可以在前後添加任何文字，不可以改寫或重組內容，也不可以翻譯或總結。
				最終回答 = 工具的回傳字串原文。
				""";
		return chatClient
				.prompt()
				.user(defaultPrompt4)
				.call()
				.content();
	}
	
	
	// 使用者依序透過 http://localhost:8080/mcp/buy?q=2橘子,1牛奶
	// 使用者依序透過 http://localhost:8080/mcp/buy?q=3麵包
	// 使用者依序透過 http://localhost:8080/mcp/buy?q=結帳
	/**
	 * 逐步調用購物順序: 每次獨立處理, AI 智慧判斷
	 * 使用者順序: 2橘子,1牛奶 -> 3麵包 -> 結帳
	 * */
	
}
