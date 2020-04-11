package com.ksying.spring.service;

import com.ksying.spring.pojo.User;

import java.util.List;

/**
 * @author <a href="jiace.ksying@gmail.com">ksying</a>
 * @version v1.0 , 2020/4/7 14:54
 */
public interface UserService {
    List<User> queryUser(User user);
}
