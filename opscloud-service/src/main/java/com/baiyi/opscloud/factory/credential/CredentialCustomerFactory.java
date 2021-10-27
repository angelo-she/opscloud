package com.baiyi.opscloud.factory.credential;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author baiyi
 * @Date 2021/10/27 10:40 上午
 * @Version 1.0
 */
@Slf4j
public class CredentialCustomerFactory {

    private CredentialCustomerFactory() {
    }

    private static final Map<String, ICredentialCustomer> context = new ConcurrentHashMap<>();

    public static ICredentialCustomer getByBeanName(String beanName) {
        return context.get(beanName);
    }

    public static void register(ICredentialCustomer bean) {
        log.info("CredentialCustomerFactory注册: beanName = {}", bean.getBeanName());
        context.put(bean.getBeanName(), bean);
    }

    public static int countByCredentialId(int credentialId) {
        return context.keySet().stream().mapToInt(k -> context.get(k).countByCredentialId(credentialId)).sum();
    }

    public static Map<String, ICredentialCustomer> getContext() {
        return context;
    }
}
