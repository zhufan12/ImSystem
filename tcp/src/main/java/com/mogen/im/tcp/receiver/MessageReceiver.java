package com.mogen.im.tcp.receiver;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mogen.im.codec.proto.MessagePack;
import com.mogen.im.common.constants.Constants;
import com.mogen.im.common.utils.JsonUtils;
import com.mogen.im.tcp.receiver.process.ProcessFactory;
import com.mogen.im.tcp.utils.MqFactory;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;

import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class MessageReceiver {


    private static final Logger log = LoggerFactory.getLogger(MessageReceiver.class);

    private static String brokerId = null;

    private static ObjectMapper objectMapper = JsonUtils.getInstance();

    private static void startReceiverMessage(){
        try {
            Channel channel = MqFactory
                    .getChannel(Constants.RabbitConstants.MessageServiceToIm + brokerId);
            channel.queueDeclare(Constants.RabbitConstants.MessageServiceToIm + brokerId,
                    true, false, false, null
            );
            channel.queueBind(Constants.RabbitConstants.MessageServiceToIm + brokerId,
                    Constants.RabbitConstants.MessageServiceToIm, brokerId);

            channel.basicConsume(Constants.RabbitConstants.MessageServiceToIm + brokerId, false,
                    new DefaultConsumer(channel) {
                        @Override
                        public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                            try {
                                String msgStr = new String(body);
                                MessagePack messagePack = objectMapper.readValue(msgStr,MessagePack.class);
                                log.info(msgStr);
                                ProcessFactory.getMessageProcess().process(messagePack);
                                channel.basicAck(envelope.getDeliveryTag(),false);

                            }catch (Exception e){
                                e.printStackTrace();
                                channel.basicNack(envelope.getDeliveryTag(),false,false);
                            }
                        }
                    }
            );
        }catch (Exception e){
            log.error("startReceiverMessage error msg: {}" ,e.getMessage());
            e.printStackTrace();
        }
    }

    public static void init() {
        startReceiverMessage();
    }

    public static void init(String brokerId) {
        if(brokerId != null || !brokerId.isEmpty()){
            MessageReceiver.brokerId = brokerId;
        }
        startReceiverMessage();
    }
}
