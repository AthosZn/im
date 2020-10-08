package ai.yunxi.im.client.handle;

import ai.yunxi.im.client.config.InitConfiguration;
import ai.yunxi.im.client.config.SpringBeanFactory;
import ai.yunxi.im.common.constant.MessageConstant;
import ai.yunxi.im.common.protocol.MessageProto;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 *  用于捕获{@link IdleState#WRITER_IDLE}事件（未在指定时间内向服务器发送数据），然后向<code>Server</code>端发送一个心跳包。
 * </p>
 */
public class ClientIdleStateTrigger extends ChannelInboundHandlerAdapter {

    private final static Logger LOGGER = LoggerFactory.getLogger(ClientIdleStateTrigger.class);

    private InitConfiguration conf;

    public ClientIdleStateTrigger() {
        super();
        this.conf = SpringBeanFactory.getBean(InitConfiguration.class);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.WRITER_IDLE) {
                // write heartbeat to server
                int userId= conf.getUserId();
                MessageProto.MessageProtocol heartBeat = MessageProto.MessageProtocol.newBuilder()
                        .setCommand(MessageConstant.PING)
                        .setTime(System.currentTimeMillis())
                        .setContent(String.valueOf(MessageConstant.PING))
                        .setUserId(userId).build();
                ctx.writeAndFlush(heartBeat);
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

}
