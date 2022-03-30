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

public class Present {
    public Present() {
    }

    public void presentlist(UserInfo userInfo) throws SQLException {
        String lastAccessTime = String.valueOf(System.currentTimeMillis() / 1000L);
        List<BasicNameValuePair> params = new ArrayList();
        params.add(new BasicNameValuePair("userId", userInfo.getUserId()));
        params.add(new BasicNameValuePair("authKey", userInfo.getAuthKey()));
        params.add(new BasicNameValuePair("appVer", Main.appVer));
        params.add(new BasicNameValuePair("dateVer", Main.dateVer));
        params.add(new BasicNameValuePair("lastAccessTime", lastAccessTime));
        params.add(new BasicNameValuePair("idempotencyKey", UUID.randomUUID().toString().toLowerCase()));
        params.add(new BasicNameValuePair("verCode", "723d93a599b6f10ef3085ff1131fa5679a91da924246b8ca40dded18eccaf3da"));
        params.add(new BasicNameValuePair("dataVer", Main.dataVer));
        String sign = (new AuthCode()).getSign(userInfo, params);
        params.add(new BasicNameValuePair("authCode", sign));
        String result = (new PostRequest()).sendPost(userInfo.getCookie(), "https://game.fate-go.jp/present/list?_userId=" + userInfo.getUserId(), params);
        JSONObject jsonObject = JSONObject.parseObject(result);
        String presentIds;
        if (jsonObject.getJSONArray("response").getJSONObject(0).getString("resCode").equals("00")) {
            JSONArray userPresentBox = jsonObject.getJSONObject("cache").getJSONObject("replaced").getJSONArray("userPresentBox");
            presentIds = null;
            if (userPresentBox != null && !userPresentBox.toString().equals("[{}]")) {
                for(int i = 0; i < userPresentBox.size(); ++i) {
                    if (userPresentBox.getJSONObject(i).getString("objectId").equals("2") || userPresentBox.getJSONObject(i).getString("objectId").equals("4") || userPresentBox.getJSONObject(i).getString("objectId").equals("100") || userPresentBox.getJSONObject(i).getString("objectId").equals("101") || userPresentBox.getJSONObject(i).getString("objectId").equals("102") || userPresentBox.getJSONObject(i).getString("objectId").equals("2000") || userPresentBox.getJSONObject(i).getString("objectId").equals("4001")) {
                        if (presentIds == null) {
                            presentIds = "[" + userPresentBox.getJSONObject(i).getString("presentId");
                        } else {
                            presentIds = presentIds + "," + userPresentBox.getJSONObject(i).getString("presentId");
                        }
                    }
                }

                if (presentIds != null) {
                    presentIds = presentIds + "]";
                    this.presentreceive(presentIds, userInfo);
                }
            }
        } else {
            Connection conn2 = Hikari.getConnection();
            presentIds = "update `order` set message=?,status='订单错误' where `order`=? and status='执行中'";
            PreparedStatement ps2 = conn2.prepareStatement(presentIds);
            ps2.setString(1, jsonObject.getJSONArray("response").getJSONObject(0).getJSONObject("fail").getString("detail"));
            ps2.setString(2, userInfo.getOrder());
            ps2.executeUpdate();
            conn2.close();
            ps2.close();
            Thread.currentThread().stop();
        }

    }

    public void presentreceive(String presentIds, UserInfo userInfo) throws SQLException {
        String lastAccessTime = String.valueOf(System.currentTimeMillis() / 1000L);
        List<BasicNameValuePair> params = new ArrayList();
        params.add(new BasicNameValuePair("userId", userInfo.getUserId()));
        params.add(new BasicNameValuePair("authKey", userInfo.getAuthKey()));
        params.add(new BasicNameValuePair("appVer", Main.appVer));
        params.add(new BasicNameValuePair("dateVer", Main.dateVer));
        params.add(new BasicNameValuePair("presentIds", presentIds));
        params.add(new BasicNameValuePair("itemSelectIdx", "0"));
        params.add(new BasicNameValuePair("itemSelectNum", "0"));
        params.add(new BasicNameValuePair("idempotencyKey", UUID.randomUUID().toString().toLowerCase()));
        params.add(new BasicNameValuePair("lastAccessTime", lastAccessTime));
        params.add(new BasicNameValuePair("verCode", "723d93a599b6f10ef3085ff1131fa5679a91da924246b8ca40dded18eccaf3da"));
        params.add(new BasicNameValuePair("dataVer", Main.dataVer));
        String sign = (new AuthCode()).getSign(userInfo, params);
        params.add(new BasicNameValuePair("authCode", sign));
        String result = (new PostRequest()).sendPost(userInfo.getCookie(), "https://game.fate-go.jp/present/receive?_userId=" + userInfo.getUserId(), params);
        JSONObject jsonObject = JSONObject.parseObject(result);
        Connection conn2;
        String sql2;
        PreparedStatement ps2;
        if (jsonObject.getJSONArray("response").getJSONObject(0).getString("resCode").equals("00")) {
            conn2 = Hikari.getConnection();
            sql2 = "update `order` set message=? where `order`=? and status='执行中'";
            ps2 = conn2.prepareStatement(sql2);
            ps2.setString(1, "领取礼物盒完成");
            ps2.setString(2, userInfo.getOrder());
            ps2.executeUpdate();
            conn2.close();
            ps2.close();
        } else {
            conn2 = Hikari.getConnection();
            sql2 = "update `order` set message=?,status='订单错误' where `order`=? and status='执行中'";
            ps2 = conn2.prepareStatement(sql2);
            ps2.setString(1, jsonObject.getJSONArray("response").getJSONObject(0).getJSONObject("fail").getString("detail"));
            ps2.setString(2, userInfo.getOrder());
            ps2.executeUpdate();
            conn2.close();
            ps2.close();
            Thread.currentThread().stop();
        }

    }
}
