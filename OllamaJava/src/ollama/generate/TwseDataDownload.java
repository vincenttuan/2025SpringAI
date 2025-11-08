package ollama.generate;

import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TwseDataDownload {
	
	public static void main(String[] args) {
		//String jsonString = getStringAllData();
		String jsonString = getStringDataWithFields("2330");
		System.out.println(jsonString);
	}
	
	public static String getStringDataWithFields(String symbol) {
		String jsonString = getStringAllData();
		
		// 將字串資料轉成 json 結構化資訊
		Gson gson = new Gson();
		JsonObject rootObj = gson.fromJson(jsonString, JsonObject.class);
		
		JsonArray fields = rootObj.getAsJsonArray("fields");
		JsonArray dataArray = rootObj.getAsJsonArray("data");
		
		// 分析 dataArray
		for(int i=0;i<dataArray.size();i++) {
			JsonArray item = dataArray.get(i).getAsJsonArray();
			if(item.size() > 0) {
				String itemSymbol = item.get(0).getAsString();
				if(itemSymbol.equals(symbol)) {
					StringBuilder financeDataBuilder = new StringBuilder();
					/*
					financeDataBuilder.append("證券代號=" + item.get(0).getAsString());
					financeDataBuilder.append("證券名稱=" + item.get(1).getAsString());
					financeDataBuilder.append("收盤價=" + item.get(2).getAsString());
					financeDataBuilder.append("殖利率(%)=" + item.get(3).getAsString());
					financeDataBuilder.append("股利年度=" + item.get(4).getAsString());
					financeDataBuilder.append("本益比=" + item.get(5).getAsString());
					financeDataBuilder.append("股價淨值比=" + item.get(6).getAsString());
					financeDataBuilder.append("財報年/季=" + item.get(7).getAsString());
					*/
					
					for(int j=0;j<item.size();j++) {
						String fieldName = fields.get(j).getAsString();
						String value = item.get(j).getAsString();
						financeDataBuilder.append(String.format("%s=%s ", fieldName, value));
					}
					return financeDataBuilder.toString();
				}
			}
		}
		
		return null;
		
	}
	
	public static String getStringAllData() {
		String url = "https://www.twse.com.tw/rwd/zh/afterTrading/BWIBBU_d?response=json&date=20251107";
		
		OkHttpClient client = new OkHttpClient();
		
		Request request = new Request.Builder()
				.url(url)
				.build();
		
		try(Response response = client.newCall(request).execute()) {
			
			if(response.isSuccessful() && response.body() != null) {
				String jsonString = response.body().string();
				return jsonString;
			}
			
		} catch (IOException e) {
			System.err.println(e);
		}
		
		return null;
	}
	
}
