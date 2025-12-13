package com.example.demo.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
		return chatClient
				.prompt()
				.user(defaultPrompt3)
				.call()
				.content();
	}
	
	
}
