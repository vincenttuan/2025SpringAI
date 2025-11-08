package ollama.generate;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Timer;

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

import ollama.generate.QueryExecutor.QueryCallback;

public class QueryGUI extends JFrame {
	// 視覺元件
	private JComboBox<String> modelCombo;
	private JTextField symbolField;
	private JTextField askField;
	private JButton queryBtn;
	private JTextArea resultArea;
	
	// 動畫
	private Timer animTimer;
	private String[] loadingFrames = {"查詢中   ", "查詢中.  ", "查詢中.. ", "查詢中..."};
	private int frameIndex = 0;
	
	// 建立 QueryExecutor 實例
	private QueryExecutor queryExecutor = new QueryExecutor();
	
	private static final String[] MODEL_NAMES = {
			"llama3.1:8b", "qwen3:4b", "qwen3:0.6b", "martain7r/finance-llama-8b:fp16"
	};
	
	private QueryGUI() {
		initUI(); // 畫面初始
		initListeners(); // 初始監聽
	}
	
	// 初始 UI 配置
	private void initUI() {
		setTitle("我的 AI 財經顧問");
		setDefaultCloseOperation(EXIT_ON_CLOSE); // 按下關閉即結束
		setSize(700, 550); // 視窗尺寸
		
		// 建立用於 UI 排版的 JPanel 並使用 GridBagLayout 版面配置管理器
		JPanel formPanel = new JPanel(new GridBagLayout());
		
		// 建立 GridBagConstraints 用於控制元件在 GridBagLayout 中的放置細節
		GridBagConstraints gbc = new GridBagConstraints();
		
		// 設定元件四周的編距為 8 像素 (上, 下, 左, 右)
		gbc.insets = new Insets(8,  8,  8,  8);
		
		// 放置元件時位置以元件的左上角為對齊基準點
		gbc.anchor = GridBagConstraints.WEST;
		
		// 元件會在水平方向伸展
		gbc.fill = GridBagConstraints.HORIZONTAL;
		
		// -- 創建 "選擇模型:" 標籤元件 ------------------------
		JLabel modelLabel = new JLabel("選擇模型:");
		// 元件放置位置
		gbc.gridx = 0;
		gbc.gridy = 0;
		// 將元件加入到 formPanel 中
		formPanel.add(modelLabel, gbc);
		
		// -- 創建 "選擇模型:" 標籤元件 ------------------------
		modelCombo = new JComboBox<>(MODEL_NAMES);
		// 元件放置位置
		gbc.gridx = 1;
		gbc.gridy = 0;
		// 將元件加入到 formPanel 中
		formPanel.add(modelCombo, gbc);
		
		// -- 創建 "股票代號:" 標籤元件 ------------------------
		JLabel symbolLabel = new JLabel("股票代號:");
		// 元件放置位置
		gbc.gridx = 0;
		gbc.gridy = 1;
		// 將元件加入到 formPanel 中
		formPanel.add(symbolLabel, gbc);
		
		// -- 創建 "輸入代號" 元件 ------------------------
		symbolField = new JTextField(10);
		// 元件放置位置
		gbc.gridx = 1;
		gbc.gridy = 1;
		// 將元件加入到 formPanel 中
		formPanel.add(symbolField, gbc);
		
		// -- 創建 "提問內容:" 標籤元件 ------------------------
		JLabel askLabel = new JLabel("提問內容:");
		// 元件放置位置
		gbc.gridx = 0;
		gbc.gridy = 2;
		// 將元件加入到 formPanel 中
		formPanel.add(askLabel, gbc);
		
		// -- 創建 "提問內容" 元件 ------------------------
		askField = new JTextField(10);
		// 元件放置位置
		gbc.gridx = 1;
		gbc.gridy = 2;
		// 將元件加入到 formPanel 中
		formPanel.add(askField, gbc);
		
		// -- 創建 "查詢" 元件 ------------------------
		queryBtn = new JButton("查詢");
		// 元件放置位置
		gbc.gridx = 1;
		gbc.gridy = 3;
		// 設定 button 不填滿整個格子大小(預設大小即可)
		gbc.fill = GridBagConstraints.NONE;
		// 將元件加入到 formPanel 中
		formPanel.add(queryBtn, gbc);
		
		// -- 創建 "多行文本區域" 元件 ------------------------
		resultArea = new JTextArea();
		resultArea.setLineWrap(true); // 允許自動換行
		resultArea.setWrapStyleWord(true); // 設定自動換行時，避免單詞被切斷
		resultArea.setFont(new Font("sansserif", Font.PLAIN, 16)); // 設定字體,風格,大小
		// 建立 JScrollPane 容器用來包覆 resultArea 用於滾動條的支援
		JScrollPane resultScroll = new JScrollPane(resultArea);
		resultScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS); // 總是顯示垂直滾動條
		
		// -- 創建 "警語" 元件 ------------------------
		JLabel footerLabel = new JLabel(" ** 投資之前，應該充分了解相關公告等相關信息，以便做出明智的投資決策 ** ");
		footerLabel.setFont(new Font("sansserif", Font.PLAIN, 14)); // 設定字體,風格,大小
		
		// 將 formPanel 放在 QueryGUI 主畫面的上方
		add(formPanel, BorderLayout.NORTH);
		
		// 將 resultScroll 放在 QueryGUI 主畫面的中央
		add(resultScroll, BorderLayout.CENTER);
		
		// 將 resultScroll 放在 QueryGUI 主畫面的下方c
		add(footerLabel, BorderLayout.SOUTH);
		
		// 預設內容
		modelCombo.setSelectedIndex(0); // 預設值 = 0
		symbolField.setText("2330");
		askField.setText("請建議此檔股票的買賣區間");		
	}
	
	// 初始監聽
	private void initListeners() {
		queryBtn.addActionListener(e -> onQueryClicked());
	}
	
	// 當查詢鍵被按下時所要做的事
	private void onQueryClicked() {
		// 資料驗證
		if(!validateInput()) return;
		
		resultArea.setText(""); // 清空上一筆查詢結果資料
		// AI 所需相關參數建立
		String modelName = (String) modelCombo.getSelectedItem();
		String symbol = symbolField.getText().trim();
		String prompt = TwseDataDownload.getStringDataWithPrompt(symbol);
		String fullPrompt = prompt + " " + askField.getText().trim();
		QueryCallback callback = new QueryCallback() {
			
			@Override
			public void onResponseChar(char ch) {
				SwingUtilities.invokeLater(() -> {
					resultArea.append(String.valueOf(ch));
				});
			}
			
			@Override
			public void onHttpError(int code) {
				SwingUtilities.invokeLater(() -> {
					resultArea.setText("HTTP 請求失敗, HTTP 狀態碼: " + code);
				});
			}
			
			@Override
			public void onError(String message) {
				SwingUtilities.invokeLater(() -> {
					resultArea.setText("執行錯誤: " + message);
				});
			}
			
			@Override
			public void onComplete() {
				SwingUtilities.invokeLater(() -> {
					resultArea.append("\n查詢完成 !");
				});
			}
		};
		
		// 執行
		queryExecutor.execute(modelName, fullPrompt, callback);
		
	}
	
	// 資料驗證
	private boolean validateInput() {
		if(symbolField.getText().trim().isEmpty() || askField.getText().trim().isEmpty()) {
			JOptionPane.showMessageDialog(this, "請填入股票代號與提問內容");
			return false;
		}
		return true;
	}
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			new QueryGUI().setVisible(true);
		});
	}
	
}
