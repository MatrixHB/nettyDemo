package client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.Scanner;

public class ChatClient {
    public static void main(String[] args) throws InterruptedException{
        NioEventLoopGroup group = new NioEventLoopGroup();
        try{
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group).channel(NioSocketChannel.class)
                    .remoteAddress("127.0.0.1",8080)      //绑定连接的host和端口
                    .handler(new ChatClientInitializer());
            ChannelFuture cf = bootstrap.connect().sync();

            Scanner sc = new Scanner(System.in);
            while(sc.hasNext()){
                String str = sc.nextLine();
                if("end".equals(str))
                    break;
                cf.channel().writeAndFlush(str);
            }

            cf.channel().closeFuture().sync();
        }finally {
            group.shutdownGracefully();
        }

    }

}
