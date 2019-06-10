package com.gnetop.ltgameui.widget.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.FacebookAuthorizationException;
import com.facebook.login.LoginManager;
import com.gnetop.ltgamecommon.base.BaseResult;
import com.gnetop.ltgamecommon.base.Constants;
import com.gnetop.ltgamecommon.impl.OnLoginSuccessListener;
import com.gnetop.ltgamecommon.model.BaseEntry;
import com.gnetop.ltgamecommon.model.BundleData;
import com.gnetop.ltgamecommon.model.Event;
import com.gnetop.ltgamecommon.model.ResultData;
import com.gnetop.ltgamecommon.util.EventUtils;
import com.gnetop.ltgamecommon.util.PreferencesUtils;
import com.gnetop.ltgamefacebook.FaceBookLoginManager;
import com.gnetop.ltgamegoogle.login.GoogleLoginManager;
import com.gnetop.ltgameui.R;
import com.gnetop.ltgameui.base.BaseFragment;
import com.gnetop.ltgameui.ui.dialog.GeneralDialogUtil;

public class LoginFragment extends BaseFragment implements View.OnClickListener {

    TextView mLytGoogle, mLytFaceBook;
    String mAgreementUrl;
    String mPrivacyUrl;
    String googleClientID;
    String LTAppID;
    String LTAppKey;
    String mAdID;
    String mPackageID;
    private static final int REQUEST_CODE = 0X01;

