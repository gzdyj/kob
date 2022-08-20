package com.kob.backend.consumer;

import com.alibaba.fastjson.JSONObject;
import com.kob.backend.consumer.utils.Game;
import com.kob.backend.consumer.utils.JwtAuthentication;
import com.kob.backend.mapper.RecordMapper;
import com.kob.backend.mapper.UserMapper;
import com.kob.backend.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.nio.channels.MulticastChannel;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
@ServerEndpoint("/websocket/{token}")  // 注意不要以'/'结尾
public class WebSocketServer {
    //线程安全的Hash表 concurrent 并发
    final public static ConcurrentHashMap<Integer, WebSocketServer> users = new ConcurrentHashMap<>();
    private User user;
    //每个链接由Session维护
    private Session session = null;
    //UserMapper 不是单例的所以用static静态
    private static UserMapper userMapper;
    public static RecordMapper recordMapper;
    private static RestTemplate restTemplate;
    //game 地图
    private Game game = null;
    private final static String addPlayerUrl = "http://127.0.0.1:3001/player/add/";
    private final static String removePlayerUrl = "http://127.0.0.1:3001/player/remove/";


    @Autowired
    public void setUserMapper(UserMapper userMapper) {
        WebSocketServer.userMapper = userMapper;
    }
    @Autowired
    public void setRecordMapper(RecordMapper recordMapper) {
        WebSocketServer.recordMapper = recordMapper;
    }
    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        WebSocketServer.restTemplate = restTemplate;
    }
    @OnOpen
    public void onOpen(Session session, @PathParam("token") String token) throws IOException {
        // 建立连接
        this.session = session;

        //connected 连接
        System.out.println("connected!");
        //Jwtauthentication是获取id的工具类
        Integer userId = JwtAuthentication.getUserId(token);
        //获取用户
        this.user = userMapper.selectById(userId);
        if (this.user != null) {
            //将用户存下来
            users.put(userId, this);
        } else {
            //断开连接
            this.session.close();
        }

        System.out.println(users);


    }

    public static void  startGame(Integer aId, Integer bId) {
        User a = userMapper.selectById(aId), b = userMapper.selectById(bId);

        Game game = new Game(13,14,20, a.getId(), b.getId());
        game.createMap();

        if (users.get(a.getId()) != null)
            users.get(a.getId()).game = game;
        if (users.get(b.getId()) != null)
            users.get(b.getId()).game = game;
        //start启动多线程
        game.start();





        JSONObject respGame = new JSONObject();
        respGame.put("a_id", game.getPlayerA().getId());
        respGame.put("a_sx", game.getPlayerA().getSx());
        respGame.put("a_sy", game.getPlayerA().getSy());
        respGame.put("b_id", game.getPlayerB().getId());
        respGame.put("b_sx", game.getPlayerB().getSx());
        respGame.put("b_sy", game.getPlayerB().getSy());
        respGame.put("map", game.getG());


        JSONObject respA = new JSONObject();
        //event 事件 操作类型
        //put a 的头像和名称发给 b
        respA.put("event", "start-matching");
        respA.put("opponent_username", b.getUsername());
        respA.put("opponent_photo", b.getPhoto());
        respA.put("game", respGame);

        //获取到a的链接，然后给a发送个消息
        if (users.get(a.getId()) != null)
            users.get(a.getId()).sendMessage(respA.toJSONString());

        JSONObject respB = new JSONObject();
        //event 事件 操作类型
        //put b 的头像和名称发给 a
        respB.put("event", "start-matching");
        respB.put("opponent_username", a.getUsername());
        respB.put("opponent_photo", a.getPhoto());
        respB.put("game", respGame);

        //获取到a的链接，然后给a发送个消息
        if (users.get(b.getId()) != null)
            users.get(b.getId()).sendMessage(respB.toJSONString());
    }


    @OnClose
    public void onClose() {
        // 关闭链接 discpmmected 分离
        System.out.println("disconnected!");
        //关闭连接时将用户全部清除掉
        if (this.user != null) {
            users.remove(this.user.getId());
        }
    }

    private void startMatching() {
        System.out.println("start-matching!");
        MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
        data.add("user_id", this.user.getId().toString());
        data.add("rating", this.user.getRating().toString());
        restTemplate.postForObject(addPlayerUrl, data, String.class);
    }

    private void stopMatching() {
        System.out.println("stop-matching!");
        MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
        data.add("user_id", this.user.getId().toString());

        restTemplate.postForObject(removePlayerUrl, data, String.class);
    }
    private void move(int direction) { // direction:方向
        if(game.getPlayerA().getId().equals(user.getId())) {
            game.setNextStepA(direction);
        } else if (game.getPlayerB().getId().equals(user.getId())) {
            game.setNextStepB(direction);
        }
    }

    @OnMessage
    public void onMessage(String message, Session session) { //当一个路由
        // 从Client接收消息 receive得到
        System.out.println("receive message!");
        JSONObject data = JSONObject.parseObject(message);
        String event = data.getString("event");
        //字符串先写在前面可以避免event为空的时候爆异常
        if ("start-matching".equals(event)) {
            startMatching();
        } else if ("stop-matching".equals(event)) {
            stopMatching();
        } else if ("move".equals(event)) {
            move(data.getInteger("direction"));
        }
    }

    @OnError
    public void onError(Session session, Throwable error) {
        error.printStackTrace();
    }

    public void sendMessage(String message) {
        synchronized (this.session) {
            try {
                //get到basic基本 remote计算机远程连接.发送文本
                this.session.getBasicRemote().sendText(message);
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }
}
