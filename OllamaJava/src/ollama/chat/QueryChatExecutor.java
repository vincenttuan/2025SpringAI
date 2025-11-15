package ollama.chat;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class QueryChatExecutor {
	
	// 定義 ollama web api url
	private static final String CHAT_WEB_API = "http://localhost:11434/api/chat";
	
	// 定義媒體格式(MediaTyep)類型為 json
	private static final MediaType JSON = MediaType.get("application/json;charset=utf-8");
	
	// 建立 OkHTTPClient 實例, 負責建立與網路連線
	private OkHttpClient client;
	
	// 定義 callback 介面, 提供逐字讀取, 完成與錯誤回調方法
	public interface QueryCallback {
		void onResponseChar(char ch); // 每收到一個字元時觸發
		void onComplete(); // 完成整個串流回應後觸發
		void onError(String message); // 發生例外或錯誤時觸發
		void onHttpError(int statusCode); // 非 200 的 HTTP 回應碼時會觸發此方法
	}
	
	// 建構子, 初始化 OkHttpClient 並設定延遲時間
	public QueryChatExecutor() {
		client = new OkHttpClient.Builder()
				.connectTimeout(60, TimeUnit.SECONDS)
				.readTimeout(60, TimeUnit.SECONDS)
				.writeTimeout(60, TimeUnit.SECONDS)
				.build();
	}
	
	/**
     * 非同步方式，對指定模型發送 chat API 請求，傳入對話歷史，並以串流讀取回應。
     * @param modelName 模型名稱，例如 'llama3.1:8b'
     * @param messages 多輪對話歷史，每個元素包含角色(role)與內容(content)
     * @param callback 回調，負責處理逐字回應與錯誤
     */
	public void execute(String modelName, List<Map<String, String>> messages, QueryCallback callback) {
		// 建立執行緒工作
		Runnable runnable = () -> {
			try {
				Gson gson = new Gson();
				// 建構 JSON body 用於傳送 chat 請求
				JsonObject jsonBody = new JsonObject();
				jsonBody.addProperty("model", modelName);
				jsonBody.add("messages", gson.toJsonTree(messages));
				jsonBody.addProperty("stream", true);
				
				// 將 jsonBody 物件轉為字串並形成請求內容
				RequestBody body = RequestBody.create(jsonBody.toString(), JSON);
				
				// 建立 HTTP POST 請求
				Request request = new Request.Builder()
						.url(CHAT_WEB_API)
						.post(body)
						.build();
				
				// 執行請求
				try(Response response = client.newCall(request).execute()) {
					
					// 檢查回應狀態
					if(!response.isSuccessful()) {
						callback.onHttpError(response.code());
					}
					
					// 處的串流資料 ByteStream 並逐行讀取
					try(InputStream        is = response.body().byteStream(); // 單位 byte 
						InputStreamReader isr = new InputStreamReader(is, "UTF-8"); // 單位 char
						BufferedReader reader = new BufferedReader(isr)) { // 可逐行讀取
							
						String line = null;
						while((line = reader.readLine()) != null) {
							if(line.isBlank()) continue; // 空行跳過
							
							// 解析 json 資料
							JsonObject obj = gson.fromJson(line, JsonObject.class);
							
							// 檢查 json 中是否有 message 欄位, message 欄位內是否有 content 欄位
							if(obj.has("message") && obj.get("message").getAsJsonObject().has("content")) {
								String content = obj.get("message").getAsJsonObject().get("content").getAsString();
								// 將 content 內容逐字回調
								for(char ch : content.toCharArray()) {
									callback.onResponseChar(ch);
								}
							}
						}	
					}
					
					// 串流結束, 觸發完成回調
					callback.onComplete();
				}
			} catch (Exception e) {
				// 發生例外錯誤, 傳送錯誤訊息到回調
				callback.onError(e.getMessage());
			}
			
		};
		
		// 建立執行緒來執行
		new Thread(runnable).start();
		
	}
}









