package com.coolweather.app.activity;

import java.util.ArrayList;
import java.util.List;

import com.coolweather.app.R;
import com.coolweather.app.db.CoolWeatherDB;
import com.coolweather.app.model.City;
import com.coolweather.app.model.County;
import com.coolweather.app.model.Province;
import com.coolweather.app.util.HttpCallBackListener;
import com.coolweather.app.util.HttpUtil;
import com.coolweather.app.util.Utility;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * ���ص����Ļ
 * @author Administrator
 *
 */
public class ChooseAreaActivity extends Activity {

	//��������ʡ��������
	public static final int LEVEL_PROVINCE = 0;
	public static final int LEVEL_CITY = 1;
	public static final int LEVEL_COUNTY = 2;
	
	//UI�ؼ�
	private ProgressDialog progressDialog;
	private TextView titleText;
	private ListView listView;
	private ArrayAdapter<String> adapter;
	private CoolWeatherDB coolWeatherDB;
	private List<String> dataList = new ArrayList<String>();
	
	//�洢������ʾ�ĸ��������б�
	private List<Province> provinceList;
	private List<City> cityList;
	private List<County> countyList;
	
	//��ѡ�ĵ���
	private Province selectedProvince;
	private City selectedCity;
	
	//��ǰ���������ļ���
	private int currentLevel;
	
	//��ע�Ƿ���������ת������
	private boolean isFromWeatherActivity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//�õ�isFromWeatherActivity��ֵ������ǰ��ǲ��Ǵ��������ת������
		isFromWeatherActivity = this.getIntent().getBooleanExtra("from_weather_activity", false);
		
