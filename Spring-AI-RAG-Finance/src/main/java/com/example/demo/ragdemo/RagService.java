package com.example.demo.ragdemo;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

@Service
public class RagService {
	
	private final VectorStore vectorStore;
	private final ChatClient chatClient;
	
	public RagService(VectorStore vectorStore, ChatClient chatClient) {
		this.vectorStore = vectorStore;
		this.chatClient = chatClient;
	}
	
	public String askQuestion(String question) {
		// 建立 SearchRequest
		// 檢索最相關的 3 份文檔 topK(3)
		SearchRequest request = SearchRequest.builder()
				.query(question)
				.topK(3)
				.build();
		
		// 相似度搜尋
		List<Document> relevantDocs = vectorStore.similaritySearch(request);
		
		// 組 context 字串
		String context = relevantDocs.stream()
				.map(Document::getFormattedContent)
				.collect(Collectors.joining("\n"));
		
		// 建立 PromptTemplate
		PromptTemplate promptTemplate = new PromptTemplate("""
				請根據以下上下文回答問題
				{context}
				
				問題:{question}
				
				上下文沒有提供的資料請統一回答不知道
				回答時請排除任何有"但是"的回覆
				回答時請刪除有"根據上下文"字眼的內容
				全部回答後請統一印出加上資料來源是{source}的字樣
				答案:
				""");
		
		Prompt prompt = promptTemplate.create(
				Map.of("context", context, "question", question, "source", "巨匠理財")
		);
		
		// 回應 ChatClient
		return chatClient.prompt(prompt).call().content();
	}
	
}
