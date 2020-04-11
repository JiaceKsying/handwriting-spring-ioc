package com.ksying.spring.test;

import com.ksying.spring.ioc.BeanDefinition;
import com.ksying.spring.ioc.PropertyValue;
import com.ksying.spring.ioc.RuntimeBeanReference;
import com.ksying.spring.ioc.TypedStringValue;
import com.ksying.spring.pojo.User;
import com.ksying.spring.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="jiace.ksying@gmail.com">ksying</a>
 * @version v1.0 , 2020/4/7 15:24
 */
public class Test2 {
    // 模拟一级缓存，村粗单例bean
    private Map<String, Object> singletonObjects = new HashMap<>();

    // 配置信息集合
    private Map<String, BeanDefinition> beanDefinitions = new HashMap<>();

    @Test
    public void query() {
        UserService userService = (UserService) getBean("userService");
        User user = new User();
        user.setName("zhangsan");
        List<User> users = userService.queryUser(user);
        System.out.println(users);

    }

    @Before
    public void init() {
        // 先获取bean信息，并封装成Java对象
        String location = "beans.xml";
        InputStream inputStream = createInputStream(location);
        Document document = createDocument(inputStream);
        registerBeanDefinitions(document.getRootElement());

    }

    private void registerBeanDefinitions(Element rootElement) {
        List<Element> elements = rootElement.elements("bean");
        elements.forEach(
                element -> {
                    if ("bean".equals(element.getName())) {
                        parseDefaultElement(element);
                    } else {
                        parseCustomElement(element);
                    }
                }
        );
    }

    /**
     * 处理bean标签
     *
     * @param element
     */
    private void parseDefaultElement(Element element) {
        try {
            if (element == null) {
                return;
            }
            String id = element.attributeValue("id");
            String name = element.attributeValue("name");
            String className = element.attributeValue("class");
            if (StringUtils.isBlank(className)) {
                return;
            }
            String initMethod = element.attributeValue("init-method");
            String scope = StringUtils.isBlank(element.attributeValue("scope")) ? "singleton" : element.attributeValue("scope");
            String beanName = StringUtils.isBlank(id) ? name : id;
            Class classType = Class.forName(className);
            beanName = StringUtils.isBlank(beanName) ? classType.getSimpleName() : beanName;
            BeanDefinition beanDefinition = new BeanDefinition(className, beanName);
            beanDefinition.setBeanName(beanName);
            beanDefinition.setClassName(className);
            beanDefinition.setClassType(classType);
            beanDefinition.setInitMethod(initMethod);
            beanDefinition.setScope(scope);
            List<Element> propertyElements = element.elements();
            propertyElements.forEach(
                    propertyElement -> {
                        parsePropertyElement(propertyElement, beanDefinition);
                    }
            );
            beanDefinitions.put(beanName, beanDefinition);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parsePropertyElement(Element propertyElement, BeanDefinition beanDefinition) {
        if (propertyElement == null) {
            return;
        }
        String name = propertyElement.attributeValue("name");
        String value = propertyElement.attributeValue("value");
        String ref = propertyElement.attributeValue("ref");
        if (StringUtils.isNotBlank(value) && StringUtils.isNotBlank(ref)) {
            return;
        }

        PropertyValue propertyValue = null;
        if (StringUtils.isNotBlank(value)) {
            TypedStringValue typedStringValue = new TypedStringValue();
            Class targetType = parseTypedStringValue(beanDefinition.getClassType(), name);
            typedStringValue.setValue(value);
            typedStringValue.setType(targetType);
            propertyValue = new PropertyValue(name, typedStringValue);
        } else if (StringUtils.isNotBlank(ref)) {
            RuntimeBeanReference runtimeBeanReference = new RuntimeBeanReference();
            runtimeBeanReference.setRef(ref);
            propertyValue = new PropertyValue(name, runtimeBeanReference);
        } else {
            return;
        }
        beanDefinition.addPropertyValues(propertyValue);
    }

    private Class parseTypedStringValue(Class classType, String name) {
        try {
            Field field = classType.getDeclaredField(name);
            return field.getType();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 处理自定义标签，暂时不处理
     *
     * @param element
     */
    private void parseCustomElement(Element element) {
    }

    private Document createDocument(InputStream inputStream) {
        try {
            SAXReader saxReader = new SAXReader();
            return saxReader.read(inputStream);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return null;
    }

    private InputStream createInputStream(String location) {
        return this.getClass().getClassLoader().getResourceAsStream(location);
    }

    private Object getBean(String beanName) {
        // 先去一级缓存中获取单例bean
        Object singletonObject = singletonObjects.get(beanName);
        if (singletonObject != null) {
            return singletonObject;
        }
        // 一级缓存中没有，则先获取bean信息，然后通过bean信息进行创建
        BeanDefinition beanDefinition = beanDefinitions.get(beanName);
        if (beanDefinition == null || beanDefinition.getClassType() == null) {
            return null;
        }

        // 判断创建单例bean还是原型bean
        if (beanDefinition.isSingleton()) {
            // 单例bean创建完成后，要塞入到一级缓存中
            singletonObject = createBean(beanDefinition);
            singletonObjects.put(beanName, singletonObject);
        } else if (beanDefinition.isPrototype()) {
            singletonObject = createBean(beanDefinition);
        } else {
            // todo
        }
        return singletonObject;
    }

    private Object createBean(BeanDefinition beanDefinition) {
        Class classType = beanDefinition.getClassType();
        if (classType == null) {
            return null;
        }
        // 创建Bean的步骤
        // 1. Bean对象实例化
        Object bean = createInstance(classType);
        // 2. Bean 对象属性赋值
        populateBean(bean, beanDefinition);

        // 3. 执行初始化方法
        initMethod(bean, beanDefinition);
        return bean;
    }

    private void initMethod(Object bean, BeanDefinition beanDefinition) {
        try {
            String initMethod = beanDefinition.getInitMethod();
            if (StringUtils.isBlank(initMethod)) {
                return;
            }
            Class clazz = bean.getClass();
            Method method = clazz.getDeclaredMethod(initMethod);
            method.invoke(bean);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void populateBean(Object bean, BeanDefinition beanDefinition) {
        List<PropertyValue> propertyValues = beanDefinition.getPropertyValues();
        propertyValues.forEach(
                propertyValue -> {
                    String name = propertyValue.getName();
                    Object value = propertyValue.getValue();
                    Object valueToUse = null;
                    if (value instanceof TypedStringValue) {
                        TypedStringValue typedStringValue = (TypedStringValue) value;
                        String stringValue = typedStringValue.getValue();
                        Class type = typedStringValue.getType();
                        // todo 此处可以使用策略模式优化
                        if (type.equals(Integer.class)) {
                            valueToUse = Integer.parseInt(stringValue);
                        } else if (type.equals(String.class)) {
                            valueToUse = stringValue;
                        } else {
                            // todo 其他类型。。。
                        }
                    } else if (value instanceof RuntimeBeanReference) {
                        RuntimeBeanReference reference = (RuntimeBeanReference) value;
                        String ref = reference.getRef();
                        valueToUse = getBean(ref);
                    }
                    setProperty(bean, name, valueToUse);
                }
        );
    }

    private void setProperty(Object bean, String name, Object valueToUse) {
        try {
            Class clazz = bean.getClass();
            Field field = clazz.getDeclaredField(name);
            field.setAccessible(true);
            field.set(bean, valueToUse);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private Object createInstance(Class classType) {
        try {
            // 通过反射调用构造方法进行实例化
            Constructor constructor = classType.getDeclaredConstructor();
            return constructor.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
