package com.baiyi.opscloud.datasource.kubernetes.driver;

import com.baiyi.opscloud.common.datasource.KubernetesConfig;
import com.baiyi.opscloud.datasource.kubernetes.client.KubernetesClientBuilder;
import io.fabric8.kubernetes.api.model.Node;
import io.fabric8.kubernetes.api.model.NodeList;
import io.fabric8.kubernetes.client.KubernetesClient;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @Author baiyi
 * @Date 2021/11/24 2:24 下午
 * @Version 1.0
 */
@Slf4j
public class KubernetesNodeDriver {

    public static List<Node> listNode(KubernetesConfig.Kubernetes kubernetes) {
        try (KubernetesClient kc = KubernetesClientBuilder.build(kubernetes)) {
            NodeList nodeList = kc.nodes().list();
            return nodeList.getItems();
        } catch (Exception e) {
            log.warn(e.getMessage());
            throw e;
        }
    }

}
