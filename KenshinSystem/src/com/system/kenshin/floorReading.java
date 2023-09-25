package com.system.kenshin;

import java.io.Serializable;
import java.util.Date;

public class floorReading implements Serializable{
	
	private Double[] readings = new Double[4];//電灯、動力、水道、ガス
	private String[] pictureUrls = new String[4];
	private String buildingName,floorName;
	private Date readingDate;
	private Boolean approved = false;
	
	//Constructor
	public floorReading(String buildingName, String floorName, Date readingDate,Double reading) {
		super();
		this.buildingName = buildingName;
		this.floorName = floorName;
		this.readingDate = readingDate;
		this.readings[0] = reading;
	}
	public floorReading(String buildingName,String floorName, Date readingDate) {
		super();
		this.buildingName = buildingName;
		this.floorName = floorName;
		this.readingDate = readingDate;
	}
	
	//Getters and Setters
	public Double[] getReadings() {
		return readings;
	}

	public void setReadings(Double[] readings) {
		this.readings=readings;
	}
	
	public Double getReading(int i) {
		return readings[i];
	}
	
	public void setReading(int i,Double reading) {
		this.readings[i] = reading;
	}
	
	public String[] getPictureUrls() {
		return pictureUrls;
	}
	
	public void setPictureUrls(String[] pictureUrls) {
		this.pictureUrls=pictureUrls;
	}
	
	public String getPictureUrl(int i) {
		return pictureUrls[i];
	}
	
	public void setPictureUrl(int i,String pictureUrl) {
		this.pictureUrls[i] = pictureUrl;
	}

	public String getBuildingName() {
		return buildingName;
	}

	public void setBuildingName(String buildingName) {
		this.buildingName = buildingName;
	}

	public String getFloorName() {
		return floorName;
	}

	public void setFloorName(String floorName) {
		this.floorName = floorName;
	}

	public Date getReadingDate() {
		return readingDate;
	}

	public void setReadingDate(Date readingDate) {
		this.readingDate = readingDate;
	}

	public Boolean getApproved() {
		return approved;
	}

	public void setApproved(Boolean approved) {
		this.approved = approved;
	}
	//ToString

	@Override
	public String toString() {
		return "floorReading [buildingName=" + buildingName + ", floorName=" + floorName + ", readingDate="
				+ readingDate + ", 電灯= "+this.readings[0]+", 動力= "+this.readings[1]+
				", 水道= "+this.readings[2]+", ガス= "+this.readings[3]+"]";
	}
	
	
	
	
}
