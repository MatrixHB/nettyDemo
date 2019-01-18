package webchat.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.CharsetUtil;
import org.springframework.stereotype.Component;

import java.nio.charset.Charset;

/*
 * websocket是一个持久化的协议，客户端先发送http请求
 * http请求头中有两项为：Upgrade:websocket、Connection: Upgrade，这表示发起的是websocket协议
 * http返回成功响应之后，说明已切换协议，建立起websocket连接，之后传输的是websocket帧
 */
@Component
@ChannelHandler.Sharable            //标注一个Handler可以被多个channel安全地共享
public class WebsocketHandler extends ChannelInboundHandlerAdapter {

    private WebSocketServerHandshaker handshaker;
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //处理websocket连接请求
        if(msg instanceof FullHttpRequest){
            FullHttpRequest request = (FullHttpRequest)msg;
            //如果请求失败或者不是一个websocket连接请求，则返回错误信息；否则建立起websocket连接
            if( !request.decoderResult().isSuccess() || !"websocket".equals(request.headers().get("Upgrade")) ){

                DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST);
                ByteBuf byteBuf = Unpooled.copiedBuffer("请求异常", CharsetUtil.UTF_8);
                response.content().writeBytes(byteBuf);
                byteBuf.release();
                ctx.writeAndFlush(response);

            }else{
                WebSocketServerHandshakerFactory factory = new WebSocketServerHandshakerFactory("ws://localhost:8888/websocket", null, false);
                handshaker = factory.newHandshaker(request);
                if(handshaker ==null){
                    factory.sendUnsupportedVersionResponse(ctx.channel());        //建立websocket连接失败
                }else{
                    handshaker.handshake(ctx.channel(), request);
                }
            }

        }
        //处理在websocket上的数据传输
        else if(msg instanceof WebSocketFrame){
            //处理关闭连接命令
            if(msg instanceof CloseWebSocketFrame){
                handshaker.close(ctx.channel(),(CloseWebSocketFrame)msg);
            }
            //处理纯文本数据
            else if(msg instanceof TextWebSocketFrame){
                String text = ((TextWebSocketFrame)msg).text();
                ctx.channel().writeAndFlush(new TextWebSocketFrame("我：" + text));
            }
        }
    }
}
