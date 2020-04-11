package com.ksying.spring.ioc;

import lombok.Data;

/**
 * @author <a href="jiace.ksying@gmail.com">ksying</a>
 * @version v1.0 , 2020/4/9 21:48
 */
@Data
public class TypedStringValue {
    private String value;
    private Class type;
}
