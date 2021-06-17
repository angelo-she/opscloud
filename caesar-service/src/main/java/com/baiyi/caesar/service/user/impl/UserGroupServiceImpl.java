package com.baiyi.caesar.service.user.impl;

import com.baiyi.caesar.domain.DataTable;
import com.baiyi.caesar.domain.generator.caesar.UserGroup;
import com.baiyi.caesar.domain.param.user.UserBusinessPermissionParam;
import com.baiyi.caesar.domain.param.user.UserGroupParam;
import com.baiyi.caesar.mapper.caesar.UserGroupMapper;
import com.baiyi.caesar.service.user.UserGroupService;
import com.baiyi.caesar.util.SQLUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author baiyi
 * @Date 2021/6/16 3:16 下午
 * @Version 1.0
 */
@Service
public class UserGroupServiceImpl implements UserGroupService {

    @Resource
    private UserGroupMapper userGroupMapper;

    @Override
    public void add(UserGroup userGroup) {
        userGroupMapper.insert(userGroup);
    }

    @Override
    public void update(UserGroup userGroup) {
        userGroupMapper.updateByPrimaryKey(userGroup);
    }

    @Override
    public DataTable<UserGroup> queryPageByParam(UserGroupParam.UserGroupPageQuery pageQuery) {
        Page page = PageHelper.startPage(pageQuery.getPage(), pageQuery.getLength());
        Example example = new Example(UserGroup.class);
        Example.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotBlank(pageQuery.getQueryName())) {
            String likeName = SQLUtil.toLike(pageQuery.getQueryName());
            criteria.andLike("name", likeName);
        }
        example.setOrderByClause("create_time");
        List<UserGroup> data = userGroupMapper.selectByExample(example);
        return new DataTable<>(data, page.getTotal());

    }

    @Override
    public DataTable<UserGroup> queryPageByParam(UserBusinessPermissionParam.UserBusinessPermissionPageQuery pageQuery) {
        Page page = PageHelper.startPage(pageQuery.getPage(), pageQuery.getLength());
        List<UserGroup> data = userGroupMapper.queryUserPermissionGroupByParam(pageQuery);
        return new DataTable<>(data, page.getTotal());
    }

}
