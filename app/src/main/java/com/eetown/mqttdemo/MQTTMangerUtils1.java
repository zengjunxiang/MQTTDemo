package com.eetown.mqttdemo;

import android.util.Log;

import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * Created with IDEA
 *
 * @Author:Elvis
 * @Date:2019/3/11
 * @Time:9:33
 * @Description:  MQTT协议工具类
 */
public class MQTTMangerUtils1  {




    public static MqttClient mMqttClient;
    private static MqttConnectOptions mMqttConnectOptions;

    public static  MqttMessage mqttMessage;


    public static MqttConnectOptions getMqttConnoctOptions(){
        mMqttConnectOptions = new MqttConnectOptions();
        //设置心跳时间，
        mMqttConnectOptions.setKeepAliveInterval(50);
        mMqttConnectOptions.setCleanSession(true);
        mMqttConnectOptions.setAutomaticReconnect(true);

        return  mMqttConnectOptions;

    }

    //=======================================1.MQTT 建立连接，无结果反馈================================

    /**
     * MQTT连接测试
     */
    public  static void  connectTest(){

         String targetServerURL = "tcp://\" + a1Yq7urfSOU + \".iot-as-mqtt.cn-shanghai.aliyuncs.com:1883";
         String clientID = "\"androidthings\"" + System.currentTimeMillis()+ "|securemode=3,signmethod=hmacsha1,timestamp=" + String.valueOf(System.currentTimeMillis()) + "|";
         String mqttUserName = "";
         String mqttPassWord = "";

         //初始化MQTT客户端
        try {
            mMqttClient = new MqttClient(targetServerURL,clientID);
            mMqttClient.connect();

            if (mMqttClient.isConnected()) {
                Log.e("MQTTMangerUtils1","建立连接成功");
            }

        } catch (MqttException e) {
            e.printStackTrace();
        }
    }


