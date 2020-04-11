package com.ksying.spring.ioc;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author <a href="jiace.ksying@gmail.com">ksying</a>
 * @version v1.0 , 2020/4/9 21:10
 */
@Data
@AllArgsConstructor
public class PropertyValue {
    private String name;
    private Object value;
}
