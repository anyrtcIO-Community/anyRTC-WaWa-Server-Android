package org.anyrtc.wawacapture;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.anyrtc.anyrtcwawaserver.AnyRTCWaWaServer;
import org.anyrtc.anyrtcwawaserver.AnyWaWaServerListener;
import org.anyrtc.rtcp_kit.AnyRTCRTCPEngine;
import org.anyrtc.rtcp_kit.AnyRTCRTCPEvent;
import org.anyrtc.rtcp_kit.RtcpKit;
import org.anyrtc.wawacapture.rtcp.RTCVideoView;
import org.anyrtc.wawacapture.rtcp.RtcpCore;
import org.anyrtc.wawacapture.utils.ScreenUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android_serialport_api.SerialPort;

public class WaWaActivity extends BaseActivity implements View.OnClickListener, AnyWaWaServerListener {
    TextView tvClose, tvWaWaNum, tvPeopleNum;
    RecyclerView rvDo;
    RelativeLayout rlVideo;
    View space;
    ActionAdapter actionAdapter;
    List<ActionBean> list = new ArrayList<>();
    private RtcpKit rtcpKit;
    private RTCVideoView rtcVideoView;
    private int anyRTCID;
    private String liveInfo = "";
    private SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
    Object object = new Object();
    SerialPort mSerialPort;
    OutputStream mOutputStream;
    InputStream mInputStream;
    ReadThread mReadThread;
    private boolean hadTurn = false;

