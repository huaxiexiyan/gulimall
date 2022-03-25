package com.atguigu.common.valid;

import javax.validation.Payload;

/**
 * @author Lionel
 * @date 2022-03-24 15:12
 */
public @interface ListValue {

    String message() default "{javax.validation.constraints.NotBlank.message}";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

}
