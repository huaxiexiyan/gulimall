package com.atguigu.common.constant;

/**
 * 通用常量
 *
 * @author Lionel
 * @date 2022-03-26 22:10
 */
public class CommonConstant {

    public static class QueryConstant {
        /**
         * list
         **/
        public static final String QUERY_KEY = "key";

        /**
         * id = 0 则是查所有
         *
         * @param idKey string、int、long
         * @return
         */
        public static boolean isQueryAll(Object idKey) {
            if (idKey == null) {
                return true;
            }
            if (idKey instanceof String) {
                return "0".equals(idKey);
            }
            if (idKey instanceof Integer) {
                return (Integer) idKey == 0;
            }
            if (idKey instanceof Long) {
                return (Long) idKey == 0;
            }
            if (idKey instanceof Byte) {
                return (Byte) idKey == 0;
            }
            return false;
        }

        public static boolean isNotQueryAll(Object idKey) {
            return !isQueryAll(idKey);
        }
    }

}
