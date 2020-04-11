package com.ksying.spring.pojo;

import lombok.Data;

/**
 * @author <a href="jiace.ksying@gmail.com">ksying</a>
 * @version v1.0 , 2020/4/7 14:42
 */
@Data
public class User {
        private int id;
        private String name;
        private String password;
        private String email;
}
