package org.anyrtc.wawacapture;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;

import org.anyrtc.wawacapture.rtcp.RtcpCore;
import org.anyrtc.wawacapture.utils.HttpUtils;
import org.anyrtc.wawacapture.utils.SharePrefUtil;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.regex.Pattern;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    Button btnStart;
    TextView call, sendCode, user;
    EditText phone, code;
    RelativeLayout rl_code;
    private TimeCount mTimeCount;
    private String strphone = "";
    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        mImmersionBar.keyboardMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN).init();
        call = (TextView) findViewById(R.id.tv_call);
        sendCode = (TextView) findViewById(R.id.tv_send);
        phone = (EditText) findViewById(R.id.et_phone);
        code = (EditText) findViewById(R.id.et_code);
        user = (TextView) findViewById(R.id.tv_user_phone);
        rl_code = (RelativeLayout) findViewById(R.id.rl_code);
        call.setOnClickListener(this);
        btnStart = (Button) findViewById(R.id.btn_Start);
        btnStart.setOnClickListener(this);
        sendCode.setOnClickListener(this);
        SharePrefUtil.putString("phone","8888");
        strphone = SharePrefUtil.getString("phone");
//        if (TextUtils.isEmpty(strphone)) {
//            phone.setVisibility(View.VISIBLE);
//            rl_code.setVisibility(View.VISIBLE);
//        } else {
//            user.setText("用户：" + strphone);
//            user.setVisibility(View.VISIBLE);
//        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            AndPermission.with(MainActivity.this)
                    .requestCode(0)
                    .permission(Manifest.permission.CAMERA,
                            Manifest.permission.RECORD_AUDIO)
                    .callback(new PermissionListener() {
                        @Override
                        public void onSucceed(int requestCode, @NonNull List<String> grantPermissions) {
                            startAnimActivity(WaWaActivity.class);
                        }

                        @Override
                        public void onFailed(int requestCode, @NonNull List<String> deniedPermissions) {
                            Toast.makeText(MainActivity.this, "您未开启摄像头或录音权限", Toast.LENGTH_LONG).show();
                        }
                    }).start();
        } else {
            startAnimActivity(WaWaActivity.class);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_send:
                sendCode();
                break;
            case R.id.btn_Start:
                btnStart.setText("正在开启...");
                startAnimActivity(WaWaActivity.class);
//                if (!TextUtils.isEmpty(strphone)) {
//
//                } else {
//                    checkCode();
//                }
                break;
            case R.id.tv_call:
                Uri uri = Uri.parse("tel:021-65650071");
                Intent intent = new Intent(Intent.ACTION_DIAL, uri);
                startActivity(intent);
                break;

        }
    }

    public  boolean checkPhone(String phone) {
        String regex = "(\\+\\d+)?(\\d{3,4}\\-?)?\\d{7,8}$";
        return Pattern.matches(regex, phone);
    }

    public void sendCode() {
        if (TextUtils.isEmpty(phone.getText().toString())) {
            Toast.makeText(MainActivity.this, "手机号不能为空", Toast.LENGTH_LONG).show();
            return;
        }

        if (!checkPhone(phone.getText().toString())){
            Toast.makeText(MainActivity.this, "手机号格式错误", Toast.LENGTH_LONG).show();
            return;
        }


        HttpUtils httpUtils = HttpUtils.getHttpUtil("http://192.168.1.112:4664/smsapi/v1/send_vercode", "domain=sign_in&u_cellphone=" + phone.getText().toString()+"&product=wawaji", this, new HttpUtils.IHttpCallback() {
            @Override
            public void onResponse(String result) {
                Log.d("result", result);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    int code = jsonObject.getInt("code");
                    if (code == 200) {
                        Toast.makeText(MainActivity.this, "验证码发送成功", Toast.LENGTH_LONG).show();
                        mTimeCount = new TimeCount(60000, 1000);
                        mTimeCount.start();
                    } else {
                        Toast.makeText(MainActivity.this, "发送失败", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        httpUtils.httpPost();
    }

    private class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            sendCode.setText("发送验证码");
            sendCode.setClickable(true);
            mTimeCount.cancel();
        }

        @Override
        public void onTick(long millisUntilFinished) {
            sendCode.setText(millisUntilFinished / 1000 + "s");
            sendCode.setClickable(false);
        }
    }

    public void checkCode() {
        if (TextUtils.isEmpty(phone.getText().toString())) {
            Toast.makeText(MainActivity.this, "手机号不能为空", Toast.LENGTH_LONG).show();
            return;
        }
        if (!checkPhone(phone.getText().toString())){
            Toast.makeText(MainActivity.this, "手机号格式错误", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(code.getText().toString())) {
            Toast.makeText(MainActivity.this, "验证码不能为空", Toast.LENGTH_LONG).show();
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            AndPermission.with(MainActivity.this)
                    .requestCode(0)
                    .permission(Manifest.permission.CAMERA,
                            Manifest.permission.RECORD_AUDIO)
                    .callback(new PermissionListener() {
                        @Override
                        public void onSucceed(int requestCode, @NonNull List<String> grantPermissions) {
                            sendCodeToServer();
                        }

                        @Override
                        public void onFailed(int requestCode, @NonNull List<String> deniedPermissions) {
                            Toast.makeText(MainActivity.this, "您未开启摄像头或录音权限", Toast.LENGTH_LONG).show();
                        }
                    }).start();
        } else {
            sendCodeToServer();
        }

    }

    private void sendCodeToServer() {
        HttpUtils httpUtils = HttpUtils.getHttpUtil("http://192.168.1.112:4664/smsapi/v1/check_vercode", "domain=sign_in&vercode=" + code.getText().toString()
                + "&u_cellphone=" + phone.getText().toString()+"&product=wawaji", this, new HttpUtils.IHttpCallback() {
            @Override
            public void onResponse(String result) {
                Log.d("result", result);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    int code = jsonObject.getInt("code");
                    if (code == 200) {
                        strphone = phone.getText().toString();
                        SharePrefUtil.putString("phone", phone.getText().toString());
                        startAnimActivity(WaWaActivity.class);
                        btnStart.setText("正在开启...");
                    } else if (code == 212) {
                        Toast.makeText(MainActivity.this, "短信验证码不存在", Toast.LENGTH_LONG).show();
                    } else if (code == 213) {
                        Toast.makeText(MainActivity.this, "短信验证码不正确", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(MainActivity.this, "短信验证码错误！" + code, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        httpUtils.httpPost();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mTimeCount != null) {
            mTimeCount.cancel();
            mTimeCount = null;
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        btnStart.setText("开始发布");
        if (!TextUtils.isEmpty(strphone)) {
            user.setText("用户：" + strphone);
            user.setVisibility(View.VISIBLE);
            phone.setVisibility(View.INVISIBLE);
            rl_code.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            System.exit(0);
            RtcpCore.Inst().getmRtcpKit().clear();//程序退出时释放
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
    @Override
    protected void onStop() {
        super.onStop();
    }


}
