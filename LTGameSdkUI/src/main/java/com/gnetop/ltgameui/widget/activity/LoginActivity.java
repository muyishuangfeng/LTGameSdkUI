package com.gnetop.ltgameui.widget.activity;


import android.os.Bundle;
import android.text.TextUtils;

import com.gnetop.ltgamecommon.base.Constants;
import com.gnetop.ltgamecommon.model.BundleData;
import com.gnetop.ltgamecommon.util.PreferencesUtils;
import com.gnetop.ltgameui.R;
import com.gnetop.ltgameui.base.BaseAppActivity;
import com.gnetop.ltgameui.widget.fragment.AgreementFragment;
import com.gnetop.ltgameui.widget.fragment.LoginFragment;

public class LoginActivity extends BaseAppActivity {

    @Override
    protected int getViewId() {
        return R.layout.activity_login;
    }

    /**
     * 初始化控件
     */
    @Override
    protected void initView() {
        Bundle bundle = getIntent().getBundleExtra("bundleData");
        String mAgreementUrl = bundle.getString("mAgreementUrl");
        String mPrivacyUrl = bundle.getString("mPrivacyUrl");
        String googleClientID = bundle.getString("googleClientID");
        String LTAppID = bundle.getString("LTAppID");
        String LTAppKey = bundle.getString("LTAppKey");
        String mAdID = bundle.getString("adID");
        String mPackageID = bundle.getString("mPackageID");

        BundleData data = new BundleData();
        data.setAgreementUrl(mAgreementUrl);
        data.setPrivacyUrl(mPrivacyUrl);
        data.setGoogleClientID(googleClientID);
        data.setLTAppID(LTAppID);
        data.setLTAppKey(LTAppKey);
        data.setmAdID(mAdID);
        data.setmPackageID(mPackageID);

        if (!TextUtils.isEmpty(mAgreementUrl) &&
                !TextUtils.isEmpty(mPrivacyUrl)) {
            if (TextUtils.isEmpty(PreferencesUtils.getString(this,
                    Constants.USER_AGREEMENT_FLAT))) {
                if (findFragment(AgreementFragment.class) == null) {
                    addFragment(AgreementFragment.newInstance(data),
                            false,
                            true);
                }
            } else {
                    if (findFragment(LoginFragment.class) == null) {
                        addFragment(LoginFragment.newInstance(data),
                                false,
                                true);
                    }
            }

        }

    }

    @Override
    protected void initData() {
    }


}
