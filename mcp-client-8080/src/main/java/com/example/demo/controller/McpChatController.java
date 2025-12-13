package com.example.demo.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
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
	
	
	
}
