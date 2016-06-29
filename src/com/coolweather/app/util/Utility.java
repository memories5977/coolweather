package com.coolweather.app.util;

import android.text.TextUtils;

import com.coolweather.app.db.CoolWeatherDB;
import com.coolweather.app.model.City;
import com.coolweather.app.model.County;
import com.coolweather.app.model.Province;

/**
 * ��������API���ص����ݣ����浽���ݿ�
 * @author Administrator
 *
 */
public class Utility {

	/**
	 * ����ʡ�����ݣ����浽���ݿ�
	 * @param coolWeatherDB
	 * @param response
	 * @return
	 */
	public synchronized static boolean handleProviencesResponse(CoolWeatherDB coolWeatherDB, String response) {
		if (!TextUtils.isEmpty(response)) {
			String[] allProviences = response.split(",");
			if (allProviences != null && allProviences.length > 0) {
				for (int i=0; i<allProviences.length; i++) {
					Province province = new Province();
					province.setProvinceCode(allProviences[i].split("\\|")[0]);
					province.setProvinceName(allProviences[i].split("\\|")[1]);
					coolWeatherDB.saveProvince(province);
				}
				return true;
			}
		}
		return false;
	}
	
	/**
	 * �����������ݣ����浽���ݿ�
	 * @param coolWeatherDB ���ݿ�ʵ������
	 * @param response ����API�õ�������
	 * @param provinceId ��������ʡ�ݵ����ݿ�Id
	 * @return 
	 */
	public synchronized static boolean handleCitiesResponse(CoolWeatherDB coolWeatherDB, String response, int provinceId) {
		if (!TextUtils.isEmpty(response)) {
			String[] allCities = response.split(",");
			if (allCities != null && allCities.length > 0) {
				for (int i=0; i<allCities.length; i++) {
					City city = new City();
					city.setCityCode(allCities[i].split("\\|")[0]);
					city.setCityName(allCities[i].split("\\|")[1]);
					city.setProvinceId(provinceId);
					coolWeatherDB.saveCity(city);
				}
				return true;
			}
		}
		return false;
	}
	
	/**
	 * �����ؼ����ݣ����浽���ݿ�
	 * @param coolWeatherDB
	 * @param response
	 * @param cityId
	 * @return
	 */
	public synchronized static boolean handleCountiesResponse(CoolWeatherDB coolWeatherDB, String response, int cityId) {
		if (!TextUtils.isEmpty(response)) {
			String[] allCounties = response.split(",");
			if (allCounties != null && allCounties.length > 0) {
				for (int i=0; i<allCounties.length; i++) {
					County county = new County();
					county.setCityId(cityId);
					county.setCountyCode(allCounties[i].split("\\|")[0]);
					county.setCountyName(allCounties[i].split("\\|")[1]);
					coolWeatherDB.saveCounty(county);
				}
				return true;
			}
		}
		return false;
	}
}
