package ollama.generate;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Timer;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

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
		initUI();
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
		
		// 創建第一個標籤元件
		JLabel modelLabel = new JLabel("選擇模型:");
		// 元件放置位置
		gbc.gridx = 0;
		gbc.gridy = 0;
		// 將元件加入到 formPanel 中
		formPanel.add(modelLabel, gbc);
		
		// 將 formPanel 放在 QueryGUI 主畫面的上方
		add(formPanel, BorderLayout.NORTH);
		
		
	}
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			new QueryGUI().setVisible(true);
		});
	}
	
}
