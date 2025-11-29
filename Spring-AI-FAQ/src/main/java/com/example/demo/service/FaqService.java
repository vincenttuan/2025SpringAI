package com.example.demo.service;

import java.util.Map;

import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.*;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.stereotype.Service;

@Service
public class FaqService {

    private final OllamaChatModel chatModel;
    private final ChatMemory chatMemory;

    // FAQ 內容
    private static final String FAQ = """
		Q: 她的名字是什麼？
		A: 小美
		
		Q: 小美今年幾歲？
		A: 22 歲
		
		Q: 小美最喜歡的顏色是什麼？
		A: 天藍色
		
		Q: 小美最喜歡吃的食物是什麼？
		A: 義大利麵和炸雞
		
		Q: 小美最喜歡的飲料是什麼？
		A: 冰拿鐵
		
		Q: 小美喜歡的音樂類型是什麼？
		A: 抒情歌
		
		Q: 小美喜歡的運動是什麼？
		A: 慢跑與瑜伽
		
		Q: 小美的興趣有哪些？
		A: 旅行、閱讀、看電影
		
		Q: 小美最喜歡的動物是什麼？
		A: 柴犬
		
		Q: 小美害怕什麼？
		A: 高處和大型昆蟲
		
		Q: 小美的星座是什麼？
		A: 天秤座
		
		Q: 小美最喜歡的季節是什麼？
		A: 秋天
	""";

    private static final PromptTemplate FAQ_PROMPT = new PromptTemplate("""
			你是一位 FAQ 助理，只能根據 FAQ 回答問題。
			
			FAQ 內容：
			{faq}
			
			問題：
			{question}
			
			規則：
			1. 必須嚴格依照 FAQ 回答。
			2. FAQ 沒有寫的內容，請回答：「我不知道」。
			3. 不可以猜測，不可以杜撰。
	""");

    public FaqService(OllamaChatModel chatModel, ChatMemory chatMemory) {
        this.chatModel = chatModel;
        this.chatMemory = chatMemory;
    }

    public String ask(String conversationId, String question) {

        String cid = (conversationId == null || conversationId.isBlank())
                ? ChatMemory.DEFAULT_CONVERSATION_ID
                : conversationId;

        // ⭐使用者提問 → 存記憶
        chatMemory.add(cid, new UserMessage(question));

        // 套入 FAQ 與問題
        Prompt prompt = FAQ_PROMPT.create(Map.of(
                "faq", FAQ,
                "question", question
        ));

        ChatResponse response = chatModel.call(prompt);
        String aiText = response.getResult().getOutput().getText();

        // ⭐AI 回答 → 存記憶
        chatMemory.add(cid, new AssistantMessage(aiText));

        return aiText;
    }
}
