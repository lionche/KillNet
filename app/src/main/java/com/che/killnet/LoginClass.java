package com.che.killnet;

import android.widget.EditText;

import com.che.killnet.javabean.LoginPostBean;

import static com.blankj.utilcode.util.DeviceUtils.getMacAddress;
import static com.blankj.utilcode.util.NetworkUtils.getIpAddressByWifi;

public class LoginClass {
    private EditText et_name;
    private EditText et_password;
    public static LoginPostBean postBean;


    public static void getPostBean(){
        postBean = new LoginPostBean();
        postBean.setMacadr(getMacAddress());
        postBean.setIpadr(getIpAddressByWifi());


    }
    public static void login(){
        LoginPostClass.LoginPost(postBean);
    }

}
