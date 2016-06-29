package com.coolweather.app.util;

/**
 * 回调接口
 * @author Administrator
 *
 */
public interface HttpCallBackListener {

	void onFinish(String Response);
	
	void onError(Exception e);
}
