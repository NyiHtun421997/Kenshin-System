package main.java.com.programming.nyihtuun.kenshin_desktop;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;

public interface ReadingOperation {
	
	public void startOperation(String buildingName, LocalDate readingDate, List<String> floor);
	
	public void  setReadings(String floorName,String reading,String readingBeforeChange,int readingType);
	
	public Double getReading(int index, String floorName);
	
	public Double getReadingBeforeChange(int index, String floorName);
	
	public String getComments(String floorName);
	
	public void setComments(String floorName, String comment);
	
	public LinkedHashMap<String,FloorReading> getAllReadings();
	
	public boolean isLatestMonth();

}
