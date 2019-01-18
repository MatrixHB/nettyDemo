package client;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.timeout.IdleStateHandler;

public class ChatClientInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel socketChannel) {
        ChannelPipeline pipeline = socketChannel.pipeline();
        pipeline.addLast(new ObjectDecoder(ClassResolvers.softCachingResolver(this.getClass().getClassLoader())));
        pipeline.addLast( new ObjectEncoder());
        //添加空闲时间超时的监控Handler
        pipeline.addLast(new IdleStateHandler(0, 0, 15));
        pipeline.addLast( new ChatClientHandler());

    }
}
