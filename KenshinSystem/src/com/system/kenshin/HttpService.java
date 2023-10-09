package com.system.kenshin;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.*;

public class HttpService {
	
	private static HttpResponse<String> getMethod(String uri) {
		
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
			throw new CustomException(e);
		}
	}
private static HttpResponse<String> postMethod(String uri,Object object) {
		
		HttpClient client = HttpClient.newHttpClient();
		HttpResponse<String> response;
		try {
			ObjectMapper objectMapper = JsonMapper.builder()
				    .addModule(new JavaTimeModule())
				    .build();
			String json = objectMapper.writeValueAsString(object);
			//Testing
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
			throw new CustomException(e);
		}		
	}
	
	public static List<String> getBuildings() {
		
		List<String> buildingName = new ArrayList<>();

		try {
				HttpResponse<String> response = getMethod("http://localhost:8080/api/kenshin/central/building_names");
			
			if(response.statusCode() == 200) {
				ObjectMapper objectMapper = JsonMapper.builder()
					    .addModule(new JavaTimeModule())
					    .build();
				buildingName = objectMapper.readValue(response.body(), new TypeReference<List<String>>() {});
				return buildingName;
			}
			else {
				String message = response.body();
				throw new CustomException(message);
			}
			
		}
		catch(IOException e) {
			e.printStackTrace();
			throw new CustomException(e);
			//will throw error message
		}
	}
	
public static String getLatestDate(String buildingName) {
		
		String latestDate;
		try {
            String encodedBuildingName = URLEncoder.encode(buildingName, "UTF-8");
			HttpResponse<String> response = getMethod("http://localhost:8080/api/kenshin/central/latest_date?building_name="+encodedBuildingName);
			
			if(response.statusCode() == 200) {
				
				//converting yyyy-mm to yyyy年mm月
				latestDate = response.body().replace('-', '年')+"月";
				return latestDate;
			}
			else{
				String message = response.body();
				throw new CustomException(message);
			}
		}
		catch(IOException e) {
			e.printStackTrace();
			throw new CustomException(e);
		}
	}
	
public static List<String> getFloorListForBld(String buildingName){
	
	List<String> floor = new ArrayList<>();
	try {
		String encodedBuildingName = URLEncoder.encode(buildingName, "UTF-8");
		HttpResponse<String> response = getMethod("http://localhost:8080/api/kenshin/central/floors?building_name="+encodedBuildingName);
		
		if(response.statusCode() == 200) {
			ObjectMapper objectMapper = JsonMapper.builder()
				    .addModule(new JavaTimeModule())
				    .build();
			floor = objectMapper.readValue(response.body(), new TypeReference<List<String>>() {});
			return floor;
		}
		else{
			String message = response.body();
			throw new CustomException(message);
		}
	}
	catch(IOException e) {
		e.printStackTrace();
		throw new CustomException(e);
	}
}

public static List<String> getTenantListForBld(String buildingName){
	
	List<String> tenants = new ArrayList<>();
	try {
		String encodedBuildingName = URLEncoder.encode(buildingName, "UTF-8");
		HttpResponse<String> response = getMethod("http://localhost:8080/api/kenshin/central/tenants?building_name="+encodedBuildingName);
		
		if(response.statusCode() == 200) {
			ObjectMapper objectMapper = JsonMapper.builder()
				    .addModule(new JavaTimeModule())
				    .build();
			tenants = objectMapper.readValue(response.body(), new TypeReference<List<String>>() {});
			return tenants;
		}
		else{
			String message = response.body();
			throw new CustomException(message);
		}
	}
	catch(IOException e) {
		e.printStackTrace();
		throw new CustomException(e);
	}
}
public static void storeToTempMap(LinkedHashMap<String,FloorReading> floorReadingsMap) {
	
	for(String x: floorReadingsMap.keySet()) {
		//loop through each reading obj inside HashMap and call post method for each obj
		postMethod("http://localhost:8080/api/kenshin/central/temporary/save_readings",floorReadingsMap.get(x));
		
	}
}

