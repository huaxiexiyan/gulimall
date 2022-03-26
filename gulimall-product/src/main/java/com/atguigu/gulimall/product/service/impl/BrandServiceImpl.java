package com.atguigu.gulimall.product.service.impl;

import com.atguigu.common.tool.ObjectUtils;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;
import com.atguigu.gulimall.product.dao.BrandDao;
import com.atguigu.gulimall.product.dao.CategoryBrandRelationDao;
import com.atguigu.gulimall.product.entity.BrandEntity;
import com.atguigu.gulimall.product.entity.CategoryBrandRelationEntity;
import com.atguigu.gulimall.product.service.BrandService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@AllArgsConstructor
@Service("brandService")
public class BrandServiceImpl extends ServiceImpl<BrandDao, BrandEntity> implements BrandService {

    private final CategoryBrandRelationDao categoryBrandRelationDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<BrandEntity> page = this.page(
                new Query<BrandEntity>().getPage(params),
                new QueryWrapper<BrandEntity>()
        );

        return new PageUtils(page);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateById(BrandEntity entity) {
        if (ObjectUtils.isNotEmpty(entity.getName())) {
            BrandEntity brand = baseMapper.selectById(entity.getBrandId());
            if (ObjectUtils.nullSafeNotEquals(brand.getName(), entity.getName())) {
                categoryBrandRelationDao.update(null, Wrappers.<CategoryBrandRelationEntity>lambdaUpdate()
                        .eq(CategoryBrandRelationEntity::getBrandId, entity.getBrandId())
                        .set(CategoryBrandRelationEntity::getBrandName, entity.getName()));
            }
        }
        return super.updateById(entity);
    }

}