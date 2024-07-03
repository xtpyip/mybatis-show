package com.pyip.framework.context;

import com.pyip.framework.beans.factory.BeanFactory;

public interface ApplicationContext extends BeanFactory {

    void refresh() throws Exception;
}