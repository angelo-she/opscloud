package com.baiyi.caesar.facade.datasource.impl;

import com.baiyi.caesar.account.IAccountProvider;
import com.baiyi.caesar.account.factory.AccountProviderFactory;
import com.baiyi.caesar.common.base.Global;
import com.baiyi.caesar.common.exception.common.CommonRuntimeException;
import com.baiyi.caesar.domain.DataTable;
import com.baiyi.caesar.domain.ErrorEnum;
import com.baiyi.caesar.domain.generator.caesar.DatasourceAccount;
import com.baiyi.caesar.domain.generator.caesar.DatasourceAccountGroup;
import com.baiyi.caesar.domain.generator.caesar.DatasourceInstance;
import com.baiyi.caesar.domain.param.datasource.DsAccountGroupParam;
import com.baiyi.caesar.domain.param.datasource.DsAccountParam;
import com.baiyi.caesar.domain.vo.datasource.DsAccountGroupVO;
import com.baiyi.caesar.domain.vo.datasource.DsAccountVO;
import com.baiyi.caesar.domain.vo.datasource.DsInstanceVO;
import com.baiyi.caesar.facade.datasource.DsInstanceFacade;
import com.baiyi.caesar.packer.datasource.DsAccountGroupPacker;
import com.baiyi.caesar.packer.datasource.DsAccountPacker;
import com.baiyi.caesar.packer.datasource.DsInstancePacker;
import com.baiyi.caesar.service.datasource.DsAccountGroupService;
import com.baiyi.caesar.service.datasource.DsAccountService;
import com.baiyi.caesar.service.datasource.DsInstanceService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Author baiyi
 * @Date 2021/6/11 10:19 上午
 * @Version 1.0
 */
@Service
public class DsInstanceFacadeImpl implements DsInstanceFacade {

    @Resource
    private DsInstanceService dsInstancService;

    @Resource
    private DsInstancePacker dsInstancePacker;

    @Resource
    private DsAccountService dsAccountService;

    @Resource
    private DsAccountGroupService dsAccountGroupService;

    @Resource
    private DsAccountGroupPacker dsAccountGroupPacker;

    @Override
    @Async(value = Global.TaskPools.EXECUTOR)
    public void pullAccount(int id) {
        DatasourceInstance dsInstance = dsInstancService.getById(id);
        DsInstanceVO.Instance instance = DsInstancePacker.toVO(dsInstance);
        dsInstancePacker.wrap(instance);
        IAccountProvider iAccount = AccountProviderFactory.getAccountByKey(instance.getInstanceType());
        if (iAccount == null) throw new CommonRuntimeException(ErrorEnum.DATASOURCE_INSTANCE_TYPE_NOT_SUPPORT_ERROR);
        iAccount.pullAccount(instance);
    }

    @Override
    @Async(value = Global.TaskPools.EXECUTOR)
    public void pullAccountGroup(int id) {
        DatasourceInstance dsInstance = dsInstancService.getById(id);
        DsInstanceVO.Instance instance = DsInstancePacker.toVO(dsInstance);
        dsInstancePacker.wrap(instance);
        IAccountProvider iAccount = AccountProviderFactory.getAccountByKey(instance.getInstanceType());
        if (iAccount == null) throw new CommonRuntimeException(ErrorEnum.DATASOURCE_INSTANCE_TYPE_NOT_SUPPORT_ERROR);
        iAccount.pullAccountGroup(instance);
    }

    @Override
    public DataTable<DsAccountVO.Account> queryAccountPage(DsAccountParam.AccountPageQuery pageQuery) {
        DatasourceInstance dsInstance = dsInstancService.getById(pageQuery.getInstanceId());
        pageQuery.setAccountUid(dsInstance.getUuid());
        DataTable<DatasourceAccount> table = dsAccountService.queryPageByParam(pageQuery);
        return new DataTable<>(DsAccountPacker.wrapVOList(table.getData(), pageQuery), table.getTotalNum());
    }

    @Override
    public DataTable<DsAccountGroupVO.Group> queryAccountGroupPage(DsAccountGroupParam.GroupPageQuery pageQuery) {
        DatasourceInstance dsInstance = dsInstancService.getById(pageQuery.getInstanceId());
        pageQuery.setAccountUid(dsInstance.getUuid());
        DataTable<DatasourceAccountGroup> table = dsAccountGroupService.queryPageByParam(pageQuery);
        return new DataTable<>(dsAccountGroupPacker.wrapVOList(table.getData(), pageQuery), table.getTotalNum());
    }

}