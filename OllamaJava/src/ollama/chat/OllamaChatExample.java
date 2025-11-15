package ollama.chat;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 範例名稱：OllamaChatExample（使用 OkHttp3 版本）
 *
 * 說明：
 *  本程式示範如何使用 OkHttp3 HTTP 客戶端函式庫，
 *  向本機或同區網的 Ollama 伺服器發送 POST 請求，
 *  呼叫 /api/chat 端點以進行多輪對話。
 *
 *  對應的 curl 範例如下：
 *  curl -X POST http://localhost:11434/api/chat \
 *   -H "Content-Type: application/json" \
 *   -d "{\"model\":\"qwen3:4b\",\"messages\":[{\"role\":\"user\",\"content\":\"請用中文介紹 Java 程式語言\"}],\"stream\":false}"
 *
 * 注意：
 *  - OkHttp3 是業界標準的 HTTP 客戶端函式庫
 *  - 程式碼簡潔、效能優異、支援同步與非同步請求
 *  - 適合 Android 開發與企業級應用
 *
 * 適用環境：
 *  - Java 8 或以上版本
 *  - OkHttp3 4.12.0 或以上版本
 *  - 已啟動 Ollama Server，且服務運行於 http://localhost:11434
 */
public class OllamaChatExample {
	
	// 定義 ollama web api url
	private static final String CHAT_WEB_API = "http://localhost:11434/api/chat";
	
	// 定義媒體格式(MediaTyep)類型為 json
	private static final MediaType JSON = MediaType.get("appliaction/json;charset=utf-8");
	
	public static void main(String[] args) throws Exception {
		Scanner scanner = new Scanner(System.in);
		
		// 選擇模型
		String[] modelNames = {"llama3.1:8b", "qwen3:4b", "qwen3:0.6b", "martain7r/finance-llama-8b:fp16"};
		System.out.print("請選擇模型(0:llama3.1:8b, 1:qwen3:4b, 2:qwen3:0.6b, 3:martain7r/finance-llama-8b:fp16) => ");
		
		int modelIndex = scanner.nextInt();
		String modelName = modelNames[modelIndex];
		
		// 建立對話訊息列表(messages)
		List<JsonObject> messages = new ArrayList<>();
		
		// 是否支援 stream
		Boolean supportStream = true;
		
		// 與 AI 持續對話
		while(true) {
			System.out.print("請輸入問題 (輸入 q/quit 結束) => ");
			String question = scanner.next();
			if(question.equals("q") || question.equals("quit")) {
				System.out.println("離開對話");
				break;
			}
			//---------------------------------------------------
			
			// 新增使用者訊息
			// 組裝 json: {"role": "user", "content": "你知道2025年世界盃在哪裡嗎？"}
			JsonObject userMessage = new JsonObject();
			userMessage.addProperty("role", "user");
			userMessage.addProperty("content", question);
			messages.add(userMessage);
			
			//---------------------------------------------------
			// 1. 建立 JSON 請求內容
			//---------------------------------------------------
			String jsonBody = """
					{
						"model": "%s",
						"messages": %s,
						"stream": %b
					}
					""";
			jsonBody = String.format(jsonBody, modelName, new Gson().toJson(messages), supportStream);
			System.out.printf("要發送的 JSON: %n%s%n", jsonBody);
			
			//---------------------------------------------------
            // 2. 建立 OkHttpClient 實例 (加入 Timeout)
            //---------------------------------------------------
			OkHttpClient client = new OkHttpClient.Builder()
					.connectTimeout(60, TimeUnit.SECONDS)
					.readTimeout(60, TimeUnit.SECONDS)
					.writeTimeout(60, TimeUnit.SECONDS)
					.build();
			
			//---------------------------------------------------
            // 3. 建立 RequestBody (將 JSON 字串包裝成請求主體)
            //---------------------------------------------------
			RequestBody body = RequestBody.create(jsonBody, JSON);
			
			//---------------------------------------------------
            // 4. 建立 Request 物件
            //---------------------------------------------------
			Request request = new Request.Builder()
					.url(CHAT_WEB_API)
					.post(body)
					.build();
			
			//---------------------------------------------------
            // 5. 同步發送請求並取得回應
            //---------------------------------------------------
			try(Response response = client.newCall(request).execute()){
				if(!response.isSuccessful()) {
					System.out.printf("請求失敗, HTTP 狀態碼: %n%s%n", response.code());
					continue;
				}
				
				// 取得回應內容
				if(supportStream) { // "stream": true
					try(InputStream        is = response.body().byteStream(); // 單位 byte 
						InputStreamReader isr = new InputStreamReader(is, "UTF-8"); // 單位 char
						BufferedReader reader = new BufferedReader(isr)) { // 可逐行讀取
						
						String line = null;
						Gson gson = new Gson();
						StringBuilder totalContent = new StringBuilder(); // 將所有逐字 content 集合起來
						while((line = reader.readLine()) != null) {
							JsonObject obj = gson.fromJson(line, JsonObject.class);
							//System.out.println(obj);
							if(obj.has("message") && obj.get("message").getAsJsonObject().has("content")) {
								String content = obj.get("message").getAsJsonObject().get("content").getAsString();
								totalContent.append(content);
								System.out.print(content);
							}
						}
					}
					
				} else { // "stream": false
					String responseBody = response.body().string();
					System.out.printf("%n回應碼: %s%n", response.code());
					System.out.printf("完整回應: %s%n", responseBody);
				}
				
				System.out.println("\n回應完畢 !\n");
			}
			
			
			//scanner.nextLine();
		}	
		
		
		scanner.close();
	}
	
	
}
