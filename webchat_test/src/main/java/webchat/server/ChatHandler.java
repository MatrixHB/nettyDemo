package webchat.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.springframework.stereotype.Component;

@Component
@ChannelHandler.Sharable
public class ChatHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    //多人聊天，多个channel
    private static ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    //广播消息
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, TextWebSocketFrame textWebSocketFrame) {
        String text = textWebSocketFrame.text();
        Channel speaker = channelHandlerContext.channel();
        for(Channel channel : channels){
            if(channel != speaker){
                channel.writeAndFlush(new TextWebSocketFrame("来自"+ speaker.remoteAddress() +"的用户：" + text));
            }else{
                channel.writeAndFlush(new TextWebSocketFrame("我：" + text));
            }
        }
    }

    //上线
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        for(Channel channel : channels){
            channel.writeAndFlush(new TextWebSocketFrame(ctx.channel().remoteAddress() + "已经进入聊天室"));
        }
        channels.add(ctx.channel());
    }

    //下线
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        for(Channel channel : channels){
            channel.writeAndFlush(new TextWebSocketFrame(ctx.channel().remoteAddress() + "已经离开聊天室"));
        }
        channels.remove(ctx.channel());
    }
}