    /**
     * MQTT连接测试
     */
    public  static void  connectTest1(){

        String targetServerURL = "tcp://\" + a1Yq7urfSOU + \".iot-as-mqtt.cn-shanghai.aliyuncs.com:1883";
        String clientID = "\"androidthings\"" + System.currentTimeMillis()+ "|securemode=3,signmethod=hmacsha1,timestamp=" + String.valueOf(System.currentTimeMillis()) + "|";
        String mqttUserName = "Lug7y4EhHEZagw0166q4" + "&" + "a1Yq7urfSOU";
        String mqttPassWord = "";

        //初始化MQTT客户端
        try {
            mMqttClient = new MqttClient(targetServerURL,clientID);

            //连接选项
            MqttConnectOptions mqttConnectOptions = getMqttConnoctOptions();

            mqttConnectOptions.setUserName(mqttUserName);
            mqttConnectOptions.setPassword(mqttPassWord.toCharArray()); //可对密码进行加密处理

            //设置心跳时间，
            mqttConnectOptions.setKeepAliveInterval(50);
            mqttConnectOptions.setCleanSession(true);
            mqttConnectOptions.setAutomaticReconnect(true);

            mMqttClient.connect(mqttConnectOptions);

        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    /**
     * MQTT连接测试
     */
    public  static void  connectTest2(){

        String targetServerURL = "";
        String clientID = "";
        String mqttUserName = "";
        String mqttPassWord = "";

        //初始化MQTT客户端
        try {
            mMqttClient = new MqttClient(targetServerURL,clientID);

            //连接选项
            MqttConnectOptions mqttConnectOptions = getMqttConnoctOptions();

            mqttConnectOptions.setUserName(mqttUserName);
            mqttConnectOptions.setPassword(mqttPassWord.toCharArray());  //可对密码进行加密处理

            mMqttClient.connect(mqttConnectOptions);

        } catch (MqttException e) {
            e.printStackTrace();
        }
    }


    public  static void  connect(String targetServerURL,String clientID,String mqttUserName,String mqttPassWord){

        try {
            mMqttClient = new MqttClient(targetServerURL,clientID);
            //连接选项
            MqttConnectOptions mqttConnectOptions = getMqttConnoctOptions();

            mqttConnectOptions.setUserName(mqttUserName);
            mqttConnectOptions.setPassword(mqttPassWord.toCharArray());  //可对密码进行加密处理

            mMqttClient.connect();

        } catch (MqttException e) {
            e.printStackTrace();
        }


    }

    public  static  void connect(String targetServerURL,String clientID){

        try {
            mMqttClient = new MqttClient(targetServerURL,clientID);
            //连接选项
            MqttConnectOptions mqttConnectOptions = getMqttConnoctOptions();
            mMqttClient.connect(mqttConnectOptions);

        } catch (MqttException e) {
            e.printStackTrace();
        }
    }


    //=======================================1.2MQTT 建立连接，有结果反馈，基于回调================================

    /**
     * 连接回调接口
     */
    public interface  ConnectCallBack{

        public void onSuccess();

        public void onFailure();

        public void onError();

    };


    public  static void  connect(String targetServerURL,String clientID,String mqttUserName,String mqttPassWord, ConnectCallBack connectCallBack){

        try {
            mMqttClient = new MqttClient(targetServerURL,clientID);
            //连接选项
            MqttConnectOptions mqttConnectOptions = getMqttConnoctOptions();

            mqttConnectOptions.setUserName(mqttUserName);
            mqttConnectOptions.setPassword(mqttPassWord.toCharArray());  //可对密码进行加密处理

            mMqttClient.connect();

        } catch (MqttException e) {
            e.printStackTrace();
        }


    }

    public  static void connect(String targetServerURL,String clientID,ConnectCallBack connectCallBack){

        try {
            mMqttClient = new MqttClient(targetServerURL,clientID);
            //连接选项
            MqttConnectOptions mqttConnectOptions = getMqttConnoctOptions();
            mMqttClient.connect(mqttConnectOptions);

        } catch (MqttException e) {
            e.printStackTrace();
        }
    }


    //=======================================1.3MQTT 建立连接，有结果反馈，基于监听================================

    /**
     * 连接监听
     */
    public interface  MqttConnectListener{

        /**
         *
         */
        public void connectSuccessListener();

        public void connectFailedListener();

        public void connectErrorLister();

    };


    public  static void  connect(String targetServerURL,String clientID,String mqttUserName,String mqttPassWord, MqttConnectListener mqttConnectListener){

        try {
            mMqttClient = new MqttClient(targetServerURL,clientID);
            //连接选项
            MqttConnectOptions mqttConnectOptions = getMqttConnoctOptions();

            mqttConnectOptions.setUserName(mqttUserName);
            mqttConnectOptions.setPassword(mqttPassWord.toCharArray());  //可对密码进行加密处理

            mMqttClient.connect();

        } catch (MqttException e) {
            e.printStackTrace();
        }


    }

    public  static void connect(String targetServerURL,String clientID,MqttConnectListener mqttConnectListener){

        try {
            mMqttClient = new MqttClient(targetServerURL,clientID);
            //连接选项
            MqttConnectOptions mqttConnectOptions = getMqttConnoctOptions();
            mMqttClient.connect(mqttConnectOptions);

        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    //=========================2   Mqtt subscribe 订阅  无结果反馈===========================

    public static  void subscribeTest() {
        String TOPIC = "mqtt/test";
        int qos = 0;

        try {
            mMqttClient.subscribe(TOPIC, 0);
        } catch (MqttException e) {
            e.printStackTrace();
        }

    }

    public static  void subscribeTest1() {
        String TOPIC = "mqtt/test";
        int qos = 0;

        try {

            mMqttClient.subscribe(TOPIC, 0, new IMqttMessageListener() {

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    Log.e("MQTT ==>subscribe",message.toString());

                }
            });


        } catch (MqttException e) {
            e.printStackTrace();
        }

    }

    public static void subscribe(String topic,int qos){


        try {
            mMqttClient.subscribe(topic);

        } catch (MqttException e) {
            e.printStackTrace();
        }
    }


    //=========================2   Mqtt subscribe 订阅 有结果反馈 基于监听===========================

    public static void subscribe(String topic, int qos,IMqttMessageListener iMqttMessageListener){


        try {
            mMqttClient.subscribe(topic, qos, new IMqttMessageListener() {
                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {

                     Log.e("MQTT ==>subscribe",message.toString());
                    mqttMessage = message;
                }
            });

        } catch (MqttException e) {
            e.printStackTrace();
        }
    }



    //===========================3 Mqtt  publish 发布 无结果返回=================================

    public static  void publishTest(){

        String Topic = "";
        String payload = "";

        MqttMessage mqttMessage = new MqttMessage();
                    mqttMessage.setPayload(null);

        try {
            mMqttClient.publish(Topic,mqttMessage);

        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public static  void publishTest1(String topic,MqttMessage mqttMessage){

        String Topic = "";
        String payload = "";

        try {
            mMqttClient.publish(Topic,mqttMessage);

        } catch (MqttException e) {
            e.printStackTrace();
        }
    }


    //===========================3 Mqtt  publish发布 有结果返回  基于回调=================================

    public  interface  PublishCallBack{

        void onSuccess();

        void onFailed();

        void onError();

    }

    public static  void publish(String topic,PublishCallBack publishCallBack){

        String payload = "";

        MqttMessage mqttMessage = new MqttMessage();
        mqttMessage.setPayload(null);

        try {
            mMqttClient.publish(topic,mqttMessage);

        } catch (MqttException e) {
            e.printStackTrace();
        }
    }



    //===========================3 Mqtt  publish发布 有结果返回 基于监听 =================================

    public interface PublishLisenter {

    }

    public static void publish(String topic,MqttMessage mqttMessage, PublishLisenter publishLisenter){

        try {

            mMqttClient.publish(topic,mqttMessage);

        } catch (MqttException e) {
            e.printStackTrace();
        }

    }

    public static void publish(String topic, PublishLisenter publishLisenter){

            String payload = "";

           MqttMessage mqttMessage = new MqttMessage();
                       mqttMessage.setPayload(payload.getBytes());
                       mqttMessage.setQos(0);
        try {
            mMqttClient.publish(topic,mqttMessage);

        } catch (MqttException e) {
            e.printStackTrace();
        }

    }


    /**
     * 断开MQTT连接
     */
    public  static  void disconnect(){

        try {
            mMqttClient.disconnect();
        } catch (MqttException e) {
            e.printStackTrace();
        }

    }

}
