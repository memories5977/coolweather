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
 * ��ʾ������Ϣ�Ļ
 * @author Administrator
 *
 */
public class WeatherActivity extends Activity {

	//������UI�ؼ�
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
		
		//��ʼ��UI�ؼ�
		weatherInfoLayout = (LinearLayout)this.findViewById(R.id.weather_info_layout);
		cityNameText = (TextView)this.findViewById(R.id.city_name);
		publishText =(TextView)this.findViewById(R.id.publish_text);
		weatherDispText = (TextView)this.findViewById(R.id.weather_disp);
		temp1Text = (TextView)this.findViewById(R.id.temp1);
		temp2Text = (TextView)this.findViewById(R.id.temp2);
		currentDateText = (TextView)this.findViewById(R.id.current_data);
		
		//�õ�ǰһ������������ؼ�����
		String countyCode = this.getIntent().getStringExtra("countyCode");
		
		if (!TextUtils.isEmpty(countyCode)) {	//����ѡ���ؼ������¼��˵���ոմ�ǰһ�����ת����������������������ݲ���ʾ
			publishText.setText("ͬ����...");
			weatherInfoLayout.setVisibility(View.INVISIBLE);
			cityNameText.setVisibility(View.INVISIBLE);
			queryWeatherCode(countyCode);
		} else {	//û���ؼ������¼��˵��֮ǰѡ���ˣ�sharedPreferences���м�¼����ֱ����ʾ
			showWeather();
		}
	}
	
	/**
	 * ƴ��URL������queryFromServer���������������
	 * @param countyCode
	 */
	private void queryWeatherCode(String countyCode) {
		String address = "http://www.weather.com.cn/data/list3/city" + countyCode + ".xml";
		queryFromServer(address, "countyCode");
	}
	
	/**
	 * ƴ��URL�����������
	 * @param weatherCode
	 */
	private void queryWeatherInfo(String weatherCode) {
		String address = "http://www.weather.com.cn/data/cityinfo/" + weatherCode + ".html";
		queryFromServer(address, "weatherCode");
	}
	
	/**
	 * �ص�ģʽ�����������
	 * @param address �����URL
	 * @param type �������������
	 */
	private void queryFromServer(final String address, final String type) {
		HttpUtil.sendHttpRequest(address, new HttpCallBackListener() {

			@Override
			public void onFinish(String Response) {
				if ("countyCode".equals(type)) {	//�����������������
					if (!TextUtils.isEmpty(Response)) {
						String[] array = Response.split("\\|");
						if (array != null && array.length == 2) {
							String weatherCode = array[1];
							queryWeatherInfo(weatherCode);	//�õ���������֮��ֱ�ӵ��ò�ѯ�����ķ���
						}
					}
				} else if ("weatherCode".equals(type)) {	//�����������
					Utility.handleWeatherResponse(WeatherActivity.this, Response);	//��������ص����ݣ��浽SharedPreference��
					
					runOnUiThread(new Runnable() {	//�ص����߳�ִ��UI��ʾ����

						@Override
						public void run() {
							showWeather();
						}
						
					});
				}
			}

			@Override
			public void onError(Exception e) {
				runOnUiThread(new Runnable() {	//�����ص����߳�ִ����Ӧ��Ui����

					@Override
					public void run() {
						publishText.setText("ͬ��ʧ��");
					}
					
				});
			}
			
		});
	}
	
	/**
	 * ��SharedPreferences�ж�ȡ������Ϣ��������UI��ʾ
	 */
	private void showWeather() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		cityNameText.setText(prefs.getString("city_name", ""));
		publishText.setText("����" + prefs.getString("publish_time", "") + "����");
		weatherDispText.setText(prefs.getString("weather_disp", ""));
		temp1Text.setText(prefs.getString("temp1", ""));
		temp2Text.setText(prefs.getString("temp2", ""));
		currentDateText.setText(prefs.getString("current_date", ""));
		weatherInfoLayout.setVisibility(View.VISIBLE);
		cityNameText.setVisibility(View.VISIBLE);
	}
}
