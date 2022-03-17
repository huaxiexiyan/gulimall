package com.atguigu.gulimall.product;

import com.atguigu.gulimall.product.entity.BrandEntity;
import com.atguigu.gulimall.product.service.BrandService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class ProductAppTest {

    @Autowired
    BrandService brandService;

    @Test
    void main() {
        List<BrandEntity> list = brandService.list();
        System.out.println(list);
    }

}