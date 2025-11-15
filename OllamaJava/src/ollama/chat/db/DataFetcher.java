package ollama.chat.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

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
	
}
