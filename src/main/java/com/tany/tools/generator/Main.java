package com.tany.tools.generator;

import com.tany.tools.generator.service.GeneratorService;

/**
 * @author hny
 * @version V1.0
 * @date 2017/11/8
 * @modified
 * @since jdk1.8
 */
public class Main {
    // 主方法
    public static void main(String[] args) throws Exception {
        String[] tableNames = {"t_dg_employ","t_dg_organ"};
        String path = "B:/back/";
        GeneratorService service = new GeneratorService();
        service.generatorCode(path, tableNames);
    }
}
