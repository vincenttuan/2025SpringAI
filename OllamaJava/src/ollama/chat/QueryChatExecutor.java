package ollama.chat;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;

public class QueryChatExecutor {
	
	// 定義 ollama web api url
	private static final String CHAT_WEB_API = "http://localhost:11434/api/chat";
	
	// 定義媒體格式(MediaTyep)類型為 json
	private static final MediaType JSON = MediaType.get("appliaction/json;charset=utf-8");
	
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
		
	}
}









