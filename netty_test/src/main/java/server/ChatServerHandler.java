package server;

import client.HeartBeat;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

/*
 * 服务端处理消息的类
 */
public class ChatServerHandler extends SimpleChannelInboundHandler {

    //多人聊天，有多个channel
    public static ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object msg) {

        if(msg instanceof HeartBeat){
            HeartBeat heartBeat = (HeartBeat)msg;
            System.out.println("---------------收到客户端心跳：" + heartBeat.toString() +"---------------");
            channelHandlerContext.channel().writeAndFlush(new HeartBeat(123456L,"pong"));
        }
        //向所有在线聊天的客户端广播消息
        else if(msg instanceof String){
            for(Channel channel : channels){
                channel.writeAndFlush(channelHandlerContext.channel().remoteAddress() + "的消息：" +msg);
            }
        }

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("来自" + ctx.channel().remoteAddress() + "的用户连接上了！");
        channels.add(ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("来自" + ctx.channel().remoteAddress() + "的用户下线了！");
        channels.remove(ctx.channel());
    }

    //重写此方法，客户端退出的时候不会报异常
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("exception");
    }

}
