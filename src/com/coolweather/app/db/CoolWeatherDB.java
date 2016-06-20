package com.coolweather.app.db;

import java.util.ArrayList;
import java.util.List;

import com.coolweather.app.model.City;
import com.coolweather.app.model.County;
import com.coolweather.app.model.Province;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * ���ݿ�����࣬��װ���ݿⳣ�ò���
 * @author Administrator
 *
 */
public class CoolWeatherDB {

	//���ݿ�����
	public static final String DB_NAME = "cool_weather";
	
	//���ݿ�汾
	private static final int VERSION = 1;
	
	//���ݿ�������ʵ�����󣬵��� 1��˽�еĳ�Ա����
	private static CoolWeatherDB coolWeatherDB;
	
	//CoolWeatherDB��װ�����ݿ�ʵ������
	private static SQLiteDatabase db;
	
	//���췽��˽�л�,����2�����췽��˽�л�
	private CoolWeatherDB(Context context) {
		CoolWeatherOpenHelper dbHelper = new CoolWeatherOpenHelper(context, DB_NAME, null, VERSION);
		db = dbHelper.getWritableDatabase();
	}
	
	//����ʵ�����󣬵���3�����еķ���ʵ������ķ���
	public synchronized static CoolWeatherDB getInstance(Context context) {
		if(coolWeatherDB == null) {
			coolWeatherDB = new CoolWeatherDB(context);
		}
		return coolWeatherDB;
	}
	
	/**
	 * ����ʡ�ݶ������ݿ��ʡ�ݱ�
	 * @param province
	 */
	public void saveProvince(Province province) {
		if(province != null) {
			db.execSQL("insert into province (province_name, province_code) values (?, ?)",
					new String[]{province.getProvinceName(), province.getProvinceCode()});
		}
	}
	
	/**
	 * ��ѯ���ݿ�ʡ�ݱ��е�����ʡ�ݶ���
	 * @return
	 */
	public List<Province> loadProvince() {
		List<Province> provinceList = new ArrayList<Province>();
		Cursor cursor = db.rawQuery("select * from province", null);
		if(cursor.moveToFirst()) {
			do {
				Province province = new Province();
				province.setId(cursor.getInt(cursor.getColumnIndex("id")));
				province.setProvinceCode(cursor.getString(cursor.getColumnIndex("province_code")));
				province.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
				provinceList.add(province);
			} while(cursor.moveToNext());
		}
		return provinceList;
	}
	
	/**
	 * ������ж������ݿ�ĳ��б�
	 * @param city
	 */
	public void saveCity(City city) {
		if (city != null) {
			db.execSQL("insert into city (city_name, city_code, province_id) values (?, ?, ?)", 
					new String[]{city.getCityName(), city.getCityCode(), String.valueOf(city.getProvinceId())});
		}
	}
	
	/**
	 * ��ѯ���ݿ���б��е����г��ж��� 
	 * @return
	 */
	public List<City> loadCity() {
		List<City> cityList = new ArrayList<City>();
		Cursor cursor = db.rawQuery("select * from city", null);
		if(cursor.moveToFirst()) {
			do {
				City city = new City();
				city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
				city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
				city.setId(cursor.getInt(cursor.getColumnIndex("id")));
				city.setProvinceId(cursor.getInt(cursor.getColumnIndex("province_id")));
				cityList.add(city);
			} while (cursor.moveToNext());
		}
		return cityList;
	}
	
	/**
	 * �����سǶ������ݿ�ĳ��б���
	 * @param county
	 */
	public void saveCounty(County county) {
		if(county != null) {
			db.execSQL("insert into county (conuty_code, county_name, city_id) values (?, ?, ?)",
					new String[]{county.getCountyCode(), county.getCountyName(), String.valueOf(county.getCityId())});
		}
	}
	
	/**
	 * ��ѯ���ݿ��سǱ��������سǶ���
	 * @return
	 */
	public List<County> loadCounty() {
		List<County> countyList = new ArrayList<County>();
		Cursor cursor = db.rawQuery("select * from county", null);
		if(cursor.moveToFirst()) {
			do {
				County county = new County();
				county.setCityId(cursor.getInt(cursor.getColumnIndex("city_id")));
				county.setCountyCode(cursor.getString(cursor.getColumnIndex("county_code")));
				county.setCountyName(cursor.getString(cursor.getColumnIndex("cursor_name")));
				county.setId(cursor.getInt(cursor.getColumnIndex("id")));
				countyList.add(county);
			} while (cursor.moveToNext());
		}
		return countyList;
	}
}
