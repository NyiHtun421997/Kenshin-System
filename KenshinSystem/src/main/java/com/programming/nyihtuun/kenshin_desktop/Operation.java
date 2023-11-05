package main.java.com.programming.nyihtuun.kenshin_desktop;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;

public class Operation implements ReadingOperation{
	
	private boolean isLatestMonth = true;
	private LinkedHashMap<String,FloorReading> floorReadingsMap;
	
	Operation(LinkedHashMap<String,FloorReading> floorReadingsMap,boolean isLatestMonth){
		this.floorReadingsMap = floorReadingsMap;
		this.isLatestMonth = isLatestMonth;
	}
	Operation(){
		floorReadingsMap = new LinkedHashMap<>();
	}	
	
	@Override
	public boolean isLatestMonth() {
		return isLatestMonth;
	}
	@Override
	public void startOperation(String buildingName, LocalDate readingDate, List<String> floor){
		
		//loop until the number of floors of a building,must get number of floors as parameter
		for(int i=0;i<floor.size();i++) {
			
			FloorReading newFloorReading = new FloorReading(buildingName,floor.get(i),readingDate);
			//save the newFloorReading obj in Map
			floorReadingsMap.put(floor.get(i), newFloorReading);
			System.out.println(newFloorReading);
		}
	}
	
	@Override
	public void  setReadings(String floorName,String reading,String readingBeforeChange,int readingType) {
		
		FloorReading newReading = floorReadingsMap.get(floorName);
		newReading.setReading(readingType, Double.parseDouble(reading));
		newReading.setReadingBeforeChange(readingType, Double.parseDouble(readingBeforeChange));
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
	public Double getReadingBeforeChange(int index,String floorName) {
		FloorReading reading = floorReadingsMap.get(floorName);
		return reading.getReadingBeforeChange(index);
	}
	
	@Override
	public LinkedHashMap<String,FloorReading> getAllReadings(){
		return floorReadingsMap;
	}
	@Override
	public String getComments(String floorName) {
		FloorReading reading = floorReadingsMap.get(floorName);
		return reading.getComment();
	}
	@Override
	public void setComments(String floorName,String comment) {
		FloorReading newReading = floorReadingsMap.get(floorName);
		newReading.setComment(comment);
		floorReadingsMap.put(floorName, newReading);
	}
}