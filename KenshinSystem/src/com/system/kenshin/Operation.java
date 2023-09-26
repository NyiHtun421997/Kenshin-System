package com.system.kenshin;

import java.awt.Component;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class Operation implements readingOperation{
	
	public static LinkedHashMap<String,floorReading> floorReadingsMap = new LinkedHashMap<>();
	
	@Override
	public void startOperation(String buildingName, String dateLabel, List<String> floor){
		
		Date readingDate = convertDate(dateLabel);
		//loop until the number of floors of a building,must get number of floors as parameter
		for(int i=0;i<floor.size();i++) {
			
			floorReading newFloorReading = new floorReading(buildingName,floor.get(i),readingDate);
			//save the newFloorReading obj in Map
			floorReadingsMap.put(floor.get(i), newFloorReading);
			System.out.println(newFloorReading);
		}
	}
	
	@Override
	public void  setReadings(String floorName,String reading,int readingType) {
		
		floorReading newReading = floorReadingsMap.get(floorName);
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
		floorReading reading = floorReadingsMap.get(floorName);
		return reading.getReading(index);
		
	}
	public Date convertDate(String dateLabel) {
		
		//Getting date as String and converting to Date
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-M");
		String year = dateLabel.substring(0,dateLabel.indexOf("年"));
		String month = dateLabel.substring(dateLabel.indexOf("年")+1,dateLabel.indexOf("月"));
		
		dateLabel = year+"-"+month;
		Date readingDate=null;
		try {
			readingDate = dateFormat.parse(dateLabel);
		}
		
		catch(Exception e) {e.printStackTrace();}
		return readingDate;
	}
	
	

}
