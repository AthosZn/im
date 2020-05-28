//package ai.yunxi.im.server.factory;
//
//import ai.yunxi.im.server.handle.WebsocketServerHandler;
//import org.jboss.netty.channel.ChannelPipeline;
//import org.jboss.netty.channel.ChannelPipelineFactory;
//import org.jboss.netty.handler.codec.http.HttpChunkAggregator;
//import org.jboss.netty.handler.codec.http.HttpRequestDecoder;
//import org.jboss.netty.handler.codec.http.HttpResponseEncoder;
//
//import static org.jboss.netty.channel.Channels.pipeline;
//
///**
// *
// * <P>Description: TODO(用一句话描述该文件做什么) </P>
// * @ClassName: WebSocketServerPipelineFactory
// * @author guojw  2014年5月14日 上午11:03:30
// * @see WebSocketServerPipelineFactory
// */
//public class WebSocketServerPipelineFactory implements ChannelPipelineFactory {
//	public ChannelPipeline getPipeline() throws Exception {
//		// Create a default pipeline implementation.
//		ChannelPipeline pipeline = pipeline();
//		pipeline.addLast("decoder", new HttpRequestDecoder());
//		pipeline.addLast("aggregator", new HttpChunkAggregator(65536));
//		pipeline.addLast("encoder", new HttpResponseEncoder());
//		pipeline.addLast(new WebsocketServerHandler());
//		return pipeline;
//	}
//}
