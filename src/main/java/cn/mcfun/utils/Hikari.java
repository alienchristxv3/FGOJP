package cn.mcfun.utils;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class Hikari {
    private static DataSource datasource;
    private static DataSource getDataSource()
    {
        if(datasource == null)
        {
            Properties props = new Properties();
            try {
                props.load(new FileInputStream("config.properties"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl("jdbc:mysql://" + props.getProperty("url") + ":" + props.getProperty("port") + "/jp_gacha?useUnicode=true&characterEncoding=utf-8&autoReconnect=true&serverTimezone=UTC");
            config.setUsername(props.getProperty("user"));
            config.setPassword(props.getProperty("password"));
            config.setMinimumIdle(20);
            config.setMaximumPoolSize(1000);
            datasource = new HikariDataSource(config);
        }
        return datasource;
    }
    public static Connection getConnection() {
        try {
            return getDataSource().getConnection();
        } catch (SQLException e) {
            if (!e.toString().contains("you can no longer use it")) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
