package cn.mcfun.api;

import cn.mcfun.Main;
import cn.mcfun.entity.UserInfo;
import cn.mcfun.request.PostRequest;
import cn.mcfun.utils.AuthCode;
import cn.mcfun.utils.Hikari;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.http.message.BasicNameValuePair;

public class FollowerList {
    public FollowerList() {
    }
    //通常助战
    public String[] followerlist1(String questId, String questPhase, UserInfo userInfo) throws SQLException {
        String lastAccessTime = String.valueOf(System.currentTimeMillis() / 1000L);
        List<BasicNameValuePair> params = new ArrayList();
        params.add(new BasicNameValuePair("questId", questId));
        params.add(new BasicNameValuePair("questPhase", questPhase));
        params.add(new BasicNameValuePair("refresh", "2"));
        params.add(new BasicNameValuePair("userId", userInfo.getUserId()));
        params.add(new BasicNameValuePair("authKey", userInfo.getAuthKey()));
        params.add(new BasicNameValuePair("appVer", Main.appVer));
        params.add(new BasicNameValuePair("dateVer", Main.dateVer));
        params.add(new BasicNameValuePair("idempotencyKey", UUID.randomUUID().toString().toLowerCase()));
        params.add(new BasicNameValuePair("lastAccessTime", lastAccessTime));
        params.add(new BasicNameValuePair("verCode", "723d93a599b6f10ef3085ff1131fa5679a91da924246b8ca40dded18eccaf3da"));
        params.add(new BasicNameValuePair("dataVer", Main.dataVer));
        String sign = (new AuthCode()).getSign(userInfo, params);
        params.add(new BasicNameValuePair("authCode", sign));
        String result = (new PostRequest()).sendPost(userInfo.getCookie(), "https://game.fate-go.jp/follower/list?_userId=" + userInfo.getUserId(), params);
        JSONObject jsonObject = JSONObject.parseObject(result);
        if (!jsonObject.getJSONArray("response").getJSONObject(0).getString("resCode").equals("00")) {
            Connection conn2 = Hikari.getConnection();
            String sql2 = "update `order` set message=?,status='订单错误' where `order`=? and status='执行中'";
            PreparedStatement ps2 = conn2.prepareStatement(sql2);
            ps2.setString(1, jsonObject.getJSONArray("response").getJSONObject(0).getJSONObject("fail").getString("detail"));
            ps2.setString(2, userInfo.getOrder());
            ps2.executeUpdate();
            conn2.close();
            ps2.close();
            Thread.currentThread().stop();
            return null;
        } else {
            String followerId = null;
            String type = null;
            String followerClassId = null;
            String supportDeckId = null;
            JSONArray followerInfo;
            flag:
            for (int m = 0; m < jsonObject.getJSONObject("cache").getJSONObject("updated").getJSONArray("userFollower").size(); m++) {
                followerInfo = jsonObject.getJSONObject("cache").getJSONObject("updated").getJSONArray("userFollower").getJSONObject(m).getJSONArray("followerInfo");
                for (int i = 0; i < followerInfo.size(); i++) {
                    if (followerInfo.getJSONObject(i).getString("type").equals("1") ||
                            followerInfo.getJSONObject(i).getString("type").equals("2")) {
                        followerId = followerInfo.getJSONObject(i).getString("userId");
                        type = followerInfo.getJSONObject(i).getString("type");
                        for (int j = 0; j < followerInfo.getJSONObject(i).getJSONArray("userSvtLeaderHash").size(); j++) {
                            if (!followerInfo.getJSONObject(i).getJSONArray("userSvtLeaderHash").getJSONObject(j).getString("userSvtId").equals("0")) {
                                followerClassId = followerInfo.getJSONObject(i).getJSONArray("userSvtLeaderHash").getJSONObject(j).getString("classId");
                                supportDeckId = followerInfo.getJSONObject(i).getJSONArray("userSvtLeaderHash").getJSONObject(j).getString("supportDeckId");
                                break flag;
                            }
                        }
                    }
                }
            }
            if (type.equals("1")) {
                type = "1";
            } else if (type.equals("2")) {
                type = "0";
            }
            String[] arr = {followerId, type, followerClassId, supportDeckId};
            try {
                Thread.currentThread().sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return arr;
        }
    }
    //活动助战
    public String[] followerlist2(String questId, String questPhase, UserInfo userInfo) {
        String lastAccessTime = String.valueOf(System.currentTimeMillis() / 1000L);
        List<BasicNameValuePair> params = new ArrayList();
        params.add(new BasicNameValuePair("questId", questId));
        params.add(new BasicNameValuePair("questPhase", questPhase));
        params.add(new BasicNameValuePair("refresh", "0"));
        params.add(new BasicNameValuePair("userId", userInfo.getUserId()));
        params.add(new BasicNameValuePair("authKey", userInfo.getAuthKey()));
        params.add(new BasicNameValuePair("appVer", Main.appVer));
        params.add(new BasicNameValuePair("dateVer", Main.dateVer));
        params.add(new BasicNameValuePair("idempotencyKey", UUID.randomUUID().toString().toLowerCase()));
        params.add(new BasicNameValuePair("lastAccessTime", lastAccessTime));
        params.add(new BasicNameValuePair("verCode", "723d93a599b6f10ef3085ff1131fa5679a91da924246b8ca40dded18eccaf3da"));
        params.add(new BasicNameValuePair("dataVer", Main.dataVer));
        String sign = (new AuthCode()).getSign(userInfo, params);
        params.add(new BasicNameValuePair("authCode", sign));
        String result = (new PostRequest()).sendPost(userInfo.getCookie(), "https://game.fate-go.jp/follower/list?_userId=" + userInfo.getUserId(), params);
        JSONObject jsonObject = JSONObject.parseObject(result);
        if (!jsonObject.getJSONArray("response").getJSONObject(0).getString("resCode").equals("00")) {
            System.out.println(jsonObject.getJSONArray("response").getJSONObject(0).getJSONObject("fail").getString("detail"));
            Thread.currentThread().stop();
            return null;
        } else {
            String followerId = null;
            String type = null;
            String followerClassId = null;
            String supportDeckId = null;
            JSONArray followerInfo;
            flag:
            for (int m = 0; m < jsonObject.getJSONObject("cache").getJSONObject("updated").getJSONArray("userFollower").size(); m++) {
                followerInfo = jsonObject.getJSONObject("cache").getJSONObject("updated").getJSONArray("userFollower").getJSONObject(m).getJSONArray("followerInfo");
                for (int i = 0; i < followerInfo.size(); i++) {
                    if (followerInfo.getJSONObject(i).getString("type").equals("1") ||
                            followerInfo.getJSONObject(i).getString("type").equals("2")) {
                        followerId = followerInfo.getJSONObject(i).getString("userId");
                        type = followerInfo.getJSONObject(i).getString("type");
                        for (int j = 0; j < followerInfo.getJSONObject(i).getJSONArray("eventUserSvtLeaderHash").size(); j++) {
                            if (!followerInfo.getJSONObject(i).getJSONArray("eventUserSvtLeaderHash").getJSONObject(j).getString("userSvtId").equals("0")) {
                                followerClassId = followerInfo.getJSONObject(i).getJSONArray("eventUserSvtLeaderHash").getJSONObject(j).getString("classId");
                                supportDeckId = followerInfo.getJSONObject(i).getJSONArray("eventUserSvtLeaderHash").getJSONObject(j).getString("supportDeckId");
                                break flag;
                            }
                        }
                    }
                }
            }

            if (type.equals("1")) {
                type = "1";
            } else if (type.equals("2")) {
                type = "0";
            }
            String[] arr = {followerId, type, followerClassId, supportDeckId};
            try {
                Thread.currentThread().sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return arr;
        }
    }
}
