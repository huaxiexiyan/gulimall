package com.atguigu.gulimall.product;

import com.atguigu.gulimall.product.entity.BrandEntity;
import com.atguigu.gulimall.product.service.BrandService;
import org.jasypt.encryption.StringEncryptor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class ProductAppTest {

    @Autowired
    BrandService brandService;
    @Autowired
    StringEncryptor stringEncryptor;
    @Test
    void main() {
        List<BrandEntity> list = brandService.list();
        System.out.println(list);
    }

    @Test
    void encrypt() {
        System.out.println(stringEncryptor.encrypt("LTAI5tMvo4STdr5so1L9T2tX"));
        System.out.println(stringEncryptor.encrypt("LLzh9yLXoYKzckpX4Awgci6NEKLECb"));
    }


}