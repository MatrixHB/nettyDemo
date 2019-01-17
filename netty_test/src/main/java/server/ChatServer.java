package server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/*
 *  用netty实现一个多人聊天室，多个客户端，服务端负责建立连接、接收并广播消息
 */

public class ChatServer {
    public static void main(String[] args) throws InterruptedException {

        //两个线程池，分别处理连接操作和读写操作
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();

        try{
            ServerBootstrap sb = new ServerBootstrap();
            sb.group(boss,worker)                                             //绑定两个线程池
                    .channel(NioServerSocketChannel.class)                   //指定使用的channel
                    .localAddress(8080)                                       //绑定监听的端口
                    .childHandler(new ChatServerInitializer())                //绑定客户端连接时触发的初始化操作
                    .childOption(ChannelOption.SO_KEEPALIVE, true);  //指定与客户端连接为长连接

            //创建一个异步IO操作，netty内部将操作交给线程池去处理
            //future接口就是用来封装异步操作的执行状态的，在执行异步操作时，可以立马返回一个future
            //future可以通过sync()，来等待执行结果，此时线程会阻塞在这里
            // 如果有些操作要等到future代表的异步操作完了才能执行，可以通过future.addListener()的方式，在异步操作完成的时候执行新的操作。
            ChannelFuture cf = sb.bind().sync();
            System.out.println("服务端已启动，正在监听端口" + cf.channel().localAddress());

            //返回channel的closeFuture对象，并执行closeFuture对象的sync()方法
            //这一步代表关闭channel
            cf.channel().closeFuture().sync();
        }finally {
            //释放线程池资源
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }
}
