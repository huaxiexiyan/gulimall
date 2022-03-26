package com.atguigu.common.constant;

import com.atguigu.common.tool.ObjectUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class ProductConstant {

    @Getter
    @AllArgsConstructor
    public enum AttrEnum {
        /****/
        ATTR_TYPE_BASE(1, "基本属性"), ATTR_TYPE_SALE(0, "销售属性");
        private final int code;
        private final String msg;

        public static AttrEnum getAttrEnum(String shortName) {
            if (ObjectUtils.nullSafeEquals(shortName, "base")) {
                return ATTR_TYPE_BASE;
            } else {
                return ATTR_TYPE_SALE;
            }
        }

        public static AttrEnum getAttrEnum(int code) {
            if (ObjectUtils.nullSafeEquals(code, 1)) {
                return ATTR_TYPE_BASE;
            } else {
                return ATTR_TYPE_SALE;
            }
        }
    }

}
