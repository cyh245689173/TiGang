package com.copico.config.nacos;

import com.alibaba.nacos.spring.context.annotation.config.NacosPropertySource;
import lombok.Data;
import org.springframework.context.annotation.Configuration;

/**
 * @author 陈玉皓
 * @date 2025/6/4 21:07
 * @description: TODO
 */
@Data
@Configuration
@NacosPropertySource(dataId = "${nacos.config.data-id}", autoRefreshed = true)
public class NacosConfig {
}
