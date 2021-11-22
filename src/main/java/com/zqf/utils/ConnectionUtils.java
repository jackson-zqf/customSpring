package com.zqf.utils;

import com.zqf.annotation.Service;

import java.sql.Connection;
import java.sql.SQLException;

@Service
public class ConnectionUtils {


    private static ThreadLocal<Connection>  connectionThreadLocal = new ThreadLocal<>();
//    private static ConnectionUtils connectionUtils = new ConnectionUtils();
//
//    private ConnectionUtils(){
//
//    }
//
//    public static ConnectionUtils getInstance(){
//        return connectionUtils;
//    }


    public Connection  getConnection() throws SQLException {
        if(connectionThreadLocal.get() == null){
            connectionThreadLocal.set(DruidUtils.getInstance().getConnection());
        }
        return  connectionThreadLocal.get();
    }


}
