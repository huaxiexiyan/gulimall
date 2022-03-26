package com.atguigu.gulimall.product.service.impl;

import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;
import com.atguigu.gulimall.product.dao.AttrGroupDao;
import com.atguigu.gulimall.product.entity.AttrGroupEntity;
import com.atguigu.gulimall.product.service.AttrGroupService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                new QueryWrapper<AttrGroupEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params, Long categoryId) {
        LambdaQueryWrapper<AttrGroupEntity> queryWrapper = Wrappers.lambdaQuery();
        if (categoryId != 0) {
            queryWrapper.eq(AttrGroupEntity::getCatelogId, categoryId);
        }
        if (params.containsKey("key")) {
            Object key = params.get("key");
            queryWrapper.and((obj) -> obj.eq(AttrGroupEntity::getAttrGroupId, key)
                    .or().like(AttrGroupEntity::getAttrGroupName, key));
        }
        IPage<AttrGroupEntity> page = baseMapper.selectPage(new Query<AttrGroupEntity>().getPage(params),
                queryWrapper);
        return new PageUtils(page);
    }

}