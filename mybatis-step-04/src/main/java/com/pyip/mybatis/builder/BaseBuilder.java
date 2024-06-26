package com.pyip.mybatis.builder;

import com.pyip.mybatis.session.Configuration;

public abstract  class BaseBuilder {
    protected final Configuration configuration;

    public BaseBuilder(Configuration configuration) {
        this.configuration = configuration;
    }

    public Configuration getConfiguration() {
        return configuration;
    }
}
