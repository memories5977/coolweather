package com.coolweather.app.util;

/**
 * �ص��ӿ�
 * @author Administrator
 *
 */
public interface HttpCallBackListener {

	void onFinish(String Response);
	
	void onError(Exception e);
}
