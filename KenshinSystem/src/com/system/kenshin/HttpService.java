package com.system.kenshin;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.*;

public class HttpService {
	
	public HttpResponse<String> getMethod(String uri) {
		
		HttpClient client = HttpClient.newHttpClient();
		HttpResponse<String> response;
		try {
			HttpRequest request = HttpRequest.newBuilder()
									.uri(URI.create(uri))
									.GET()
									.build();
			response = client.send(request, HttpResponse.BodyHandlers.ofString());
			return response;
		}
		catch(IOException  | InterruptedException e) {
			e.printStackTrace();
			return null;
			//will throw error message
		}
		
	}
public HttpResponse<String> postMethod(String uri,FloorReading floorReading) {
		
		HttpClient client = HttpClient.newHttpClient();
		HttpResponse<String> response;
		try {
			ObjectMapper objectMapper = JsonMapper.builder()
				    .addModule(new JavaTimeModule())
				    .build();
			String json = objectMapper.writeValueAsString(floorReading);
			System.out.println(json);
			HttpRequest request = HttpRequest.newBuilder()
									.header("Content-Type", "application/json")
									.uri(URI.create(uri))
									.POST(HttpRequest.BodyPublishers.ofString(json))
									.build();
			response = client.send(request, HttpResponse.BodyHandlers.ofString());
			return response;
		}
		catch(IOException  | InterruptedException e) {
			e.printStackTrace();
			return null;
			//will throw error message
		}
		
	}
	
	public List<String> getBuildings() {
		
		List<String> buildingName = new ArrayList<>();

		try {
				HttpResponse<String> response = getMethod("http://localhost:8080/api/kenshin/central/building_names");
			
			if(response.statusCode() == 200) {
				ObjectMapper objectMapper = new ObjectMapper();
				buildingName = objectMapper.readValue(response.body(), new TypeReference<List<String>>() {});
				return buildingName;
			}
			else {
				return null;
				//will throw error message
			}
			
		}
		catch(IOException e) {
			e.printStackTrace();
			return null;
			//will throw error message
		}
	}
	
public String getLatestDate(String buildingName) {
		
		String latestDate;
		try {
            String encodedBuildingName = URLEncoder.encode(buildingName, "UTF-8");
			HttpResponse<String> response = getMethod("http://localhost:8080/api/kenshin/central/latest_date?building_name="+encodedBuildingName);
			
			if(response.statusCode() == 200) {
				
				//converting yyyy-mm to yyyy年mm月
				latestDate = response.body().replace('-', '年')+"月";
				return latestDate;
			}
			else {
				return null;
				//will throw error message
			}
		}
		catch(UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
		catch(IOException e) {
			e.printStackTrace();
			return null;
			//will throw error message
		}
	}
	
public List<String> getFloorListForBld(String buildingName){
	
	List<String> floor = new ArrayList<>();
	try {
		String encodedBuildingName = URLEncoder.encode(buildingName, "UTF-8");
		HttpResponse<String> response = getMethod("http://localhost:8080/api/kenshin/central/floors?building_name="+encodedBuildingName);
		
		if(response.statusCode() == 200) {
			ObjectMapper objectMapper = new ObjectMapper();
			floor = objectMapper.readValue(response.body(), new TypeReference<List<String>>() {});
			return floor;
		}
		else {
			//throw error message;
			return null;
		}
	}
	catch(UnsupportedEncodingException e) {
		e.printStackTrace();
		return null;
	}
	catch(IOException e) {
		e.printStackTrace();
		return null;
	}
}
public void storeToTempMap(LinkedHashMap<String,FloorReading> floorReadingsMap) {
	
	for(String x: floorReadingsMap.keySet()) {
		//loop through each reading obj inside HashMap and call post method for each obj
		postMethod("http://localhost:8080/api/kenshin/central/save_readings",floorReadingsMap.get(x));
		
	}
}
	

}