package com.pao11.android.lib.fingerprint.base;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

/**
 *
 * Created by pao11 on 2017/9/4.
 */
public abstract class BaseFingerprint {

    protected Context mContext;

    private Handler mHandler;
    private FingerprintIdentifyListener mIdentifyListener;
    private FingerprintIdentifyExceptionListener mExceptionListener;

    private int mNumberOfFailures = 0;                      // number of failures
    private int mMaxAvailableTimes = 3;                     // the most available times

    private boolean mIsHardwareEnable = false;              // if the phone equipped fingerprint hardware
    private boolean mIsRegisteredFingerprint = false;       // if the phone has any fingerprints

    private boolean mIsCalledStartIdentify = false;         // if started identify
    private boolean mIsCanceledIdentify = false;            // if canceled identify

    public BaseFingerprint(Context context, FingerprintIdentifyExceptionListener exceptionListener) {
        mContext = context;
        mExceptionListener = exceptionListener;
        mHandler = new Handler(Looper.getMainLooper());
    }

    // DO
    public void startIdentify(int maxAvailableTimes, FingerprintIdentifyListener listener) {
        mMaxAvailableTimes = maxAvailableTimes;
        mIsCalledStartIdentify = true;
        mIdentifyListener = listener;
        mIsCanceledIdentify = false;
        mNumberOfFailures = 0;

        doIdentify();
    }

    public void restartIdentify() {
        if (mIdentifyListener != null) {
            mIsCalledStartIdentify = true;
            mIsCanceledIdentify = false;
            mNumberOfFailures = 0;

            doIdentify();
        }
    }

    public void resumeIdentify() {
        if (mIsCalledStartIdentify && mIdentifyListener != null && mNumberOfFailures < mMaxAvailableTimes) {
            mIsCanceledIdentify = false;
            doIdentify();
        }
    }

    public void cancelIdentify() {
        mIsCanceledIdentify = true;
        doCancelIdentify();
    }

    // IMPL
    protected abstract void doIdentify();

    protected abstract void doCancelIdentify();

    // CALLBACK
    protected void onSucceed() {
        if (mIsCanceledIdentify) {
            return;
        }

        mNumberOfFailures = mMaxAvailableTimes;

        if (mIdentifyListener != null) {
            runOnUiThread(() -> mIdentifyListener.onSucceed());
        }

        cancelIdentify();
    }

    protected void onNotMatch() {
        if (mIsCanceledIdentify) {
            return;
        }

        if (++mNumberOfFailures < mMaxAvailableTimes) {
            if (mIdentifyListener != null) {
                final int chancesLeft = mMaxAvailableTimes - mNumberOfFailures;
                runOnUiThread(() -> mIdentifyListener.onNotMatch(chancesLeft));
            }

            if (needToCallDoIdentifyAgainAfterNotMatch()) {
                doIdentify();
            }

            return;
        }

        onFailed(-1000, "您已多次指纹匹配失败");
    }

    protected void onFailed(int errMsgId, CharSequence errString) {
        if (mIsCanceledIdentify) {
            return;
        }

        final boolean isStartFailedByDeviceLocked = errMsgId == 7 && mNumberOfFailures == 0;

        mNumberOfFailures = mMaxAvailableTimes;

        if (mIdentifyListener != null) {
            runOnUiThread(() -> {
                if (isStartFailedByDeviceLocked) {
                    mIdentifyListener.onStartFailedByDeviceLocked();
                } else {
                    mIdentifyListener.onFailed(errMsgId, errString);
                }
            });
        }

        cancelIdentify();
    }

    protected void onCatchException(Throwable exception) {
        if (mExceptionListener != null && exception != null) {
            mExceptionListener.onCatchException(exception);
        }
    }

    // GET & SET
    public boolean isEnable() {
        return mIsHardwareEnable && mIsRegisteredFingerprint;
    }

    public boolean isHardwareEnable() {
        return mIsHardwareEnable;
    }

    protected void setHardwareEnable(boolean hardwareEnable) {
        mIsHardwareEnable = hardwareEnable;
    }

    public boolean isRegisteredFingerprint() {
        return mIsRegisteredFingerprint;
    }

    protected void setRegisteredFingerprint(boolean registeredFingerprint) {
        mIsRegisteredFingerprint = registeredFingerprint;
    }

    // OTHER
    protected void runOnUiThread(Runnable runnable) {
        mHandler.post(runnable);
    }

    protected boolean needToCallDoIdentifyAgainAfterNotMatch() {
        return true;
    }

    public interface FingerprintIdentifyListener {
        void onSucceed();

        void onNotMatch(int availableTimes);

        void onFailed(int errMsgId, CharSequence errString);

        void onStartFailedByDeviceLocked();
    }

    public interface FingerprintIdentifyExceptionListener {
        void onCatchException(Throwable exception);
    }
}