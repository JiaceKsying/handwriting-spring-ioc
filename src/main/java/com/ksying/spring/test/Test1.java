package com.ksying.spring.test;

import com.ksying.spring.dao.impl.UserDaoImpl;
import com.ksying.spring.pojo.User;
import com.ksying.spring.service.UserService;
import com.ksying.spring.service.impl.UserServiceImpl;
import org.apache.commons.dbcp.BasicDataSource;
import org.junit.Test;

import javax.sql.DataSource;
import java.util.List;

/**
 * @author <a href="jiace.ksying@gmail.com">ksying</a>
 * @version v1.0 , 2020/4/7 15:09
 */
public class Test1 {

    @Test
    public void query() {
        UserServiceImpl userService = new UserServiceImpl();
        UserDaoImpl userDao = new UserDaoImpl();
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://rm-uf690gd3w60ce2psamo.mysql.rds.aliyuncs.com:3306/my_db?characterEncoding=utf-8");
        dataSource.setUsername("opt_user1");
        dataSource.setPassword("root123456");
        userService.setUserDao(userDao);
        userDao.setDataSource(dataSource);

        User user = new User();
        user.setName("zhangsan");
        List<User> users = userService.queryUser(user);
        System.out.println(users);

    }
}
