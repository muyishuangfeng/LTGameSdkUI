package com.gnetop.ltgameui.manager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.gnetop.ltgamecommon.model.ResultData;
import com.gnetop.ltgamegoogle.login.GooglePlayLoginManager;
import com.gnetop.ltgamegoogle.login.OnGoogleSignOutListener;
import com.gnetop.ltgameui.base.Constants;
import com.gnetop.ltgameui.impl.OnReLoginInListener;
import com.gnetop.ltgameui.util.PreferencesUtils;
import com.gnetop.ltgameui.widget.activity.LoginActivity;

/**
 * 登录工具类
 */
public class LoginUIManager {


    private static LoginUIManager sInstance;

    private LoginUIManager() {
    }


    public static LoginUIManager getInstance() {
        if (sInstance == null) {
            synchronized (LoginUIManager.class) {
                if (sInstance == null) {
                    sInstance = new LoginUIManager();
                }
            }
        }
        return sInstance;
    }


    /**
     * 登录进入
     *
     * @param activity       上下文
     * @param mAgreementUrl  用户协议
     * @param mPrivacyUrl    隐私协议
     * @param googleClientID googleClientID
     * @param LTAppID        乐推AppID
     * @param LTAppKey       乐推AppKey
     * @param mListener      登录接口
     */
    public void loginIn(Activity activity, String mAgreementUrl, String mPrivacyUrl, String googleClientID,
                        String LTAppID, String LTAppKey, OnReLoginInListener mListener) {
        if (isLoginStatus(activity)) {
            login(activity, mAgreementUrl, mPrivacyUrl, googleClientID, LTAppID, LTAppKey);
        } else {
            ResultData resultData = new ResultData();
            resultData.setLt_uid(PreferencesUtils.getString(activity, Constants.USER_LT_UID));
            resultData.setApi_token(PreferencesUtils.getString(activity, Constants.USER_LT_UID_TOKEN));
            mListener.OnLoginResult(resultData);
        }
    }

    /**
     * 登出
     *
     * @param activity       上下文
     * @param mAgreementUrl  用户协议
     * @param mPrivacyUrl    隐私协议
     * @param googleClientID googleClientID
     * @param LTAppID        乐推AppID
     * @param LTAppKey       乐推AppKey
     */
    public void loginOut(final Activity activity, final String mAgreementUrl, final String mPrivacyUrl,
                         final String googleClientID,
                         final String LTAppID, final String LTAppKey) {
        PreferencesUtils.remove(activity, Constants.USER_LT_UID);
        PreferencesUtils.remove(activity, Constants.USER_LT_UID_TOKEN);
        GooglePlayLoginManager.GoogleSingOut(activity, googleClientID, new OnGoogleSignOutListener() {
            @Override
            public void onSignOutSuccess() {
                if (TextUtils.isEmpty(PreferencesUtils.getString(activity,
                        Constants.USER_LT_UID)) &&
                        TextUtils.isEmpty(PreferencesUtils.getString(activity,
                                Constants.USER_LT_UID_TOKEN))) {
                    login(activity, mAgreementUrl, mPrivacyUrl, googleClientID, LTAppID, LTAppKey);
                }
            }
        });
    }

    /**
     * 是否登录成功
     *
     * @param activity
     * @return
     */
    private boolean isLoginStatus(Activity activity) {
        return TextUtils.isEmpty(PreferencesUtils.getString(activity,
                Constants.USER_LT_UID)) &&
                TextUtils.isEmpty(PreferencesUtils.getString(activity,
                        Constants.USER_LT_UID_TOKEN));
    }

    /**
     * 登录方法
     *
     * @param activity       上下文
     * @param mAgreementUrl  用户协议
     * @param mPrivacyUrl    隐私协议
     * @param googleClientID googleClient
     * @param LTAppID        乐推AppID
     * @param LTAppKey       乐推AppKey
     */
    private void login(Activity activity, String mAgreementUrl, String mPrivacyUrl, String googleClientID,
                       String LTAppID, String LTAppKey) {
        Intent intent = new Intent(activity, LoginActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("mAgreementUrl", mAgreementUrl);
        bundle.putString("mPrivacyUrl", mPrivacyUrl);
        bundle.putString("googleClientID", googleClientID);
        bundle.putString("LTAppID", LTAppID);
        bundle.putString("LTAppKey", LTAppKey);
        intent.putExtra("bundleData", bundle);
        activity.startActivity(intent);
    }
}