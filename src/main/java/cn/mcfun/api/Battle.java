package cn.mcfun.api;

import cn.mcfun.Main;
import cn.mcfun.entity.UserInfo;
import cn.mcfun.request.PostRequest;
import cn.mcfun.utils.AES;
import cn.mcfun.utils.AuthCode;
import cn.mcfun.utils.BattleStatus;
import cn.mcfun.utils.Hikari;
import com.alibaba.fastjson.JSONObject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.apache.http.message.BasicNameValuePair;

public class Battle {
    public Battle() {
    }

    public void battlescenario(String questId, String questPhase, UserInfo userInfo) throws SQLException {
        String lastAccessTime = String.valueOf(System.currentTimeMillis() / 1000L);
        List<BasicNameValuePair> params = new ArrayList();
        params.add(new BasicNameValuePair("questId", questId));
        params.add(new BasicNameValuePair("questPhase", questPhase));
        params.add(new BasicNameValuePair("routeSelect", "[]"));
        params.add(new BasicNameValuePair("userId", userInfo.getUserId()));
        params.add(new BasicNameValuePair("authKey", userInfo.getAuthKey()));
        params.add(new BasicNameValuePair("appVer", Main.appVer));
        params.add(new BasicNameValuePair("idempotencyKey", UUID.randomUUID().toString().toLowerCase()));
        params.add(new BasicNameValuePair("dateVer", Main.dateVer));
        params.add(new BasicNameValuePair("lastAccessTime", lastAccessTime));
        params.add(new BasicNameValuePair("verCode", "e92c481b51ff8203344cf768b2f5bc84b14409bc2ad7084292c9370ea97621de"));
        params.add(new BasicNameValuePair("dataVer", Main.dataVer));
        String sign = (new AuthCode()).getSign(userInfo, params);
        params.add(new BasicNameValuePair("authCode", sign));
        String result = (new PostRequest()).sendPost(userInfo.getCookie(), "https://game.fate-go.jp/battle/scenario?_userId=" + userInfo.getUserId(), params);
        JSONObject jsonObject = JSONObject.parseObject(result);
        if (jsonObject.getJSONArray("response").getJSONObject(0).getString("resCode").equals("00")) {
            String battleId = jsonObject.getJSONObject("cache").getJSONObject("replaced").getJSONArray("battle").getJSONObject(0).getString("id");

            try {
                Thread.currentThread();
                Thread.sleep((long)((60 + ((new Random()).nextInt(21) - 10)) * 1000));
            } catch (InterruptedException var13) {
                var13.printStackTrace();
            }

            this.battleresult(battleId, userInfo);
        } else {
            Connection conn2 = Hikari.getConnection();
            String sql2 = "update `order` set message=?,status='订单错误' where `order`=? and status='执行中'";
            PreparedStatement ps2 = conn2.prepareStatement(sql2);
            ps2.setString(1, jsonObject.getJSONArray("response").getJSONObject(0).getJSONObject("fail").getString("detail"));
            ps2.setString(2, userInfo.getOrder());
            ps2.executeUpdate();
            conn2.close();
            ps2.close();
            Thread.currentThread().stop();
        }

    }

