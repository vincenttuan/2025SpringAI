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
		Document doc1 = new Document("ETF（Exchange Traded Fund）是一種追蹤指數的基金，例如台灣最知名的 0050 就是追蹤台灣50指數。ETF 在交易所買賣，像股票一樣可以即時交易，費用通常比主動型基金低。", Map.of("source", "doc1"));
		Document doc2 = new Document("台指期是台灣加權指數的期貨商品，交易者可以利用槓桿放大部位。台指期常用於避險，例如投資人持有大量股票時，可以做空台指期降低市場下跌風險。", Map.of("source", "doc2"));
		Document doc3 = new Document("均線是一種技術分析工具。當股價站在所有均線之上時，通常代表多頭強勢；反之，跌破所有均線則常表示空頭主導。5日線與20日線的交叉是學生最常使用的判斷方法。", Map.of("source", "doc3"));
		Document doc4 = new Document("成交量代表市場的參與程度。價漲量增通常表示上漲有力道；價跌量縮代表下跌無力，可能只是暫時回檔。專業操盤手會特別留意突破時量能是否放大。", Map.of("source", "doc4"));
		// 書庫
		List<Document> docs = List.of(doc1, doc2, doc3, doc4);
		// 將書庫的書加入到向量資料庫
		vectorStore.add(docs);
		System.out.println("文檔已成功載入向量資料庫, 文檔數量:" + docs.size());
		
	}
	
}
