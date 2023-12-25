package com.baiyi.opscloud.zabbix.provider.base;

import com.baiyi.opscloud.common.annotation.SingleTask;
import com.baiyi.opscloud.common.constants.enums.DsTypeEnum;
import com.baiyi.opscloud.common.datasource.ZabbixConfig;
import com.baiyi.opscloud.core.factory.AssetProviderFactory;
import com.baiyi.opscloud.core.model.DsInstanceContext;
import com.baiyi.opscloud.core.provider.asset.AbstractAssetRelationProvider;
import com.baiyi.opscloud.core.util.AssetUtil;
import com.baiyi.opscloud.domain.generator.opscloud.DatasourceConfig;
import com.baiyi.opscloud.domain.generator.opscloud.DatasourceInstanceAsset;
import com.baiyi.opscloud.domain.constants.DsAssetTypeConstants;
import com.baiyi.opscloud.zabbix.provider.ZabbixHostProvider;
import com.baiyi.opscloud.zabbix.v5.driver.ZabbixV5HostDriver;
import com.baiyi.opscloud.zabbix.v5.entity.ZabbixHost;

import jakarta.annotation.Resource;
import java.util.List;

import static com.baiyi.opscloud.common.constants.SingleTaskConstants.PULL_ZABBIX_HOST;

/**
 * @Author baiyi
 * @Date 2021/8/2 6:20 下午
 * @Version 1.0
 */
public abstract class AbstractZabbixHostProvider<T> extends AbstractAssetRelationProvider<ZabbixHost.Host, T> {

    @Resource
    protected ZabbixV5HostDriver zabbixV5HostDrive;

    @Resource
    private ZabbixHostProvider zabbixHostTargetGroupProvider;

    @Override
    public String getInstanceType() {
        return DsTypeEnum.ZABBIX.name();
    }

    protected ZabbixConfig.Zabbix buildConfig(DatasourceConfig dsConfig) {
        return dsConfigManager.build(dsConfig, ZabbixConfig.class).getZabbix();
    }

    @Override
    protected List<ZabbixHost.Host> listEntities(DsInstanceContext dsInstanceContext) {
        return zabbixV5HostDrive.list(buildConfig(dsInstanceContext.getDsConfig()));
    }

    @Override
    @SingleTask(name = PULL_ZABBIX_HOST, lockTime = "5m")
    public void pullAsset(int dsInstanceId) {
        doPull(dsInstanceId);
    }

    @Override
    public String getAssetType() {
        return DsAssetTypeConstants.ZABBIX_HOST.name();
    }

    @Override
    protected boolean equals(DatasourceInstanceAsset a1, DatasourceInstanceAsset a2) {
        if (!AssetUtil.equals(a2.getName(), a1.getName())) {
            return false;
        }
        if (!AssetUtil.equals(a2.getAssetKey(), a1.getAssetKey())) {
            return false;
        }
        if (!AssetUtil.equals(a2.getKind(), a1.getKind())) {
            return false;
        }
        if (!a2.getIsActive().equals(a1.getIsActive())) {
            return false;
        }
        if (!AssetUtil.equals(a2.getDescription(), a1.getDescription())) {
            return false;
        }
        return true;
    }

    @Override
    public void afterPropertiesSet() {
        AssetProviderFactory.register(zabbixHostTargetGroupProvider);
    }
}

