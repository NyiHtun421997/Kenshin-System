package com.system.kenshin;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class HttpService {
	
	public List<String> getBuildings() {
		
		List<String> buildingName = new ArrayList<>();
		HttpClient client = HttpClient.newHttpClient();
		
		try {
			HttpRequest request = HttpRequest.newBuilder()
									.uri(URI.create("http://localhost:8080/api/kenshin/central/building_names"))
									.GET()
									.build();
			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
			
			if(response.statusCode() == 200) {
				ObjectMapper objectMapper = new ObjectMapper();
				buildingName = objectMapper.readValue(response.body(), new TypeReference<List<String>>() {});
				
			}
			else {
				return null;
				//will throw error message
			}
			
		}
		catch(IOException  | InterruptedException e) {
			e.printStackTrace();
			//will throw error message
		}
		return buildingName;
	}
	
public String getLatestDate(String buildingName) {
		
		String latestDate = "";

		HttpClient client = HttpClient.newHttpClient();
		
		try {
			
            String encodedBuildingName = URLEncoder.encode(buildingName, "UTF-8");
			HttpRequest request = HttpRequest.newBuilder()
									.uri(URI.create("http://localhost:8080/api/kenshin/central/latest_date?building_name="+encodedBuildingName))
									.GET()
									.build();
			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
			
			if(response.statusCode() == 200) {
				
				//converting yyyy-mm to yyyy年mm月
				latestDate = response.body().replace('-', '年')+"月";
				
			}
			else {
				return null;
				//will throw error message
			}
			
		}
		catch(UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		catch(IOException  | InterruptedException e) {
			e.printStackTrace();
			//will throw error message
		}
		
		return latestDate;
	}
	
	

}
