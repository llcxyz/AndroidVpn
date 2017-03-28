package com.yoopoon.cordova.plugin.alipay;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaArgs;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CordovaPreferences;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;

public class Alipay extends CordovaPlugin{

	private static final int SDK_PAY_FLAG = 1;
	private static final int SDK_CHECK_FLAG = 2;
	CallbackContext currentCallbackContext;

	@Override
	public boolean execute(String action, CordovaArgs args,
			CallbackContext callbackContext) throws JSONException {
		// save the current callback context
		currentCallbackContext = callbackContext;
		if (action.equals("pay")) {
			return pay(args);
		}
		return true;
	}

	@Override
	public void initialize(CordovaInterface cordova, CordovaWebView webView) {
		super.initialize(cordova, webView);

	}

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SDK_PAY_FLAG: {
				Result resultObj = new Result((String) msg.obj);
				String resultStatus = resultObj.resultStatus;

				currentCallbackContext.success(resultStatus);

//				// 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
//				if (TextUtils.equals(resultStatus, "9000")) {
//
//					Toast.makeText(cordova.getActivity(), "支付成功",
//							Toast.LENGTH_SHORT).show();
//				} else {
//					// 判断resultStatus 为非“9000”则代表可能支付失败
//					// “8000” 代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
//					if (TextUtils.equals(resultStatus, "8000")) {
//						Toast.makeText(cordova.getActivity(), "支付结果确认中",
//								Toast.LENGTH_SHORT).show();
//
//					} else {
//						Toast.makeText(cordova.getActivity(), "支付失败",
//								Toast.LENGTH_SHORT).show();
//
//					}
//				}
				break;
			}
			case SDK_CHECK_FLAG: {
				Toast.makeText(cordova.getActivity(), "检查结果为：" + msg.obj,
						Toast.LENGTH_SHORT).show();
				break;
			}
			default:
				break;
			}
		};
	};



	/**
	 * call alipay sdk pay. 调用SDK支付
	 *
	 */
	private  boolean pay(CordovaArgs args) {
		try {
			JSONObject orderInfoArgs =  args.getJSONObject(0);
      final String payInfo = orderInfoArgs.getString("order");

			Runnable payRunnable = new Runnable() {

				@Override
				public void run() {
					// 构造PayTask 对象
					PayTask alipay = new PayTask(cordova.getActivity());
					// 调用支付接口
					String result = alipay.pay(payInfo,true);

					Message msg = new Message();
					msg.what = SDK_PAY_FLAG;
					msg.obj = result;
					mHandler.sendMessage(msg);
				}
			};

			Thread payThread = new Thread(payRunnable);
			payThread.start();

		} catch (JSONException e1) {
			e1.printStackTrace();
			currentCallbackContext.error("订单参数不正确");
		}


		return true;
	}

	/**
	 * check whether the device has authentication alipay account.
	 * 查询终端设备是否存在支付宝认证账户
	 *
	 */
	public void check(View v) {
		Runnable checkRunnable = new Runnable() {

			@Override
			public void run() {
				PayTask payTask = new PayTask(cordova.getActivity());
				boolean isExist = true;

				Message msg = new Message();
				msg.what = SDK_CHECK_FLAG;
				msg.obj = isExist;
				mHandler.sendMessage(msg);
			}
		};

		Thread checkThread = new Thread(checkRunnable);
		checkThread.start();

	}

	/**
	 * get the sdk version. 获取SDK版本号
	 *
	 */
	public void getSDKVersion() {
		PayTask payTask = new PayTask(cordova.getActivity());
		String version = payTask.getVersion();
		Toast.makeText(cordova.getActivity(), version, Toast.LENGTH_SHORT).show();
	}


}
