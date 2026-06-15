package com.zzl.umr.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zzl.umr.model.SysMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author zhangzl
 * @description WebSocket服务
 * @date 2026/01/30 14:31:55
 */
@Component
@Slf4j
@ServerEndpoint("/websocket/{userId}")
public class WebSocketServer {

    private static int onlineCount = 0;
    private static CopyOnWriteArraySet<WebSocketServer> webSocketSet = new CopyOnWriteArraySet<>();
    private static ConcurrentHashMap<String, WebSocketServer> webSocketMap = new ConcurrentHashMap<>();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private Session session;
    private String userId = "";

    @OnOpen
    public void onOpen(Session session, @PathParam("userId") String userId) {
        this.session = session;
        this.userId = userId;

        webSocketSet.add(this);
        if (webSocketMap.containsKey(userId)) {
            webSocketMap.remove(userId);
        }
        webSocketMap.put(userId, this);

        addOnlineCount();
        log.info("用户{}连接成功,当前在线人数:{}", userId, getOnlineCount());

        try {
            sendMessage("连接成功");
        } catch (IOException e) {
            log.error("用户:{}网络异常!", userId);
        }
    }

    @OnClose
    public void onClose() {
        webSocketSet.remove(this);
        webSocketMap.remove(userId);
        subOnlineCount();
        log.info("用户{}断开连接,当前在线人数:{}", userId, getOnlineCount());
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        log.info("收到来自用户{}的消息:{}", userId, message);
    }

    @OnError
    public void onError(Session session, Throwable error) {
        log.error("用户{}发生错误", userId, error);
    }

    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }

    public void sendMessageToAll(SysMessage sysMessage) {
        try {
            String message = objectMapper.writeValueAsString(sysMessage);
            log.info("推送消息给所有用户");

            for (WebSocketServer item : webSocketSet) {
                try {
                    item.sendMessage(message);
                } catch (IOException e) {
                    log.error("推送消息失败", e);
                }
            }
        } catch (Exception e) {
            log.error("群发消息失败", e);
        }
    }

    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    public static synchronized void addOnlineCount() {
        WebSocketServer.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        WebSocketServer.onlineCount--;
    }
}