    public int getLayoutId() {
        return R.layout.activity_wa_wa;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); //保持屏幕常亮
        super.onCreate(savedInstanceState);
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        tvClose = (TextView) findViewById(R.id.tv_close);
        tvPeopleNum = (TextView) findViewById(R.id.tv_people_num);
        tvWaWaNum = (TextView) findViewById(R.id.tv_wawa_num);
        rvDo = (RecyclerView) findViewById(R.id.rv_do);
        rvDo.setLayoutManager(new LinearLayoutManager(this));
        space = findViewById(R.id.view_space);
        rlVideo = (RelativeLayout) findViewById(R.id.rl_video);
        tvClose.setOnClickListener(this);
        mImmersionBar.titleBar(space).init();
        actionAdapter = new ActionAdapter();
        AnyRTCWaWaServer.getInstance().setServerListener(this);
        rtcpKit = RtcpCore.Inst().getmRtcpKit();
        rtcpKit.setAudioEnable(false);
        rtcpKit.setRtcpEvent(anyRTCRTCPEvent);
        rtcVideoView = new RTCVideoView(rlVideo, this, AnyRTCRTCPEngine.Inst().Egl());
        rtcpKit.setLocalVideoCapturer(rtcVideoView.OnRtcOpenLocalRender().GetRenderPointer());
        anyRTCID = (int) ((Math.random() * 9 + 1) * 100000);
//        anyRTCID = 888888;
        rtcpKit.publish(anyRTCID + "", true);
        rvDo.setAdapter(actionAdapter);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.tv_close) {
            if (rtcpKit != null) {
                rtcpKit.unPublish();
                AnyRTCWaWaServer.getInstance().closeServer();
                finish();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        rtcVideoView.OnRtcRemoveLocalRender();//移除视频图像
        rtcpKit.stop();//停止采集
    }


    public void intiSocketAndSerial() {
        try {
            mSerialPort = new SerialPort(new File("/dev/ttyS1"), 115200, 0);
        } catch (IOException e) {
            e.printStackTrace();
        }
//        Arrays.fill(mBuffer, (bEyte) 0x55);
        if (mSerialPort != null) {
            Log.d("not null ", " ######################~~");
            mOutputStream = mSerialPort.getOutputStream();
            mInputStream = mSerialPort.getInputStream();
            mReadThread = new ReadThread();
            mReadThread.start();
        } else
            Log.d("!!!!!!!!is null ", " ######################~~");


    }

    //开始
    byte[] mbegin = {(byte) 0xfe, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0xff, (byte) 0xff, (byte) 0x10, (byte) 0x31, (byte) 0x1e, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x5f};
    //向前
    byte[] mfront = {(byte) 0xfe, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0xff, (byte) 0xff, (byte) 0x0c, (byte) 0x32, (byte) 0x00, (byte) 0x2c, (byte) 0x01, (byte) 0x07};
    //向后
    byte[] mback = {(byte) 0xfe, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0xff, (byte) 0xff, (byte) 0x0c, (byte) 0x32, (byte) 0x01, (byte) 0x2c, (byte) 0x01, (byte) 0x08};

    //向左
    byte[] mleft = {(byte) 0xfe, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0xff, (byte) 0xff, (byte) 0x0c, (byte) 0x32, (byte) 0x02, (byte) 0x2c, (byte) 0x01, (byte) 0x09};
    //向右
    byte[] mright = {(byte) 0xfe, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0xff, (byte) 0xff, (byte) 0x0c, (byte) 0x32, (byte) 0x03, (byte) 0x2c, (byte) 0x01, (byte) 0x0a};

    //下抓
    byte[] mdown = {(byte) 0xfe, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0xff, (byte) 0xff, (byte) 0x0c, (byte) 0x32, (byte) 0x04, (byte) 0x00, (byte) 0x00, (byte) 0x42};

    //电机停止
    byte[] machineStop = {(byte) 0xfe, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0xff, (byte) 0xff, (byte) 0x0c, (byte) 0x32, (byte) 0x05, (byte) 0x00, (byte) 0x00, (byte) 0x43};

    //故障查询
    byte[] check = {(byte) 0xfe, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0xff, (byte) 0xff, (byte) 0x09, (byte) 0x34, (byte) 0x3d};


    void sendDataToSerial(int i) {
        try {
            switch (i) {
                case 0:
                    mOutputStream.write(check);
                    mOutputStream.write(mbegin);//开局
                    break;
                case 1:
                    mOutputStream.write(mfront);
                    break;
                case 2:
                    mOutputStream.write(mback);
                    break;
                case 3:
                    mOutputStream.write(mleft);
                    break;
                case 4:
                    mOutputStream.write(mright);
                    break;
                case 5:
                    mOutputStream.write(mdown);
                    break;
                default:
                    break;
            }
            mOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //    串口的读
    private class ReadThread extends Thread {
        @Override
        public void run() {
            super.run();
            while (!isInterrupted()) {
                int size = 0;
                try {
                    byte[] buffer = new byte[128];
                    for (int k = 0; k < 128; k++)
                        buffer[k] = 0;

                    if (mInputStream == null) {
                        return;
                    }
                    size = mInputStream.read(buffer);

                    if (size > 0) {
                        Log.i("wawaji", "com_recv size" + size);
                        onDataReceived(buffer, size);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
    }


    //------------------------RTCP 服务-----------------------------------------------------
    private AnyRTCRTCPEvent anyRTCRTCPEvent = new AnyRTCRTCPEvent() {
        /**
         * 发布成功
         * @param strRtcpId 发布媒体id
         */
        @Override
        public void onPublishOK(final String strRtcpId, final String strLiveInfo) {
            WaWaActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d("RTCP", "OnPublishOK:" + strRtcpId + "liveInfo:" + strLiveInfo);
                    liveInfo = strLiveInfo;
                    tvWaWaNum.setText("娃娃机" + anyRTCID);
                    AnyRTCWaWaServer.getInstance().openServer();
                    addData("发布视频流成功");
                    intiSocketAndSerial();

                }
            });
        }

        /**
         * 发布媒体失败
         * @param nCode 状态码
         */
        @Override
        public void onPublishFailed(final int nCode) {
            WaWaActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d("RTCP", "OnPublishFailed:code=" + nCode);
                    if (actionAdapter != null && rvDo != null) {
                        addData("发布视频流失败 code=" + nCode);
                    }
                }
            });
        }

        /**
         * 订阅媒体成功
         * @param strRtcpId 订阅的媒体的id
         */
        @Override
        public void onSubscribeOK(final String strRtcpId) {
            WaWaActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d("RTCP", "OnSubscribeOK:" + strRtcpId);
                }
            });
        }

        /**
         * 订阅失败
         * @param strRtcpId 订阅的媒体的id
         * @param nCode 状态码
         */
        @Override
        public void onSubscribeFailed(final String strRtcpId, final int nCode) {
            WaWaActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d("RTCP", "OnSubscribeFailed:" + strRtcpId);

                }
            });
        }

        /**
         * 订阅的媒体视频即将显示
         * @param strLivePeerId 订阅的媒体的视频像id
         */
        @Override
        public void onRTCOpenVideoRender(final String strLivePeerId) {
            WaWaActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d("RTCP", "OnRTCOpenVideoRender:" + strLivePeerId);
                }
            });
        }

        /**
         * 订阅的媒体视频关闭
         * @param strLivePeerId 订阅的媒体的视频像id
         */

        @Override
        public void onRTCCloseVideoRender(final String strLivePeerId) {
            WaWaActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d("RTCP", "OnRTCCloseVideoRender:" + strLivePeerId);
                }
            });
        }
    };

    //---------------------------------socket 服务-------------------------------------------------------------//
    @Override
    public void onConnectServerSuccess() {
        WaWaActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                addData("连接服务器成功");
            }
        });
    }

    @Override
    public void onDisconnect() {
        WaWaActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                addData("与服务器断开连接");
            }
        });
    }

    @Override
    public void onReconnect() {
        WaWaActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                addData("服务正在重连...");
            }
        });
    }

    @Override
    public void onConnAnyRTCServerSuccess() {
        WaWaActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!TextUtils.isEmpty(liveInfo)) {
                    AnyRTCWaWaServer.getInstance().joinAnyRTC(anyRTCID + "", "anyRTC娃娃机", ScreenUtils.getUniquePsuedoID(), liveInfo);
                }else {
                    addData("未能获取到视频流信息");
                }
                addData("初始化anyRTC信息成功");
            }
        });
    }

    @Override
    public void onConnAnyRTCFaild() {
        WaWaActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                addData("初始化anyRTC信息失败");
            }
        });
    }


    @Override
    public void onJoinRoomResult(final int data) {
        WaWaActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                addData(data == 0 ? "加入房间成功" : "加入房间失败");
            }
        });
    }


    @Override
    public void onTransformCamera() {
        WaWaActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (rtcpKit != null) {
                   new Handler().postDelayed(new Runnable() {
                       @Override
                       public void run() {
                           rtcpKit.switchCamera();
                           hadTurn = !hadTurn;
                       }
                   },500);

                }
            }
        });
    }

    @Override
    public void onTurnLeft() {
        if (hadTurn) {
            sendDataToSerial(1);
        } else {
            sendDataToSerial(3);
        }
    }

    @Override
    public void onTurnRight() {
        if (hadTurn) {
            sendDataToSerial(2);
        } else {
            sendDataToSerial(4);
        }

    }

    @Override
    public void onTurnForward() {
        if (hadTurn) {
            sendDataToSerial(3);
        } else {
            sendDataToSerial(2);
        }
    }

    @Override
    public void onTurnBackWard() {
        WaWaActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (hadTurn) {
                    sendDataToSerial(4);
                } else {
                    sendDataToSerial(1);
                }
            }
        });
    }

    @Override
    public void onBegin() {
        WaWaActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                sendDataToSerial(0);
            }
        });
    }

    @Override
    public void onRrab() {
        WaWaActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                sendDataToSerial(5);
            }
        });

    }

    @Override
    public void onSendResultSuccess() {
        WaWaActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                addData("发送结果成功");
            }
        });
    }

    @Override
    public void onSendResultFaild() {
        WaWaActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                addData("发送结果失败");
            }
        });
    }


    public void addData(String content) {
        if (!TextUtils.isEmpty(content) && actionAdapter != null && rvDo != null) {
            actionAdapter.addData(new ActionBean(sdf.format(new Date(System.currentTimeMillis())), content));
            rvDo.scrollToPosition(actionAdapter.getItemCount() - 1);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (rtcpKit != null) {
                rtcpKit.unPublish();
                AnyRTCWaWaServer.getInstance().closeServer();
                Destroy();
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    public static String bytes2HexString(byte[] b, int len) {
        String ret = "";
        for (int i = 0; i < len; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            ret += hex.toUpperCase();
        }
        return ret;
    }


    String readBuffer = "";

    protected void onDataReceived(byte[] buffer, int size) {
        Log.e("wawaji", String.valueOf(buffer) + " ##### " + bytes2HexString(buffer, size) + " *** " + readBuffer);
        readBuffer = readBuffer + bytes2HexString(buffer, size);

        //开头可能就不正确
        if (readBuffer.contains("FE")) {
            readBuffer = readBuffer.substring(readBuffer.indexOf("FE"));
        } else {
            readBuffer = "";
            Log.e("wawaji", "开头可能就不正确 readBuffer = kong ");
        }


        //指令 至少是9位 包长度在第 7位
        while (readBuffer.length() > 9 * 2) {
            String slen = readBuffer.substring(12, 14);
            int len = Integer.parseInt(slen, 16);

            //包长度最大50
            if (len > 50) {
                //包长度出错 应该是数据干扰
                Log.e("wawaji", "包长度出错");
                //丢弃这条指令
                readBuffer = readBuffer.substring(0, 2);
                if (readBuffer.contains("FE")) {
                    readBuffer = readBuffer.substring(readBuffer.indexOf("FE"));
                } else {
                    readBuffer = "";
                    Log.e("wawaji", "包长度出错 readBuffer = kong ");
                }

                continue;
            }

            if (readBuffer.length() >= len * 2) {
                String sBegin = readBuffer.substring(0, 2);
                Log.e("wawaji", "sBegin ******" + sBegin);
                if (sBegin.equals("FE")) {
                    //开头正确
                    String msgContent = readBuffer.substring(0, len * 2);
                    Log.e("wawaji", "开头正确" + msgContent);
                    //校验指令
                    if (check_com_data_string(msgContent, len * 2)) {
                        Log.e("wawaji", "指令正确" + msgContent);
                        readBuffer = readBuffer.substring(len * 2);
                        Log.e("wawaji", "发送指令" + msgContent);
                        //指令正确
                        if (msgContent.length() >= 18) {
                            String type = msgContent.substring(14, 16);
                            if (type.equals("33")) {
                                String result = msgContent.substring(16, 18);
                                if (result.equals("01")) {

                                    Log.d("wawaji", "结果抓到娃娃");
                                    AnyRTCWaWaServer.getInstance().sendResult(true);
                                } else {
                                    Log.d("wawaji", "结果没抓到娃娃");
                                    AnyRTCWaWaServer.getInstance().sendResult(false);
                                }
                            }
                        }
                    } else {
                        //指令不正确
                        Log.e("wawaji", "指令不正确" + msgContent + "***" + readBuffer);
                        readBuffer = readBuffer.substring(2);
                        if (readBuffer.contains("FE")) {
                            readBuffer = readBuffer.substring(readBuffer.indexOf("FE"));
                        } else {
                            readBuffer = "";
                            Log.e("wawaji", "指令不正确 不包含FE readBuffer = kong");
                        }
                    }
                } else {
                    //开头不正确
                    Log.e("wawaji", "开头不正确" + readBuffer);
                    if (readBuffer.contains("FE")) {
                        readBuffer = readBuffer.substring(readBuffer.indexOf("FE"));
                    } else {
                        readBuffer = "";
                        Log.e("wawaji", "开头不正确 不包含FE readBuffer = kong");
                    }
                }
            } else {
                //等下一次接
                Log.e("wawaji", "数据不够等待");
                break;
            }
        }
    }


    public void Destroy() {
        if (mReadThread != null) {
            mReadThread.interrupt();
        }

        if (mSerialPort != null) {
            mSerialPort.close();
            mSerialPort = null;
        }
        AnyRTCWaWaServer.getInstance().closeServer();
    }

    boolean check_com_data_string(String data, int len) {
        if (len < 12) return false;
        int check_total = 0;
        //check sum
        for (int i = 6 * 2; i < len - 2; i = i + 2) {
            check_total += Integer.parseInt(data.substring(i, i + 2), 16);
        }
        if (check_total % 100 != Integer.parseInt(data.substring(len - 2, len), 16))
            return false;

        if (Integer.parseInt(data.substring(0, 2), 16) + Integer.parseInt(data.substring(6, 8), 16) != 255)
            return false;

        if (Integer.parseInt(data.substring(2, 4), 16) + Integer.parseInt(data.substring(8, 10), 16) != 255)
            return false;

        if (Integer.parseInt(data.substring(4, 6), 16) + Integer.parseInt(data.substring(10, 12), 16) != 255)
            return false;

        return true;
    }


}
