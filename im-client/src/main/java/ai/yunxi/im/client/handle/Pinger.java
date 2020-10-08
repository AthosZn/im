package ai.yunxi.im.client.handle;

import ai.yunxi.im.client.config.InitConfiguration;
import ai.yunxi.im.client.config.SpringBeanFactory;
import ai.yunxi.im.common.constant.MessageConstant;
import ai.yunxi.im.common.protocol.MessageProto;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.ScheduledFuture;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * <p>客户端连接到服务器端后，会循环执行一个任务：随机等待几秒，然后ping一下Server端，即发送一个心跳包。</p>
 */
public class Pinger extends ChannelInboundHandlerAdapter {

    private Random random = new Random();
    private int baseRandom = 8;

    private Channel channel;

    private AttributeKey<Integer> userIdKey = AttributeKey.valueOf("userId");


    private InitConfiguration conf;

    public Pinger() {
        super();
        this.conf = SpringBeanFactory.getBean(InitConfiguration.class);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        ping(ctx.channel());
    }

    private void ping(final Channel channel) {
        int second = Math.max(1, random.nextInt(baseRandom));
        System.out.println("next heart beat will send after " + second + "s.");
        ScheduledFuture<?> future = channel.eventLoop().schedule(new Runnable() {
            @Override
            public void run() {
                if (channel.isActive()) {
                    System.out.println("sending heart beat to the server...");
                    MessageProto.MessageProtocol heartBeat = MessageProto.MessageProtocol.newBuilder()
                            .setCommand(MessageConstant.PING)
                            .setTime(System.currentTimeMillis())
                            .setContent(String.valueOf(MessageConstant.PING))
                            .setUserId(conf.getUserId()).build();
                    channel.writeAndFlush(heartBeat);
                } else {
                    System.err.println("The connection had broken, cancel the task that will send a heart beat.");
                    channel.closeFuture();
                    throw new RuntimeException();
                }
            }
        }, second, TimeUnit.SECONDS);

        future.addListener(new GenericFutureListener() {
            @Override
            public void operationComplete(Future future) throws Exception {
                if (future.isSuccess()) {
                    ping(channel);
                }
            }
        });
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // 当Channel已经断开的情况下, 仍然发送数据, 会抛异常, 该方法会被调用.
        cause.printStackTrace();
        ctx.close();
    }
}
