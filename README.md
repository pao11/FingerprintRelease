[![](https://jitpack.io/v/pao11/FingerprintRelease.svg)](https://jitpack.io/#pao11/FingerprintRelease)

# 概况


这是一个可拓展的Android指纹识别API兼容库，目前集成了以下API：

安卓API：最低支持安卓**6.0**系统 [(查看详细介绍)](https://developer.android.com/reference/android/support/v4/hardware/fingerprint/FingerprintManagerCompat.html)

三星SDK：最低支持安卓**4.2**系统 [(查看详细介绍)](http://developer.samsung.com/galaxy/pass#)

魅族SDK：最低支持安卓**5.1**系统 [(查看详细介绍)](http://open-wiki.flyme.cn/index.php?title=%E6%8C%87%E7%BA%B9%E8%AF%86%E5%88%ABAPI)

API调用优先级：安卓API > 三星SDK > 魅族SDK

**1. Gradle 添加引用**

    compile 'com.github.pao11:FingerprintRelease:v1.0.2'

**2. AndroidManifest 添加权限**

    <uses-permission android:name="android.permission.USE_FINGERPRINT"/>
    <uses-permission android:name="com.fingerprints.service.ACCESS_FINGERPRINT_MANAGER"/>
    <uses-permission android:name="com.samsung.android.providers.context.permission.WRITE_USE_APP_FEATURE_SURVEY"/>

**3. FingerprintIdentify 方法解释**

    mFingerprintIdentify = new FingerprintIdentify(this);                       // 构造对象
    mFingerprintIdentify = new FingerprintIdentify(this, exceptionListener);    // 构造对象，并监听错误回调（错误仅供开发使用）
    mFingerprintIdentify.isFingerprintEnable();                                 // 指纹硬件可用并已经录入指纹
    mFingerprintIdentify.isHardwareEnable();                                    // 指纹硬件是否可用
    mFingerprintIdentify.isRegisteredFingerprint();                             // 是否已经录入指纹
    mFingerprintIdentify.startIdentify(maxTimes, listener);                     // 开始验证指纹识别
    mFingerprintIdentify.cancelIdentify();                                      // 关闭指纹识别
    mFingerprintIdentify.resumeIdentify();                                      // 恢复指纹识别并保证错误次数不变

**4. startIdentify 方法解析**

    mFingerprintIdentify.startIdentify(3, new BaseFingerprint.FingerprintIdentifyListener() {
        @Override
        public void onSucceed() {
            // 验证成功，自动结束指纹识别
        }

        @Override
        public void onNotMatch(int availableTimes) {
            // 指纹不匹配，并返回可用剩余次数并自动继续验证
        }

        @Override
        public void onFailed(boolean isDeviceLocked) {
            // 错误次数达到上限或者API报错停止了验证，自动结束指纹识别
            // isDeviceLocked 表示指纹硬件是否被暂时锁定
        }

        @Override
        public void onStartFailedByDeviceLocked() {
            // 第一次调用startIdentify失败，因为设备被暂时锁定
        }
    });

**5. 混淆设置**

    # MeiZuFingerprint
    -keep class com.fingerprints.service.** { *; }
    
    # SmsungFingerprint
    -keep class com.samsung.android.sdk.** { *; }

**6. 更新记录**

**v1.0.2**　`2017.09.05`　集成三星、魅族SDK

**v1.0.1**　`2017.09.04`　发布第一版本，最低支持安卓6.0系统

## Copyright Notice ##
``` 
Copyright (C) 2016 pao11

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 ```
