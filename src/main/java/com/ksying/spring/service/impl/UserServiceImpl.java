package com.ksying.spring.service.impl;

import com.ksying.spring.dao.UserDao;
import com.ksying.spring.pojo.User;
import com.ksying.spring.service.UserService;
import lombok.Data;

import java.util.List;

/**
 * @author <a href="jiace.ksying@gmail.com">ksying</a>
 * @version v1.0 , 2020/4/7 14:44
 */
@Data
public class UserServiceImpl implements UserService {
    private UserDao userDao;

    @Override
    public List<User> queryUser(User user) {
        return userDao.queryUserList("queryUserById", user);
    }
}
