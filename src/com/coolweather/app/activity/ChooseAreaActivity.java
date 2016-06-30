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
 * 加载地区的活动
 * @author Administrator
 *
 */
public class ChooseAreaActivity extends Activity {

	//地区级别，省市县三级
	public static final int LEVEL_PROVINCE = 0;
	public static final int LEVEL_CITY = 1;
	public static final int LEVEL_COUNTY = 2;
	
	//UI控件
	private ProgressDialog progressDialog;
	private TextView titleText;
	private ListView listView;
	private ArrayAdapter<String> adapter;
	private CoolWeatherDB coolWeatherDB;
	private List<String> dataList = new ArrayList<String>();
	
	//存储用于显示的各级地区列表
	private List<Province> provinceList;
	private List<City> cityList;
	private List<County> countyList;
	
	//已选的地区
	private Province selectedProvince;
	private City selectedCity;
	
	//当前所处地区的级别
	private int currentLevel;
	
	//标注是否从天气活动中转过来的
	private boolean isFromWeatherActivity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//得到isFromWeatherActivity的值，看当前活动是不是从天气活动中转过来的
		isFromWeatherActivity = this.getIntent().getBooleanExtra("from_weather_activity", false);
		
		//判断是否预先选择过地区
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		if (prefs.getBoolean("city_selected", false) && !isFromWeatherActivity) {	//如果有选择过且该活动不是由天气活动转过来的，则直接跳转到天气活动页面
			Intent intent = new Intent(this, WeatherActivity.class);
			this.startActivity(intent);
			this.finish();	//结束当前活动
			return;	//直接返回
		}
		
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.choose_area);
		listView = (ListView)this.findViewById(R.id.list_view);
		titleText = (TextView)this.findViewById(R.id.title_text);
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataList);
		listView.setAdapter(adapter);
		coolWeatherDB = CoolWeatherDB.getInstance(this);	//获取数据库实例对象
		//为ListView中的各项添加点击事件
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (currentLevel == LEVEL_PROVINCE) {	//如果当前所处的区域是省级，则点击显示该选项所包含的城市
					selectedProvince = provinceList.get(position);
					queryCities();
				} else if (currentLevel == LEVEL_CITY) {	//如果当前所处的区域是市级，则点击显示该选项所包含的县级区域
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
		
		queryProvinces();	//开始时加载所有省份
	}
	
	/**
	 * 查询省份列表，显示到ListView中
	 */
	private void queryProvinces() {
		provinceList = coolWeatherDB.loadProvince();	//首先从数据库中查询
		if (provinceList.size() > 0) {	//数据库中有数据，则从数据库中读取，速度更快
			dataList.clear();
			for (Province province : provinceList) {
				dataList.add(province.getProvinceName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText("中国");
			currentLevel = LEVEL_PROVINCE;
		} else {	//数据库中没有对应数据，则从服务器上查询
			queryFromServer(null, "province");
		}
	}
	
	/**
	 * 根据省份id，查询该省所有城市列表
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
	 * 根据城市Id，查询该城市包含的县级区域列表
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
	 * 从服务器上读取地区信息
	 * @param code 上级地区编号
	 * @param type 地区级别
	 */
	private void queryFromServer(final String code, final String type) {
		String address = "";	//请求API格式
		if (!TextUtils.isEmpty(code)) {	//表示请求省级区域数据
			address = "http://www.weather.com.cn/data/list3/city" + code + ".xml";
		} else {	//表示请求省级以下地区数据
			address = "http://www.weather.com.cn/data/list3/city.xml";
		}
		
		//显示进度框
		showProgressDialog();
		//请求URL，参数实现HttpCallbackListener接口
		HttpUtil.sendHttpRequest(address, new HttpCallBackListener() {

			/**
			 * onFinish方法实现将请求结果存储到数据库中，并且显示相应数据
			 * 有前面可知，该方法是在子线程中调用的
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
					runOnUiThread(new Runnable() {	//回到主线程中，执行显示功能

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
			 * 请求服务器时出现异常
			 * 显示加载失败
			 */
			@Override
			public void onError(Exception e) {
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						closeProgressDialog();
						Toast.makeText(ChooseAreaActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
					}
					
				});
			}
			
		});
	}
	
	/**
	 * 显示进度框
	 */
	private void showProgressDialog() {
		if (progressDialog == null) {
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("正在加载");
			progressDialog.setCancelable(false);
		}
		progressDialog.show();
	}
	
	/**
	 * 关闭进度框
	 */
	private void closeProgressDialog() {
		if (progressDialog != null) {
			progressDialog.dismiss();
		}
	}

	/**
	 * 重写后退方法，按下Back键执行
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
