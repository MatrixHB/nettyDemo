package server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

/*
 * 服务端的初始化程序(channel的初始化)
 */
public class ChatServerInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel socketChannel) {
        ChannelPipeline pipeline = socketChannel.pipeline();

        //之前出现bug的原因是同时添加了String编解码器和HeartBeat编解码器，出现了冲突
        //因此用ObjectDecoder/ObjectEncoder统一多种类型消息的编解码

        pipeline.addLast(new ObjectDecoder(ClassResolvers.softCachingResolver(this.getClass().getClassLoader())));
        pipeline.addLast( new ObjectEncoder());
        pipeline.addLast(new ChatServerHandler());
    }
}
