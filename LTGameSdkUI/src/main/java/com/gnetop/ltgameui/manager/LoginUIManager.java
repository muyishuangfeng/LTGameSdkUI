package com.gnetop.ltgameui.manager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.facebook.login.LoginManager;
import com.gnetop.ltgamecommon.base.Constants;
import com.gnetop.ltgamecommon.impl.OnAutoLoginCheckListener;
import com.gnetop.ltgamecommon.login.LoginBackManager;
import com.gnetop.ltgamecommon.model.BaseEntry;
import com.gnetop.ltgamecommon.model.ResultData;
import com.gnetop.ltgamecommon.util.PreferencesUtils;
import com.gnetop.ltgamegoogle.login.GoogleLoginManager;
import com.gnetop.ltgamegoogle.login.OnGoogleSignOutListener;
import com.gnetop.ltgameui.impl.OnReLoginInListener;
import com.gnetop.ltgameui.widget.activity.LoginActivity;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * 登录工具类
 */
public class LoginUIManager {


    private volatile static LoginUIManager sInstance;

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
    public void loginIn(final Activity activity, final String mAgreementUrl, final String mPrivacyUrl, final String googleClientID,
                        final String LTAppID, final String LTAppKey, final String adID, final String packageID, final OnReLoginInListener mListener) {
        if (isLoginStatus(activity)) {
            login(activity, mAgreementUrl, mPrivacyUrl, googleClientID, LTAppID, LTAppKey, adID,packageID);
        } else {
            Map<String, Object> params = new WeakHashMap<>();
            params.put("lt_uid", PreferencesUtils.getString(activity, Constants.USER_LT_UID));
            params.put("lt_uid_token", PreferencesUtils.getString(activity, Constants.USER_LT_UID_TOKEN));
            params.put("platform_id", packageID);
            LoginBackManager.autoLoginCheck(LTAppID, LTAppKey, params, new OnAutoLoginCheckListener() {
                @Override
                public void onCheckSuccess(BaseEntry result) {
                    if (result != null) {
                        if (result.getCode() == 200) {
                            ResultData resultData = new ResultData();
                            resultData.setLt_uid(PreferencesUtils.getString(activity, Constants.USER_LT_UID));
                            resultData.setLt_uid_token(PreferencesUtils.getString(activity, Constants.USER_LT_UID_TOKEN));
                            mListener.OnLoginResult(resultData);
                        } else {
                            loginOut(activity, mAgreementUrl, mPrivacyUrl, googleClientID, LTAppID, LTAppKey, adID,packageID);
                        }
                    }
                }

                @Override
                public void onCheckFailed(Throwable ex) {

                }
            });

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
                         final String LTAppID, final String LTAppKey, final String adID, final String mPackageID) {
        PreferencesUtils.remove(activity, Constants.USER_LT_UID);
        PreferencesUtils.remove(activity, Constants.USER_LT_UID_TOKEN);
        LoginManager.getInstance().logOut();
        GoogleLoginManager.GoogleSingOut(activity, googleClientID, new OnGoogleSignOutListener() {
            @Override
            public void onSignOutSuccess() {
                if (TextUtils.isEmpty(PreferencesUtils.getString(activity,
                        Constants.USER_LT_UID)) &&
                        TextUtils.isEmpty(PreferencesUtils.getString(activity,
                                Constants.USER_LT_UID_TOKEN))) {
                    login(activity, mAgreementUrl, mPrivacyUrl, googleClientID, LTAppID, LTAppKey, adID,mPackageID);
                }
            }
        });
    }

    /**
     * 是否登录成功
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
                       String LTAppID, String LTAppKey, String adID,String mPackageID) {
        Intent intent = new Intent(activity, LoginActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("mAgreementUrl", mAgreementUrl);
        bundle.putString("mPrivacyUrl", mPrivacyUrl);
        bundle.putString("googleClientID", googleClientID);
        bundle.putString("LTAppID", LTAppID);
        bundle.putString("LTAppKey", LTAppKey);
        bundle.putString("adID", adID);
        bundle.putString("mPackageID",mPackageID);
        intent.putExtra("bundleData", bundle);
        activity.startActivity(intent);
    }
}
