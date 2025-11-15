package ollama.chat.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataFetcher {
	
	// 從 chat_prompts 資料表中讀取所有預設提示詞
	public static String[] loadPromptsFromDB() {
		
		List<String> prompts = new ArrayList<>();
		String sql = "select prompt_text from chat_prompts";
		
		try(Connection conn = DatabaseUtil.getConnection();
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql)) {
			
			while (rs.next()) {
				prompts.add(rs.getString("prompt_text"));
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return prompts.toArray(new String[0]);
	}
	
	// log 儲存
	public static void saveLog(String userInput, String botResponse) {
		String sql = "insert into chat_logs (user_input, bot_response) values (?, ?)";
		try(Connection conn = DatabaseUtil.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql)) {
			
			pstmt.setString(1, userInput);
			pstmt.setString(2, botResponse);
			pstmt.executeUpdate();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	// 從 chat_logs 資料表中讀取歷史對話
	public static List<Map<String, String>> loadChatHistory() {
		List<Map<String, String>> history = new ArrayList<>();
		String sql = "select user_input, bot_response from chat_logs order by created_at asc";
		
		try(Connection conn = DatabaseUtil.getConnection();
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql)) {
			
			while (rs.next()) {
				// user 訊息
				Map<String, String> userMessage = new HashMap<>();
				userMessage.put("role", "user");
				userMessage.put("content", rs.getString("user_input"));
				history.add(userMessage);
				
				// assistant 訊息
				Map<String, String> assistantMessage = new HashMap<>();
				assistantMessage.put("role", "assistant");
				assistantMessage.put("content", rs.getString("bot_response"));
				history.add(assistantMessage);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		
		return history;
	}
	
	
	
	
}
