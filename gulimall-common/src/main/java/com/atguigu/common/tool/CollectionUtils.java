package com.atguigu.common.tool;

import org.springframework.lang.Nullable;

import java.util.Collection;

/**
 * @author Lionel
 * @date 2022-03-26 23:21
 */
public class CollectionUtils extends org.springframework.util.CollectionUtils {


    public static boolean isNotEmpty(@Nullable Collection<?> collection) {
        return !isEmpty(collection);
    }

}
