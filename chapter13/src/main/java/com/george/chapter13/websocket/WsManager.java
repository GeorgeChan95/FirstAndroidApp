package com.george.chapter13.websocket;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.george.chapter13.enums.WsStatus;
import com.george.chapter13.listener.WsListener;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketFactory;

import java.io.IOException;

/**
 * WebSocket连接管理
 * https://cloud.tencent.com/developer/article/1937845
 */
public class WsManager {
    private static final String TAG = "WsManager";

    // WebSocket服务连接地址
    private static final String WEB_SOCKET_API = "ws://192.168.6.209:8008/ws/helmet";
    // WebSocket服务连接超时时间
    private static final int CONNECT_TIMEOUT = 5000;
    // 声明一个处理器对象
    private Handler mHandler = new Handler(Looper.myLooper());
    // 声明上下文对象
    private Activity mActivity;
    private WsListener mListener;

    // 当前类实例
    private volatile static WsManager wsManger;
    // WebSocket连接对象
    private WebSocket ws;
    // WebSocket连接状态
    private WsStatus wsStatus;

    private WsManager() {
    }

    /**
     * 获取Websocket对象
     * @return
     */
    public WebSocket getWs() {
        return ws;
    }

    /**
     * 初始化WebSocket连接
     * @param act
     */
    public void initWebSocket(Activity act, WsListener listener) {
        this.mActivity = act;
        this.mListener = listener;
        // 连接WebSocket服务端
        connect();
    }

    /**
     * 单例获取实例对象
     * @return
     */
    public static WsManager getWsManger() {
        if (wsManger == null) {
            synchronized (WsManager.class) {
                if (wsManger == null) {
                    wsManger = new WsManager();
                }
            }
        }
        return wsManger;
    }



    /**
     * Websocket连接方法，连接前需要判断是否已经登录，当前无
     */
    public void connect() {
        try {
            // WEB_SOCKET_API 是连接的url地址，
            // CONNECT_TIMEOUT是连接的超时时间 这里是 5秒
            ws = new WebSocketFactory().createSocket(WEB_SOCKET_API, CONNECT_TIMEOUT)
                    // 设置帧队列最大值为5
                    .setFrameQueueSize(5)
                    // 设置不允许服务端关闭连接却未发送关闭帧
                    .setMissingCloseFrameAllowed(false)
                    // 添加回调监听
                    .addListener(mListener)
                    // 异步连接
                    .connectAsynchronously();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 设置Websocket连接状态为：连接中
        setWsStatus(WsStatus.CONNECTING);
    }

    /**
     * 重新连接WebSocket服务端
     */
    public void reconnect() {
        if (ws != null && !ws.isOpen() && getWsStatus() != WsStatus.CONNECTING) {
            setWsStatus(WsStatus.CONNECTING);
            // 延迟重连
            mHandler.postDelayed(reconnectRunable, 10*1000);
        }
    }

    /**
     * 重新连接WebSocket服务端
     */
    private Runnable reconnectRunable = new Runnable() {
        @Override
        public void run() {
            wsManger.connect();
        }
    };

    /**
     * 设置WebSocket连接状态
     * @param wsStatus
     */
    public void setWsStatus(WsStatus wsStatus) {
        this.wsStatus = wsStatus;
    }

    /**
     * 获取WebSocket服务连接状态
     * @return
     */
    public WsStatus getWsStatus() {
        return wsStatus;
    }

    /**
     * 发送文本到服务端
     * @param message 消息内容
     */
    public boolean sendTextMessage(String message) {
        if (ws != null && ws.isOpen()) {
            ws.sendText(message);
            return true;
        } else {
            Log.d(TAG, "调用sendTextMessage方法，发送消息失败，ws连接异常");
            return false;
        }
    }
}
