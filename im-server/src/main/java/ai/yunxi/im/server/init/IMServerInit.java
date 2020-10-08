package ai.yunxi.im.server.init;

import javax.annotation.PostConstruct;

import ai.yunxi.im.common.protocol.MessageProto;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ai.yunxi.im.common.protocol.MessageProto;
import ai.yunxi.im.server.config.InitConfiguration;
import ai.yunxi.im.server.handle.IMServerHandle;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;

/**
 *
 * @author Athos
 * @createTime 2019年2月27日 下午1:35:52
 *
 */
//@Component
public class IMServerInit {

	private final static Logger LOGGER = LoggerFactory.getLogger(IMServerInit.class);

	private EventLoopGroup acceptorGroup = new NioEventLoopGroup();
	private EventLoopGroup workerGroup = new NioEventLoopGroup();

	@Autowired
	private InitConfiguration conf;

	@PostConstruct
	public void start() throws Exception{
		try {
			//Netty用于启动NIO服务器的辅助启动类
			ServerBootstrap sb = new ServerBootstrap();
			//将两个NIO线程组传入辅助启动类中
			sb.group(acceptorGroup, workerGroup)
				//设置创建的Channel为NioServerSocketChannel类型
				.channel(NioServerSocketChannel.class)
				//保持长连接
				.childOption(ChannelOption.SO_KEEPALIVE, true)
				//设置绑定IO事件的处理类
				.childHandler(new ChannelInitializer<SocketChannel>() {
					@Override
					protected void initChannel(SocketChannel ch) throws Exception {
						ChannelPipeline pipeline = ch.pipeline();
						pipeline.addLast(new IdleStateHandler(60, 0, 0));
						// google Protobuf 编解码
						pipeline.addLast(new ProtobufVarint32FrameDecoder());
					    pipeline.addLast(new ProtobufDecoder(MessageProto.MessageProtocol.getDefaultInstance()));
					    pipeline.addLast(new ProtobufVarint32LengthFieldPrepender());
					    pipeline.addLast(new ProtobufEncoder());
//				在 channelRead() 方法里面，我们打印当前 handler 的信息，然后调用父类的
//				channelRead() 方法，而这里父类的 channelRead() 方法会自动调用到下一个
//				inBoundHandler 的 channelRead() 方法，并且会把当前 inBoundHandler
//				里处理完毕的对象传递到下一个 inBoundHandler，我们例子中传递的对象都是同一个 msg。
						pipeline.addLast(new IMServerHandle());
					}
				});
			ChannelFuture conn = sb.bind(conf.getNettyPort()).sync();
			if(conn.isSuccess()){
				LOGGER.info("---服务端启动成功，端口["+conf.getNettyPort()+"]");
			}
		} finally {
//			acceptorGroup.shutdownGracefully();
//			workerGroup.shutdownGracefully();
		}
	}
}
