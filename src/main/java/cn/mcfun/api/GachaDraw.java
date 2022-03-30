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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.http.message.BasicNameValuePair;

public class GachaDraw {
    String gachaId;
    String svtId;
    int content;
    int current;
    int total;
    String type;
    String stone;
    String hufu;

    public GachaDraw() {
    }

    public void start(UserInfo userInfo) throws SQLException {
        if (!((String)userInfo.getQuest().get("sellData")).equals("[]")) {
            this.sell(userInfo);
        }

        if (userInfo.getQuest().containsKey("stone")) {
            this.stone = (String)userInfo.getQuest().get("stone");
        } else {
            this.stone = "0";
        }

        if (userInfo.getQuest().containsKey("w4001")) {
            this.hufu = (String)userInfo.getQuest().get("w4001");
        } else {
            this.hufu = "0";
        }

        Connection conn = Hikari.getConnection();
        String sql = "select gachaId,svtId,`content`,`current`,type from `order` where `order`=? and status='执行中'";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, userInfo.getOrder());

        ResultSet rs;
        for(rs = ps.executeQuery(); rs.next(); this.type = rs.getString("type")) {
            this.gachaId = rs.getString("gachaId");
            this.svtId = rs.getString("svtId");
            this.content = rs.getInt("content");
            this.current = rs.getInt("current");
        }

        conn.close();
        rs.close();
        ps.close();

        while(this.current < this.content) {
            this.gachadraw(userInfo);
        }

        String message = "抽卡完成，本次" + this.total + "抽，剩余呼符：" + this.hufu + "，剩余石头：" + this.stone;
        Connection conn2 = Hikari.getConnection();
        String sql2 = "update `order` set message=?,status='已完成' where `order`=? and status='执行中'";
        PreparedStatement ps2 = conn2.prepareStatement(sql2);
        ps2.setString(1, message);
        ps2.setString(2, userInfo.getOrder());
        ps2.executeUpdate();
        conn2.close();
        ps2.close();
    }

    public void gachadraw(UserInfo userInfo) throws SQLException {
        String lastAccessTime = String.valueOf(System.currentTimeMillis() / 1000L);
        List<BasicNameValuePair> params = new ArrayList();
        params.add(new BasicNameValuePair("gachaId", this.gachaId));
        params.add(new BasicNameValuePair("num", this.type));
        params.add(new BasicNameValuePair("storyAdjustIds", "[]"));
        params.add(new BasicNameValuePair("userId", userInfo.getUserId()));
        params.add(new BasicNameValuePair("authKey", userInfo.getAuthKey()));
        params.add(new BasicNameValuePair("appVer", Main.appVer));
        params.add(new BasicNameValuePair("idempotencyKey", UUID.randomUUID().toString().toLowerCase()));
        params.add(new BasicNameValuePair("dateVer", Main.dateVer));
        params.add(new BasicNameValuePair("lastAccessTime", lastAccessTime));
        params.add(new BasicNameValuePair("verCode", "723d93a599b6f10ef3085ff1131fa5679a91da924246b8ca40dded18eccaf3da"));
        params.add(new BasicNameValuePair("dataVer", Main.dataVer));
        String ticketItemId = "0";
        if (this.type.equals("1")) {
            if (!this.hufu.equals("0")) {
                ticketItemId = "4001";
            }

            params.add(new BasicNameValuePair("shopIdIndex", "1"));
        } else {
            params.add(new BasicNameValuePair("shopIdIndex", "2"));
        }

        params.add(new BasicNameValuePair("ticketItemId", ticketItemId));
        params.add(new BasicNameValuePair("gachaSubId", "0"));
        String sign = (new AuthCode()).getSign(userInfo, params);
        params.add(new BasicNameValuePair("authCode", sign));
        String result = (new PostRequest()).sendPost(userInfo.getCookie(), "https://game.fate-go.jp/gacha/draw?_userId=" + userInfo.getUserId(), params);
        JSONObject jsonObject = JSONObject.parseObject(result);
        String message;
        if (jsonObject.getJSONArray("response").getJSONObject(0).getString("resCode").equals("00")) {
            this.stone = jsonObject.getJSONObject("cache").getJSONObject("updated").getJSONArray("userGame").getJSONObject(0).getString("stone");
            if (jsonObject.getJSONObject("cache").getJSONObject("updated").getJSONArray("userItem") != null) {
                this.hufu = jsonObject.getJSONObject("cache").getJSONObject("updated").getJSONArray("userItem").getJSONObject(0).getString("num");
            }

            JSONArray gachaInfos = jsonObject.getJSONArray("response").getJSONObject(0).getJSONObject("success").getJSONArray("gachaInfos");

            for(int i = 0; i < gachaInfos.size(); ++i) {
                if (gachaInfos.getJSONObject(i).getString("objectId").equals(this.svtId)) {
                    ++this.current;
                }
            }

            this.total += 1 * Integer.parseInt(this.type);
            message = "正在抽卡，累计" + this.total + "抽";
            Connection conn2 = Hikari.getConnection();
            String sql2 = "update `order` set `current`=?,message=? where `order`=? and status='执行中'";
            PreparedStatement ps2 = conn2.prepareStatement(sql2);
            ps2.setString(1, String.valueOf(this.current));
            ps2.setString(2, message);
            ps2.setString(3, userInfo.getOrder());
            ps2.executeUpdate();
            conn2.close();
            ps2.close();

            try {
                Thread.currentThread();
                Thread.sleep(3000L);
            } catch (InterruptedException var14) {
                var14.printStackTrace();
            }
        } else {
            Connection conn2 = Hikari.getConnection();
            message = "update `order` set message=?,status='订单错误' where `order`=? and status='执行中'";
            PreparedStatement ps2 = conn2.prepareStatement(message);
            ps2.setString(1, jsonObject.getJSONArray("response").getJSONObject(0).getJSONObject("fail").getString("detail"));
            ps2.setString(2, userInfo.getOrder());
            ps2.executeUpdate();
            conn2.close();
            ps2.close();
            Thread.currentThread().stop();
        }

    }

    public void sell(UserInfo userInfo) throws SQLException {
        List<BasicNameValuePair> params = new ArrayList();
        String lastAccessTime = String.valueOf(System.currentTimeMillis() / 1000L);
        String result = null;
        params.add(new BasicNameValuePair("sellData", (String)userInfo.getQuest().get("sellData")));
        params.add(new BasicNameValuePair("sellCommandCode", "[]"));
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
        result = (new PostRequest()).sendPost(userInfo.getCookie(), "https://game.fate-go.jp/shop/sellsvt?_userId=" + userInfo.getUserId(), params);
        JSONObject jsonObject = JSONObject.parseObject(result);
        Connection conn2;
        String sql2;
        PreparedStatement ps2;
        if (jsonObject.getJSONArray("response").getJSONObject(0).getString("resCode").equals("00")) {
            conn2 = Hikari.getConnection();
            sql2 = "update `order` set message='模拟卖掉不需要的卡牌' where `order`=? and status='执行中'";
            ps2 = conn2.prepareStatement(sql2);
            ps2.setString(1, userInfo.getOrder());
            ps2.executeUpdate();
            conn2.close();
            ps2.close();

            try {
                Thread.currentThread();
                Thread.sleep(3000L);
            } catch (InterruptedException var11) {
                var11.printStackTrace();
            }
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
