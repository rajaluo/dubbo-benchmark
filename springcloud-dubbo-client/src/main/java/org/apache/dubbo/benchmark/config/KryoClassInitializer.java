package org.apache.dubbo.benchmark.config;

import org.apache.dubbo.benchmark.bean.Page;
import org.apache.dubbo.benchmark.bean.User;
import org.apache.dubbo.common.serialize.kryo.utils.KryoUtils;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class KryoClassInitializer {

    @PostConstruct
    public void registerKryo(){
        KryoUtils.register(User.class);
        KryoUtils.register(Page.class);
    }
} 