package com.zqf.factory;

import com.zqf.annotation.Autowired;
import com.zqf.annotation.Service;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import javax.xml.ws.ServiceMode;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

@Service
public class ProxyFactory {

    @Autowired
    TransactionManager transactionManager;

    public Object getJDKDynamicProxy(Object obj) {

        return Proxy.newProxyInstance(obj.getClass().getClassLoader(), obj.getClass().getInterfaces(),
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        Object invoke = null;
                        try {
                            //开启事务
                            transactionManager.startTransaction();
                            invoke = method.invoke(obj, args);
                            //提交事务
                            transactionManager.commit();
                        } catch (Exception e) {
                            //回滚事务
                            transactionManager.rollback();
                            //异常抛出
                            throw e;
                        }
                        return invoke;
                    }
                });
    }


    public Object getCglibDynamicProxy(Object obj) {

        return new Enhancer().create(obj.getClass(), new MethodInterceptor() {
            @Override
            public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
                Object invoke = null;
                try {
                    //开启事务
                    transactionManager.startTransaction();
                    invoke = method.invoke(obj, objects);
                    //提交事务
                    transactionManager.commit();
                } catch (Exception e) {
                    //回滚事务
                    transactionManager.rollback();
                    //异常抛出
                    throw e;
                }
                return invoke;
            }
        });
    }

    public void setTransactionManager(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }
}
