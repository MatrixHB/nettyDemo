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
 * NIO实现聊天功能的客户端（发起请求的一方）
 */
public class NioClient {

    private static NioUtil nioUtil = new NioUtil();
    private static boolean online = true;           //是否在线聊天

    public static void main(String[] args) throws IOException {

        System.out.println("客户端已启动！！");
        final SocketChannel channel = SocketChannel.open(new InetSocketAddress(8080));
        channel.configureBlocking(false);

        final Selector selector = Selector.open();
        channel.register(selector, SelectionKey.OP_READ);

        //发送消息的线程
        new Thread(new Runnable() {
            @Override
            public void run() {
                //输入发送到服务端的消息
                try {
                    nioUtil.doWrite(channel);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        channel.close();
                        online = false;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        //接收消息的线程（run方法重写不能抛出异常，所以写起来很费劲）
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (online && selector.select() > 0) {
                        Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                        while (online && iterator.hasNext()) {
                            SelectionKey key = iterator.next();
                            if(key.isReadable()){
                                nioUtil.doRead(key);
                            }
                            iterator.remove();
                        }
                    }
                }catch(IOException e){
                    e.printStackTrace();
                }finally {
                    try {
                        selector.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

}
