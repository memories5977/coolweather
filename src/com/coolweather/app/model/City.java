package com.coolweather.app.model;

/**
 * ����ʵ����
 * @author Administrator
 *
 */
public class City {

	//���ݿ�����Id
	private int id;
	
	//��������
	private String cityName;
	
	//���б���
	private String cityCode;
	
	//����ʡ��Id
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
