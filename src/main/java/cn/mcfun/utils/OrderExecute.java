package cn.mcfun.utils;

import cn.mcfun.api.GachaDraw;
import cn.mcfun.api.JPLogin;
import cn.mcfun.api.Present;
import cn.mcfun.entity.UserInfo;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.http.impl.client.BasicCookieStore;

public class OrderExecute implements Runnable {
    private Order order;

    public OrderExecute(Order order) {
        this.order = order;
    }

    public void run() {
        try {
            this.login();
        } catch (SQLException var2) {
            if (!var2.toString().contains("you can no longer use it")) {
                var2.printStackTrace();
            }
        }

    }

    public void login() throws SQLException {
        Connection conn2 = Hikari.getConnection();
        String sql2 = "update `order` set status='执行中' where `order`=" + this.order.getOrderId() + " and status!='执行中'";
        PreparedStatement ps2 = conn2.prepareStatement(sql2);
        ps2.executeUpdate();
        conn2.close();
        ps2.close();
        UserInfo userInfo = new UserInfo();
        userInfo.setCookie(new BasicCookieStore());
        userInfo.setOrder(this.order.getOrderId());
        conn2 = Hikari.getConnection();
        sql2 = "select `user_id`,`auth_key`,`sec_key` from `order` where `order`=?";
        ps2 = conn2.prepareStatement(sql2);
        ps2.setString(1, this.order.getOrderId());
        ResultSet rs = ps2.executeQuery();

        while(rs.next()) {
            userInfo.setUserId(rs.getString("user_id"));
            userInfo.setAuthKey(rs.getString("auth_key"));
            userInfo.setSecretKey(rs.getString("sec_key"));
        }

        conn2.close();
        ps2.close();
        Connection conn = Hikari.getConnection();
        String sql = "update `order` set message=? where `order`=?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, "正在登录游戏");
        ps.setString(2, this.order.getOrderId());
        ps.executeUpdate();
        conn.close();
        ps.close();
        (new JPLogin()).topLogin(userInfo);

/*

        conn = C3P0Utils.getConnection();
        sql = "update `order` set message=? where `order`=?";
        ps = conn.prepareStatement(sql);
        ps.setString(1, "正在刷图");
        ps.setString(2, this.order.getOrderId());
        ps.executeUpdate();
        conn.close();
        ps.close();
        String[] arr = new FollowerList().followerlist1("93000001","3",userInfo);
        new Battle().battlesetup(userInfo.getActiveDeckId(), userInfo.getUserEquipId(), "93000001","3",arr[0],arr[2],arr[1],userInfo);

*/

        conn = Hikari.getConnection();
        sql = "update `order` set message=? where `order`=?";
        ps = conn.prepareStatement(sql);
        ps.setString(1, "正在领取礼物盒");
        ps.setString(2, this.order.getOrderId());
        ps.executeUpdate();
        conn.close();
        ps.close();
        (new Present()).presentlist(userInfo);
        conn = Hikari.getConnection();
        sql = "update `order` set message=? where `order`=?";
        ps = conn.prepareStatement(sql);
        ps.setString(1, "正在抽卡中");
        ps.setString(2, this.order.getOrderId());
        ps.executeUpdate();
        conn.close();
        ps.close();
        (new GachaDraw()).start(userInfo);
    }
}
