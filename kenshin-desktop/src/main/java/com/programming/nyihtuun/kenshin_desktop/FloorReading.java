package com.programming.nyihtuun.kenshin_desktop;

import java.io.Serializable;
import java.time.LocalDate;

public class FloorReading implements Serializable{
	
	private Double[] readings = new Double[4];//電灯、動力、水道、ガス
	private Double[] readingsBeforeChange = {0d,0d,0d,0d};
	private String buildingName,floorName;
	private LocalDate readingDate;
	private String comment="";
	private Boolean isParent;
	
	//Constructor
	public FloorReading(String buildingName, String floorName, LocalDate readingDate,Double reading) {
		super();
		this.buildingName = buildingName;
		this.floorName = floorName;
		this.readingDate = readingDate;
		this.readings[0] = reading;
	}
	public FloorReading(String buildingName,String floorName, LocalDate readingDate) {
		super();
		this.buildingName = buildingName;
		this.floorName = floorName;
		this.readingDate = readingDate;
	}
	public FloorReading() {}
	
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

    public void setReadingsBeforeChange(Double[] readingsBeforeChange) {
        this.readingsBeforeChange = readingsBeforeChange;
    }
	public Double[] getReadingsBeforeChange() {
        return readingsBeforeChange;
    }
	
	public void setReadingBeforeChange(int i,Double reading) {
		this.readingsBeforeChange[i] = reading;
	}
	public Double getReadingBeforeChange(int i) {
		return readingsBeforeChange[i];
	}
	
	public void setReading(int i,Double reading) {
		this.readings[i] = reading;
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

	public LocalDate getReadingDate() {
		return readingDate;
	}

	public void setReadingDate(LocalDate readingDate) {
		this.readingDate = readingDate;
	}

	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	
	public Boolean getIsParent() {
		return isParent;
	}
	public void setIsParent(Boolean isParent) {
		this.isParent = isParent;
	}
	//ToString
	@Override
	public String toString() {
		return "floorReading [buildingName=" + buildingName + ", floorName=" + floorName + ", readingDate="
				+ readingDate + "ParentMeter="+isParent+ ", 電灯= "+this.readings[0]+", 動力= "+this.readings[1]+
				", 水道= "+this.readings[2]+", ガス= "+this.readings[3]+"]";
	}
}