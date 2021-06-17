package com.baiyi.caesar.facade.user.impl;

import com.baiyi.caesar.domain.DataTable;
import com.baiyi.caesar.domain.generator.caesar.UserGroup;
import com.baiyi.caesar.domain.param.user.UserBusinessPermissionParam;
import com.baiyi.caesar.domain.param.user.UserGroupParam;
import com.baiyi.caesar.domain.types.BusinessTypeEnum;
import com.baiyi.caesar.domain.vo.user.UserGroupVO;
import com.baiyi.caesar.domain.vo.user.UserVO;
import com.baiyi.caesar.facade.user.UserGroupFacade;
import com.baiyi.caesar.facade.user.base.IUserBusinessPermissionPageQuery;
import com.baiyi.caesar.facade.user.factory.UserBusinessPermissionFactory;
import com.baiyi.caesar.packer.user.UserGroupPacker;
import com.baiyi.caesar.packer.user.UserPermissionPacker;
import com.baiyi.caesar.service.user.UserGroupService;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author baiyi
 * @Date 2021/6/16 3:20 下午
 * @Version 1.0
 */
@Service
public class UserGroupFacadeImpl implements UserGroupFacade, IUserBusinessPermissionPageQuery, InitializingBean {

    @Resource
    private UserGroupService userGroupService;

    @Resource
    private UserGroupPacker userGroupPacker;

    @Resource
    private UserPermissionPacker userPermissionPacker;

    @Override
    public int getBusinessType() {
        return BusinessTypeEnum.USERGROUP.getType();
    }

    @Override
    public DataTable<UserGroupVO.UserGroup> queryUserGroupPage(UserGroupParam.UserGroupPageQuery pageQuery) {
        DataTable<UserGroup> table = userGroupService.queryPageByParam(pageQuery);
        return new DataTable<>(userGroupPacker.wrapVOList(table.getData(), pageQuery), table.getTotalNum());
    }

    @Override
    public DataTable<UserVO.IUserPermission> queryUserBusinessPermissionPage(UserBusinessPermissionParam.UserBusinessPermissionPageQuery pageQuery) {
        DataTable<UserGroup> table = userGroupService.queryPageByParam(pageQuery);

        List<UserGroupVO.UserGroup> data = userGroupPacker.wrapVOList(table.getData(), pageQuery);
        if (pageQuery.getAuthorized()) {
            data.forEach(e -> {
                e.setUserId(pageQuery.getUserId());
                userPermissionPacker.wrap(e);
            });
        }
        return new DataTable<>(Lists.newArrayList(data), table.getTotalNum());
    }

    /**
     * 注册
     */
    @Override
    public void afterPropertiesSet() {
        UserBusinessPermissionFactory.register(this);
    }
}
