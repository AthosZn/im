package ai.yunxi.im.server.kit;

import ai.yunxi.im.common.constant.BasicConstant;
import ai.yunxi.im.server.config.InitConfiguration;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * Function: 处理心跳下线逻辑
 *
 * @author crossoverJie
 * Date: 2019-01-20 17:16
 * @since JDK 1.8
 */
@Service
public class ServerHeartBeatHandlerImpl implements HeartBeatHandler {

    private final static Logger LOGGER = LoggerFactory.getLogger(ServerHeartBeatHandlerImpl.class);

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private AttributeKey<Integer> userIdKey = AttributeKey.valueOf("userId");

    @Autowired
    private InitConfiguration conf;


    @Override
    public void process(ChannelHandlerContext ctx) throws Exception {
        int userId= ctx.channel().attr(userIdKey).get();
        redisTemplate.opsForValue().getOperations().delete(BasicConstant.ROUTE_PREFIX+  userId);
        LOGGER.info("路由端处理了用户下线逻辑："+userId);
        ctx.channel().close();

//        long heartBeatTime = conf.getHeartbeatTime() * 1000;
//
//        Long lastReadTime = NettyAttrUtil.getReaderTime(ctx.channel());
//        long now = System.currentTimeMillis();
//        if (lastReadTime != null && now - lastReadTime > heartBeatTime){
//            CIMUserInfo userInfo = SessionSocketHolder.getUserId((NioSocketChannel) ctx.channel());
//            if (userInfo != null){
//                LOGGER.warn("客户端[{}]心跳超时[{}]ms，需要关闭连接!",userInfo.getUserName(),now - lastReadTime);
//            }
//            routeHandler.userOffLine(userInfo, (NioSocketChannel) ctx.channel());
//            ctx.channel().close();
//        }
    }
}
