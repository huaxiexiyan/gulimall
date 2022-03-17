package com.atguigu.gulimall.order;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * <p>
 *
 * </p>
 *
 * @author xiyan
 * @version V1.0
 * @description
 * @date Created in 2022-02-06 20:23
 */
@EnableEncryptableProperties
@MapperScan("com.atguigu.gulimall.order.dao")
@SpringBootApplication
public class OrderApp {
    public static void main(String[] args) {
        SpringApplication.run(OrderApp.class, args);
    }
}
