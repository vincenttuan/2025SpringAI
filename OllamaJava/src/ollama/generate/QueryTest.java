package ollama.generate;

import ollama.generate.QueryExecutor.QueryCallback;

public class QueryTest {

	public static void main(String[] args) {
		// 基本財經變數
		String symbol = "2330";
		String prompt = TwseDataDownload.getStringDataWithPrompt(symbol);
		
		// QueryExecutor 需要的參數資訊
		String modelName = "llama3.1:8b";
		String fullPrompt = prompt + "請問台積電現在適合買進嗎?";
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
		//---------------------------------------------------------------
		QueryExecutor executor = new QueryExecutor();
		executor.execute(modelName, fullPrompt, callback);

	}

}
