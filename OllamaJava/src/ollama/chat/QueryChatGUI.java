package ollama.chat;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import ollama.chat.db.DataFetcher;

/**
 * QueryChatGUI 是一個基於 Swing 的簡易聊天介面範例，
 * 配合 QueryChatExecutor 調用本地 Ollama Chat API，
 * 支持多輪對話上下文記憶，實現流式回應字元輸出，
 * 展示聊天機器人如何追蹤並回應使用者的多輪提問。
 */
public class QueryChatGUI extends JFrame {
    // UI 元件定義
    private JComboBox<String> modelCombo;    // 模型下拉選單
    private JComboBox<String> askDefaultCombo; // 預設提問下拉選單
    private JTextField askField;              // 用戶輸入提問欄
    private JButton queryBtn;                 // 查詢按鈕
    private JTextArea resultArea;             // 回應結果顯示區域

    // 查詢中動畫的幀陣列與索引，用於顯示查詢中變化文字
    private Timer animTimer;
    private String[] loadingFrames = {"查詢中   ", "查詢中.  ", "查詢中.. ", "查詢中..."};
    private int frameIndex = 0;

    // 使用 QueryChatExecutor 進行非同步串流聊天請求
    private QueryChatExecutor queryExecutor = new QueryChatExecutor();

    // 支援選擇的模型名稱
    private static final String[] MODEL_NAMES = {
        "llama3.1:8b", "qwen3:4b", "qwen3:0.6b", "martain7r/finance-llama-8b:fp16"
    };

    // 預設提問列表，設計為展示聊天上下文記憶特性的例子劇本
    private static final String[] ASK_DEFAULT = DataFetcher.loadPromptsFromDB();

    // 用於保存整個多輪聊天歷史，格式為List<Map<String,String>>，
    // 每條訊息包含role(user或assistant)與content文字。
    private final List<Map<String, String>> messageHistory = DataFetcher.loadChatHistory();

    /**
     * 建構子，執行 UI 初始化與事件監聽設定
     */
    public QueryChatGUI() {
        initUI();
        initListeners();
    }

