package com.coolweather.app.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * �������ݿ��ȡ��
 * @author Administrator
 *
 */
public class CoolWeatherOpenHelper extends SQLiteOpenHelper {

	//ʡ�ݱ������
	private static final String CREATE_PROVINCE = "create table province ("
			+ "id integer primary key autoincrement, "
			+ "province_name text, "
			+ "province_code text)";
	
	//���б������
	private static final String CREATE_CITY = "create table city ("
			+ "id integer primary key autoincrement, "
			+ "city_name text, "
			+ "city_code text, "
			+ "province_id integer)";
	
	//�سǱ������
	private static final String CREATE_COUNTY = "create table county ("
			+ "id integer primary key autoincrement, "
			+ "county_name text, "
			+ "county_code text, "
			+ "city_id integer)";

	/**
	 * ���췽����ֱ�Ӽ̳и���
	 * @param context
	 * @param name
	 * @param factory
	 * @param version
	 */
	public CoolWeatherOpenHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	/**
	 * ʵ��������ʱ�Զ����ã�ִ������������䣬�����������ݿ�
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_CITY);
		db.execSQL(CREATE_COUNTY);
		db.execSQL(CREATE_PROVINCE);
	}

	/**
	 * �������ݿ�ʱʹ��
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}

}
