package com.taotao.content.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.EnableLoadTimeWeaving;
import org.springframework.stereotype.Service;

import com.mysql.fabric.xmlrpc.base.Array;
import com.taotao.common.pojo.EasyUITreeNode;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.content.service.ContentCategoryService;
import com.taotao.mapper.TbContentCategoryMapper;
import com.taotao.pojo.TbContentCategory;
import com.taotao.pojo.TbContentCategoryExample;
import com.taotao.pojo.TbContentCategoryExample.Criteria;


/**
 * 内容分类管理Service
 * @author yyj
 *
 */

@Service
public class ContentCategoryServiceImpl implements ContentCategoryService {

	@Autowired
	private TbContentCategoryMapper contentCategoryMapper;
	@Override
	public List<EasyUITreeNode> getContentCategoryList(long parentId) {
		//根据parentId查询子节点列表
		TbContentCategoryExample example = new TbContentCategoryExample();
		//设置查询条件
		Criteria criteria = example.createCriteria();
		criteria.andParentIdEqualTo(parentId);
		List<TbContentCategory> list = contentCategoryMapper.selectByExample(example);
		List<EasyUITreeNode> resultList = new ArrayList<>();
		for (TbContentCategory tbContentCategory : list) {
			EasyUITreeNode node = new EasyUITreeNode();
			node.setId(tbContentCategory.getId());
			node.setText(tbContentCategory.getName());
			node.setState(tbContentCategory.getIsParent()?"closed":"open");
			//添加到结果列表
			resultList.add(node);
		}
		return resultList;
	}
	
	/*
	 * 创建分类
	 */
	@Override
	public TaotaoResult addContentCategory(Long parentId, String name) {
		//创建一个pojo对象
		TbContentCategory contentCategory = new TbContentCategory();
		//补全对象属性
		contentCategory.setParentId(parentId);
		contentCategory.setName(name);
		//状态。可选值:1(正常),2(删除)
		contentCategory.setStatus(1);
		//排序，默认为1
		contentCategory.setSortOrder(1);
		contentCategory.setIsParent(false);
		contentCategory.setCreated(new Date());
		contentCategory.setUpdated(new Date());
		//插入到数据库
		contentCategoryMapper.insert(contentCategory);
		//判断父节点的状态
		TbContentCategory parent = contentCategoryMapper.selectByPrimaryKey(parentId);
		if (!parent.getIsParent()) {
			//如果父节点为叶子节点应该改成父节点
			parent.setIsParent(true);
			//更新父节点
			contentCategoryMapper.updateByPrimaryKey(parent);
		}
		//返回结果
		return TaotaoResult.ok(contentCategory);
	}
	
	/*
	 * 重命名分类
	 */
	@Override
	public TaotaoResult updateContentCategory(Long id, String name) {
		//创建tb_content_category表对应的pojo对象
		TbContentCategory node = contentCategoryMapper.selectByPrimaryKey(id);
		//更新名字
		node.setName(name);
		contentCategoryMapper.updateByPrimaryKey(node);
		return null;
	}
	
	/*
	 * 删除分类
	 */
	@Override
	public TaotaoResult deleteContentCategory(Long id) {
		TbContentCategory contentCategory = contentCategoryMapper.selectByPrimaryKey(id);
        if (contentCategory.getIsParent()) {
            List<EasyUITreeNode> list = getContentCategoryList(id);
            // 递归删除
            for (EasyUITreeNode tbcontentCategory : list) {
                deleteContentCategory(tbcontentCategory.getId());
            }
        }
            if (getContentCategoryList(contentCategory.getParentId()).size() == 1) {
                TbContentCategory parentCategory = contentCategoryMapper
                        .selectByPrimaryKey(contentCategory.getParentId());
                parentCategory.setIsParent(false);
                contentCategoryMapper.updateByPrimaryKey(parentCategory);
            }
            contentCategoryMapper.deleteByPrimaryKey(id);
            return TaotaoResult.ok();
	}

}