    public void battlesetup(String activeDeckId, String userEquipId, String questId, String questPhase, String followerId, String followerClassId,String followerSupportDeckId, String followerType, UserInfo userInfo) throws SQLException {
        String lastAccessTime = String.valueOf(System.currentTimeMillis() / 1000L);
        List<BasicNameValuePair> params = new ArrayList();
        params.add(new BasicNameValuePair("activeDeckId", activeDeckId));
        params.add(new BasicNameValuePair("followerId", followerId));
        params.add(new BasicNameValuePair("userEquipId", userEquipId));
        params.add(new BasicNameValuePair("routeSelect", "[]"));
        params.add(new BasicNameValuePair("choiceRandomLimitCounts", "{}"));
        params.add(new BasicNameValuePair("questId", questId));
        params.add(new BasicNameValuePair("questPhase", questPhase));
        params.add(new BasicNameValuePair("idempotencyKey", UUID.randomUUID().toString().toLowerCase()));
        params.add(new BasicNameValuePair("followerClassId", followerClassId));
        params.add(new BasicNameValuePair("itemId", "0"));
        params.add(new BasicNameValuePair("enemySelect", "0"));
        params.add(new BasicNameValuePair("questSelect", "0"));
        params.add(new BasicNameValuePair("followerType", followerType));
        params.add(new BasicNameValuePair("followerRandomLimitCount", "0"));
        params.add(new BasicNameValuePair("followerSupportDeckId", followerSupportDeckId));
        params.add(new BasicNameValuePair("userId", userInfo.getUserId()));
        params.add(new BasicNameValuePair("authKey", userInfo.getAuthKey()));
        params.add(new BasicNameValuePair("appVer", Main.appVer));
        params.add(new BasicNameValuePair("dateVer", Main.dateVer));
        params.add(new BasicNameValuePair("lastAccessTime", lastAccessTime));
        params.add(new BasicNameValuePair("verCode", "e92c481b51ff8203344cf768b2f5bc84b14409bc2ad7084292c9370ea97621de"));
        params.add(new BasicNameValuePair("dataVer", Main.dataVer));
        String sign = (new AuthCode()).getSign(userInfo, params);
        params.add(new BasicNameValuePair("authCode", sign));
        String result = (new PostRequest()).sendPost(userInfo.getCookie(), "https://game.fate-go.jp/battle/setup?_userId=" + userInfo.getUserId(), params);
        JSONObject jsonObject = JSONObject.parseObject(result);
        if (jsonObject.getJSONArray("response").getJSONObject(0).getString("resCode").equals("00")) {
            String battleId = jsonObject.getJSONObject("cache").getJSONObject("replaced").getJSONArray("battle").getJSONObject(0).getString("id");
            try {
                Thread.currentThread();
                Thread.sleep((long)((30 + ((new Random()).nextInt(21) - 10)) * 1000));
            } catch (InterruptedException var18) {
                var18.printStackTrace();
            }

            this.battleresult(battleId, userInfo);
        } else {
            Connection conn2 = Hikari.getConnection();
            String sql2 = "update `order` set message=?,status='订单错误' where `order`=? and status='执行中'";
            PreparedStatement ps2 = conn2.prepareStatement(sql2);
            ps2.setString(1, jsonObject.getJSONArray("response").getJSONObject(0).getJSONObject("fail").getString("detail"));
            ps2.setString(2, userInfo.getOrder());
            ps2.executeUpdate();
            conn2.close();
            ps2.close();
            Thread.currentThread().stop();
        }

    }