//getting tenant readings from DB
public static LinkedHashMap<String,FloorReading> getTenantReadings(String buildingName, String dateLabel) {
	
	try {
		String encodedBuildingName = URLEncoder.encode(buildingName,"UTF-8");
		
		String encodedReadingDate = URLEncoder.encode(dateLabel,"UTF-8");
		
		HttpResponse<String> response = getMethod("http://localhost:8080/api/kenshin/central/past_readings?building_name="+encodedBuildingName+"&reading_date="+encodedReadingDate);
		
		if(response.statusCode() == 200) {
			
			ObjectMapper objectMapper = JsonMapper.builder()
				    .addModule(new JavaTimeModule())
				    .build();
			LinkedHashMap<String,FloorReading> tenant_readings = objectMapper.readValue(response.body(), new TypeReference<LinkedHashMap<String,FloorReading>>() {});
			
			return tenant_readings;
		}
		else{
			String message = response.body();
			throw new CustomException(message);
		}
	}
	catch(IOException e) {
		e.printStackTrace();
		throw new CustomException(e);
	}

}
//getting tenant readings temporarily stored on server
public static LinkedHashMap<String,FloorReading> getTenantReadingsFromTempo(String buildingName) {
	
	try {
		String encodedBuildingName = URLEncoder.encode(buildingName,"UTF-8");
		
		HttpResponse<String> response = getMethod("http://localhost:8080/api/kenshin/central/temporary/get_readings?building_name="+encodedBuildingName);
		
		if(response.statusCode() == 200) {
			
			ObjectMapper objectMapper = JsonMapper.builder()
				    .addModule(new JavaTimeModule())
				    .build();
			LinkedHashMap<String,FloorReading> tenant_readings = objectMapper.readValue(response.body(), new TypeReference<LinkedHashMap<String,FloorReading>>() {});
			
			return tenant_readings;
		}
		else{
			String message = response.body();
			throw new CustomException(message);
		}
	}
	catch(IOException e) {
		e.printStackTrace();
		throw new CustomException(e);
	}
}
//before opening input screen,check whether that buliding's data is being made
public static Boolean checkForBuilding(String buildingName) {
	
	try {
		String encodedBuildingName = URLEncoder.encode(buildingName,"UTF-8");
		HttpResponse<String> response = getMethod("http://localhost:8080/api/kenshin/central/temporary/check?building_name="+encodedBuildingName);
		
		if(response.statusCode() == 200) {
			return Boolean.parseBoolean(response.body());
		}
		else {
			String message = response.body();
			throw new CustomException(message);
		}
	}
	catch(IOException e) {
		e.printStackTrace();
		throw new CustomException(e);
	}
}

//method for saving comments for readings of each tenant
public static void storeComments(LinkedHashMap<String,String> commentData, String buildingName) {
	
	try {
		String encodedBuildingName =URLEncoder.encode(buildingName,"UTF-8"); 
			//loop through each reading obj inside HashMap and call post method
			postMethod("http://localhost:8080/api/kenshin/central/temporary/save_comments?building_name="+encodedBuildingName,commentData);			
	}
	catch(IOException e) {
		e.printStackTrace();
		throw new CustomException(e);
		
	}
}
//method to iterate through app's image file and send each image file to server
public static void storeImages() throws IOException,CustomException{
	String rootFolder = "/Users/nyinyihtun/git/repository/KenshinSystem/resources/user_input";
    String searchFor = "jpg";
    SimpleFileVisitor<Path> myFileVisitor = new SimpleFileVisitor<>() {
    	@Override
    	public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException{
    		if(path.toString().contains(searchFor)) {
    			CloseableHttpClient client = HttpClients.createDefault();
    			try {
    				MultipartEntityBuilder builder = MultipartEntityBuilder.create();
    				File file = path.toFile();
    				String encodedFileName = URLEncoder.encode(file.getName(),"UTF-8");
    				builder.addPart("image",new FileBody(file,ContentType.IMAGE_JPEG,encodedFileName));
    				HttpEntity entity = builder.build();
    				HttpPost request = new HttpPost("http://localhost:8080/api/kenshin/central/images/upload");
    				request.setEntity(entity);
    				
    				CloseableHttpResponse response = client.execute(request);
    				if(response.getStatusLine().getStatusCode() == 400) {
    					String message = EntityUtils.toString(response.getEntity());
    					throw new CustomException(message);
    				}
    				response.close();
    			}
    			finally {
    				client.close();
    			}
    		}
    		return FileVisitResult.CONTINUE;
    	}
    };
    Files.walkFileTree(Paths.get(rootFolder), myFileVisitor);
}
	

}
