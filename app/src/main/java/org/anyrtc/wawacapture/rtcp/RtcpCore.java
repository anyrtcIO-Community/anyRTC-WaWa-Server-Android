package org.anyrtc.wawacapture.rtcp;

import org.anyrtc.common.enums.AnyRTCCommonMediaType;
import org.anyrtc.common.enums.AnyRTCScreenOrientation;
import org.anyrtc.common.enums.AnyRTCVideoLayout;
import org.anyrtc.common.enums.AnyRTCVideoMode;
import org.anyrtc.rtcp_kit.AnyRTCRTCPEngine;
import org.anyrtc.rtcp_kit.AnyRTCRTCPOption;
import org.anyrtc.rtcp_kit.RtcpKit;

/**
 * Created by Skyline on 2017/6/14.
 */

public class RtcpCore {

    private static RtcpCore mInstance;

    public static RtcpCore Inst() {
        if(null == mInstance) {
            mInstance = new RtcpCore();
        }
        return mInstance;
    }



    private RtcpKit mRtcpKit;

    public RtcpKit getmRtcpKit() {
        //写成单例模式 全局持有一个rtcp对象
        if(null == mRtcpKit) {
            AnyRTCRTCPOption anyRTCRTCPOption = AnyRTCRTCPEngine.Inst().getAnyRTCRTCPOption();
            anyRTCRTCPOption.setOptionParams(true, AnyRTCScreenOrientation.AnyRTC_SCRN_Portrait, AnyRTCVideoMode.AnyRTC_Video_HD, AnyRTCVideoLayout.AnyRTC_V_3X3_auto, AnyRTCCommonMediaType.AnyRTC_M_Video);
             AnyRTCRTCPEngine.Inst().setAnyRTCRTCPOption(anyRTCRTCPOption);
            mRtcpKit = new RtcpKit();
        }

        return mRtcpKit;
    }
}
