package main.java.com.programming.nyihtuun.kenshin_desktop;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.*;

public class HttpService {
	
private final TokenManager tokenManager;
private final String serverIP;
	
public HttpService(TokenManager tokenManager,String ipAddress) {
        this.tokenManager = tokenManager;
        this.serverIP = ipAddress;
}
	
private  HttpResponse<String> getMethod(String uri) {
		
		HttpClient client = HttpClient.newHttpClient();
		HttpResponse<String> response;
		try {
			HttpRequest request = HttpRequest.newBuilder()
									.header("Authorization", "Bearer "+tokenManager.getToken())
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
private  HttpResponse<String> postMethod(String uri,Object object) {
		
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
									.header("Authorization", "Bearer "+tokenManager.getToken())
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
private  HttpResponse<String> putMethod(String uri,Object object) {
	
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
								.header("Authorization", "Bearer "+tokenManager.getToken())
								.uri(URI.create(uri))
								.PUT(HttpRequest.BodyPublishers.ofString(json))
								.build();
		response = client.send(request, HttpResponse.BodyHandlers.ofString());
		return response;
	}
	catch(IOException  | InterruptedException e) {
		e.printStackTrace();
		throw new CustomException(e);
	}		
}
	
public  List<String> getBuildings() {
		
		List<String> buildingName = new ArrayList<>();

		try {
				HttpResponse<String> response = getMethod("http://"+serverIP+":8080/api/kenshin/central/building_names");
			
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
	
public  LocalDate getLatestDate(String buildingName) {
		
		try {
            String encodedBuildingName = URLEncoder.encode(buildingName, "UTF-8");
			HttpResponse<String> response = getMethod("http://"+serverIP+":8080/api/kenshin/central/latest_date?building_name="+encodedBuildingName);
					
			if(response.statusCode() == 200) {
				
				ObjectMapper objectMapper = JsonMapper.builder()
					    .addModule(new JavaTimeModule())
					    .build();
				return objectMapper.readValue(response.body(), new TypeReference<LocalDate>() {});
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

public  List<String> getReadingDatesForBuilding(String buildingName){
	try {
		String encodedBuildingName = URLEncoder.encode(buildingName,"UTF-8");
		HttpResponse<String> response = getMethod("http://"+serverIP+":8080/api/kenshin/central/reading_dates?building_name="+encodedBuildingName);
		if(response.statusCode() == 200) {
			ObjectMapper objectMapper = JsonMapper.builder()
										.addModule(new JavaTimeModule())
										.build();
			List<String> readingDateList = objectMapper.readValue(response.body(), new TypeReference<List<String>>() {});
			return readingDateList;
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
	
public  List<String> getFloorListForBld(String buildingName){
	
	List<String> floor = new ArrayList<>();
	try {
		String encodedBuildingName = URLEncoder.encode(buildingName, "UTF-8");
		HttpResponse<String> response = getMethod("http://"+serverIP+":8080/api/kenshin/central/floors?building_name="+encodedBuildingName);
		
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

public  List<String> getTenantListForBld(String buildingName){
	
	List<String> tenants = new ArrayList<>();
	try {
		String encodedBuildingName = URLEncoder.encode(buildingName, "UTF-8");
		HttpResponse<String> response = getMethod("http://"+serverIP+":8080/api/kenshin/central/tenants?building_name="+encodedBuildingName);
		
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
public  void storeToTempMap(LinkedHashMap<String,FloorReading> floorReadingsMap) {
	
	for(String x: floorReadingsMap.keySet()) {
		//loop through each reading obj inside HashMap and call post method for each obj
		postMethod("http://"+serverIP+":8080/api/kenshin/central/temporary/save_readings",floorReadingsMap.get(x));	
	}
}
//method to update readings from DB
public  void updateReadings(String buildingName,String dateLabel,String floorName,LinkedHashMap<String,FloorReading> floorReadingsMap) {
	for(String x: floorReadingsMap.keySet()) {
		
		try {
			String encodedBuildingName = URLEncoder.encode(buildingName,"UTF-8");
			String encodedDateLabel = URLEncoder.encode(dateLabel,"UTF-8");
			String encodedFloorName = URLEncoder.encode(x,"UTF-8");
			//loop through each reading obj inside HashMap and call post method for each obj
			putMethod("http://"+serverIP+":8080/api/kenshin/central/floor/update_readings?building_name="+encodedBuildingName
					+"&reading_date="+encodedDateLabel+"&floor_name="+encodedFloorName,floorReadingsMap.get(x));	
		}
		catch(IOException e) {
			e.printStackTrace();
			throw new CustomException(e);
		}
		
	}
}
//getting readings from DB
private  LinkedHashMap<String,FloorReading> getReadingsFromDB(String url) throws IOException{
	
		HttpResponse<String> response = getMethod(url);
		if(response.statusCode() == 200) {
			
			ObjectMapper objectMapper = JsonMapper.builder()
				    .addModule(new JavaTimeModule())
				    .build();
			return objectMapper.readValue(response.body(), new TypeReference<LinkedHashMap<String,FloorReading>>() {});
		}
		else{
			String message = response.body();
			throw new CustomException(message);
		}

}

//getting tenant readings from DB
public  LinkedHashMap<String,FloorReading> getTenantReadings(String buildingName, String dateLabel) {
	
	try {
		String encodedBuildingName = URLEncoder.encode(buildingName,"UTF-8");
		String encodedReadingDate = URLEncoder.encode(dateLabel,"UTF-8");
		String url = "http://"+serverIP+":8080/api/kenshin/central/tenant/past_readings?building_name="+encodedBuildingName+"&reading_date="+encodedReadingDate;
			
		LinkedHashMap<String,FloorReading> tenant_readings = getReadingsFromDB(url);
		return tenant_readings;
	}
	catch(IOException e) {
		e.printStackTrace();
		throw new CustomException(e);
	}
}
//getting floor readings from DB
public  LinkedHashMap<String,FloorReading> getFloorReadings(String buildingName, String dateLabel) {
	
	try {
		String encodedBuildingName = URLEncoder.encode(buildingName,"UTF-8");
		String encodedReadingDate = URLEncoder.encode(dateLabel,"UTF-8");
		String url = "http://"+serverIP+":8080/api/kenshin/central/floor/past_readings?building_name="+encodedBuildingName+"&reading_date="+encodedReadingDate;
			
		LinkedHashMap<String,FloorReading> floor_readings = getReadingsFromDB(url);
		return floor_readings;
	}
	catch(IOException e) {
		e.printStackTrace();
		throw new CustomException(e);
	}

}
//getting readings temporarily stored on server
private  LinkedHashMap<String,FloorReading> getReadingsFromTempo(String url) throws IOException{

		HttpResponse<String> response = getMethod(url);	
		if(response.statusCode() == 200) {	
			ObjectMapper objectMapper = JsonMapper.builder()
				    .addModule(new JavaTimeModule())
				    .build();
			LinkedHashMap<String,FloorReading> readings = objectMapper.readValue(response.body(), new TypeReference<LinkedHashMap<String,FloorReading>>() {});	
			return readings;
		}
		else{
			String message = response.body();
			throw new CustomException(message);
		}
}
//getting tenant readings temporarily stored on server
public  LinkedHashMap<String,FloorReading> getTenantReadingsFromTempo(String buildingName) {
	
	try {
		String encodedBuildingName = URLEncoder.encode(buildingName,"UTF-8");
		String url = "http://"+serverIP+":8080/api/kenshin/central/temporary/tenant/get_readings?building_name="+encodedBuildingName;
		
		LinkedHashMap<String,FloorReading> tenant_readings = getReadingsFromTempo(url);
		return tenant_readings;
	}
	catch(IOException e) {
		e.printStackTrace();
		throw new CustomException(e);
	}
}
//getting floor readings temporarily stored on server
public  LinkedHashMap<String,FloorReading> getFloorReadingsFromTempo(String buildingName) {
	
	try {
		String encodedBuildingName = URLEncoder.encode(buildingName,"UTF-8");
		String url = "http://"+serverIP+":8080/api/kenshin/central/temporary/floor/get_readings?building_name="+encodedBuildingName;
		
		LinkedHashMap<String,FloorReading> floor_readings = getReadingsFromTempo(url);
		return floor_readings;
	}
	catch(IOException e) {
		e.printStackTrace();
		throw new CustomException(e);
	}
}
//before opening input screen,check whether that buliding's data is being made
public  Boolean checkForBuilding(String buildingName) {
	
	try {
		String encodedBuildingName = URLEncoder.encode(buildingName,"UTF-8");
		HttpResponse<String> response = getMethod("http://"+serverIP+":8080/api/kenshin/central/temporary/check?building_name="+encodedBuildingName);
		
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

//method for updating comments for readings of each tenant
public  void storeComments(LinkedHashMap<String,String> commentData, String buildingName) {
	
	try {
		String encodedBuildingName =URLEncoder.encode(buildingName,"UTF-8"); 
			//loop through each reading obj inside HashMap and call post method
			postMethod("http://"+serverIP+":8080/api/kenshin/central/temporary/save_comments?building_name="+encodedBuildingName,commentData);			
	}
	catch(IOException e) {
		e.printStackTrace();
		throw new CustomException(e);
		
	}
}
//method to iterate through app's image file and send each image file to server
private  void fileUpload(HttpRequestBase request,String fileName, byte[] imageData) throws IOException,CustomException{
	
    			CloseableHttpClient client = HttpClients.createDefault();
    			try {
    				MultipartEntityBuilder builder = MultipartEntityBuilder.create();
    				String encodedFileName = URLEncoder.encode(fileName,"UTF-8");
    				
    				builder.addBinaryBody("image", imageData, ContentType.IMAGE_JPEG, encodedFileName);
    				HttpEntity entity = builder.build();
    				
    				if(request instanceof HttpPost) {
    					((HttpPost)request).setEntity(entity);
    				}
    				if(request instanceof HttpPut) {
    					((HttpPut)request).setEntity(entity);
    				}
    				request.setHeader("Authorization", "Bearer "+tokenManager.getToken());
    				
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

public  void storeImages(String fileName, byte[] imageData) throws IOException,CustomException{
	HttpPost request = new HttpPost("http://"+serverIP+":8080/api/kenshin/central/images/upload");
	fileUpload(request,fileName,imageData);
}

public  void updateImages(String fileName, byte[] imageData) throws IOException,CustomException{
	HttpPut request = new HttpPut("http://"+serverIP+":8080/api/kenshin/central/images/upload");
	fileUpload(request,fileName,imageData);
}

public  byte[] getImages(String fileName) throws IOException,CustomException{
	String encodedFileName = URLEncoder.encode(fileName,"UTF-8");
	HttpGet request = new HttpGet("http://"+serverIP+":8080/api/kenshin/central/images/download?file_name="+encodedFileName);
	request.setHeader("Authorization", "Bearer "+tokenManager.getToken());
	CloseableHttpClient client = HttpClients.createDefault();
	CloseableHttpResponse response = client.execute(request);
		try {
			if(response.getStatusLine().getStatusCode() == 200) {
				HttpEntity entity = response.getEntity();
				byte[] bytes = EntityUtils.toByteArray(entity);
				return  bytes;
			}
			else {
				return null;
			}
		}
		finally {
			response.close();
			client.close();
		}
	}

public  void approve(String buildingName,String dateLabel) {
	try {
		String encodedBuildingName =URLEncoder.encode(buildingName,"UTF-8"); 
		String encodedReadingDate = URLEncoder.encode(dateLabel,"UTF-8");
			//loop through each reading obj inside HashMap and call post method
			postMethod("http://"+serverIP+":8080/api/kenshin/central/temporary/approve?building_name="+encodedBuildingName+"&reading_date="+encodedReadingDate,"Approve");			
	}
	catch(IOException e) {
		e.printStackTrace();
		throw new CustomException(e);
		
	}
}
public  void finalApprove(String buildingName,String dateLabel) {
	try {
		String encodedBuildingName =URLEncoder.encode(buildingName,"UTF-8"); 
		String encodedReadingDate = URLEncoder.encode(dateLabel,"UTF-8");
			//loop through each reading obj inside HashMap and call post method
			postMethod("http://"+serverIP+":8080/api/kenshin/central/temporary/final_approve?building_name="+encodedBuildingName+"&reading_date="+encodedReadingDate,"Approve");			
	}
	catch(IOException e) {
		e.printStackTrace();
		throw new CustomException(e);
		
	}
}
public static HttpResponse<String> loginMethod(AuthRequest authRequest,String serverIP) {
	
	HttpClient client = HttpClient.newHttpClient();
	HttpResponse<String> response;
	try {
		ObjectMapper objectMapper = JsonMapper.builder()
			    .addModule(new JavaTimeModule())
			    .build();
		String json = objectMapper.writeValueAsString(authRequest);
		//Testing
		System.out.println(json);
		HttpRequest request = HttpRequest.newBuilder()
								.header("Content-Type", "application/json")
								.uri(URI.create("http://"+serverIP+":8080/api/kenshin/secure/login"))
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
public void logoutMethod() {
	this.tokenManager.setToken(null);
}
}
