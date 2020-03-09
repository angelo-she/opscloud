package com.baiyi.opscloud.builder;

import com.baiyi.opscloud.bo.AssetsAssetBO;
import com.baiyi.opscloud.common.util.BeanCopierUtils;
import com.baiyi.opscloud.common.util.UUIDUtils;
import com.baiyi.opscloud.domain.generator.jumpserver.AssetsAsset;
import com.baiyi.opscloud.domain.generator.opscloud.OcServer;

/**
 * @Author baiyi
 * @Date 2020/3/9 2:37 下午
 * @Version 1.0
 */
public class AssetsAssetBuilder {

    public static AssetsAsset build(OcServer ocServer, String ip, String adminUserId, String hostname) {
        AssetsAssetBO assetsAssetBO = AssetsAssetBO.builder()
                .id(UUIDUtils.getUUID())
                .ip(ip)
                .adminUserId(adminUserId)
                .hostname(hostname)
                .comment(ocServer.getComment())
                .build();
        return covert(assetsAssetBO);
    }


    private static AssetsAsset covert(AssetsAssetBO assetsAssetBO) {
        return BeanCopierUtils.copyProperties(assetsAssetBO, AssetsAsset.class);
    }
}
