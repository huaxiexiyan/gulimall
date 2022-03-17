package io.renren.config;

import com.alibaba.druid.filter.config.ConfigTools;
import com.alibaba.druid.util.DruidPasswordCallback;
import lombok.extern.slf4j.Slf4j;

import java.util.Properties;

/**
 * @author Lionel
 * @date 2022-03-15 09:16
 */
@Slf4j
public class DbPasswordCallback extends DruidPasswordCallback {

    @Override
    public void setProperties(Properties properties) {
        super.setProperties(properties);
        String password = (String) properties.get("password");
        String publickey = (String) properties.get("publicKey");
        try {
            String dbpassword = ConfigTools.decrypt(publickey, password);
            setPassword(dbpassword.toCharArray());
        } catch (Exception e) {
            log.error("Druid ConfigTools.decrypt", e);
        }
    }
}