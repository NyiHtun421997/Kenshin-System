package com.system.kenshin;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;

public class Operation implements ReadingOperation{
	
	private static LinkedHashMap<String,FloorReading> floorReadingsMap = new LinkedHashMap<>();
	
	@Override
	public void startOperation(String buildingName, String dateLabel, List<String> floor){
		
		LocalDate readingDate = convertDate(dateLabel);
		//loop until the number of floors of a building,must get number of floors as parameter
		for(int i=0;i<floor.size();i++) {
			
			FloorReading newFloorReading = new FloorReading(buildingName,floor.get(i),readingDate);
			//save the newFloorReading obj in Map
			floorReadingsMap.put(floor.get(i), newFloorReading);
			System.out.println(newFloorReading);
		}
	}
	
	@Override
	public void  setReadings(String floorName,String reading,int readingType) {
		
		FloorReading newReading = floorReadingsMap.get(floorName);
		newReading.setReading(readingType, Double.parseDouble(reading));
		floorReadingsMap.put(floorName, newReading);
		
		//For Testing
		System.out.println("New Readings");
		for(String f:floorReadingsMap.keySet()) {
			System.out.println(floorReadingsMap.get(f));
		}
	}
	//index is for readingType,floorName is key for HashMap
	@Override
	public Double getReading(int index,String floorName) {
		FloorReading reading = floorReadingsMap.get(floorName);
		return reading.getReading(index);
	}
	
	@Override
	public LinkedHashMap<String,FloorReading> getAllReadings(){
		return floorReadingsMap;
	}
	
	public LocalDate convertDate(String dateLabel) {
		
		//Getting date as String and converting to LocalDate
		DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-M-dd");
		String year = dateLabel.substring(0,dateLabel.indexOf("年"));
		String month = dateLabel.substring(dateLabel.indexOf("年")+1,dateLabel.indexOf("月"));
		
		dateLabel = year+"-"+month+"-01";
		LocalDate readingDate;
		try {
			readingDate = LocalDate.parse(dateLabel, dateFormat);
			return readingDate;
		}
		
		catch(Exception e) {e.printStackTrace();return null;}
		
	}
	
	

}
