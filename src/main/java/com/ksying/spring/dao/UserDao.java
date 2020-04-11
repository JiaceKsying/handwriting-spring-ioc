package com.ksying.spring.dao;

import com.ksying.spring.pojo.User;

import java.util.List;

/**
 * @author <a href="jiace.ksying@gmail.com">ksying</a>
 * @version v1.0 , 2020/4/7 14:56
 */
public interface UserDao {
    List<User> queryUserList(String sqlId, Object param);
}
