package com.copico.common;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.rules.DbColumnType;

import com.baomidou.mybatisplus.generator.engine.VelocityTemplateEngine;

import java.sql.Types;
import java.util.Collections;

public class MyBatisPlusGenerator {
    public static void main(String[] args) {
        FastAutoGenerator.create("jdbc:mysql://localhost:3306/demo?useUnicode=true&useSSL=false&characterEncoding=utf8&serverTimezone=Asia/Shanghai", "root", "123456")
                .globalConfig(builder -> {
                    builder.author("yuhao.chen") // 设置作者
                            .enableSwagger() // 开启 swagger 模式
                            .outputDir("D://WorkPlace//TiGang//"); // 指定输出目录
                })
                .dataSourceConfig(builder ->
                        builder.typeConvertHandler((globalConfig, typeRegistry, metaInfo) -> {
                            int typeCode = metaInfo.getJdbcType().TYPE_CODE;
                            if (typeCode == Types.SMALLINT) {
                                // 自定义类型转换
                                return DbColumnType.INTEGER;
                            }
                            return typeRegistry.getColumnType(metaInfo);
                        })
                )
                .packageConfig(builder ->
                        builder.parent("com") // 设置父包名
                                .moduleName("copico") // 设置父包模块名
                                .pathInfo(Collections.singletonMap(OutputFile.xml, "D://WorkPlace//TiGang//")) // 设置mapperXml生成路径
                )
                .strategyConfig(builder ->
                        builder.addInclude("user") // 设置需要生成的表名
                )
                .templateEngine(new VelocityTemplateEngine())
                .execute();
    }
}