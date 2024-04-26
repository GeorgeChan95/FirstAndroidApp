package com.george.chapter13.listener;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.george.chapter13.entity.LoginInfo;
import com.george.chapter13.enums.WsStatus;
import com.george.chapter13.websocket.WsManager;
import com.google.gson.Gson;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFrame;

import java.util.List;
import java.util.Map;

/**
 * WebSocket连接监听器
 */
public class WsListener extends WebSocketAdapter {
    private static final String TAG = "WsListener";

    private Activity mActivity;
    private WsMsgRespListener mListener;

    public WsListener(Activity act, WsMsgRespListener listener) {
        this.mActivity = act;
        this.mListener = listener;
    }

    /**
     * WebSocket客户端接收到服务端发来的字符串消息时的回调
     * @param websocket WebSocket连接对象
     * @param text 消息文本
     * @throws Exception
     */
    @Override
    public void onTextMessage(WebSocket websocket, String text) throws Exception {
        super.onTextMessage(websocket, text);
        Log.d(TAG, "调用了onTextMessage方法，获取到服务端消息内容：" + text);
        // 调用自定义接口处理服务端消息
        mListener.receiveResponse(text);
    }

    /**
     * WebSocket客户端连接服务端 成功 时的回调
     * @param websocket WebSocket连接对象
     * @param headers 消息头
     * @throws Exception
     */
    @Override
    public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws Exception {
        super.onConnected(websocket, headers);
        // 设置WebSocket连接状态为：连接成功
        WsManager wsManger = WsManager.getWsManger();
        wsManger.setWsStatus(WsStatus.CONNECT_SUCCESS);

        // 连接成功，发送消息给服务端,执行登录操作
        WebSocket ws = wsManger.getWs();
        LoginInfo loginInfo = new LoginInfo();
        loginInfo.username = "george";
        loginInfo.password = "1234";
        Gson gson = new Gson();
        String msg = gson.toJson(loginInfo);
        ws.sendText(msg);
    }

    /**
     * WebSocket客户端连接服务端 失败 时的回调
     * @param websocket WebSocket连接对象
     * @param exception 异常消息
     * @throws Exception
     */
    @Override
    public void onConnectError(WebSocket websocket, WebSocketException exception) throws Exception {
        super.onConnectError(websocket, exception);
        Log.d(TAG, "WebSocket客户端连接异常，异常消息：" + exception.getMessage());
        // 设置WebSocket连接状态为：连接失败
        WsManager wsManger = WsManager.getWsManger();
        wsManger.setWsStatus(WsStatus.CONNECT_FAIL);
    }

    /**
     * 断开连接时的回调
     * @param websocket WebSocket连接对象
     * @param serverCloseFrame
     * @param clientCloseFrame
     * @param closedByServer 是否由服务端主动断开连接
     * @throws Exception
     */
    @Override
    public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame, boolean closedByServer) throws Exception {
        super.onDisconnected(websocket, serverCloseFrame, clientCloseFrame, closedByServer);
        Log.d(TAG, "WebSocket连接断开，断开原因：" + (closedByServer ? "服务端关闭" : "其它原因"));
        // 设置WebSocket连接状态为：连接失败
        WsManager wsManger = WsManager.getWsManger();
        wsManger.setWsStatus(WsStatus.CONNECT_FAIL);
        // 重新连接WebSocket服务端
        wsManger.reconnect();
    }
}
