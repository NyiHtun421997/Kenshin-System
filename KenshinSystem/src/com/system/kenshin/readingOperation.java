package com.system.kenshin;

import java.util.List;

public interface readingOperation {
	
	public void startOperation(String buildingName, String dateLabel, List<String> floor);
	
	public void  setReadings(String floorName,String reading,int readingType);
	
	public Double getReading(int index,String floorName);
	

}
