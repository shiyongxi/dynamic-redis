package com.yx.dynamic.redis.factory;

import io.lettuce.core.resource.ClientResources;
import io.lettuce.core.resource.DefaultClientResources;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.AbstractFactoryBean;


@Qualifier(ClientResourcesFactoryBean.FACTORY_BEAN_PREFIX)
@Slf4j
public class ClientResourcesFactoryBean extends AbstractFactoryBean<ClientResources> implements BeanNameAware {

    public static final String FACTORY_BEAN_PREFIX = "internal-clientResourcesFactoryBean-";

    private String poolName;


    @Override
    public void setBeanName(String name) {
        this.poolName = name;
    }

    @Override
    public Class<?> getObjectType() {
        return ClientResources.class;
    }

    @Override
    protected ClientResources createInstance() throws Exception {
        return DefaultClientResources.builder().build();
    }


    @Override
    protected void destroyInstance(ClientResources instance) throws Exception {
        log.info("ClientResources:{} destroy", poolName);
        instance.shutdown();
    }
}
