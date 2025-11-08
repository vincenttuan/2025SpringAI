package ollama.generate;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * QueryExecutor 負責與指定的 AI 模型生成 API 進行溝通，
 * 以非同步方式發送查詢請求並逐字回傳結果。
 * 
 * 使用者可透過 QueryCallback 監聽查詢過程的各種事件，
 * 包括接收資料字元、完成通知、錯誤處理與 HTTP 狀態碼異常。
 * 
 * 此類設計易於整合入 CLI 或 GUI 應用中，實現串流形式的以用戶體驗，
 * 並內建簡易測試 main 函數，方便本地調試與快速上手。
 * 
 * 主要是透過 OkHttp3 進行網路請求，Gson 用於 JSON 解析，
 * 且配合自定義回調介面實現分段回傳，支持高效率並發查詢。
 */
public class QueryExecutor {
	// 定義 ollama web api url
	private static final String GENERATE_WEB_API = "http://localhost:11434/api/generate";
	// 定義媒體(MediaType)型別為 json
	private static final MediaType JSON = MediaType.get("application/json;charset=utf-8");
	// 定義 OkHttp
	private final OkHttpClient client;
	
	public interface QueryCallback {
		void onResponseChar(char ch); // 逐字回應
		void onComplete(); // 查詢完成
		void onError(String message); // 一般錯誤
		void onHttpError(int code); // Http 狀態錯誤
	}
	
	public QueryExecutor() {
		// 建立 client
		client = new OkHttpClient.Builder()
				.connectTimeout(60, TimeUnit.SECONDS)
				.readTimeout(60, TimeUnit.SECONDS)
				.writeTimeout(60, TimeUnit.SECONDS)
				.build();
	}
	
	// 以非同步方式對指定模型發起查詢請求，並回傳串流資料。
	public void execute(String modelName, String fullPrompt, QueryCallback callback) {
		// 建立執行緒工作
		Runnable runnable = () -> {
			try {
				String jsonBody = String.format("""
						{
							"model":"%s",
							"prompt":"%s",
							"stream":true
						}
						
						""", modelName, fullPrompt.replace("\n", "\\\"")); // 避免 json 字串中使用跳脫字元的錯誤
				
				RequestBody body = RequestBody.create(jsonBody, JSON);
				
				Request request = new Request.Builder()
						.url(GENERATE_WEB_API)
						.post(body)
						.build();
				
				try(Response response = client.newCall(request).execute()) {
					
					if(!response.isSuccessful()) {
						callback.onHttpError(response.code());
						return;
					}
					
					// 接收串流資料+分析
					try(InputStream is = response.body().byteStream();
						InputStreamReader isr = new InputStreamReader(is, "UTF-8");
						BufferedReader reader = new BufferedReader(isr);) {
						
						// 分析資料
						Gson gson = new Gson();
						String line = null;
						while((line = reader.readLine()) != null) {
							if(line.isBlank()) continue; // 此行掠過讀下一行
							/**
							 * line 的資料範例:
							 	{
									"model": "qwen3:4b",
									"created_at": "2025-11-08T04:13:00.9136281Z",
									"response": "",
									"thinking": "嗯",
									"done": false
								}					 
							 * */
							
							JsonObject obj = gson.fromJson(line, JsonObject.class);
							if(obj.has("response") && !obj.get("response").isJsonNull()) {
								String resp = obj.get("response").getAsString();
								// 將字串轉成字元逐一回報
								for(char ch : resp.toCharArray()) {
									callback.onResponseChar(ch);
								}
							}
							if(obj.has("done") && obj.get("done").getAsBoolean()) {
								break;
							}
						}
						
					}
					
					// 完成
					callback.onComplete();
					
				}
				
			} catch (Exception e) {
				callback.onError(e.getMessage());
			}
			
		};
		
		// 建立一條執行緒來處理
		new Thread(runnable).start();
		
	}
	
}