    public static LoginFragment newInstance(BundleData data) {
        Bundle args = new Bundle();
        LoginFragment fragment = new LoginFragment();
        args.putSerializable(ARG_NUMBER, data);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    protected int getFragmentId() {
        return R.layout.fragment_loign;
    }

    @Override
    protected void initView(View view) {
        mLytGoogle = view.findViewById(R.id.lyt_login_google);
        mLytGoogle.setOnClickListener(this);

        mLytFaceBook = view.findViewById(R.id.lyt_login_facebook);
        mLytFaceBook.setOnClickListener(this);
    }

    @Override
    public void lazyLoadData() {
        super.lazyLoadData();
        Bundle args = getArguments();
        if (args != null) {
            BundleData mData = (BundleData) args.getSerializable(ARG_NUMBER);
            if (mData != null) {
                mAgreementUrl = mData.getAgreementUrl();
                mPrivacyUrl = mData.getPrivacyUrl();
                googleClientID = mData.getGoogleClientID();
                LTAppID = mData.getLTAppID();
                LTAppKey = mData.getLTAppKey();
                mAdID = mData.getmAdID();
                mPackageID = mData.getmPackageID();
                Log.e("LoginFragment", mPrivacyUrl + "====" + mAgreementUrl
                        + "===" + "===" + LTAppKey + "===" + LTAppID + "==" + mAdID);
            }
        }
        initFaceBook();
    }

    @Override
    public void onClick(View view) {
        int resID = view.getId();
        if (resID == R.id.lyt_login_facebook) {//facebook
            FaceBookLoginManager.getInstance().faceBookLogin(mActivity);
        } else if (resID == R.id.lyt_login_google) {//google
            if (!TextUtils.isEmpty(googleClientID)) {
                GoogleLoginManager.initGoogle(mActivity,
                        googleClientID, REQUEST_CODE);
            }

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        FaceBookLoginManager.getInstance().setOnActivityResult(requestCode, resultCode, data);
        if (!TextUtils.isEmpty(LTAppID) &&
                !TextUtils.isEmpty(LTAppKey) && !TextUtils.isEmpty(mAdID)) {
            GoogleLoginManager.onActivityResult(requestCode, data, REQUEST_CODE, mActivity,
                    LTAppID, LTAppKey, mAdID,mPackageID,
                    new OnLoginSuccessListener() {
                        @Override
                        public void onSuccess(BaseEntry<ResultData> result) {
                            if (result != null) {
                                if (result.getCode() == 200) {
                                    if (result.getData().getApi_token() != null &&
                                            result.getData().getLt_uid() != null) {
                                        EventUtils.sendEvent(new Event<>(BaseResult.MSG_RESULT_GOOGLE_SUCCESS,
                                                result.getData()));
                                        PreferencesUtils.putString(mActivity, Constants.USER_API_TOKEN, result.getData().getApi_token());
                                        PreferencesUtils.putString(mActivity, Constants.USER_LT_UID, result.getData().getLt_uid());
                                        PreferencesUtils.putString(mActivity, Constants.USER_LT_UID_TOKEN, result.getData().getLt_uid_token());
                                        getProxyActivity().finish();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailed(Throwable ex) {
                            Log.e("google==", "onFailed" + ex.getMessage());
                            loginFailed();
                        }

                        @Override
                        public void onComplete() {
                            Log.e("google==", "onComplete");
                        }

                        @Override
                        public void onParameterError(String result) {
                            Log.e("google==", "nParameterError" + result);
                            loginFailed();
                        }

                        @Override
                        public void onError(String error) {
                            Log.e("google==", "onError" + error);
                            loginFailed();
                        }

                        @Override
                        public void onCodeResult(int code) {
                            GeneralDialogUtil.showActionDialog(mActivity,code);
                        }
                    });
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    /**
     * 登录失败
     */
    private void loginFailed() {
        if (findChildFragment(LoginFailedFragment.class) == null) {
            BundleData data = new BundleData();
            data.setAgreementUrl(mAgreementUrl);
            data.setPrivacyUrl(mPrivacyUrl);
            data.setLTAppKey(LTAppKey);
            data.setLTAppID(LTAppID);
            data.setGoogleClientID(googleClientID);
            data.setmAdID(mAdID);
            data.setmPackageID(mPackageID);
            getProxyActivity().addFragment(LoginFailedFragment.newInstance(data),
                    false,
                    true);
        }
    }

    /**
     * 登录失败
     */
    private void toAgreementUrl() {
        if (findChildFragment(AgreementFragment.class) == null) {
            BundleData data = new BundleData();
            data.setAgreementUrl(mAgreementUrl);
            data.setPrivacyUrl(mPrivacyUrl);
            data.setLTAppKey(LTAppKey);
            data.setLTAppID(LTAppID);
            data.setGoogleClientID(googleClientID);
            data.setmAdID(mAdID);
            data.setmPackageID(mPackageID);
            getProxyActivity().addFragment(AgreementFragment.newInstance(data),
                    false,
                    true);
        }
    }

    /**
     * 初始化facebook
     */
    private void initFaceBook() {
        if (!TextUtils.isEmpty(LTAppID) &&
                !TextUtils.isEmpty(LTAppKey) && !TextUtils.isEmpty(mAdID)) {

            FaceBookLoginManager.getInstance().initFaceBook(mActivity,
                    LTAppID, LTAppKey, mAdID,mPackageID,
                    new OnLoginSuccessListener() {
                        @Override
                        public void onSuccess(BaseEntry<ResultData> result) {
                            if (result != null) {
                                if (result.getCode() == 200) {
                                    if (result.getData().getApi_token() != null &&
                                            result.getData().getLt_uid() != null) {
                                        EventUtils.sendEvent(new Event<>(BaseResult.MSG_RESULT_FACEBOOK_SUCCESS,
                                                result.getData()));
                                        PreferencesUtils.putString(mActivity, Constants.USER_API_TOKEN, result.getData().getApi_token());
                                        PreferencesUtils.putString(mActivity, Constants.USER_LT_UID, result.getData().getLt_uid());
                                        PreferencesUtils.putString(mActivity, Constants.USER_LT_UID_TOKEN, result.getData().getLt_uid_token());
                                        getProxyActivity().finish();
                                    }

                                }
                            }
                        }

                        @Override
                        public void onFailed(Throwable ex) {
                            if (ex instanceof FacebookAuthorizationException) {
                                if (AccessToken.getCurrentAccessToken() != null) {
                                    LoginManager.getInstance().logOut();
                                }
                            }
                            Log.e("facebook", ex.getMessage());
                            loginFailed();
                        }

                        @Override
                        public void onComplete() {
                            Log.e("facebook", "===onComplete");
                        }

                        @Override
                        public void onParameterError(String result) {
                            Log.e("facebook", result);
                            loginFailed();
                        }

                        @Override
                        public void onError(String error) {
                            Log.e("facebook", error);
                            loginFailed();
                        }

                        @Override
                        public void onCodeResult(int code) {
                            GeneralDialogUtil.showActionDialog(mActivity,code);
                        }
                    });
        }
    }

}
