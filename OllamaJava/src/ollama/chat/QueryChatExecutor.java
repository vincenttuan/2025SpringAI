package ollama.chat;

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
	
	
}