		//�ж��Ƿ�Ԥ��ѡ�������
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		if (prefs.getBoolean("city_selected", false) && !isFromWeatherActivity) {	//�����ѡ����Ҹû�����������ת�����ģ���ֱ����ת�������ҳ��
			Intent intent = new Intent(this, WeatherActivity.class);
			this.startActivity(intent);
			this.finish();	//������ǰ�
			return;	//ֱ�ӷ���
		}
		
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.choose_area);
		listView = (ListView)this.findViewById(R.id.list_view);
		titleText = (TextView)this.findViewById(R.id.title_text);
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataList);
		listView.setAdapter(adapter);
		coolWeatherDB = CoolWeatherDB.getInstance(this);	//��ȡ���ݿ�ʵ������
		//ΪListView�еĸ�����ӵ���¼�
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (currentLevel == LEVEL_PROVINCE) {	//�����ǰ������������ʡ����������ʾ��ѡ���������ĳ���
					selectedProvince = provinceList.get(position);
					queryCities();
				} else if (currentLevel == LEVEL_CITY) {	//�����ǰ�������������м���������ʾ��ѡ�����������ؼ�����
					selectedCity = cityList.get(position);
					queryCounties();
				} else if (currentLevel == LEVEL_COUNTY) {
					String countyCode = countyList.get(position).getCountyCode();
					Intent intent = new Intent(ChooseAreaActivity.this, WeatherActivity.class);
					intent.putExtra("countyCode", countyCode);
					startActivity(intent);
					finish();
				}
			}
		});
		
		queryProvinces();	//��ʼʱ��������ʡ��
	}
	
	/**
	 * ��ѯʡ���б���ʾ��ListView��
	 */
	private void queryProvinces() {
		provinceList = coolWeatherDB.loadProvince();	//���ȴ����ݿ��в�ѯ
		if (provinceList.size() > 0) {	//���ݿ��������ݣ�������ݿ��ж�ȡ���ٶȸ���
			dataList.clear();
			for (Province province : provinceList) {
				dataList.add(province.getProvinceName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText("�й�");
			currentLevel = LEVEL_PROVINCE;
		} else {	//���ݿ���û�ж�Ӧ���ݣ���ӷ������ϲ�ѯ
			queryFromServer(null, "province");
		}
	}
	
	/**
	 * ����ʡ��id����ѯ��ʡ���г����б�
	 */
	private void queryCities() {
		cityList = coolWeatherDB.loadCity(selectedProvince.getId());
		if (cityList.size() > 0) {
			dataList.clear();
			for (City city : cityList) {
				dataList.add(city.getCityName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText(selectedProvince.getProvinceName());
			currentLevel = LEVEL_CITY;
		} else {
			queryFromServer(selectedProvince.getProvinceCode(), "city");
		}
	}
	
	/**
	 * ���ݳ���Id����ѯ�ó��а������ؼ������б�
	 */
	private void queryCounties() {
		countyList = coolWeatherDB.loadCounty(selectedCity.getId());
		if (countyList.size() > 0) {
			dataList.clear();
			for (County county : countyList) {
				dataList.add(county.getCountyName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText(selectedCity.getCityName());
			currentLevel = LEVEL_COUNTY;
		} else {
			queryFromServer(selectedCity.getCityCode(), "county");
		}
	}
	
	/**
	 * �ӷ������϶�ȡ������Ϣ
	 * @param code �ϼ��������
	 * @param type ��������
	 */
	private void queryFromServer(final String code, final String type) {
		String address = "";	//����API��ʽ
		if (!TextUtils.isEmpty(code)) {	//��ʾ����ʡ����������
			address = "http://www.weather.com.cn/data/list3/city" + code + ".xml";
		} else {	//��ʾ����ʡ�����µ�������
			address = "http://www.weather.com.cn/data/list3/city.xml";
		}
		
		//��ʾ���ȿ�
		showProgressDialog();
		//����URL������ʵ��HttpCallbackListener�ӿ�
		HttpUtil.sendHttpRequest(address, new HttpCallBackListener() {

			/**
			 * onFinish����ʵ�ֽ��������洢�����ݿ��У�������ʾ��Ӧ����
			 * ��ǰ���֪���÷����������߳��е��õ�
			 */
			@Override
			public void onFinish(String Response) {
				boolean result = false;
				if ("province".equals(type)) {
					result = Utility.handleProviencesResponse(coolWeatherDB, Response);
				} else if ("city".equals(type)) {
					result = Utility.handleCitiesResponse(coolWeatherDB, Response, selectedProvince.getId());
				} else if ("county".equals(type)) {
					result = Utility.handleCountiesResponse(coolWeatherDB, Response, selectedCity.getId());
				}
				
				if (result) {
					runOnUiThread(new Runnable() {	//�ص����߳��У�ִ����ʾ����

						@Override
						public void run() {
							closeProgressDialog();
							if ("province".equals(type)) {
								queryProvinces();
							} else if ("city".equals(type)) {
								queryCities();
							} else if ("county".equals(type)) {
								queryCounties();
							}
						}
						
					});
				}
			}

			/**
			 * ���������ʱ�����쳣
			 * ��ʾ����ʧ��
			 */
			@Override
			public void onError(Exception e) {
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						closeProgressDialog();
						Toast.makeText(ChooseAreaActivity.this, "����ʧ��", Toast.LENGTH_SHORT).show();
					}
					
				});
			}
			
		});
	}
	
	/**
	 * ��ʾ���ȿ�
	 */
	private void showProgressDialog() {
		if (progressDialog == null) {
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("���ڼ���");
			progressDialog.setCancelable(false);
		}
		progressDialog.show();
	}
	
	/**
	 * �رս��ȿ�
	 */
	private void closeProgressDialog() {
		if (progressDialog != null) {
			progressDialog.dismiss();
		}
	}

	/**
	 * ��д���˷���������Back��ִ��
	 */
	@Override
	public void onBackPressed() {
		if (currentLevel == LEVEL_COUNTY) {
			queryCities();
		} else if (currentLevel == LEVEL_CITY) {
			queryProvinces();
		} else {
			finish();
		}
	}
}
