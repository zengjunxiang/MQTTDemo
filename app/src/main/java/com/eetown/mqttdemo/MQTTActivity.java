package com.eetown.mqttdemo;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import org.eclipse.paho.client.mqttv3.MqttClient;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created with IDEA
 *
 * @Author:Elvis
 * @Date:2019/3/9
 * @Time:11:30
 * @Description:
 */
public class MQTTActivity  extends AppCompatActivity  implements View.OnClickListener {


    private Button mConnectIot;
    private Button mPostData;
    private Button mDisConnectIot;
    private Button mSubscribe;


    private MqttClient mMqttClient;

    private  String targetServer = "tcp://192.168.3.14:1883";

    private  String   mqttclientId = "cyd";


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    Toast.makeText(MQTTActivity.this, "发送数据成功", Toast.LENGTH_LONG).show();
                    break;
                case 2:
                    Toast.makeText(MQTTActivity.this, "post数据失败", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_mqtt);

        initView();

        initEvent();
    }


    private void initEvent() {
        mConnectIot.setOnClickListener(this);
        mPostData.setOnClickListener(this);
        mDisConnectIot.setOnClickListener(this);
        mSubscribe.setOnClickListener(this);

    }

    private void initView() {

        mConnectIot = findViewById(R.id.connect_ali_iot);
        mPostData = findViewById(R.id.post_data);
        mDisConnectIot = findViewById(R.id.disconnect_ali_iot);
        mSubscribe = findViewById(R.id.subscribe);

    }


    @Override
    public void onClick(View v) {


        switch (v.getId()) {
            case R.id.connect_ali_iot:
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                       MQTTMangerUtils1.connectTest();

                    }
                };

                ThreadFactory nameThreadFactory = new ThreadFactoryBuilder().setNameFormat("mdns-pool-%d").build();

                ExecutorService singleThreadPool = new ThreadPoolExecutor(1, 1, 0L,
                        TimeUnit.MILLISECONDS, new LinkedBlockingDeque<Runnable>(1024), nameThreadFactory,
                        new ThreadPoolExecutor.AbortPolicy());

                singleThreadPool.execute(runnable);

                break;

            case R.id.post_data:
                Toast.makeText(this, "设备数据上报", Toast.LENGTH_LONG).show();

                break;

            case R.id.disconnect_ali_iot:
                Toast.makeText(this, "断开连接", Toast.LENGTH_LONG).show();

                break;

            case R.id.subscribe:

                MQTTMangerUtils1.subscribeTest1();
                Toast.makeText(this, "订阅成功:"+MQTTMangerUtils1.mqttMessage, Toast.LENGTH_LONG).show();

                break;

            default:
                break;
        }
    }




}
