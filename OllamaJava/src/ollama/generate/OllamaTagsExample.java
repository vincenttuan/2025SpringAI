package ollama.generate;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OllamaTagsExample {
	// 定義 ollama web api url
	private static final String TAGS_WEB_API = "http://localhost:11434/api/tags";
		
	public static void main(String[] args) throws Exception {
		// 利用 OKhttp + http://localhost:11434/api/tags
		// 印出 model 名稱
		
		OkHttpClient client = new OkHttpClient();
		
		Request request = new Request.Builder()
				.url(TAGS_WEB_API)
				.get()
				.build();
		
		try(Response response = client.newCall(request).execute()) {
			
			// 檢查回應是否成功 ?
			if(!response.isSuccessful()) {
				System.out.printf("請求失敗, HTTP 狀態碼: %n%s", response.code());
				return;
			}
			
			String responseBody = response.body().string();
			System.out.printf("完整回應: %s%n", responseBody);
			
		}
		
		
	}

}
