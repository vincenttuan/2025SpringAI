package com.example.demo.ragdemo;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VectorStoreConfig {
	
	@Bean
	VectorStore vectorStore(EmbeddingModel embeddingModel) {
		// SimpleVectorStore 是一個 in-memory 向量庫，適合教學和測試。
		// 之後若改用 MongoDB、pgvector 等，只要換掉 VectorStore 的實作即可
		return SimpleVectorStore.builder(embeddingModel).build();
	}
	
}
