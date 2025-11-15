package ollama.chat;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
 * 利用 QueryChatExecutor 來完成本範例
 *
 */
public class OllamaChatCLI {
	
	public static void main(String[] args) throws Exception {
		Scanner scanner = new Scanner(System.in);
		
		// 選擇模型
		String[] modelNames = {"llama3.1:8b", "qwen3:4b", "qwen3:0.6b", "martain7r/finance-llama-8b:fp16"};
		System.out.print("請選擇模型(0:llama3.1:8b, 1:qwen3:4b, 2:qwen3:0.6b, 3:martain7r/finance-llama-8b:fp16) => ");
		
		int modelIndex = scanner.nextInt();
		String modelName = modelNames[modelIndex];
		
		// 建立對話訊息列表(messages)
		List<Map<String, String>> messages = new ArrayList<>();
		
		// 利用 QueryChatExecutor 與 AI 持續對話
		// 請完成 !
		
		scanner.close();
	}
	
	
}
