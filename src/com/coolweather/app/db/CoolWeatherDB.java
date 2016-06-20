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
 * 数据库操作类，封装数据库常用操作
 * @author Administrator
 *
 */
public class CoolWeatherDB {

	//数据库名称
	public static final String DB_NAME = "cool_weather";
	
	//数据库版本
	private static final int VERSION = 1;
	
	//数据库操作类的实例对象，单例 1：私有的成员变量
	private static CoolWeatherDB coolWeatherDB;
	
	//CoolWeatherDB封装的数据库实例对象
	private static SQLiteDatabase db;
	
	//构造方法私有化,单例2：构造方法私有化
	private CoolWeatherDB(Context context) {
		CoolWeatherOpenHelper dbHelper = new CoolWeatherOpenHelper(context, DB_NAME, null, VERSION);
		db = dbHelper.getWritableDatabase();
	}
	
	//返回实例对象，单例3：公有的返回实例对象的方法
	public synchronized static CoolWeatherDB getInstance(Context context) {
		if(coolWeatherDB == null) {
			coolWeatherDB = new CoolWeatherDB(context);
		}
		return coolWeatherDB;
	}
	
	/**
	 * 保存省份对象到数据库的省份表
	 * @param province
	 */
	public void saveProvince(Province province) {
		if(province != null) {
			db.execSQL("insert into province (province_name, province_code) values (?, ?)",
					new String[]{province.getProvinceName(), province.getProvinceCode()});
		}
	}
	
	/**
	 * 查询数据库省份表中的所有省份对象
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
	 * 保存城市对象到数据库的城市表
	 * @param city
	 */
	public void saveCity(City city) {
		if (city != null) {
			db.execSQL("insert into city (city_name, city_code, province_id) values (?, ?, ?)", 
					new String[]{city.getCityName(), city.getCityCode(), String.valueOf(city.getProvinceId())});
		}
	}
	
	/**
	 * 查询数据库城市表中的所有城市对象 
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
	 * 保存县城对象到数据库的城市表中
	 * @param county
	 */
	public void saveCounty(County county) {
		if(county != null) {
			db.execSQL("insert into county (conuty_code, county_name, city_id) values (?, ?, ?)",
					new String[]{county.getCountyCode(), county.getCountyName(), String.valueOf(county.getCityId())});
		}
	}
	
	/**
	 * 查询数据库县城表中所有县城对象
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
