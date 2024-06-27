package com.pyip.mybatis.transaction;

import com.pyip.mybatis.session.TransactionIsolationLevel;

import javax.sql.DataSource;
import java.sql.Connection;

public interface TransactionFactory {

    //根据 Connection 创建 Transaction
    Transaction newTransaction(Connection conn);

    //根据数据源和事务隔离级别创建 Transaction
    Transaction newTransaction(DataSource dataSource,
                               TransactionIsolationLevel level,
                               boolean autoCommit);

}
