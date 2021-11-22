package com.zqf.factory;

import com.zqf.annotation.Autowired;
import com.zqf.annotation.Service;
import com.zqf.utils.ConnectionUtils;

import java.sql.SQLException;

/**
 * 事务管理器
 */
@Service
public class TransactionManager {
    @Autowired
    ConnectionUtils connectionUtils;
//    private TransactionManager(){
//
//    }
//    private  static TransactionManager transactionManager = new TransactionManager();
//
//    public static  TransactionManager getInstance(){
//        return  transactionManager;
//    }

    //开启手动事务
    public void   startTransaction() throws SQLException {
        connectionUtils.getConnection().setAutoCommit(false);
    }

    //提交事务
    public void   commit() throws SQLException {
        connectionUtils.getConnection().commit();
    }

    //回滚事务
    public  void  rollback() throws SQLException {
        connectionUtils.getConnection().rollback();
    }

    public void setConnectionUtils(ConnectionUtils connectionUtils) {
        this.connectionUtils = connectionUtils;
    }
}
