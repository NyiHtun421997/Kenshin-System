package com.system.kenshin;

import java.util.LinkedHashMap;
import java.util.List;

public interface ReadingOperation {
	
	public void startOperation(String buildingName, String dateLabel, List<String> floor);
	
	public void  setReadings(String floorName,String reading,String readingBeforeChange,int readingType);
	
	public Double getReading(int index,String floorName);
	
	public Double getReadingBeforeChange(int index, String floorName);
	
	public LinkedHashMap<String,FloorReading> getAllReadings();

}
