package com.gnetop.ltgameui.widget.fragment;

import android.os.Bundle;
import android.support.v7.widget.AppCompatCheckBox;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.gnetop.ltgamecommon.base.BaseResult;
import com.gnetop.ltgamecommon.model.BundleData;
import com.gnetop.ltgamecommon.model.Event;
import com.gnetop.ltgamecommon.util.EventUtils;
import com.gnetop.ltgameui.base.BaseFragment;
import com.gnetop.ltgamecommon.util.UrlUtils;
import com.gnetop.ltgameui.R;

import java.util.concurrent.atomic.DoubleAccumulator;


public class AgreementFragment extends BaseFragment implements View.OnClickListener,
        CompoundButton.OnCheckedChangeListener {

    TextView mTxtAgreement, mTxtPrivacy;
    Button mBtnInto;
    AppCompatCheckBox mCkbAgreement, mCkbPrivacy;
    boolean isAgreement = false;
    boolean isPrivacy = false;
    String mAgreementUrl;
    String mPrivacyUrl;


    public static AgreementFragment newInstance(BundleData data) {
        Bundle args = new Bundle();
        AgreementFragment fragment = new AgreementFragment();
        args.putSerializable(ARG_NUMBER, data);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getFragmentId() {
        return R.layout.fragment_agreement;
    }

    @Override
    protected void initView(View view) {
        isAgreement = false;
        isPrivacy = false;
        mTxtAgreement = view.findViewById(R.id.txt_agreement);
        mTxtPrivacy = view.findViewById(R.id.txt_privacy);

        mCkbAgreement = view.findViewById(R.id.ckb_agreement);
        mCkbAgreement.setOnCheckedChangeListener(this);

        mCkbPrivacy = view.findViewById(R.id.ckb_privacy);
        mCkbPrivacy.setOnCheckedChangeListener(this);


        mBtnInto = view.findViewById(R.id.btn_into_game);
        mBtnInto.setOnClickListener(this);
    }

    @Override
    public void lazyLoadData() {
        Bundle args = getArguments();
        if (args != null) {
            BundleData mData = (BundleData) args.getSerializable(ARG_NUMBER);
            if (mData != null) {
                mAgreementUrl = mData.getAgreementUrl();
                mPrivacyUrl = mData.getPrivacyUrl();
                Log.e("AgreementFragment",mPrivacyUrl+"===="+mAgreementUrl);
                initData(mAgreementUrl,mPrivacyUrl);
            }
        }

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_into_game) {
            if (isPrivacy && isAgreement) {
                EventUtils.sendEvent(new Event(BaseResult.MSG_RESULT_JUMP_INTO_GAME));
                getProxyActivity().finish();
            }
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.getId() == R.id.ckb_agreement) {
            isAgreement = isChecked;

        } else if (buttonView.getId() == R.id.ckb_privacy) {
            isPrivacy = isChecked;
        }
        if (isPrivacy && isAgreement) {
            mBtnInto.setBackgroundResource(R.drawable.btn_blue_corner);
        } else {
            mBtnInto.setBackgroundResource(R.drawable.btn_corner);
        }
    }

    /**
     * 初始化数据
     */
    private void initData(final String mAgreementUrl, final String mPrivacyUrl) {
        SpannableStringBuilder style = new SpannableStringBuilder();
        SpannableStringBuilder style2 = new SpannableStringBuilder();
        //设置文字
        String agreement=getResources().getString(R.string.text_agreement);
        String privacy=getResources().getString(R.string.text_privacy);
        style.append(agreement);
        style2.append(privacy);

        //设置部分文字点击事件
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                if (!TextUtils.isEmpty(mAgreementUrl)){
                    UrlUtils.getInstance().loadUrl(mActivity, mAgreementUrl);
                }
            }
        };
        //设置部分文字点击事件
        ClickableSpan clickableSpan2 = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                if (!TextUtils.isEmpty(mPrivacyUrl)){
                    UrlUtils.getInstance().loadUrl(mActivity, mPrivacyUrl);
                }
            }
        };
        style.setSpan(clickableSpan, 0,agreement.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        style2.setSpan(clickableSpan2, 0, privacy.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mTxtPrivacy.setText(style2);
        mTxtAgreement.setText(style);

        //设置部分文字颜色
        ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(getResources().getColor(R.color.colorBlue));
        ForegroundColorSpan foregroundColorSpan2 = new ForegroundColorSpan(getResources().getColor(R.color.colorBlue));
        style.setSpan(foregroundColorSpan, 0, agreement.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        style2.setSpan(foregroundColorSpan2, 0, privacy.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        //配置给TextView
        mTxtPrivacy.setMovementMethod(LinkMovementMethod.getInstance());
        mTxtPrivacy.setText(style2);
        //配置给TextView
        mTxtAgreement.setMovementMethod(LinkMovementMethod.getInstance());
        mTxtAgreement.setText(style);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isPrivacy = false;
        isAgreement = false;
    }



}
