package com.kob.backend.service.impl.user.account.acwing;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kob.backend.mapper.UserMapper;
import com.kob.backend.pojo.User;
import com.kob.backend.service.impl.user.account.acwing.utils.HttpClientUtil;
import com.kob.backend.service.user.account.acwing.AcAppService;

import com.kob.backend.utils.JwtUtil;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.Duration;
import java.util.*;

@Service
public class AcAppServiceImpl implements AcAppService {
    private final static String appid = "3277";
    //secret 秘密
    private final static String appsecret = "99b98439f67b4f78ac41aad69d63c7ce";
    //回调链接
    private final static String redirectUrl = "https://app3277.acapp.acwing.com.cn/api/user/account/acwing/acapp/receive_code";
    private final static String applyAccessToken = "https://www.acwing.com/third_party/api/oauth2/access_token/";
    private final static String getUserInfoUrl = "https://www.acwing.com/third_party/api/meta/identity/getinfo/";
    private final static Random random = new Random();

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public JSONObject applyCode() {
        JSONObject resp = new JSONObject();
        resp.put("appid",appid);
        try {
            resp.put("redirect_uri", URLEncoder.encode(redirectUrl,"UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            resp.put("result", "failed");
            return resp;
        }
        resp.put("scope", "userinfo");
        StringBuilder state = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            state.append((char) (random.nextInt(10) + '0'));
        }
        resp.put("state", state.toString());
        resp.put("result", "success");
        redisTemplate.opsForValue().set(state.toString(), "true");
        redisTemplate.expire(state.toString(), Duration.ofMinutes(10));
        return resp;


    }

    @Override
    public JSONObject receiveCode(String code, String state) {
        JSONObject resp = new JSONObject();
        resp.put("result", "failed");
        if (code == null || state == null) return resp;
        if (Boolean.FALSE.equals(redisTemplate.hasKey(state))) return resp;
        redisTemplate.delete(state);
        List<NameValuePair> nameValuePairs = new LinkedList<>();
        nameValuePairs.add(new BasicNameValuePair("appid", appid));
        nameValuePairs.add(new BasicNameValuePair("secret", appsecret));
        nameValuePairs.add(new BasicNameValuePair("code", code));

        String getString = HttpClientUtil.get(applyAccessToken, nameValuePairs);
        if (getString == null) return resp;
        JSONObject getResp = JSONObject.parseObject(getString);
        String accessToken = getResp.getString("access_token");
        String  openid = getResp.getString("openid");
        if (accessToken == null || openid == null) return resp;

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("openid", openid);
        List<User> users = userMapper.selectList(queryWrapper);
        if (!users.isEmpty()) {
            User user = users.get(0);
            String jwt = JwtUtil.createJWT(user.getId().toString());
            resp.put("error_message","success");
            resp.put("jwt_token",jwt);
            return resp;
        }

        nameValuePairs = new LinkedList<>();
        nameValuePairs.add(new BasicNameValuePair("access_token", accessToken));
        nameValuePairs.add(new BasicNameValuePair("openid", openid));

        getString = HttpClientUtil.get(getUserInfoUrl, nameValuePairs);
        if (getString == null) return resp;
        getResp = JSONObject.parseObject(getString);
        String username = getResp.getString("username");
        String photo = getResp.getString("photo");

        if (username == null || photo == null) return resp;
        //保证用户名不相同
        for (int i = 0; i < 100; i++) {
            QueryWrapper<User> usernameQueryWrapper = new QueryWrapper<>();
            usernameQueryWrapper.eq("username", usernameQueryWrapper);
            if (userMapper.selectList(usernameQueryWrapper).isEmpty()) break;
            username += (char)(random.nextInt(10) + '0');
            if (i == 99) return resp;
        }

        User user = new User(
                null,
                username,
                null,
                photo,
                1500,
                openid
        );
        userMapper.insert(user);
        String jwt = JwtUtil.createJWT(user.getId().toString());
        resp.put("error_message","success");
        resp.put("jwt_token",jwt);
        return resp;
    }
}