    /**
     * 初始化 UI 佈局與元件
     * 使用 GridBagLayout 控制表單區元件排版，
     * 主區域為結果顯示文本框，底部顯示提示文字。
     */
    private void initUI() {
        setTitle("聊天 AI 教學示範");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(700, 550);

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 模型選擇標籤與下拉選單
        JLabel modelLabel = new JLabel("選擇模型:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(modelLabel, gbc);

        modelCombo = new JComboBox<>(MODEL_NAMES);
        gbc.gridx = 1;
        gbc.gridy = 0;
        formPanel.add(modelCombo, gbc);

        // 提問欄標籤與文字欄位
        JLabel askLabel = new JLabel("提問內容:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(askLabel, gbc);

        askField = new JTextField(10);
        gbc.gridx = 1;
        gbc.gridy = 1;
        formPanel.add(askField, gbc);

        // 預設提問下拉選單，方便快速選擇示範問句
        askDefaultCombo = new JComboBox<>(ASK_DEFAULT);
        gbc.gridx = 1;
        gbc.gridy = 2;
        formPanel.add(askDefaultCombo, gbc);

        // 查詢按鈕
        queryBtn = new JButton("查詢");
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(queryBtn, gbc);

        // 業務區為不可編輯之多行文本區，顯示 AI 回覆結果
        resultArea = new JTextArea();
        resultArea.setEditable(false);
        resultArea.setLineWrap(true);
        resultArea.setWrapStyleWord(true);
        resultArea.setFont(new Font("sansserif", Font.PLAIN, 16));
        JScrollPane resultScroll = new JScrollPane(resultArea);
        resultScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        // 底部提示文字，告知此為教學示範用途
        JLabel footerLabel = new JLabel(" ** 本示範以記憶聊天上下文為教學目的，展示多輪對話能力 ** ");
        footerLabel.setFont(new Font("sansserif", Font.PLAIN, 14));

        add(formPanel, BorderLayout.NORTH);
        add(resultScroll, BorderLayout.CENTER);
        add(footerLabel, BorderLayout.SOUTH);

        // 預設選模型與提問欄空白
        modelCombo.setSelectedIndex(0);
        askField.setText("");
    }

    /**
     * 初始化事件監聽器：
     * 下拉選擇預設問句會自動帶入問句文字欄，
     * 按下查詢按鈕後觸發sendQuery事件。
     */
    private void initListeners() {
        askDefaultCombo.addActionListener(e -> {
            if (askDefaultCombo.getSelectedIndex() != 0) { // 非預設項目才設值
                askField.setText((String) askDefaultCombo.getSelectedItem());
            }
        });
        queryBtn.addActionListener(e -> onQueryClicked());
    }

    /**
     * 查詢按鈕點擊事件處理，
     * 主要流程：
     *  1. 驗證輸入不為空
     *  2. 新增用戶訊息到對話歷史
     *  3. 清空結果區並禁用輸入及顯示loading動畫
     *  4. 呼叫 QueryChatExecutor 非同步請求並逐字呈現回應
     *  5. 回應完成後將結果回存對話歷史，恢復輸入狀態
     */
    private void onQueryClicked() {
        String input = askField.getText().trim();
        if(input.isEmpty()) {
        	JOptionPane.showMessageDialog(this, "請輸入提問內容");
        	return;
        }
    	
        // 將使用者輸入以 user 角色加入到對話歷史
    	Map<String, String> userMessage = new HashMap<>();
    	userMessage.put("role", "user");
    	userMessage.put("content", input);
    	messageHistory.add(userMessage);
    	
    	// 清空顯示區, 同時禁用輸入元件避免重複觸發
    	resultArea.setText("");
    	disableInputs(true);
    	startLoadingAnimation(); // 啟動 loading 動畫
    	
    	// 模型名稱
    	String modelName = modelCombo.getSelectedItem().toString();
    	
    	// 實作 callback 回應
    	QueryChatExecutor.QueryCallback callback = new QueryChatExecutor.QueryCallback() {
    		// 累積 AI 的回應字串, 以便回存到歷史紀錄中
    		StringBuilder assistantContent = new StringBuilder();
    		
			@Override
			public void onResponseChar(char ch) {
				String data = String.valueOf(ch);
				resultArea.append(data); // 字元轉字串
				assistantContent.append(data); // 收集累計所有的回應文字
			}

			@Override
			public void onComplete() {
				// 回應完成後將 assistantContent 加入到歷史資料集合中
				Map<String, String> assistantMessage = new HashMap<>();
				assistantMessage.put("role", "assistant");
				assistantMessage.put("content", assistantContent.toString());
				messageHistory.add(assistantMessage);
				
				// log 儲存 -------------------------------------------------
				String userInput = askField.getText();
				String botMessage = assistantContent.toString();
				DataFetcher.saveLog(userInput, botMessage);
				// -------------------------------------------------
				
				resultArea.append("\n\n=== 查詢完成 ===\n");
				disableInputs(false);
				stopLoadingAnimation();
			}

			@Override
			public void onError(String message) {
				resultArea.setText("執行錯誤: " + message);
				disableInputs(false);
				stopLoadingAnimation();
			}

			@Override
			public void onHttpError(int statusCode) {
				resultArea.setText("HTTP 請求失敗, HTTP 狀態碼: " + statusCode);
				disableInputs(false);
				stopLoadingAnimation();
			}
    		
    	};
    	
    	// 執行 Chat 請求
    	queryExecutor.execute(modelName, messageHistory, callback);
        
    }

    /**
     * enable/disable 輸入與查詢按鈕，避免查詢時重複操作
     */
    private void disableInputs(boolean disable) {
        queryBtn.setEnabled(!disable);
        modelCombo.setEnabled(!disable);
        askField.setEnabled(!disable);
        askDefaultCombo.setEnabled(!disable);
    }

    /**
     * 啟動查詢中動畫，輪播提示文字
     */
    private void startLoadingAnimation() {
        frameIndex = 0;
        animTimer = new Timer(350, e -> {
            queryBtn.setText(loadingFrames[frameIndex]);
            frameIndex = (frameIndex + 1) % loadingFrames.length;
        });
        animTimer.start();
    }

    /**
     * 停止動畫並還原查詢按鈕文字
     */
    private void stopLoadingAnimation() {
        if (animTimer != null) {
            animTimer.stop();
        }
        queryBtn.setText("查詢");
    }

    /**
     * 程式進入點，啟動 GUI
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new QueryChatGUI().setVisible(true);
        });
    }
}
