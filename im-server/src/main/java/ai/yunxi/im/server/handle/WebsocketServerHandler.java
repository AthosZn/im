package ai.yunxi.im.server.handle;

import ai.yunxi.im.common.protocol.MessageProto;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;

import java.util.Date;

public class WebsocketServerHandler extends SimpleChannelInboundHandler<Object> {

    private WebSocketServerHandshaker handshaker;

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
        //构造握手响应返回
        WebSocketServerHandshakerFactory webSocketServerHandshakerFactory=new WebSocketServerHandshakerFactory("ws://localhost:20000/web",null,false);
        handshaker=webSocketServerHandshakerFactory.newHandshaker(req);
        handshaker.handshake(ctx.channel(),req);

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
        if(frame.getClass().equals(TextWebSocketFrame.class)){
//            ctx.fireChannelRead(((TextWebSocketFrame)frame).text());
        }else {
            ctx.fireChannelRead(((BinaryWebSocketFrame) frame).content());
            ByteBuf buf = ((BinaryWebSocketFrame) frame).content();
        }
//        MessageProto.MessageProtocol message = (MessageProto.MessageProtocol)frame.content();
//        //文本消息处理
//        String request=((TextWebSocketFrame)frame).text();
//        System.out.println("接受的信息是："+request);
//        String date=new Date().toString();
//        //将接收消息写回给客户端
//        ctx.channel().write(new TextWebSocketFrame("现在时刻:"+date+"发送了:"+request));
    }
}
