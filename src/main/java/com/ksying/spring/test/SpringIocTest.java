package com.ksying.spring.test;

import com.ksying.spring.factory.impl.DefaultListableBeanFactory;
import com.ksying.spring.io.impl.ClasspathResource;
import com.ksying.spring.pojo.User;
import com.ksying.spring.io.Resource;
import com.ksying.spring.reader.XmlBeanDefinitionReader;
import com.ksying.spring.service.UserService;
import org.junit.Test;

import java.io.InputStream;
import java.util.List;

/**
 * @author <a href="jiace.ksying@gmail.com">jiakai.zhang</a>
 * @version v1.0 , 2020/4/11 15:42
 */
public class SpringIocTest {
    @Test
    public void SpringIoCTest() {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(beanFactory);
        Resource resource = new ClasspathResource("beans.xml");
        InputStream inputStream = resource.getResource();
        reader.loadBeanDefinitions(inputStream);


        UserService userService = (UserService) beanFactory.getBean("userService");
        User user = new User();
        user.setName("zhangsan");
        List<User> users = userService.queryUser(user);
        System.out.println(users);


    }
}
