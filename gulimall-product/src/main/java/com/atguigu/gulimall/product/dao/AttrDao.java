package com.atguigu.gulimall.product.dao;

import com.atguigu.gulimall.product.entity.AttrEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品属性
 * 
 * @author xiyan
 * @email xiyan@gmail.com
 * @date 2022-03-17 15:18:28
 */
@Mapper
public interface AttrDao extends BaseMapper<AttrEntity> {
	
}
