package com.eetown.mqttdemo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.aliyun.alink.dm.api.DeviceInfo;
import com.aliyun.alink.linkkit.api.ILinkKitConnectListener;
import com.aliyun.alink.linkkit.api.IoTMqttClientConfig;
import com.aliyun.alink.linkkit.api.LinkKit;
import com.aliyun.alink.linkkit.api.LinkKitInitParams;
import com.aliyun.alink.linksdk.cmp.connect.channel.MqttRrpcRegisterRequest;
import com.aliyun.alink.linksdk.cmp.core.base.AMessage;
import com.aliyun.alink.linksdk.cmp.core.base.ARequest;
import com.aliyun.alink.linksdk.cmp.core.base.AResponse;
import com.aliyun.alink.linksdk.cmp.core.base.ConnectState;
import com.aliyun.alink.linksdk.cmp.core.listener.IConnectNotifyListener;
import com.aliyun.alink.linksdk.cmp.core.listener.IConnectRrpcHandle;
import com.aliyun.alink.linksdk.cmp.core.listener.IConnectRrpcListener;
import com.aliyun.alink.linksdk.cmp.core.listener.IConnectSubscribeListener;
import com.aliyun.alink.linksdk.tools.AError;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;



public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    private Button mConnectIot;
    private Button mPostData;
    private Button mDisConnectIot;

    private String productKey = "a1Yq7urfSOU";// 高级版产品key
    private String deviceName = "Lug7y4EhHEZagw0166q4";//已经注册的设备id
    private String deviceSecret = "eMV93cEWgLoCrMYuwoDLEQnPaMUwEq3h";//设备秘钥

    private MqttClient mMqttClient;


    private final String payloadJson = "{\"id\":%s,\"params\":{\"temperature\": %s,\"humidity\": %s},\"method\":\"thing.event.property.post\"}";

    final int POST_DEVICE_PROPERTIES_SUCCESS = 1002;
    final int POST_DEVICE_PROPERTIES_ERROR = 1003;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case POST_DEVICE_PROPERTIES_SUCCESS:
                    Toast.makeText(MainActivity.this,"发送数据成功",Toast.LENGTH_LONG).show();
                    break;
                case POST_DEVICE_PROPERTIES_ERROR:
                    Toast.makeText(MainActivity.this,"post数据失败",Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };


    @Override
    protected void onPause() {
        super.onPause();


        LinkKit.getInstance().unRegisterOnPushListener(onPushListener);
    }

    @Override
    protected void onResume() {
        super.onResume();

       LinkKit.getInstance().registerOnPushListener(mIConnectNotifyListener);

       LinkKit.getInstance().registerOnPushListener(onPushListener);

    }

    /**
     *  连接通知监听
     */

    private IConnectNotifyListener mIConnectNotifyListener = new IConnectNotifyListener() {
        @Override
        public void onNotify(String s, String s1, AMessage aMessage) {

                 String message = new String();

                Toast.makeText(MainActivity.this,"topic1:"+s1+"下发数据:"+message,Toast.LENGTH_LONG).show();
        }

        @Override
        public boolean shouldHandle(String s, String s1) {
            return true;
        }

        @Override
        public void onConnectStateChange(String s, ConnectState connectState) {

        }
    };


    private String responseBody = "";
    private Button mSubscribe;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        startActivity(new Intent(this,MQTTActivity.class));

        initView();
        initEvent();

        init();
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

        switch (v.getId()){
            case R.id.connect_ali_iot:
                Toast.makeText(this,"建立连接",Toast.LENGTH_LONG).show();

                Runnable runnable= new Runnable() {
                    @Override
                    public void run() {

                        initAliyunIoTClient();
                        Log.e("MainActivity", "=====调用initAliyunIoTClient（）======= ");

                    }
                };

                ThreadFactory nameThreadFactory = new ThreadFactoryBuilder().setNameFormat("mdns-pool-%d").build();

                ExecutorService singleThreadPool = new ThreadPoolExecutor(1,1,0L,
                        TimeUnit.MILLISECONDS,new LinkedBlockingDeque<Runnable>(1024),nameThreadFactory,
                        new ThreadPoolExecutor.AbortPolicy());

                singleThreadPool.execute(runnable);

                break;

            case R.id.post_data:
                Toast.makeText(this,"设备数据上报",Toast.LENGTH_LONG).show();
                postDeviceProperties();
                break;

            case R.id.disconnect_ali_iot:
                Toast.makeText(this,"断开连接",Toast.LENGTH_LONG).show();
                 disconnectIot();
                break;

            case R.id.subscribe:

                //   subscribe();
                rrpc();
                break;

            default:
                break;
        }
    }




    /**
     * 使用 productKey，deviceName，deviceSecret 三元组建立IoT MQTT连接
     */
    private void initAliyunIoTClient() {

        try {
            String clientId = "androidthings" + System.currentTimeMillis();

            Map<String, String> params = new HashMap<String, String>(16);
            params.put("productKey", productKey);
            params.put("deviceName", deviceName);
            params.put("clientId", clientId);
            String timestamp = String.valueOf(System.currentTimeMillis());
            params.put("timestamp", timestamp);
            // cn-shanghai
            String MQTT_TARGET_SERVER= "tcp://" + productKey + ".iot-as-mqtt.cn-shanghai.aliyuncs.com:1883";

            String mqttclientId = clientId + "|securemode=3,signmethod=hmacsha1,timestamp=" + timestamp + "|";
            String mqttUsername = deviceName + "&" + productKey;
            String mqttPassword = AliyunIoTSignUtil.sign(params, deviceSecret, "hmacsha1");

            connectMqtt(MQTT_TARGET_SERVER, mqttclientId, mqttUsername, mqttPassword);

        } catch (Exception e) {
            e.printStackTrace();
            responseBody = e.getMessage();
            mHandler.sendEmptyMessage(POST_DEVICE_PROPERTIES_ERROR);
        }
    }

    /**
     * 建立MQTT连接
     * @param url
     * @param clientId
     * @param mqttUsername
     * @param mqttPassword
     */

     private void connectMqtt(String url, String clientId, String mqttUsername, String mqttPassword) {

             MemoryPersistence memoryPersistence = new MemoryPersistence();

         try {
             mMqttClient = new MqttClient(url,clientId,memoryPersistence);

             //连接属性设置
             MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();

                                mqttConnectOptions.setMqttVersion(4);
                                mqttConnectOptions.setAutomaticReconnect(true);
                                mqttConnectOptions.setCleanSession(true);
                                mqttConnectOptions.setUserName(mqttUsername);
                                mqttConnectOptions.setPassword(mqttPassword.toCharArray());
                                mqttConnectOptions.setKeepAliveInterval(60);

              mMqttClient.connect(mqttConnectOptions);

             Log.e("MainActivity", "connected " + url);

         } catch (MqttException e) {
             e.printStackTrace();
         }

     }



    /**
     * 断开连接
     */
    private void disconnectIot() {

        try {
            mMqttClient.disconnect();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }


    /**
     * post 数据  ，数据上行，发布， 定时发送 ，通过handler 消息机制），
     */
    private void postDeviceProperties() {

        try {

            Random random = new Random();

            //上报数据
         //   String payload = String.format(payloadJson);
            String payload = String.format(payloadJson, String.valueOf(System.currentTimeMillis()), 10 + random.nextInt(20), 50 + random.nextInt(50));
            responseBody = payload;
            MqttMessage message = new MqttMessage(payload.getBytes("utf-8"));
            message.setQos(1);


            String pubTopic = "/sys/" + productKey + "/" + deviceName + "/thing/event/property/post";
            mMqttClient.publish(pubTopic, message);

            Log.e("MainActivity", "publish topic=" + pubTopic + ",payload=" + payload);
            mHandler.sendEmptyMessage(POST_DEVICE_PROPERTIES_SUCCESS);

            //间隔上报数据，间隔时间自定义
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    postDeviceProperties();
                }
            },5000);
        } catch (Exception e) {
            e.printStackTrace();
            responseBody = e.getMessage();
            mHandler.sendEmptyMessage(POST_DEVICE_PROPERTIES_ERROR);
            Log.e("MainActivity", "postDeviceProperties error " + e.getMessage(), e);
        }
    }




    private void  init(){


        IoTMqttClientConfig ioTMqttClientConfig = new IoTMqttClientConfig(productKey,deviceName,deviceSecret);

        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.productKey = productKey;// 产品类型
        deviceInfo.deviceName = deviceName;// 设备名称
        deviceInfo.deviceSecret = deviceSecret;// 设备密钥


        LinkKitInitParams linkKitInitParams = new LinkKitInitParams();
                         linkKitInitParams.deviceInfo =deviceInfo;
                         linkKitInitParams.mqttClientConfig  = ioTMqttClientConfig;


        LinkKit.getInstance().init(this, linkKitInitParams, new ILinkKitConnectListener() {
            @Override
            public void onError(AError aError) {

            }

            @Override
            public void onInitDone(Object o) {

            }
        });


    }




    /**
     * 设备端 订阅  ，发送一个topic

     */

    public  void subscribe(){


        LinkKit.getInstance().registerOnPushListener(onPushListener);

        String TOPIC = "mqtt/test";


        try {

       init();

            mMqttClient.subscribe(TOPIC,0);
        } catch (MqttException e) {
            e.printStackTrace();
        }




        com.aliyun.alink.linksdk.cmp.connect.channel.MqttSubscribeRequest mqttSubscribeRequest = new com.aliyun.alink.linksdk.cmp.connect.channel.MqttSubscribeRequest();
        mqttSubscribeRequest.isSubscribe = true;


        mqttSubscribeRequest.topic = "/a1Yq7urfSOU/Lug7y4EhHEZagw0166q4/user/get";

        LinkKit.getInstance().subscribe(mqttSubscribeRequest, new IConnectSubscribeListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(MainActivity.this,"订阅成功",Toast.LENGTH_LONG).show();

            }

            @Override
            public void onFailure(AError aError) {
                Toast.makeText(MainActivity.this,"订阅失败"+aError.toString(),Toast.LENGTH_LONG).show();
            }
        });
    }



    // 下行数据监听
    IConnectNotifyListener onPushListener = new IConnectNotifyListener() {
        @Override
        public void onNotify(String connectId, String topic, AMessage aMessage) {
            // 下行数据通知

         //   Toast.makeText(MainActivity.this,"通知内容:",Toast.LENGTH_LONG).show();

        }
        @Override
        public boolean shouldHandle(String connectId, String topic) {
            return true; // 是否需要处理 该 topic
        }
        @Override
        public void onConnectStateChange(String connectId, ConnectState connectState) {
            // 连接状态变化

            Toast.makeText(MainActivity.this,"连接状态:"+connectState,Toast.LENGTH_LONG).show();
        }
    };
