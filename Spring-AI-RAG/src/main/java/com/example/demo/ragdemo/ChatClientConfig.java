package com.example.demo.ragdemo;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatClientConfig {
	
	public ChatClient chatClient(ChatClient.Builder builder) {
		return builder.build();
	}
	
}
