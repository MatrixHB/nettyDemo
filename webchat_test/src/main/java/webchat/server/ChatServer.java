package webchat.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;


/*
 * Server的启动类
 */
@Component
public class ChatServer {

    @Autowired
    ServerBootstrap serverBootstrap;

    private static Channel channel;

    public void start() throws InterruptedException{
        Channel channel = serverBootstrap.bind().sync().channel().closeFuture().sync().channel();
    }

    @PreDestroy
    public void stop(){
        System.out.println("聊天服务器关闭！");
        channel.close();
    }
}
