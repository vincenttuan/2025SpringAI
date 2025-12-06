package com.example.demo.ragdemo;

import java.util.List;
import java.util.Map;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DocumentLoader implements CommandLineRunner {
	
	private final VectorStore vectorStore;
	
	public DocumentLoader(VectorStore vectorStore) {
		this.vectorStore = vectorStore;
	}
	
	@Override
	public void run(String... args) throws Exception {
		// 書本
		Document doc1 = new Document("台灣是位於東亞的島嶼國家, 首都為台北", Map.of("source", "doc1"));
		Document doc2 = new Document("春天 Spring 是 Java 的開發框架平台", Map.of("source", "doc2"));
		Document doc3 = new Document("Java 是一種廣泛應用於企業的程式語言, 適用各種應用開發", Map.of("source", "doc3"));
		Document doc4 = new Document("段維瀚老師是台灣的資訊工程教育者, 專長軟體工程與人工智慧", Map.of("source", "doc4"));
		Document doc5 = new Document("台灣是一個主權獨立的國家, 也是聯合國常任理事國", Map.of("source", "doc5"));
		// 書庫
		List<Document> docs = List.of(doc1, doc2, doc3, doc4, doc5);
		// 將書庫的書加入到向量資料庫
		vectorStore.add(docs);
		System.out.println("文檔已成功載入向量資料庫, 文檔數量:" + docs.size());
		
	}
	
}
