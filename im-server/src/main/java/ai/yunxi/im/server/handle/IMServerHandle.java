package ai.yunxi.im.server.handle;

import ai.yunxi.im.server.kit.HeartBeatHandler;
import ai.yunxi.im.server.kit.ServerHeartBeatHandlerImpl;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ai.yunxi.im.common.constant.MessageConstant;
import ai.yunxi.im.common.protocol.MessageProto;
import ai.yunxi.im.server.config.SpringBeanFactory;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.AttributeKey;

/**
 *
 * @author Athos
 * @createTime 2019年2月27日 下午2:02:42
 *
 */
public class IMServerHandle extends ChannelInboundHandlerAdapter {

	private final static Logger LOGGER = LoggerFactory.getLogger(IMServerHandle.class);

	private AttributeKey<Long> userId = AttributeKey.valueOf("userId");

	private ChannelMap CHANNEL_MAP = ChannelMap.newInstance();

	private final AttributeKey<Integer> userIdKey = AttributeKey.valueOf("userId");

	private ClientProcessor clientProcessor;


	public IMServerHandle() {
		this.clientProcessor = SpringBeanFactory.getBean(ClientProcessor.class);
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		MessageProto.MessageProtocol message = (MessageProto.MessageProtocol)msg;

		//处理客户端向服务端推送的消息
		if(MessageConstant.LOGIN.equals(message.getCommand())){
			//登录，保存Channel
			ctx.channel().attr(userId).set(message.getUserId());
			CHANNEL_MAP.putClient(message.getUserId(), ctx.channel());
			LOGGER.info("---客户端登录成功。userId:"+message.getUserId());
		}else if(MessageConstant.PING.equals(message.getCommand())){
			int userId= ctx.channel().attr(userIdKey).get();
			MessageProto.MessageProtocol heartBeat = MessageProto.MessageProtocol.newBuilder()
					.setCommand(MessageConstant.PONG)
					.setTime(System.currentTimeMillis())
//			         SingleMessage.Builder msg = SingleMessage.newBuilder();
//					.setMsg(MessageProto.SingleMessage.getDefaultInstance())
					.setUserId(userId).build();
			ctx.channel().writeAndFlush(heartBeat);
		}

	}

	/**
	 * 异常
	 * @param ctx
	 * @throws Exception
	 */
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		Long uid = ctx.channel().attr(userId).get();
		//从Channel缓存删除客户端
		CHANNEL_MAP.getCHANNEL_MAP().remove(uid);
		clientProcessor.down(uid);

		LOGGER.info("----客户端强制下线。userId:"+uid);
	}

	/**
	 * https://blog.csdn.net/lalalahaitang/article/details/81512844  ReaderIdleTimeoutTask run会
	 * 调用channelIdle方法。该方法会把时间下传到下一个handler的userEventTriggered方法。
	 *
	 *  超时 会将IdleStateHandler 添加到 ChannelPipeline 中
	 *  超时会触发用户的 userEventTriggered 方法：
	 * @param ctx
	 * @param evt
	 * @throws Exception
	 */
	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if (evt instanceof IdleStateEvent) {
			IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
			if (idleStateEvent.state() == IdleState.READER_IDLE) {

				LOGGER.info("定时检测客户端端是否存活");

				HeartBeatHandler heartBeatHandler = SpringBeanFactory.getBean(ServerHeartBeatHandlerImpl.class) ;
				heartBeatHandler.process(ctx) ;
			}
		}
		super.userEventTriggered(ctx, evt);
	}
}
