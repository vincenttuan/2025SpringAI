package ollama.generate;

import java.util.Scanner;

import ollama.generate.QueryExecutor.QueryCallback;

public class QueryCLI {

	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		QueryExecutor queryExecutor = new QueryExecutor();
		String[] modelNames = {"llama3.1:8b", "qwen3:4b", "qwen3:0.6b", "martain7r/finance-llama-8b:fp16"};
		
		System.out.print("請選擇模型(0:llama3.1:8b, 1:qwen3:4b, 2:qwen3:0.6b, 3:martain7r/finance-llama-8b:fp16) => ");
		int modelIndex = scanner.nextInt();
		scanner.nextLine(); // 清理換行符號
		String modelName = modelNames[modelIndex];
		
		System.out.print("請輸入股票代號(例如:2330) => ");
		String symbol = scanner.nextLine();
		
		System.out.print("請輸入分析需求問題 => ");
		String userQuestion = scanner.nextLine();
		
		// 透過 TwseDataDownload 取得 prompt
		String prompt = TwseDataDownload.getStringDataWithPrompt(symbol);
		String fullPrompt = prompt + " " + userQuestion;
		
		// QueryCallback 回調函式
		QueryCallback callback = new QueryCallback() { // Callback 參數
			
			@Override
			public void onResponseChar(char ch) {
				System.out.print(ch);
			}
			
			@Override
			public void onHttpError(int code) {
				System.err.println("\nHTTP 請求失敗, HTTP 狀態碼: " + code);
			}
			
			@Override
			public void onError(String message) {
				System.err.println("\n執行錯誤: " + message);
			}
			
			@Override
			public void onComplete() {
				System.out.println("\n查詢完成");
			}
		};
		// 執行查詢
		queryExecutor.execute(modelName, fullPrompt, callback);
		
	}

}
