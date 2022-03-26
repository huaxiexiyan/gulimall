package com.atguigu.common.tool;

import org.springframework.lang.Nullable;

/**
 * @author Lionel
 * @date 2022-03-25 22:28
 */
public class ObjectUtils extends org.springframework.util.ObjectUtils {

    public static boolean nullSafeNotEquals(@Nullable Object o1, @Nullable Object o2) {
        return !nullSafeEquals(o1, o2);
    }

    public static boolean isNotEmpty(@Nullable Object obj) {
        return !isEmpty(obj);
    }
}
