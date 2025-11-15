package ollama.chat.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseUtil {
	/****************************************************************
		MySQL Community Downloads 
		https://dev.mysql.com/downloads/windows/installer/8.0.html
		mysql-installer-community-8.0.44.0.msi
	
		<dependency>
        	<groupId>com.mysql</groupId>
        	<artifactId>mysql-connector-j</artifactId>
        	<version>8.0.33</version>
		</dependency>
	 *****************************************************************/
    private static final String URL = "jdbc:mysql://localhost:3306/ai?useSSL=false&serverTimezone=Asia/Taipei";
    private static final String USER = "root";
    private static final String PASSWORD = "12345678";

    static {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement()) {

            // 建立 chat_prompts 表
            String createPromptsTable = """
                CREATE TABLE IF NOT EXISTS chat_prompts (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    prompt_text VARCHAR(255) NOT NULL
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
                """;
            stmt.execute(createPromptsTable);

            // 建立 chat_logs 表
            String createLogsTable = """
                CREATE TABLE IF NOT EXISTS chat_logs (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    user_input TEXT NOT NULL,
                    bot_response TEXT NOT NULL,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
                """;
            stmt.execute(createLogsTable);

            // 檢查 chat_prompts 是否已有資料，無資料則插入預設值
            String checkSql = "SELECT COUNT(*) FROM chat_prompts";
            ResultSet rs = stmt.executeQuery(checkSql);
            rs.next();
            int count = rs.getInt(1);
            if (count == 0) {
                String[] defaultPrompts = {
                    
                		
                		
                };

                // 使用 PreparedStatement 批次插入資料
                String insertSql = "INSERT INTO chat_prompts (prompt_text) VALUES (?)";
                try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
                    for (String prompt : defaultPrompts) {
                        pstmt.setString(1, prompt);
                        pstmt.addBatch();
                    }
                    pstmt.executeBatch();
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}

