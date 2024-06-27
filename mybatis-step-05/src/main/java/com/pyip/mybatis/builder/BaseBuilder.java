package com.pyip.mybatis.builder;

import com.pyip.mybatis.session.Configuration;
import com.pyip.mybatis.type.TypeAliasRegistry;

public class BaseBuilder {

    protected final Configuration configuration;
    protected final TypeAliasRegistry typeAliasRegistry;

    public BaseBuilder(Configuration configuration) {
        this.configuration = configuration;
        this.typeAliasRegistry = this.configuration.getTypeAliasRegistry();
    }

    public Configuration getConfiguration() {
        return configuration;
    }
}
