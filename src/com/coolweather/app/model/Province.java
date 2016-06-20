package com.coolweather.app.model;

/**
 * 省份实体类
 * @author Administrator
 *
 */
public class Province {

	//数据库主键Id
	private int id;
	
	//省份名称
	private String provinceName;
	
	//省份编码
	private String provinceCode;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getProvinceName() {
		return provinceName;
	}

	public void setProvinceName(String provinceName) {
		this.provinceName = provinceName;
	}

	public String getProvinceCode() {
		return provinceCode;
	}

	public void setProvinceCode(String provinceCode) {
		this.provinceCode = provinceCode;
	}
	
	
}
