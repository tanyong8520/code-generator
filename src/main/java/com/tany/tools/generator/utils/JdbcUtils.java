package com.tany.tools.generator.utils;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author hny
 * @version V1.0
 * @date 2017/11/8
 * @modified
 * @since jdk1.8
 */
public class JdbcUtils {

    private static Configuration config;

    static {
        try {
            config = new PropertiesConfiguration("jdbc.properties");
            Class.forName(config.getString("driver"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(config.getString("url"), config.getString("user"),
                config.getString("password"));
    }

    public static List<Map<String, String>> query(String sql) {
        List<Map<String, String>> result = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement stat = conn.prepareStatement(sql);
             ResultSet rs = stat.executeQuery()) {
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            while (rs.next()) {
                Map<String, String> map = new HashMap<>(columnCount);
                for (int i = 1; i <= columnCount; i++) {
                    map.put(metaData.getColumnLabel(i), String.valueOf(rs.getObject(metaData.getColumnLabel(i))));
                }
                result.add(map);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static void main(String[] args) {
        System.out.println(query("select table_name tableName, engine, table_comment tableComment, create_time createTime from information_schema.tables " +
                "where table_schema = (select database()) and table_name = 'test'"));
    }
}
