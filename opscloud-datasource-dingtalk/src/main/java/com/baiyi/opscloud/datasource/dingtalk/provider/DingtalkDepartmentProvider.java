package com.baiyi.opscloud.datasource.dingtalk.provider;

import com.baiyi.opscloud.common.annotation.SingleTask;
import com.baiyi.opscloud.common.constant.SingleTaskConstants;
import com.baiyi.opscloud.common.datasource.DingtalkConfig;
import com.baiyi.opscloud.core.factory.AssetProviderFactory;
import com.baiyi.opscloud.core.model.DsInstanceContext;
import com.baiyi.opscloud.core.util.AssetUtil;
import com.baiyi.opscloud.datasource.dingtalk.convert.DingtalkAssetConvert;
import com.baiyi.opscloud.datasource.dingtalk.entity.DingtalkDepartment;
import com.baiyi.opscloud.datasource.dingtalk.param.DingtalkDepartmentParam;
import com.baiyi.opscloud.datasource.dingtalk.provider.base.AbstractDingtalkAssetProvider;
import com.baiyi.opscloud.domain.builder.asset.AssetContainer;
import com.baiyi.opscloud.domain.generator.opscloud.DatasourceConfig;
import com.baiyi.opscloud.domain.generator.opscloud.DatasourceInstance;
import com.baiyi.opscloud.domain.generator.opscloud.DatasourceInstanceAsset;
import com.baiyi.opscloud.domain.types.DsAssetTypeEnum;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;

/**
 * @Author baiyi
 * @Date 2021/11/30 1:26 下午
 * @Version 1.0
 */
@Component
public class DingtalkDepartmentProvider extends AbstractDingtalkAssetProvider<DingtalkDepartment.Department> {

    @Resource
    private DingtalkDepartmentProvider dingtalkDepartmentProvider;

    @Override
    public String getAssetType() {
        return DsAssetTypeEnum.DINGTALK_DEPARTMENT.name();
    }

    private DingtalkConfig.Dingtalk buildConfig(DatasourceConfig dsConfig) {
        return dsConfigHelper.build(dsConfig, DingtalkConfig.class).getDingtalk();
    }

    @Override
    protected List<DingtalkDepartment.Department> listEntities(DsInstanceContext dsInstanceContext) {
        DingtalkConfig.Dingtalk dingtalk = buildConfig(dsInstanceContext.getDsConfig());
        try {
            Set<Long> deptIdSet = queryDeptSubIds(dsInstanceContext);
            List<DingtalkDepartment.Department> entities = Lists.newArrayList();
            deptIdSet.forEach(deptId -> {
                DingtalkDepartmentParam.GetDepartment getDepartment = DingtalkDepartmentParam.GetDepartment.builder()
                        .deptId(deptId)
                        .build();
                DingtalkDepartment.GetDepartmentResponse getDepartmentResponse = dingtalkDepartmentDrive.get(dingtalk, getDepartment);
                if (getDepartmentResponse.getResult() != null)
                    entities.add(getDepartmentResponse.getResult());
            });
            return entities;
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new RuntimeException("查询条目失败");
    }

    @Override
    @SingleTask(name = SingleTaskConstants.PULL_DINGTALK_DEPARTMENT, lockTime = "2m")
    public void pullAsset(int dsInstanceId) {
        doPull(dsInstanceId);
    }

    @Override
    protected boolean equals(DatasourceInstanceAsset asset, DatasourceInstanceAsset preAsset) {
        if (!AssetUtil.equals(preAsset.getName(), asset.getName()))
            return false;
        if (preAsset.getIsActive() != asset.getIsActive())
            return false;
        return true;
    }

    @Override
    protected AssetContainer toAssetContainer(DatasourceInstance dsInstance, DingtalkDepartment.Department entity) {
        return DingtalkAssetConvert.toAssetContainer(dsInstance, entity);
    }

    @Override
    public void afterPropertiesSet() {
        AssetProviderFactory.register(dingtalkDepartmentProvider);
    }
}