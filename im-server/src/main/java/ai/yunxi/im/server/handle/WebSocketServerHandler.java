package ai.yunxi.im.server.handle;

import ai.yunxi.im.common.protocol.MessageProto;
import ai.yunxi.im.common.utils.Util;
import ai.yunxi.im.server.init.IMWebSocketServerInit;
import com.sun.jndi.toolkit.url.UrlUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.util.AttributeKey;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpHeaderNames.HOST;
import static io.netty.handler.codec.http.HttpUtil.isKeepAlive;
import static io.netty.handler.codec.http.HttpUtil.setContentLength;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import static org.springframework.http.HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS;

public class WebSocketServerHandler extends SimpleChannelInboundHandler<Object> {

    private WebSocketServerHandshaker handshaker;
    private final static Logger LOGGER = LoggerFactory.getLogger(WebSocketServerHandler.class);
    // websocket 服务的 uri
    private static final String WEBSOCKET_PATH = "/websocket";
    private AttributeKey<Integer> userId = AttributeKey.valueOf("userId");
    private ChannelMap CHANNEL_MAP = ChannelMap.newInstance();

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {
        //传统的http接入
        if(o instanceof FullHttpRequest){
            handleHttpRequest(channelHandlerContext,(FullHttpRequest) o);
        }
        //webSocket接入
        else if(o instanceof WebSocketFrame){

            handleWebsocketFrame(channelHandlerContext,(WebSocketFrame) o);
        }
        else{
            channelHandlerContext.fireChannelRead(o);
        }

    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }


    private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest req){
        //HTTP 请异常
        if (!req.decoderResult().isSuccess() || !"websocket".equals(req.headers().get("Upgrade"))) {
            System.out.println(req.decoderResult().isSuccess());
            System.out.println(req.headers().get("Upgrade"));
            sendHttpResponse(ctx, req, new DefaultFullHttpResponse(HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
            return;
        }
        //构造握手响应返回
//        WebSocketServerHandshakerFactory webSocketServerHandshakerFactory=new WebSocketServerHandshakerFactory("ws://localhost:20000/web",null,false);
        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(getWebSocketLocation(req), null, true);
        handshaker=wsFactory.newHandshaker(req);
        if (handshaker == null) {
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
        } else {
            ChannelFuture channelFuture =handshaker.handshake(ctx.channel(), req);
            if (channelFuture.isSuccess()) {
                Integer uid= Integer.valueOf(Util.getParamByUrl(req.uri(),"userId"));
                ctx.channel().attr(userId).set(uid);
                CHANNEL_MAP.putClient(uid, ctx.channel());
                LOGGER.info("---客户端登录成功。userId:"+uid);
                return;
            }
        }
    }

    private void handleWebsocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame){
        //判断是否是链路关闭消息
        if(frame instanceof CloseWebSocketFrame){
            handshaker.close(ctx.channel(),(CloseWebSocketFrame) frame.retain());
            return;
        }
        //判断是否是ping消息
        if(frame instanceof PingWebSocketFrame){
            ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
            return;
        }
//        ReferenceCountUtil.release(frame);
        if(frame.getClass().equals(TextWebSocketFrame.class)){
            ctx.fireChannelRead(((TextWebSocketFrame)frame).text());
        }else {
            ctx.fireChannelRead(frame);
//            ByteBuf buf = ((BinaryWebSocketFrame) frame).content();
        }
//        MessageProto.MessageProtocol message = (MessageProto.MessageProtocol)frame.content();
//        //文本消息处理
//        String request=((TextWebSocketFrame)frame).text();
//        System.out.println("接受的信息是："+request);
//        String date=new Date().toString();
//        //将接收消息写回给客户端
//        ctx.channel().write(new TextWebSocketFrame("现在时刻:"+date+"发送了:"+request));
    }

    private static String getWebSocketLocation(FullHttpRequest req) {
        String location = req.headers().get(HOST) + WEBSOCKET_PATH;
        return "ws://" + location;
    }


    private void sendHttpResponse(ChannelHandlerContext ctx, FullHttpRequest msg, FullHttpResponse resp) {

//        response.headers().set(CONTENT_TYPE, "text/plain; charset=UTF-8");
        resp.headers().set(CONTENT_TYPE, "application/json;charset=UTF-8");
//        String host = msg.headers().get("Host");
        //允许跨域访问
//        resp.headers().set("Access-Control-Allow-Origin", "http://localhost:8090");
        resp.headers().set("Access-Control-Allow-Origin", "*");
        resp.headers().set("Access-Control-Allow-Methods", "POST, GET, PUT, OPTIONS, DELETE,HEAD");
        resp.headers().set("Access-Control-Max-Age", "3600");
        resp.headers().set("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");
        resp.headers().set(ACCESS_CONTROL_ALLOW_CREDENTIALS,"false");

        //响应
        if (resp.status().code() != 200) {
            ByteBuf buf = Unpooled.copiedBuffer(resp.status().toString(), CharsetUtil.UTF_8);
            resp.content().writeBytes(buf);
            buf.release();
            setContentLength(resp, resp.content().readableBytes());
        }

        //非Keep-Alive,关闭链接
        ChannelFuture future = ctx.channel().writeAndFlush(resp);
        if (!isKeepAlive(resp) || resp.status().code() != 200) {
            future.addListener(ChannelFutureListener.CLOSE);
        }

    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel incoming = ctx.channel();
        System.out.println("收到" + incoming.remoteAddress() + " 握手请求");
    }
}
