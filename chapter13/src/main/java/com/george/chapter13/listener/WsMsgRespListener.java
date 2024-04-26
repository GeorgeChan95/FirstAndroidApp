package com.george.chapter13.listener;

/**
 * 处理WS消息回调
 */
public interface WsMsgRespListener {
    /**
     * 接收到的服务端消息
     * @param msg
     */
    void receiveResponse(String msg);
}
