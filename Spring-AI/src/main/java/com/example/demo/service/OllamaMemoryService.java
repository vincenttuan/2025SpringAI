package com.example.demo.service;

import java.util.List;

import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;

@Service
public class OllamaMemoryService {
	
	private final OllamaChatModel chatModel;
	private final ChatMemory chatMemory;
	
	// 使用建構子注入, Spring 會自動注入 OllamaChatModel 與 ChatMemory
	public OllamaMemoryService(OllamaChatModel chatModel, ChatMemory chatMemory) {
		this.chatModel = chatModel;
		this.chatMemory = chatMemory;
	}
	
	// 一次性回傳
	public String askWithMemory(String conversationId, String q) {
		if(conversationId == null || conversationId.isEmpty()) {
			conversationId = ChatMemory.DEFAULT_CONVERSATION_ID;
		}
		
		// 1. 將使用者問的問題透過 UserMessage 存入 memory
		chatMemory.add(conversationId, new UserMessage(q));
		
		// 2. 從 memory 中取出訊息組成 Prompt
		List<Message> messagesInMemory = chatMemory.get(conversationId);
		Prompt prompt = new Prompt(messagesInMemory);
		
		// 3. 呼叫模型
		ChatResponse response = chatModel.call(prompt);
		
		// 4. 將 AI 回應透過 AssistantMessage 存入 memory
		String aiText = response.getResult().getOutput().getText();
		chatMemory.add(conversationId, new AssistantMessage(aiText));
		
		// 5. 回應文字
		return aiText;
	}
	
	// 逐字回傳
	public Flux<String> streamWithMemory(String conversationId, String q) {
		if(conversationId == null || conversationId.isEmpty()) {
			conversationId = ChatMemory.DEFAULT_CONVERSATION_ID;
		}
		
		final String finalConversationId = conversationId;
		
		// 1. 將使用者問的問題透過 UserMessage 存入 memory
		chatMemory.add(finalConversationId, new UserMessage(q));
		
		// 2. 從 memory 中取出訊息組成 Prompt
		List<Message> messagesInMemory = chatMemory.get(finalConversationId);
		Prompt prompt = new Prompt(messagesInMemory);
		
		StringBuilder fullAnswer = new StringBuilder();
		
		return chatModel.stream(prompt)
				.map(chunk -> {
					String text = chunk.getResult().getOutput().getText();
					fullAnswer.append(text); // 累積完整回答
					return text;
				})
				.doOnComplete(() -> {
					chatMemory.add(finalConversationId, new AssistantMessage(fullAnswer.toString())); // 將完整回答存入記憶
				});
		
		
	}
	
	
	
}