    public void battleresult(String battleId, UserInfo userInfo) throws SQLException {
        String lastAccessTime = String.valueOf(System.currentTimeMillis() / 1000L);
        List<BasicNameValuePair> params = new ArrayList();
        String battleStatus = new BattleStatus().getBattleStatus(Long.parseLong(battleId),Long.parseLong(userInfo.getUserId()));
        String re = "{\"battleId\":" + battleId + ",\"battleResult\":1,\"winResult\":1,\"scores\":\"\",\"action\":\"{ \\\"logs\\\":[{\\\"uid\\\":1,\\\"ty\\\":2},{\\\"uid\\\":3,\\\"ty\\\":2},{\\\"uid\\\":1,\\\"ty\\\":2},{\\\"uid\\\":3,\\\"ty\\\":2},{\\\"uid\\\":2,\\\"ty\\\":1},{\\\"uid\\\":3,\\\"ty\\\":3},{\\\"uid\\\":2,\\\"ty\\\":2},{\\\"uid\\\":3,\\\"ty\\\":3},{\\\"uid\\\":1,\\\"ty\\\":3},{\\\"uid\\\":3,\\\"ty\\\":2},{\\\"uid\\\":1,\\\"ty\\\":2},{\\\"uid\\\":3,\\\"ty\\\":1},{\\\"uid\\\":3,\\\"ty\\\":3},{\\\"uid\\\":1,\\\"ty\\\":1},{\\\"uid\\\":3,\\\"ty\\\":3},{\\\"uid\\\":4,\\\"ty\\\":2},{\\\"uid\\\":3,\\\"ty\\\":1},{\\\"uid\\\":1,\\\"ty\\\":1},{\\\"uid\\\":3,\\\"ty\\\":3},{\\\"uid\\\":3,\\\"ty\\\":2},{\\\"uid\\\":1,\\\"ty\\\":2},{\\\"uid\\\":1,\\\"ty\\\":2},{\\\"uid\\\":3,\\\"ty\\\":1},{\\\"uid\\\":4,\\\"ty\\\":2},{\\\"uid\\\":4,\\\"ty\\\":2},{\\\"uid\\\":3,\\\"ty\\\":3},{\\\"uid\\\":1,\\\"ty\\\":1}], \\\"dt\\\":[{\\\"uniqueId\\\":13,\\\"hp\\\":0,\\\"atk\\\":6121},{\\\"uniqueId\\\":14,\\\"hp\\\":0,\\\"atk\\\":6121},{\\\"uniqueId\\\":15,\\\"hp\\\":0,\\\"atk\\\":7493}], \\\"hd\\\":\\\"AA==\\\", \\\"data\\\":\\\"AA==\\\" }\",\"raidResult\":\"[]\",\"superBossResult\":\"[]\",\"elapsedTurn\":3,\"recordType\":1,\"recordValueJson\":{\"turnMaxDamage\":0,\"knockdownNum\":0,\"totalDamageToAliveEnemy\":0},\"tdPlayed\":\"[]\",\"usedEquipSkillList\":{},\"svtCommonFlagList\":{},\"skillShiftUniqueIds\": [],\"skillShiftNpcSvtIds\": [],\"aliveUniqueIds\":[],\"battleStatus\":"+battleStatus+",\"voicePlayedList\":\"[]\",\"usedTurnList\":[3,3,3]}";
        params.add(new BasicNameValuePair("result", AES.encryptWithAesCBC(re)));
        params.add(new BasicNameValuePair("userId", userInfo.getUserId()));
        params.add(new BasicNameValuePair("authKey", userInfo.getAuthKey()));
        params.add(new BasicNameValuePair("appVer", Main.appVer));
        params.add(new BasicNameValuePair("dateVer", Main.dateVer));
        params.add(new BasicNameValuePair("idempotencyKey", UUID.randomUUID().toString().toLowerCase()));
        params.add(new BasicNameValuePair("lastAccessTime", lastAccessTime));
        params.add(new BasicNameValuePair("verCode", "e92c481b51ff8203344cf768b2f5bc84b14409bc2ad7084292c9370ea97621de"));
        params.add(new BasicNameValuePair("dataVer", Main.dataVer));
        String sign = (new AuthCode()).getSign(userInfo, params);
        params.add(new BasicNameValuePair("authCode", sign));
        String result = (new PostRequest()).sendPost(userInfo.getCookie(), "https://game.fate-go.jp/battle/result?_userId=" + userInfo.getUserId(), params);
        JSONObject jsonObject = JSONObject.parseObject(result);
        if (jsonObject.getJSONArray("response").getJSONObject(0).getString("resCode").equals("00")) {
            Connection conn2 = Hikari.getConnection();
            String sql2 = "update `order` set message=?,status='已完成' where `order`=? and status='执行中'";
            PreparedStatement ps2 = conn2.prepareStatement(sql2);
            ps2.setString(1, "订单完成");
            ps2.setString(2, userInfo.getOrder());
            ps2.executeUpdate();
            conn2.close();
            ps2.close();
            Thread.currentThread().stop();
        } else {
            Connection conn2 = Hikari.getConnection();
            String sql2 = "update `order` set message=?,status='订单错误' where `order`=? and status='执行中'";
            PreparedStatement ps2 = conn2.prepareStatement(sql2);
            ps2.setString(1, jsonObject.getJSONArray("response").getJSONObject(0).getJSONObject("fail").getString("detail"));
            ps2.setString(2, userInfo.getOrder());
            ps2.executeUpdate();
            conn2.close();
            ps2.close();
            Thread.currentThread().stop();
        }

    }
}