package ollama.chat;

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

}
