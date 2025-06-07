package com.copico.config.mybatis;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class MybatisPlusConfig {

    /**
     * 新的分页插件，
     * 一缓和二缓遵循nybatis的规则，
     * 需要设置 MybatisConfiguration#useDeprecatedExecutor =false
     * 避免缓存出现问题(该属性会在旧插件移除后一同移除)
     * 添加分页插件添加乐观锁插件
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
        return interceptor;
    }

    static class PaginationInnerInterceptor extends com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor {
        public PaginationInnerInterceptor(DbType dbType) {
            super(dbType);
            //开启溢出总页数是否进行处理
            super.setOverflow(true);
        }

        @Override
        public void handlerOverflow(IPage<?> page) {
            page.setCurrent(page.getPages());
        }
    }
}