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
		String defaultPrompt = "請將2個橘子與1瓶牛奶放到購物車中, 最後顯示購物車的內容";
		
		return chatClient
				.prompt()
				.user(defaultPrompt)
				.call()
				.content();
	}
	
	
}
