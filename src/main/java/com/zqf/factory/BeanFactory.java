package com.zqf.factory;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BeanFactory {

    //定义一个map集合，来存储解析后并通过反射得到的对象。
    static Map<String, Object> map = new HashMap<>();

    static {
        /**
         * 加载bean的配置文件，解析配置文件中的bean标签，将得到的对象添加到内存map中。
         */
        InputStream inputStream = BeanFactory.class.getClassLoader().getResourceAsStream("beans.xml");
        SAXReader saxReader = new SAXReader();
        try {
            Document document = saxReader.read(inputStream);
            Element rootElement = document.getRootElement();

            List<Element> elementList = rootElement.selectNodes("//bean");
            for (Element element : elementList) {
                String id = element.attributeValue("id");
                String clazz = element.attributeValue("class");
                Class<?> cla = Class.forName(clazz);
                Object obj = cla.newInstance();

                List<Element> propertyList = element.selectNodes("property");
                if (propertyList != null && propertyList.size() > 0) {
                    for (Element element1 : propertyList) {
                        String name = element1.attributeValue("name");
                        String ref = element1.attributeValue("ref");
                        Method[] methods = obj.getClass().getMethods();
                        Method methodM = null;
                        for (Method method : methods) {
                            if (method.getName().equals(name)) {
                                methodM = method;
                            }
                        }
                        methodM.invoke(obj, map.get(ref));
                    }
                }
                map.put(id, obj);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Object getBean(String id) {
        return map.get(id);
    }

}
