package com.coolweather.app.activity;

import com.coolweather.app.R;
import com.coolweather.app.util.HttpCallBackListener;
import com.coolweather.app.util.HttpUtil;
import com.coolweather.app.util.Utility;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 显示天气信息的活动
 * @author Administrator
 *
 */
public class WeatherActivity extends Activity {

	//声明各UI控件
	private LinearLayout weatherInfoLayout;
	
	private TextView cityNameText;
	
	private TextView publishText;
	
	private TextView weatherDispText;
	
	private TextView temp1Text;
	
	private TextView temp2Text;
	
	private TextView currentDateText;
	
	private Button switchCity;
	
	private Button refreshWeather;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.weather_layout);
		
		//初始化UI控件
		weatherInfoLayout = (LinearLayout)this.findViewById(R.id.weather_info_layout);
		cityNameText = (TextView)this.findViewById(R.id.city_name);
		publishText =(TextView)this.findViewById(R.id.publish_text);
		weatherDispText = (TextView)this.findViewById(R.id.weather_disp);
		temp1Text = (TextView)this.findViewById(R.id.temp1);
		temp2Text = (TextView)this.findViewById(R.id.temp2);
		currentDateText = (TextView)this.findViewById(R.id.current_data);
		
		//得到前一个活动传过来的县级代码
		String countyCode = this.getIntent().getStringExtra("countyCode");
		
		if (!TextUtils.isEmpty(countyCode)) {	//有已选的县级代码记录，说明刚刚从前一个活动跳转过来，则向服务器请求数据并显示
			publishText.setText("同步中...");
			weatherInfoLayout.setVisibility(View.INVISIBLE);
			cityNameText.setVisibility(View.INVISIBLE);
			queryWeatherCode(countyCode);
		} else {	//没有县级代码记录，说明之前选过了，sharedPreferences中有记录，则直接显示
			showWeather();
		}
	}
	
	/**
	 * 拼接URL，调用queryFromServer向服务器请求数据
	 * @param countyCode
	 */
	private void queryWeatherCode(String countyCode) {
		String address = "http://www.weather.com.cn/data/list3/city" + countyCode + ".xml";
		queryFromServer(address, "countyCode");
	}
	
	/**
	 * 拼接URL，请求服务器
	 * @param weatherCode
	 */
	private void queryWeatherInfo(String weatherCode) {
		String address = "http://www.weather.com.cn/data/cityinfo/" + weatherCode + ".html";
		queryFromServer(address, "weatherCode");
	}
	
	/**
	 * 回调模式，请求服务器
	 * @param address 请求的URL
	 * @param type 请求的数据类型
	 */
	private void queryFromServer(final String address, final String type) {
		HttpUtil.sendHttpRequest(address, new HttpCallBackListener() {

			@Override
			public void onFinish(String Response) {
				if ("countyCode".equals(type)) {	//请求地区的天气代码
					if (!TextUtils.isEmpty(Response)) {
						String[] array = Response.split("\\|");
						if (array != null && array.length == 2) {
							String weatherCode = array[1];
							queryWeatherInfo(weatherCode);	//得到天气代码之后，直接调用查询天气的方法
						}
					}
				} else if ("weatherCode".equals(type)) {	//请求天气情况
					Utility.handleWeatherResponse(WeatherActivity.this, Response);	//处理请求回的数据，存到SharedPreference中
					
					runOnUiThread(new Runnable() {	//回到主线程执行UI显示操作

						@Override
						public void run() {
							showWeather();
						}
						
					});
				}
			}

			@Override
			public void onError(Exception e) {
				runOnUiThread(new Runnable() {	//出错，回到主线程执行相应的Ui操作

					@Override
					public void run() {
						publishText.setText("同步失败");
					}
					
				});
			}
			
		});
	}
	
	/**
	 * 从SharedPreferences中读取天气信息，并进行UI显示
	 */
	private void showWeather() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		cityNameText.setText(prefs.getString("city_name", ""));
		publishText.setText("今天" + prefs.getString("publish_time", "") + "发布");
		weatherDispText.setText(prefs.getString("weather_disp", ""));
		temp1Text.setText(prefs.getString("temp1", ""));
		temp2Text.setText(prefs.getString("temp2", ""));
		currentDateText.setText(prefs.getString("current_date", ""));
		weatherInfoLayout.setVisibility(View.VISIBLE);
		cityNameText.setVisibility(View.VISIBLE);
	}
}
