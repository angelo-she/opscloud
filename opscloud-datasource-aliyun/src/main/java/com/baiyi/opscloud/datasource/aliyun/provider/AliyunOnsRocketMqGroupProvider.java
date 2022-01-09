package com.baiyi.opscloud.datasource.aliyun.provider;

import com.aliyuncs.exceptions.ClientException;
import com.baiyi.opscloud.common.annotation.SingleTask;
import com.baiyi.opscloud.common.constants.enums.DsTypeEnum;
import com.baiyi.opscloud.common.datasource.AliyunConfig;
import com.baiyi.opscloud.core.factory.AssetProviderFactory;
import com.baiyi.opscloud.core.model.DsInstanceContext;
import com.baiyi.opscloud.core.provider.annotation.ChildProvider;
import com.baiyi.opscloud.core.provider.asset.AbstractAssetChildProvider;
import com.baiyi.opscloud.core.util.AssetUtil;
import com.baiyi.opscloud.datasource.aliyun.ons.drive.AliyunOnsRocketMqGroupDrive;
import com.baiyi.opscloud.datasource.aliyun.ons.drive.AliyunOnsRocketMqInstanceDrive;
import com.baiyi.opscloud.datasource.aliyun.util.AliyunRegionIdUtil;
import com.baiyi.opscloud.domain.generator.opscloud.DatasourceConfig;
import com.baiyi.opscloud.domain.generator.opscloud.DatasourceInstanceAsset;
import com.baiyi.opscloud.domain.constants.DsAssetTypeConstants;
import com.google.common.collect.Lists;
import com.baiyi.opscloud.datasource.aliyun.ons.entity.OnsInstance;
import com.baiyi.opscloud.datasource.aliyun.ons.entity.OnsRocketMqGroup;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static com.baiyi.opscloud.common.constants.SingleTaskConstants.PULL_ALIYUN_ONS_ROCKETMQ_GROUP;

/**
 * @Author baiyi
 * @Date 2021/9/30 4:34 下午
 * @Version 1.0
 */
@Component
@ChildProvider(parentType = DsAssetTypeConstants.ONS_ROCKETMQ_INSTANCE)
public class AliyunOnsRocketMqGroupProvider extends AbstractAssetChildProvider<OnsRocketMqGroup.Group> {

    @Resource
    private AliyunOnsRocketMqInstanceDrive aliyunOnsRocketMqInstanceDrive;

    @Resource
    private AliyunOnsRocketMqGroupDrive aliyunOnsRocketMqGroupDrive;

    @Resource
    private AliyunOnsRocketMqGroupProvider aliyunOnsRocketMqGroupProvider;

    @Override
    @SingleTask(name = PULL_ALIYUN_ONS_ROCKETMQ_GROUP, lockTime = "5m")
    public void pullAsset(int dsInstanceId) {
        doPull(dsInstanceId);
    }

    private AliyunConfig.Aliyun buildConfig(DatasourceConfig dsConfig) {
        return dsConfigHelper.build(dsConfig, AliyunConfig.class).getAliyun();
    }

    @Override
    protected boolean equals(DatasourceInstanceAsset asset, DatasourceInstanceAsset preAsset) {
        if (!AssetUtil.equals(preAsset.getName(), asset.getName()))
            return false;
        if (!AssetUtil.equals(preAsset.getDescription(), asset.getDescription()))
            return false;
        return true;
    }

    @Override
    protected List<OnsRocketMqGroup.Group> listEntities(DsInstanceContext dsInstanceContext) {
        AliyunConfig.Aliyun aliyun = buildConfig(dsInstanceContext.getDsConfig());
        Set<String> regionIds = AliyunRegionIdUtil.toOnsRegionIds(aliyun);
        List<OnsRocketMqGroup.Group> entities = Lists.newArrayList();
        regionIds.forEach(regionId -> {
            try {
                List<OnsInstance.InstanceBaseInfo> instances = aliyunOnsRocketMqInstanceDrive.listInstance(regionId, aliyun);
                if (!CollectionUtils.isEmpty(instances)) {
                    instances.forEach(instance -> {
                        try {
                            entities.addAll(aliyunOnsRocketMqGroupDrive.listGroup(regionId, aliyun, instance.getInstanceId()));
                        } catch (ClientException e) {
                        }
                    });
                }
            } catch (ClientException e) {
            }
        });
        return entities;
    }

    @Override
    protected List<OnsRocketMqGroup.Group> listEntities(DsInstanceContext dsInstanceContext, DatasourceInstanceAsset asset) {
        AliyunConfig.Aliyun aliyun = buildConfig(dsInstanceContext.getDsConfig());
        try {
            return aliyunOnsRocketMqGroupDrive.listGroup(asset.getRegionId(), aliyun, asset.getAssetId());
        } catch (ClientException e) {
            return Collections.emptyList();
        }
    }

    @Override
    public String getInstanceType() {
        return DsTypeEnum.ALIYUN.name();
    }

    @Override
    public String getAssetType() {
        return DsAssetTypeConstants.ONS_ROCKETMQ_GROUP.name();
    }

    @Override
    public void afterPropertiesSet() {
        AssetProviderFactory.register(aliyunOnsRocketMqGroupProvider);
    }

}


