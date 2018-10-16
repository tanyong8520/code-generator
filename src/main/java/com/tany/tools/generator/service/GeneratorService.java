package com.tany.tools.generator.service;

import com.tany.tools.generator.utils.JdbcUtils;
import com.tany.tools.generator.utils.GeneratorUtils;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.zip.ZipOutputStream;

/**
 * 代码生成工具类
 *
 * @author hny
 * @version V1.0
 * @date 2017/11/8
 * @modified
 * @since jdk1.8
 */
public class GeneratorService {

    private Map<String, String> queryTable(String tableName) {
        String sql = "select table_name tableName, engine, table_comment tableComment, create_time createTime" +
                " from information_schema.tables where table_schema = (select database()) and table_name = '" + tableName + "'";
        return JdbcUtils.query(sql).get(0);
    }

    private List<Map<String, String>> queryColumns(String tableName) {
        String sql = "select column_name columnName, data_type dataType, column_comment columnComment, column_key columnKey, extra " +
                "from information_schema.columns where table_schema = (select database()) and table_name = '" + tableName + "'  order by ordinal_position";
        return JdbcUtils.query(sql);
    }

    private boolean exists(String tableName) {
        String sql = "select count(*) count from information_schema.tables where table_name='" + tableName + "'";
        return !Objects.equals("0", JdbcUtils.query(sql).get(0).get("count"));
    }

    private byte[] generatorByte(String[] tableNames) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ZipOutputStream zip = new ZipOutputStream(outputStream);

        for (String tableName : tableNames) {
            //查询表信息
            Map<String, String> table = queryTable(tableName);
            //查询列信息
            List<Map<String, String>> columns = queryColumns(tableName);
            //生成代码
            GeneratorUtils.generatorCode(table, columns, zip);
        }
        IOUtils.closeQuietly(zip);
        return outputStream.toByteArray();
    }

    /**
     * 将代码文件生成到指定路径下
     *
     * @param path       文件路径
     * @param tableNames 表名
     * @throws Exception
     */
    public void generatorCode(String path, String... tableNames) throws Exception {
        for (String tableName : tableNames) {
            if (!exists(tableName)) {
                System.out.println("表'" + tableName + "'不存在");
                return;
            }
        }
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        file = new File(file.getCanonicalPath() + File.separator + "code.zip");

        byte[] b = generatorByte(tableNames);
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(b);
        fos.close();
        System.out.println("生成代码成功，文件路径：" + file.getCanonicalPath());
    }
}