//
//
    private void rrpc(){

        init();

        final MqttRrpcRegisterRequest registerRequest = new MqttRrpcRegisterRequest();
// rrpcTopic 替换成用户自己自定义的 RRPC topic
        registerRequest.topic = "/sys/" + productKey + "/" + deviceName + "/thing/event/property/post";;
// rrpcReplyTopic 替换成用户自己定义的RRPC 响应 topic
//        registerRequest.replyTopic = rrpcReplyTopic;
// 根据需要填写，一般不填
// registerRequest.payloadObj = payload;
// 先订阅 rrpcTopic
// 云端发布消息到 rrpcTopic
// 收到下行数据 回复云端（rrpcReplyTopic） 具体可参考 Demo 同步服务调用
        LinkKit.getInstance().subscribeRRPC(registerRequest, new IConnectRrpcListener() {
            @Override
            public void onSubscribeSuccess(ARequest aRequest) {
                // 订阅成功
                Toast.makeText(MainActivity.this,"订阅成功1",Toast.LENGTH_LONG).show();
            }
            @Override
            public void onSubscribeFailed(ARequest aRequest, AError aError) {
                // 订阅失败
                Toast.makeText(MainActivity.this,"订阅失败1"+aError.toString(),Toast.LENGTH_LONG).show();
            }
            @Override
            public void onReceived(ARequest aRequest, IConnectRrpcHandle iConnectRrpcHandle) {
                // 收到云端下行
                // 响应获取成功
                if (iConnectRrpcHandle != null){
                    AResponse aResponse = new  AResponse();
                    // 仅供参考，具体返回云端的数据用户根据实际场景添加到data结构体
                    aResponse.data = "{\"id\":\"" + 123 + "\", \"code\":\"200\"" + ",\"data\":{} }";
                    iConnectRrpcHandle.onRrpcResponse(registerRequest.replyTopic, aResponse);
                }
            }
            @Override
            public void onResponseSuccess(ARequest aRequest) {
                // RRPC 响应成功
                Toast.makeText(MainActivity.this,"响应成功"+aRequest.toString(),Toast.LENGTH_LONG).show();
            }
            @Override
            public void onResponseFailed(ARequest aRequest, AError aError) {
                // RRPC 响应失败

                Toast.makeText(MainActivity.this,"响应失败"+aError.toString(),Toast.LENGTH_LONG).show();
            }
        });
    }

}


