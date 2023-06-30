package com.mogen.im.service.message.entity.id;


import com.mogen.im.service.utils.SnowflakeIdWorker;
import com.mogen.im.service.utils.SpringUtil;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.util.TypeInformation;

public class MessageKeyGenerator implements IdentifierGenerator {


    @Override
    public Object generate(SharedSessionContractImplementor session, Object object) {
        SnowflakeIdWorker snowflakeIdWorker = SpringUtil.getBean(SnowflakeIdWorker.class);
        long id = snowflakeIdWorker.nextId();
        return id;
    }
}
