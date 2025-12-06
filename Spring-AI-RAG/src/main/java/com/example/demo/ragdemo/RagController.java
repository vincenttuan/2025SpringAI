package com.example.demo.ragdemo;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.parameters.RequestBody;

@RestController
@RequestMapping("/rag")
public class RagController {
	
	private final RagService ragService;
	
	public RagController(RagService ragService) {
		this.ragService = ragService;
	}
	
	@PostMapping("/ask")
	public String ask(@RequestBody String question) {
		return ragService.askQuestion(question);
	}
	
}
