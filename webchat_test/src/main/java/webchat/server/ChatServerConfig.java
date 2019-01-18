package webchat.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/*
 * 配置Netty线程池、ServerBootstrap
 * 注意：自定义的WebsocketHandler也处理了TextWebSocketFrame，
 *       如果和显示处理TextWebSocketFrame的ChatHandler同时加到pipeline中，则ChatHandler中对TextWebSocketFrame的处理不生效
 */
@Configuration
public class ChatServerConfig {

//    @Autowired
//    WebsocketHandler websocketHandler;

    @Autowired
    ChatHandler chatHandler;

    @Bean(destroyMethod = "shutdownGracefully")
    public NioEventLoopGroup bossGroup(){
        return new NioEventLoopGroup();
    }

    @Bean(destroyMethod = "shutdownGracefully")
    public NioEventLoopGroup workerGroup(){
        return new NioEventLoopGroup();
    }

    @Bean
    public ServerBootstrap serverBootstrap(){
        ServerBootstrap sb = new ServerBootstrap();
        sb.group(bossGroup(), workerGroup())
                .channel(NioServerSocketChannel.class)
                .localAddress(8888)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) {
                        //添加http请求响应的编码解码器
                        socketChannel.pipeline().addLast(new HttpServerCodec());
                        //HttpObjectAggregator这个handler用于将多个消息对象变成一个完整的请求或响应 fullHttpMessage
                        socketChannel.pipeline().addLast(new HttpObjectAggregator(65536));
                        //ChunkedWriteHandler用于处理大数据流，比如超过JVM大小的文件
                        socketChannel.pipeline().addLast(new ChunkedWriteHandler());
//                        socketChannel.pipeline().addLast(websocketHandler);

                        //在这里我们在chatHandler中显示处理 TextWebSocketFrame，其他的会由 WebSocketServerProtocolHandler 自动处理
                        socketChannel.pipeline().addLast(new WebSocketServerProtocolHandler("/websocket"));
                        socketChannel.pipeline().addLast(chatHandler);
                    }
                }).childOption(ChannelOption.SO_KEEPALIVE,true);
        return sb;
    }
}
