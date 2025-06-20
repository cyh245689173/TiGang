//package com.copico.config.nacos;
//
//import com.alibaba.nacos.api.NacosFactory;
//import com.alibaba.nacos.api.PropertyKeyConst;
//import com.alibaba.nacos.api.config.annotation.NacosValue;
//import com.alibaba.nacos.api.exception.NacosException;
//import com.alibaba.nacos.api.naming.NamingService;
//import jakarta.annotation.PostConstruct;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//
//import java.util.Properties;
//
///**
// * @author 陈玉皓
// * @date 2025/6/4 21:04
// * @description: TODO
// */
////@Data
////@NoArgsConstructor
////@Component
//public class LoadNacosProperty {
//    @NacosValue(value = "${nacos.discovery.server-addr}", autoRefreshed = true)
//    private String service;
//    @Value("${nacos.config.namespace}")
//    private String nameSpace;
//    @Value("${nacos.config.group}")
//    private String groupName;
//    @Value("${nacos.config.username}")
//    private String username;
//    @Value("${nacos.config.password}")
//    private String password;
//    private NamingService namingService;
//    private final String NACOS_USERNAME = "username";
//    private final String NACOS_PASSWORD = "password";
//
//    @PostConstruct
//    public void init() throws NacosException {
//        Properties properties = new Properties();
//        // Nacos 服务器地址
//        properties.put(PropertyKeyConst.SERVER_ADDR, service);
//        // 指定命名空间TD
//        properties.put(PropertyKeyConst.NAMESPACE, nameSpace);
//        properties.put(NACOS_USERNAME, username);
//        properties.put(NACOS_PASSWORD, password);
//        namingService = NacosFactory.createNamingService(properties);
//    }
//}
//
//
//
