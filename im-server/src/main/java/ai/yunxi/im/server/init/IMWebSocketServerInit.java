package ai.yunxi.im.server.init;

import ai.yunxi.im.common.protocol.MessageProto;
import ai.yunxi.im.server.config.InitConfiguration;
import ai.yunxi.im.server.handle.IMServerHandle;
import ai.yunxi.im.server.handle.WebSocketServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 *
 * @author Athos
 * @createTime 2019年2月27日 下午1:35:52
 *
 */
//@Component
public class IMWebSocketServerInit {

	private final static Logger LOGGER = LoggerFactory.getLogger(IMWebSocketServerInit.class);

	private EventLoopGroup bossGroup = new NioEventLoopGroup();
	private EventLoopGroup workerGroup = new NioEventLoopGroup();

	@Autowired
	private InitConfiguration conf;

	@PostConstruct
	public void start() throws Exception{
		try {
			ServerBootstrap b = new ServerBootstrap();

			Runtime.getRuntime().addShutdownHook(new Thread() {
				@Override
				public void run() {
					try {
						bossGroup.shutdownGracefully();
						workerGroup.shutdownGracefully();
						System.err.println("The JVM Hook is execute!");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			b.group(bossGroup, workerGroup)
					.channel(NioServerSocketChannel.class)
					.childHandler(new ChannelInitializer<SocketChannel>() {
						protected void initChannel(SocketChannel socketChannel) throws Exception {
							ChannelPipeline pipeline = socketChannel.pipeline();
							// HTTP请求的解码和编码
							pipeline.addLast("http-codec", new HttpServerCodec());
                            // 把多个消息转换为一个单一的FullHttpRequest或是FullHttpResponse，
							// 原因是HTTP解码器会在每个HTTP消息中生成多个消息对象HttpRequest/HttpResponse,HttpContent,LastHttpContent
							pipeline.addLast("aggregator", new HttpObjectAggregator(65536));
							// 主要用于处理大数据流，比如一个1G大小的文件如果你直接传输肯定会撑暴jvm内存的; 增加之后就不用考虑这个问题了
							pipeline.addLast("http-chunked", new ChunkedWriteHandler());
							pipeline.addLast(new WebSocketServerCompressionHandler());
							pipeline.addLast("wshandler", new WebSocketServerHandler());
							// google Protobuf 编解码
							pipeline.addLast(new ProtobufVarint32FrameDecoder());
							pipeline.addLast(new ProtobufDecoder(MessageProto.MessageProtocol.getDefaultInstance()));
							pipeline.addLast(new ProtobufVarint32LengthFieldPrepender());
							pipeline.addLast(new ProtobufEncoder());
							pipeline.addLast(new IdleStateHandler(60, 0, 0));
							pipeline.addLast(new IMServerHandle());
						}
					});

			ChannelFuture conn = b.bind(conf.getNettyPort()).sync();
			if(conn.isSuccess()){
				System.out.println("websocketserver start port at " + conf.getNettyPort());
			}

		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
//			bossGroup.shutdownGracefully();
//			workerGroup.shutdownGracefully();
		}
	}

}
