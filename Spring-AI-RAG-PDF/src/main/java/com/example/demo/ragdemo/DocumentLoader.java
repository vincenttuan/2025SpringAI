package com.example.demo.ragdemo;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.ai.document.Document;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

@Component
public class DocumentLoader implements CommandLineRunner {
	
	private final VectorStore vectorStore;
	
	public DocumentLoader(VectorStore vectorStore) {
		this.vectorStore = vectorStore;
	}
	
	@Override
	public void run(String... args) throws Exception {
		// 書本(PDF) 放在 src/main/respurces/docs
		List<Document> pdfDocs = Stream.of(
					new PagePdfDocumentReader(new ClassPathResource("docs/eat.pdf")).read(),    // List<Document>
					new PagePdfDocumentReader(new ClassPathResource("docs/java.pdf")).read(),   // List<Document>
					new PagePdfDocumentReader(new ClassPathResource("docs/spring.pdf")).read(), // List<Document>
					new PagePdfDocumentReader(new ClassPathResource("docs/taiwan.pdf")).read(), // List<Document>
					new PagePdfDocumentReader(new ClassPathResource("docs/teacher.pdf")).read(), // List<Document>
					new PagePdfDocumentReader(new ClassPathResource("docs/thanksgiving.pdf")).read() // List<Document>
					).flatMap(List::stream) // List<List<Document>> 變成 List<Document> -> List<Document> -> List<Document>...
				.collect(Collectors.toList());
		
		// 書庫
		// 將書庫的書加入到向量資料庫
		vectorStore.add(pdfDocs);
		System.out.println("PDF 文檔已成功載入向量資料庫, 給 RAG 讀的 Document 數量:" + pdfDocs.size());
		
	}
	
}
