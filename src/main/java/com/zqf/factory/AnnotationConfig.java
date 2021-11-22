package com.zqf.factory;

import com.alibaba.druid.util.StringUtils;
import com.zqf.annotation.Autowired;
import com.zqf.annotation.Service;
import com.zqf.annotation.Transactional;
import com.zqf.utils.ClassUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

public class AnnotationConfig {

    //定义一个map集合，来存储解析后并通过反射得到的对象。
    static Map<String, Object> map = new HashMap<>();
    static String packageName = "com.zqf";

    static {

        Set<String> classNames = ClassUtils.getClassName(packageName, true);
        if (classNames != null && classNames.size() > 0) {
            Class<?> clazz = null;
            for (String className : classNames) {
                try {
                    if (className.equals("com.zqf.servlet.TransferServlet")) {
                        continue;
                    }
                    clazz = Class.forName(className);
                    doCreateBean(clazz);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            System.out.println(map);
        }
    }

    private static void doCreateBean(Class<?> clazz) throws InstantiationException, IllegalAccessException {
        Service declaredAnnotation = clazz.getDeclaredAnnotation(Service.class);
        String beanName = null;
        if (declaredAnnotation != null) {
            beanName = StringUtils.isEmpty(declaredAnnotation.value()) ? getNameByClassName(clazz) : declaredAnnotation.value();
            if (map.containsKey(beanName)) {
                return;
            }
            map.put(beanName, clazz.newInstance());

            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                Autowired autowired = field.getDeclaredAnnotation(Autowired.class);
                if (autowired != null) {
                    String autowiredValue = StringUtils.isEmpty(autowired.value()) ? field.getName() : autowired.value();
                    Object o = map.get(autowiredValue);
                    if (Objects.isNull(o)) {
                        AtomicReference<Class<?>> aClass = new AtomicReference<>(field.getType());
                        if (aClass.get().isInterface()) {
                            ClassUtils.getClassName(packageName, true).forEach(t -> {
                                try {
                                    if (t.equals("com.zqf.servlet.TransferServlet")) {
                                    } else {
                                        Class<?> aClass1 = Class.forName(t);
                                        if (aClass.get().isAssignableFrom(aClass1) && !aClass1.getName().equals(aClass.get().getName())) {
                                            aClass.set(aClass1);
                                        }
                                    }
                                } catch (ClassNotFoundException e) {
                                    e.printStackTrace();
                                }
                            });
                        }
                        doCreateBean(aClass.get());
                        o = map.get(autowiredValue);
                        if (Objects.isNull(o)) {
                            throw new RuntimeException("找不到name为" + autowiredValue + "的bean");
                        }
                    }
                    field.setAccessible(true);
                    field.set(map.get(beanName), o);
                }
            }
            if (clazz.getDeclaredAnnotation(Transactional.class) != null) {
                if (!StringUtils.isEmpty(beanName)) {
                    map.put(beanName, getProxy(map.get(beanName)));
                }
            }
        }
    }

    private static Object getProxy(Object obj) throws InstantiationException, IllegalAccessException {
        ProxyFactory factory = (ProxyFactory) getBean(ProxyFactory.class);
        Object result = null;
        if (factory.getClass().getSuperclass().isInterface()) {
            result = factory.getJDKDynamicProxy(obj);
        } else {
            result = factory.getCglibDynamicProxy(obj);
        }
        return result;
    }

    private static String getNameByClassName(Class<?> clz) {
        if (clz != null) {
            String[] split = clz.getName().split("\\.");
            String className = split[split.length - 1];
            return toLowCase(className);
        }
        return null;
    }

    public static String toLowCase(String className) {
        char[] chars = className.toCharArray();
        String fistStr = String.valueOf(chars[0]).toLowerCase();
        chars[0] = fistStr.toCharArray()[0];
        return String.valueOf(chars);
    }

    public static void main(String[] args) throws ClassNotFoundException {
        Class<?> clz = Class.forName("com.zqf.factory.ProxyFactory");
        String name = clz.getName();
        Annotation[] annotations = clz.getAnnotations();
        Annotation[] declaredAnnotations = clz.getDeclaredAnnotations();
        Field[] fields = clz.getFields();
        for (Field field : fields) {
            Autowired declaredAnnotation = field.getDeclaredAnnotation(Autowired.class);
            if (declaredAnnotation != null) {
                String value = declaredAnnotation.value();
            }
        }
    }

    public static Object getBean(String id) {
        return map.get(id);
    }

    public static Object getBean(Class<?> clazz) throws InstantiationException, IllegalAccessException {
        doCreateBean(clazz);
        for (Object value : map.values()) {
            if (clazz.isAssignableFrom(value.getClass())) {
                return value;
            }
        }
        return null;
    }
}
