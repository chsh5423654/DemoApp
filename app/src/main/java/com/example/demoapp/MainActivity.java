package com.example.demoapp;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.UiThread;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.demoapp.domain.IMSInfo;
import com.example.demoapp.util.ImsiUtil;
import com.example.demoapp.util.MD5Util;
import com.example.demoapp.util.OkHttpUtil;
import com.example.demoapp.util.PrefUtils;
import com.example.demoapp.util.URLUtils;

/**
 * <!-- 下载App奖励流量专用测试AppID和AppCode --> <add key="AppRewardTestAppID"
 * value="1026"/> <add key="AppRewardTestAppKey"
 * value="20sdfhw8huwhfq29387rhwafeaksf432"/> <add key="AppRewardTestApp_Code"
 * value="78cab0"/>
 * 
 * @author chsh
 */
@SuppressWarnings("deprecation")
public class MainActivity extends Activity {

	@SuppressWarnings("unused")
	private static String Tag = "MainActivity";

	private SimpleDateFormat formatter;
	private Date curDate;

	private String imei;
	private String time;
	private EditText et_mobile;

	private String AppID = "1026"; // 数据从管理后台查看
	private String QMKey = "20sdfhw8huwhfq29387rhwafeaksf432"; // 数据从管理后台查看
	private String signKey;
	private boolean first_start;
	private MyDialog dialog;

	private EditText et_code;
	private String result_doPost;

	@SuppressLint("SimpleDateFormat")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_main);


		first_start = PrefUtils.getBoolean(this, "first_start", false);

		if (!first_start) {
			formatter = new SimpleDateFormat("yyyyMMddHHmmss");
			curDate = new Date(System.currentTimeMillis());
			time = formatter.format(curDate);

			ImsiUtil imsiUtil = new ImsiUtil(getApplicationContext());
			IMSInfo info = imsiUtil.getIMSInfo();
			imei = info.imei_1;

			View view = getLayoutInflater().inflate(R.layout.putcode_dailog,
					null);
			et_mobile = (EditText) view.findViewById(R.id.et_mobile);
			et_code = (EditText) view.findViewById(R.id.et_code);
			TextView buttonPositiveCode = (TextView) view
					.findViewById(R.id.button_positive_code);
			TextView buttonNegtiveCode = (TextView) view
					.findViewById(R.id.button_negtive_code);
			dialog = new MyDialog(MainActivity.this, 0, 0, view, R.style.dialog);

			// 请求流量
			buttonPositiveCode.setOnClickListener(new OnClickListener() {

				private ProgressDialog progressDialog;

				@Override
				public void onClick(View v) {

					progressDialog = ProgressDialog.show(MainActivity.this,
							null, "正在验证...", false);

					final String mobile = et_mobile.getText().toString().trim();
					final String etCode = et_code.getText().toString().trim();

					try {
						signKey = MD5Util.getMD5Entry(AppID + time + mobile
								+ QMKey);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					new Thread() {
						private String flow;
						private String flag;

						public void run() {
							String result = doPost(AppID, imei, time, mobile,
									etCode, signKey);
							try {
								JSONObject jsonObject = new JSONObject(result);
								flag = jsonObject.getString("flag");
								flow = jsonObject.getString("Flow");
							} catch (JSONException e) {
								e.printStackTrace();
							}
							if ("0".equals(flag)) { // 验证通过
								PrefUtils.putBoolean(MainActivity.this,
										"first_start", true);
								runOnUiThread(new Runnable() {
									@Override
									public void run() {

										if (dialog != null) {
											dialog.dismiss();
										}
										progressDialog.dismiss();
										View view = getLayoutInflater()
												.inflate(
														R.layout.get_red_packet,
														null);
										TextView tv_appName = (TextView) view
												.findViewById(R.id.tv_appName);
										TextView tv_zengsongliuliang = (TextView) view
												.findViewById(R.id.tv_zengsongliuliang);
										TextView tv_OK = (TextView) view
												.findViewById(R.id.tv_OK);
										final MyDialog dialog = new MyDialog(
												MainActivity.this, 0, 0, view,
												R.style.dialog);
										tv_appName.setText("应用名称");

										tv_zengsongliuliang.setText("送您" + flow
												+ "MB三网流量");
										tv_OK.setOnClickListener(new OnClickListener() {
											@Override
											public void onClick(View v) {
												dialog.dismiss();
											}
										});
										dialog.show();
									}
								});
							} else if ("-1".equals(flag) || "-3".equals(flag)) { // 验证码错误
								runOnUiThread(new Runnable() {

									@Override
									public void run() {
										progressDialog.dismiss();
										Toast.makeText(getApplicationContext(),
												"验证码错误", Toast.LENGTH_LONG)
												.show();
									}
								});
							} else {
								runOnUiThread(new Runnable() {

									@Override
									public void run() {
										progressDialog.dismiss();
										Toast.makeText(getApplicationContext(),
												"验证失败", Toast.LENGTH_LONG)
												.show();
									}
								});
							}
						};
					}.start();

				}
			});
			buttonNegtiveCode.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});
			dialog.show();
		}
	}
	private String doPost(String AppID, String IMEI, String Time,
			String Mobile, String Code, String SignKey) {
		// String action = "http://www.zp315.cn/AjaxService/Interface_AppFlow";
		String action = URLUtils.APPFLOW_SERVER;
		// String action = URLUtils.UPDATECARD_SERVER;
		/* 建立HttpPost连接 */
		HttpPost httpPost = new HttpPost(action);
		/* Post运作传送变数必须用NameValuePair[]阵列储存 */
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("AppID", AppID));
		params.add(new BasicNameValuePair("IMEI", IMEI));
		params.add(new BasicNameValuePair("Time", Time));
		params.add(new BasicNameValuePair("Mobile", Mobile));
		params.add(new BasicNameValuePair("Code", Code));
		params.add(new BasicNameValuePair("SignKey", SignKey));



		HttpResponse httpResponse = null;
		try {
			// 设置httpPost请求参数
			httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			httpResponse = new DefaultHttpClient().execute(httpPost);
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				result_doPost = EntityUtils.toString(httpResponse.getEntity());
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Log.i(Tag, "end url...");
		return result_doPost;
	}

}
