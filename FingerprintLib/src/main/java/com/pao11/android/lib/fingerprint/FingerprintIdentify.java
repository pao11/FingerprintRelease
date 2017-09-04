package com.pao11.android.lib.fingerprint;

import android.content.Context;

import com.pao11.android.lib.fingerprint.base.BaseFingerprint;
import com.pao11.android.lib.fingerprint.base.BaseFingerprint.FingerprintIdentifyExceptionListener;
import com.pao11.android.lib.fingerprint.impl.AndroidFingerprint;
import com.pao11.android.lib.fingerprint.impl.MeiZuFingerprint;
import com.pao11.android.lib.fingerprint.impl.SamsungFingerprint;

/**
 *
 * Created by pao11 on 2017/9/4.
 */
public class FingerprintIdentify {

    private BaseFingerprint mFingerprint;
    private BaseFingerprint mSubFingerprint;

    public FingerprintIdentify(Context context) {
        this(context, null);
    }

    public FingerprintIdentify(Context context, FingerprintIdentifyExceptionListener exceptionListener) {
        AndroidFingerprint androidFingerprint = new AndroidFingerprint(context, exceptionListener);
        if (androidFingerprint.isHardwareEnable()) {
            mSubFingerprint = androidFingerprint;
            if (androidFingerprint.isRegisteredFingerprint()) {
                mFingerprint = androidFingerprint;
                return;
            }
        }

        SamsungFingerprint samsungFingerprint = new SamsungFingerprint(context, exceptionListener);
        if (samsungFingerprint.isHardwareEnable()) {
            mSubFingerprint = samsungFingerprint;
            if (samsungFingerprint.isRegisteredFingerprint()) {
                mFingerprint = samsungFingerprint;
                return;
            }
        }

        MeiZuFingerprint meiZuFingerprint = new MeiZuFingerprint(context, exceptionListener);
        if (meiZuFingerprint.isHardwareEnable()) {
            mSubFingerprint = meiZuFingerprint;
            if (meiZuFingerprint.isRegisteredFingerprint()) {
                mFingerprint = meiZuFingerprint;
            }
        }
    }

    // DO
    public void startIdentify(int maxAvailableTimes, BaseFingerprint.FingerprintIdentifyListener listener) {
        if (!isFingerprintEnable()) {
            return;
        }

        mFingerprint.startIdentify(maxAvailableTimes, listener);
    }

    public void cancelIdentify() {
        if (mFingerprint != null) {
            mFingerprint.cancelIdentify();
        }
    }

    public void resumeIdentify() {
        if (!isFingerprintEnable()) {
            return;
        }

        mFingerprint.resumeIdentify();
    }

    // GET & SET
    public boolean isFingerprintEnable() {
        return mFingerprint != null && mFingerprint.isEnable();
    }

    public boolean isHardwareEnable() {
        return isFingerprintEnable() || (mSubFingerprint != null && mSubFingerprint.isHardwareEnable());
    }

    public boolean isRegisteredFingerprint() {
        return isFingerprintEnable() || (mSubFingerprint != null && mSubFingerprint.isRegisteredFingerprint());
    }
}