package com.coolweather.app.model;

/**
 * 城市实体类
 * @author Administrator
 *
 */
public class City {

	//数据库主键Id
	private int id;
	
	//城市名称
	private String cityName;
	
	//城市编码
	private String cityCode;
	
	//所属省份Id
	private int provinceId;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public String getCityCode() {
		return cityCode;
	}

	public void setCityCode(String cityCode) {
		this.cityCode = cityCode;
	}

	public int getProvinceId() {
		return provinceId;
	}

	public void setProvinceId(int provinceId) {
		this.provinceId = provinceId;
	}
	
	
}
