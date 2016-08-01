package com.hand_china.hrms.wxapi;

import com.google.gson.Gson;
import com.hand_china.hrms.TokenBean;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelbase.BaseResp.ErrCode;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

	private IWXAPI api;
	private TokenBean tokenBean;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		api = WXAPIFactory.createWXAPI(this, "wxaaeac85f01714b29");
		api.handleIntent(getIntent(), this);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
		api.handleIntent(getIntent(), this);
	}

	@Override
	public void onReq(BaseReq arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onResp(BaseResp resp) {
		if (resp instanceof SendAuth.Resp) {
			SendAuth.Resp newResp = (SendAuth.Resp) resp;
			switch (newResp.errCode) {
			case ErrCode.ERR_OK:// 用户同意
				// 获取微信传回的code
				String code = newResp.code;
				getToken(code);
				Log.e("399", code);
				break;
			case ErrCode.ERR_AUTH_DENIED:// 用户拒绝授权

				break;
			case ErrCode.ERR_USER_CANCEL:// 用户取消

				break;
			default:
				break;
			}

		}
	}

	/**
	 * 获取token
	 * 
	 * @param code
	 */
	private void getToken(String code) {
		String url = "https://api.weixin.qq.com/sns/oauth2/access_token";
		HttpUtils httpUtils = new HttpUtils();
		RequestParams params = new RequestParams();
		params.addBodyParameter("appid", "wxaaeac85f01714b29");
		params.addBodyParameter("secret", "44d3af96ad276e74c992380c71d20929");
		params.addBodyParameter("code", code);
		params.addBodyParameter("grant_type", "authorization_code");
		httpUtils.send(HttpMethod.POST, url, params, new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				Log.e("399", "失败");

			}

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				String result = responseInfo.result;
				Gson gson  = new Gson();
				tokenBean = gson.fromJson(result, TokenBean.class);
				Log.e("399", tokenBean.toString());
				getUserInfo(tokenBean);
			}
		});
	}
	
	private void getUserInfo(TokenBean tokenBean) {
		String url = "https://api.weixin.qq.com/sns/userinfo";
		HttpUtils httpUtils = new HttpUtils();
		RequestParams params = new RequestParams();
		params.addBodyParameter("access_token", tokenBean.access_token);
		params.addBodyParameter("openid", tokenBean.openid);
		httpUtils.send(HttpMethod.POST, url,params, new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				Log.e("399", "失败");
			}

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				Log.e("399", responseInfo.result);
				Intent intent = new Intent();
				intent.putExtra("result", responseInfo.result);
				setResult(1, intent);
				finish();
				overridePendingTransition(0, 0);
			}
		});
	}
}
