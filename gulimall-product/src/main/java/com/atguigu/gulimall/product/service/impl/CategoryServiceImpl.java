package com.atguigu.gulimall.product.service.impl;

import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;
import com.atguigu.gulimall.product.dao.CategoryDao;
import com.atguigu.gulimall.product.entity.CategoryEntity;
import com.atguigu.gulimall.product.service.CategoryService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryEntity> listWithTree() {
        //1、 查出所有分类列表
        List<CategoryEntity> categories = baseMapper.selectList(Wrappers.emptyWrapper());
        if (CollectionUtils.isEmpty(categories)) {
            log.warn("未查询到任何分类数据");
            return new ArrayList<>();
        }
        //2、 找出根，递归找出子分类
        return categories.stream()
                .filter(c -> c.getParentCid() == 0)
                .peek(c -> c.setChildren(getChildren(c, categories)))
                .sorted(Comparator.comparingInt(c -> c.getSort() == null ? 0 : c.getSort()))
                .collect(Collectors.toList());
    }

    @Override
    public void removeCategoryByIds(Collection<Long> ids) {
        // 1、检查 分类 是否有关联数据
        baseMapper.deleteBatchIds(ids);
    }

    @Override
    public Long[] findCategoryPath(Long categoryId) {
        List<Long> categoryPathList = new ArrayList<>();
        List<Long> parentPath = findParentPath(categoryId, categoryPathList);
        //调换顺序
        Collections.reverse(parentPath);
        return parentPath.toArray(new Long[0]);
    }

    /**
     * 递归收集父分类id
     *
     * @param categoryId 分类id
     * @param categoryPath 收集器
     * @return
     */
    private List<Long> findParentPath(Long categoryId,List<Long> categoryPath){
        categoryPath.add(categoryId);
        CategoryEntity categoryEntity = baseMapper.selectById(categoryId);
        if (categoryEntity.getParentCid() != 0){
            findParentPath(categoryEntity.getParentCid(),categoryPath);
        }
        return categoryPath;
    }

    /**
     * 获取 node 节点的子集合
     *
     * @param parent
     * @param all
     * @return
     */
    private List<CategoryEntity> getChildren(CategoryEntity parent, List<CategoryEntity> all) {
        return all.stream()
                .filter(node -> parent.getCatId().equals(node.getParentCid()))
                .peek(node -> node.setChildren(getChildren(node, all)))
                .sorted(Comparator.comparingInt(node -> node.getSort() == null ? 0 : node.getSort()))
                .collect(Collectors.toList());
    }

}