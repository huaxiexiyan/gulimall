package com.atguigu.gulimall.product.service.impl;

import com.atguigu.common.constant.CommonConstant;
import com.atguigu.common.constant.ProductConstant;
import com.atguigu.common.tool.BeanUtils;
import com.atguigu.common.tool.CollectionUtils;
import com.atguigu.common.tool.ObjectUtils;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;
import com.atguigu.gulimall.product.dao.AttrAttrgroupRelationDao;
import com.atguigu.gulimall.product.dao.AttrDao;
import com.atguigu.gulimall.product.dao.AttrGroupDao;
import com.atguigu.gulimall.product.dao.CategoryDao;
import com.atguigu.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.atguigu.gulimall.product.entity.AttrEntity;
import com.atguigu.gulimall.product.entity.AttrGroupEntity;
import com.atguigu.gulimall.product.entity.CategoryEntity;
import com.atguigu.gulimall.product.service.AttrService;
import com.atguigu.gulimall.product.service.CategoryService;
import com.atguigu.gulimall.product.vo.AttrGroupRelationVo;
import com.atguigu.gulimall.product.vo.AttrRespVo;
import com.atguigu.gulimall.product.vo.AttrVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {

    private final CategoryDao categoryDao;
    private final AttrGroupDao attrGroupDao;
    private final AttrAttrgroupRelationDao attrAttrgroupRelationDao;
    private final CategoryService categoryService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                new QueryWrapper<AttrEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params, Long catelogId, String attrType) {
        LambdaQueryWrapper<AttrEntity> queryWrapper = Wrappers.<AttrEntity>lambdaQuery()
                .eq(AttrEntity::getAttrType, ProductConstant.AttrEnum.getAttrEnum(attrType).getCode());
        if (CommonConstant.QueryConstant.isNotQueryAll(catelogId)) {
            queryWrapper.eq(AttrEntity::getCatelogId, catelogId);
        }
        Object key = params.get(CommonConstant.QueryConstant.QUERY_KEY);
        if (ObjectUtils.isNotEmpty(key)) {
            queryWrapper.and((obj) -> obj.eq(AttrEntity::getAttrId, key)
                    .or().like(AttrEntity::getAttrName, key));
        }
        IPage<AttrEntity> page = this.page(new Query<AttrEntity>().getPage(params), queryWrapper);
        PageUtils pageUtils = new PageUtils(page);
        List<AttrEntity> records = page.getRecords();

        // 获取分组名称map
        Set<Long> catelogIds = new HashSet<>(records.size());
        Set<Long> attrIds = new HashSet<>(records.size());
        records.forEach(r -> {
            catelogIds.add(r.getCatelogId());
            attrIds.add(r.getAttrId());
        });
        Map<Long, String> attrIdToGroupNameMap = new HashMap<>();
        Map<Long, String> catelogIdToNameMap = new HashMap<>();
        // 分组名称
        if (CollectionUtils.isNotEmpty(attrIds) &&
                ProductConstant.AttrEnum.ATTR_TYPE_BASE.equals(ProductConstant.AttrEnum.getAttrEnum(attrType))) {
            List<AttrAttrgroupRelationEntity> attrAttrgroupRelationEntities = attrAttrgroupRelationDao.selectList(Wrappers.<AttrAttrgroupRelationEntity>lambdaQuery()
                    .in(AttrAttrgroupRelationEntity::getAttrId, attrIds));
            List<Long> cateGroupIds = attrAttrgroupRelationEntities.stream()
                    .map(AttrAttrgroupRelationEntity::getAttrGroupId)
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(cateGroupIds)) {
                List<AttrGroupEntity> attrGroupEntities = attrGroupDao.selectBatchIds(cateGroupIds);
                Map<Long, String> attrGroupIdToNameMap = attrGroupEntities.stream()
                        .collect(Collectors.toMap(AttrGroupEntity::getAttrGroupId, AttrGroupEntity::getAttrGroupName));
                Map<Long, String> attrIdToGroupNameMapTmp = attrAttrgroupRelationEntities.stream()
                        .collect(Collectors.toMap(AttrAttrgroupRelationEntity::getAttrId, v -> attrGroupIdToNameMap.getOrDefault(v.getAttrGroupId(), "")));
                attrIdToGroupNameMap.putAll(attrIdToGroupNameMapTmp);
            }
        }

        //获取分类名称map
        if (CollectionUtils.isNotEmpty(catelogIds)) {
            List<CategoryEntity> categoryEntities = categoryDao.selectBatchIds(catelogIds);
            Map<Long, String> catelogIdToNameMapTemp = categoryEntities.stream()
                    .collect(Collectors.toMap(CategoryEntity::getCatId, CategoryEntity::getName));
            catelogIdToNameMap.putAll(catelogIdToNameMapTemp);
        }

        List<AttrRespVo> attrVOList = records.stream().map(attr -> {
            AttrRespVo attrVO = new AttrRespVo();
            BeanUtils.copyProperties(attr, attrVO);
            //catelogName": "手机/数码/手机", //所属分类名字
            //"groupName": "主体", //所属分组名字
            if (ObjectUtils.isNotEmpty(attr.getCatelogId())) {
                attrVO.setCatelogName(catelogIdToNameMap.get(attr.getCatelogId()));
            }
            if (ObjectUtils.isNotEmpty(attr.getAttrId())) {
                attrVO.setGroupName(attrIdToGroupNameMap.get(attr.getAttrId()));
            }
            return attrVO;
        }).collect(Collectors.toList());

        pageUtils.setList(attrVOList);
        return pageUtils;
    }

    @Override
    public List<AttrEntity> getRelationAttr(Long attrgroupId) {
        List<Long> attrIds = getRelationAttrIds(Collections.singleton(attrgroupId));
        return baseMapper.selectBatchIds(attrIds);
    }

    private List<Long> getRelationAttrIds(Collection<Long> attrgroupIds) {
        List<AttrAttrgroupRelationEntity> attrRelations = attrAttrgroupRelationDao.selectList(Wrappers.<AttrAttrgroupRelationEntity>lambdaQuery()
                .in(AttrAttrgroupRelationEntity::getAttrGroupId, attrgroupIds));
        return attrRelations.stream()
                .map(AttrAttrgroupRelationEntity::getAttrId)
                .collect(Collectors.toList());
    }

    @Override
    public PageUtils getNoRelationAttr(Map<String, Object> params, Long attrgroupId) {
        // 1、只能关联同一个分类下的属性
        AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrgroupId);
        // 2、查出内有被关联过的属性
        List<AttrGroupEntity> attrGroupEntities = attrGroupDao.selectList(Wrappers.<AttrGroupEntity>lambdaQuery()
                .eq(AttrGroupEntity::getCatelogId, attrGroupEntity.getCatelogId()));
        List<Long> attrgroupIds = attrGroupEntities.stream().map(AttrGroupEntity::getAttrGroupId).collect(Collectors.toList());
        List<Long> attrRelationIds = getRelationAttrIds(attrgroupIds);
        // 3、最终查询条件
        LambdaQueryWrapper<AttrEntity> queryWrapper = Wrappers.<AttrEntity>lambdaQuery()
                .eq(AttrEntity::getCatelogId, attrGroupEntity.getCatelogId())
                .notIn(AttrEntity::getAttrId, attrRelationIds);
        Object key = params.get(CommonConstant.QueryConstant.QUERY_KEY);
        if (ObjectUtils.isNotEmpty(key)) {
            queryWrapper.and((obj) -> obj.eq(AttrEntity::getAttrId, key)
                    .or().like(AttrEntity::getAttrName, key));
        }
        IPage<AttrEntity> page = this.page(new Query<AttrEntity>().getPage(params), queryWrapper);
        return new PageUtils(page);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateAttr(AttrVo attr) {
        // 查询更新前的关系
        AttrEntity updateAttrEntity = new AttrEntity();
        BeanUtils.copyProperties(attr, updateAttrEntity);
        baseMapper.updateById(updateAttrEntity);

        if (ProductConstant.AttrEnum.ATTR_TYPE_BASE.equals(ProductConstant.AttrEnum.getAttrEnum(attr.getAttrType()))
                && ObjectUtils.isNotEmpty(attr.getAttrGroupId())) {
            AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
            BeanUtils.copyProperties(attr, relationEntity);
            AttrAttrgroupRelationEntity dbRelationEntity = attrAttrgroupRelationDao.selectOne(Wrappers.<AttrAttrgroupRelationEntity>lambdaQuery()
                    .eq(AttrAttrgroupRelationEntity::getAttrId, attr.getAttrId())
                    .eq(AttrAttrgroupRelationEntity::getAttrGroupId, attr.getAttrGroupId()));
            if (ObjectUtils.isEmpty(dbRelationEntity)) {
                attrAttrgroupRelationDao.insert(relationEntity);
            } else {
                relationEntity.setId(dbRelationEntity.getId());
                attrAttrgroupRelationDao.updateById(relationEntity);
            }
        }
    }

    @Override
    public AttrRespVo getAttrInfo(Long attrId) {
        AttrEntity attrEntity = baseMapper.selectById(attrId);
        AttrRespVo attrRespVo = new AttrRespVo();
        BeanUtils.copyProperties(attrEntity, attrRespVo);
        attrRespVo.setCatelogPath(categoryService.findCategoryPath(attrEntity.getCatelogId()));

        if (ProductConstant.AttrEnum.ATTR_TYPE_BASE.equals(ProductConstant.AttrEnum.getAttrEnum(attrEntity.getAttrType()))) {
            AttrAttrgroupRelationEntity relationEntity = attrAttrgroupRelationDao.selectOne(Wrappers.<AttrAttrgroupRelationEntity>lambdaQuery()
                    .eq(AttrAttrgroupRelationEntity::getAttrId, attrId));
            attrRespVo.setAttrGroupId(relationEntity.getAttrGroupId());
        }
        return attrRespVo;
    }

    @Override
    public void saveAttr(AttrVo attr) {
        AttrEntity insertAttrEntity = new AttrEntity();
        BeanUtils.copyProperties(attr, insertAttrEntity);
        baseMapper.insert(insertAttrEntity);

        if (ProductConstant.AttrEnum.ATTR_TYPE_BASE.equals(ProductConstant.AttrEnum.getAttrEnum(attr.getAttrType()))
                && ObjectUtils.isNotEmpty(attr.getAttrGroupId())) {
            AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
            relationEntity.setAttrId(insertAttrEntity.getAttrId());
            relationEntity.setAttrGroupId(attr.getAttrGroupId());
            attrAttrgroupRelationDao.insert(relationEntity);
        }
    }

    @Override
    public void deleteRelation(AttrGroupRelationVo[] vos) {
        List<AttrAttrgroupRelationEntity> entities = Arrays.stream(vos).map((item) -> {
            AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
            org.springframework.beans.BeanUtils.copyProperties(item, relationEntity);
            return relationEntity;
        }).collect(Collectors.toList());
        attrAttrgroupRelationDao.deleteBatchRelation(entities);
    }

    @Override
    public boolean removeByIds(Collection<?> list) {
        if (CollectionUtils.isEmpty(list)) {
            return false;
        }
        attrAttrgroupRelationDao.delete(Wrappers.<AttrAttrgroupRelationEntity>lambdaQuery()
                .in(AttrAttrgroupRelationEntity::getAttrId, list));
        return SqlHelper.retBool(getBaseMapper().deleteBatchIds(list));
    }
}