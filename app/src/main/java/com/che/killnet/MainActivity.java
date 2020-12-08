package com.che.killnet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.che.killnet.callback.LoginCallBackListener;
import com.che.killnet.callback.WIFICallBackListener;
import com.che.killnet.javabean.DevicesInfoBean;
import com.google.android.material.snackbar.Snackbar;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static com.che.killnet.DeleteDevicesClass.DeleteDevices;
import static com.che.killnet.LoginClass.getPostBean;
import static com.che.killnet.LoginClass.login;
import static com.che.killnet.LoginClass.postBean;
import static com.che.killnet.RequestDevicesClass.devicesInfoBeanArrayList;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "testhttp";

    private EditText et_name;
    private Button btn_login;
    static LoginCallBackListener loginCallBackListener;
    static WIFICallBackListener wifiCallBackListener;

    @Override
    protected void onRestart() {
        super.onRestart();
        getPostBean();
        Log.d(TAG, "onRestart: 我回来啦");

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


        getPostBean();

        LoginCallBack();


        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LegalToCrack();
            }
        });
    }

    private void LegalToCrack() {
        if (!nameValidate()) {
            return;
        }

        postBean.setName(et_name.getText().toString());
        postBean.setPassword("000000");
        login();

    }

    private void initView() {
        et_name = findViewById(R.id.et_name);
        btn_login = findViewById(R.id.btn_login);

    }

    public boolean nameValidate() {
        boolean valid = true;
        String loginname = et_name.getText().toString();

        if (loginname.isEmpty()) {
            et_name.setError("ATTACK!!!");
            valid = false;
        } else {
            et_name.setError(null);
        }
        return valid;
    }

    private void showDeleteDevicesDialog(List<DevicesInfoBean> devicesInfoBeanArrayList) {
        final List<Integer> choice = new ArrayList<>();
        Log.d(TAG, "showDeleteDevicesDialog大小: " + devicesInfoBeanArrayList.size());
        String[] deviceslistDeviceType = new String[devicesInfoBeanArrayList.size()];



        if (devicesInfoBeanArrayList.size() == 0){
            Log.d(TAG, "showDeleteDevicesDialog: z找不到任何设备");
            showNoDevicesDialog();

        }
        else{


            for (int i = 0; i < devicesInfoBeanArrayList.size(); i++) {
                deviceslistDeviceType[i] = devicesInfoBeanArrayList.get(i).getDeviceType();
            }


            //默认都未选中
            boolean[] isSelect = {false, false};


            AlertDialog builder = new AlertDialog.Builder(this).setIcon(R.drawable.ic_hacker_black)
                    .setTitle("ATTACK DEVICES ")
                    .setMultiChoiceItems(deviceslistDeviceType, isSelect, new DialogInterface.OnMultiChoiceClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i, boolean b) {

                            if (b) {
                                choice.add(i);
                            } else {
                                choice.remove(choice.indexOf(i));
                            }

                        }
                    }).setPositiveButton("ATTACK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int whitch) {
                            StringBuilder str = new StringBuilder();

                            for (int i = 0; i < choice.size(); i++) {
                                Log.d(TAG, "onClick抛弃名字: " + devicesInfoBeanArrayList.get(choice.get(i)).getDeviceType() + "    ");
                                str.append(devicesInfoBeanArrayList.get(choice.get(i)).getDeviceType() + "    ");
                                DeleteDevices(devicesInfoBeanArrayList.get(choice.get(i)).getAcct_unique_id());
                            }
                            Log.d(TAG, "onClick: " + "你抛弃了" + str);

                        }
                    })
                    .show();;


            builder.getButton(AlertDialog.BUTTON_POSITIVE).setTextSize(20);

            builder.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getColor(R.color.black));
//        builder.create().show();

        }


    }

    private void LoginCallBack() {
        Log.d(TAG, "loginCallBack: 我在等登录消息的回调");

        loginCallBackListener = new LoginCallBackListener();
        loginCallBackListener.setmListener(new LoginCallBackListener.Listener() {
            @Override
            public void SendLoginMessage(Boolean b) {
                Message message = Message.obtain();
                Bundle bundle = new Bundle();
                bundle.putBoolean("IfDevices", b);
                bundle.putString("TYPE", "LoginCallBack");
                message.setData(bundle);
                handler.sendMessage(message);

            }
        });
    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            Bundle bundle = message.getData();
            String type = bundle.getString("TYPE");

            if ("LoginCallBack".equals(type)) {
                Boolean ifDevices = bundle.getBoolean("IfDevices");
                Log.d(TAG, "handleMessage: 收到的有无设备"+ifDevices);
                if (ifDevices) {
                    showDeleteDevicesDialog(devicesInfoBeanArrayList);
                } else{
                    Log.d(TAG, "handleMessage: 找不到设备");
                    showWrongNumberDialog();

                }


            }
            return false;
        }

    });


    private void showNoDevicesDialog() {

        AlertDialog builder = new AlertDialog.Builder(this,R.style.MyDialogTheme)
                .setTitle("ERROR")
                .setMessage("\n     NO DEVICES DETECTED")
                .setPositiveButton("OK", null)
                .show();

        builder.getButton(AlertDialog.BUTTON_POSITIVE).setTextSize(20);

        builder.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getColor(R.color.black));

    }

    private void showWrongNumberDialog() {

        AlertDialog builder = new AlertDialog.Builder(this,R.style.MyDialogTheme)
                .setTitle("ERROR")
                .setMessage("\n     WRONG NUMBER")
                .setPositiveButton("OK", null)
                .show();

        builder.getButton(AlertDialog.BUTTON_POSITIVE).setTextSize(20);

        builder.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getColor(R.color.black));

    }


}


