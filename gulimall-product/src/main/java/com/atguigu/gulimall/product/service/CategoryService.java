package com.atguigu.gulimall.product.service;

import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.product.entity.CategoryEntity;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 商品三级分类
 *
 * @author xiyan
 * @email xiyan@gmail.com
 * @date 2022-03-17 15:18:28
 */
public interface CategoryService extends IService<CategoryEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<CategoryEntity> listWithTree();

    void removeCategoryByIds(Collection<Long> ids);

    /**
     * 查找 分类路径
     * 父-子-孙
     * @param categoryId 分类id
     * @return
     */
    Long[] findCategoryPath(Long categoryId);
}

