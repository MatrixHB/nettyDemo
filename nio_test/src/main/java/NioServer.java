import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;

/*
 * NIO实现聊天功能的服务端（接收请求的一方）
 */
public class NioServer {
    private static NioUtil nioUtil = new NioUtil();
    private static SocketChannel channel = null;

    public static void main(String[] args) throws IOException {

        System.out.println("服务端已启动！");
        //1、创建一个通道，并设置为非阻塞模式
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.configureBlocking(false);

        //2、绑定监听的端口，监听客户端连接
        serverChannel.bind(new InetSocketAddress(8080));

        //3、获取一个选择器，把通道注册到选择器上，监听Accept事件
        Selector selector = Selector.open();
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);

        //4、服务器轮询selector, selector如果有准备就绪的key，则可以进行处理
        while(selector.select() >0){
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while(iterator.hasNext()){
                SelectionKey key =  iterator.next();
                //监听到了客户端的接入请求
                if(key.isAcceptable()){
                    channel = nioUtil.doAccept(key);
                    //服务端也可以开始写消息了
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                nioUtil.doWrite(channel);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();

                }
                //监听到了客户端发来的消息
                else if(key.isReadable()){
                    nioUtil.doRead(key);
                }
                iterator.remove();
            }
        }

    }
}
