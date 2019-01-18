package client;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

public class ChatClientHandler extends SimpleChannelInboundHandler {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object msg) {
        if(msg instanceof HeartBeat){
            HeartBeat heartBeat = (HeartBeat)msg;
            System.out.println("---------------收到服务器回应：" + heartBeat.toString() + "---------------");
        }
        else if(msg instanceof String){
            System.out.println(msg);
        }

    }


    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if(evt instanceof IdleStateEvent){
            IdleStateEvent idleStateEvent = (IdleStateEvent)evt;
            //readerIdleTimeSeconds：一段时间内没有数据读取
            //writerIdleTimeSeconds：一段时间内没有数据发送
            //allIdleTimeSeconds：以上两种满足其中一个即可
            if(idleStateEvent.state() == IdleState.ALL_IDLE){
                System.out.println("---------------已经15秒没有收到消息了---------------");
                String port = ctx.channel().localAddress().toString().split(":")[1];
                ctx.writeAndFlush(new HeartBeat(Long.valueOf(port),"ping"))
                        .addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            }
        }
    }
}
