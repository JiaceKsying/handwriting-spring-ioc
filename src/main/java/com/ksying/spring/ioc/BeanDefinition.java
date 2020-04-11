package com.ksying.spring.ioc;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="jiace.ksying@gmail.com">ksying</a>
 * @version v1.0 , 2020/4/9 21:05
 */
@Getter
@Setter
public class BeanDefinition {
    private String beanName;
    private String className;
    private Class classType;
    private String scope;
    private String initMethod;

    private List<PropertyValue> propertyValues = new ArrayList<>();

    private final static String SCOPE_SINGLETONE = "singleton";
    private final static String SCOPE_PROTOTYPE = "prototype";

    public BeanDefinition(String beanName, String className) {
        this.beanName = beanName;
        this.className = className;
    }

    public Boolean isSingleton () {
        return SCOPE_SINGLETONE.equals(this.getScope());
    }

    public Boolean isPrototype () {
        return SCOPE_PROTOTYPE.equals(this.getScope());
    }


    public void addPropertyValues(PropertyValue propertyValue) {
        this.propertyValues.add(propertyValue);
    }
}
