package org.anyrtc.wawacapture;

import android.app.Application;

import org.anyrtc.anyrtcwawaserver.AnyRTCWaWaServer;
import org.anyrtc.rtcp_kit.AnyRTCRTCPEngine;
import org.anyrtc.wawacapture.utils.SharePrefUtil;

/**
 * Created by liuxiaozhong on 2017/11/29.
 */

public class AnyWawaApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        SharePrefUtil.init(getApplicationContext());
        AnyRTCRTCPEngine.Inst().initEngineWithAnyrtcInfo(getApplicationContext(),"", "", "", "");
        AnyRTCWaWaServer.getInstance().initEngineWithAnyRTCInfo("","","","");



    }
}
