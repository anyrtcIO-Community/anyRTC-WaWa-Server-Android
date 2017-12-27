package org.anyrtc.wawacapture.rtcp;

import android.text.TextUtils;

public class AnyRTCConfig {

    static AnyRTCConfig instance;

    String anyrtc_developerid="";

    String anyrtc_appid="";

    String anyrtc_appkey="";

    String anyrtc_apptoken="";

    String anyRTCId="";

    String boardId="";

    public boolean isInitSuccess = false;

    private AnyRTCConfig() {
    }

    public static AnyRTCConfig getInstance() {
        if (instance == null) {
            synchronized (AnyRTCConfig.class) {
                if (instance == null) {
                    instance = new AnyRTCConfig();
                }
            }
        }
        return instance;
    }

    public void setRoomInfo(String anyRTCId, String boardId){
        this.anyRTCId=anyRTCId;
        this.boardId=boardId;
    }
    public void initAnyRTCInfo(String strDeveloperId, String strAppId, String strAppKey, String strToken){
        this.anyrtc_developerid=strDeveloperId;
        this.anyrtc_appid=strAppId;
        this.anyrtc_appkey=strAppKey;
        this.anyrtc_apptoken=strToken;
    }

    public String getAnyrtc_developerid() {
        return anyrtc_developerid;
    }


    public String getAnyrtc_appid() {
        return anyrtc_appid;
    }


    public String getAnyrtc_appkey() {
        return anyrtc_appkey;
    }


    public String getAnyrtc_apptoken() {
        return anyrtc_apptoken;
    }


    public boolean isInitSuccess() {
        return isInitSuccess;
    }

    public void setInitSuccess(boolean initSuccess) {
        isInitSuccess = initSuccess;
    }

    public String getAnyRTCId() {
        return anyRTCId;
    }

    public String getBoardId() {
        return boardId;
    }

    public void setAnyRTCId(String anyRTCId) {
        this.anyRTCId = anyRTCId;
    }

    public void setBoardId(String boardId) {
        this.boardId = boardId;
    }

    public boolean AnyRTCInfoNotFull(){
        return TextUtils.isEmpty(anyrtc_apptoken)|| TextUtils.isEmpty(anyrtc_appkey)|| TextUtils.isEmpty(anyrtc_appid)|| TextUtils.isEmpty(anyrtc_developerid);
    }
}
