package ollama.generate;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * 範例名稱：OllamaGenerateExample（使用 OkHttp3 版本）
 *
 * 說明：
 *  本程式示範如何使用 OkHttp3 HTTP 客戶端函式庫，
 *  向本機或同區網的 Ollama 伺服器發送 POST 請求，
 *  呼叫 /api/generate 端點以生成文字回應結果。
 *
 *  對應的 curl 範例如下：
 *  curl -X POST http://localhost:11434/api/generate \
 *   -H "Content-Type: application/json" \
 *   -d "{\"model\":\"qwen3:4b\",\"prompt\":\"請用中文介紹 Java 程式語言\",\"stream\":false}"
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
public class OllamaGenerateExample {
	// 定義 ollama web api url
	private static final String GENERATE_WEB_API = "http://localhost:11434/api/generate";
	
	// 定義媒體(MediaType)型別為 json
	private static final MediaType JSON = MediaType.get("application/json;charset=utf-8");
	
	
	public static void main(String[] args) throws Exception {
		// 是否支援 stream
		Boolean supportStream = true;
		
		//---------------------------------------------------
		// 1. 建立 JSON 請求內容
		//---------------------------------------------------
		String jsonBody = """
				{
					"model":"qwen3:4b",
					"prompt": "請用中文介紹 Java 程式語言",
					"stream": %b
				}
				""";
		jsonBody = String.format(jsonBody, supportStream);
		System.out.printf("要發送的 JSON:%n%s%n", jsonBody);
		
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
				.url(GENERATE_WEB_API)
				.post(body)
				.build();
		
		//---------------------------------------------------
		// 5. 同步發送請求並取得回應
		//---------------------------------------------------
		try(Response response = client.newCall(request).execute()){
			
			// 檢查回應是否成功 ?
			if(!response.isSuccessful()) {
				System.out.printf("請求失敗, HTTP 狀態碼: %n%s", response.code());
				return;
			}
			
			// 取得回應內容
			if(supportStream) { // "stream": true
				try(InputStream        is = response.body().byteStream(); // 單位 byte 
					InputStreamReader isr = new InputStreamReader(is, "UTF-8"); // 單位 char
					BufferedReader reader = new BufferedReader(isr)) { // 可逐行讀取
					
					String line = null;
					while((line = reader.readLine()) != null) {
						System.out.println(line); // 逐字顯示
					}
				}
				
			} else { // "stream": false
				String responseBody = response.body().string();
				System.out.printf("%n回應碼: %s%n", response.code());
				System.out.printf("完整回應: %s%n", responseBody);
			}
			
		}
		
	}

}
