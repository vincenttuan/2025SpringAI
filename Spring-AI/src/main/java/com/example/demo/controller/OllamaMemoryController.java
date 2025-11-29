package com.example.demo.controller;

import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.service.OllamaMemoryService;

@RestController
@RequestMapping("/ollama-memory")
public class OllamaMemoryController {
	
	private final OllamaMemoryService ollamaMemoryService;
	
	public OllamaMemoryController(OllamaMemoryService ollamaMemoryService) {
		this.ollamaMemoryService = ollamaMemoryService;
	}
	
	// 一次性回覆
	// http://localhost:8080/ollama-memory/ask?q=我喜歡爬山運動請記住
	// http://localhost:8080/ollama-memory/ask?q=請問我喜歡什麼運動
	@GetMapping("/ask")
	public String ask(@RequestParam String q, @RequestParam(defaultValue = "default") String conversationId) {
		return ollamaMemoryService.askWithMemory(conversationId, q);
	}
	
	
}
