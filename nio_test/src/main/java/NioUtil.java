import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

public class NioUtil {

    //用于客户端（或服务端）写消息
    public void doWrite(SocketChannel channel) throws IOException{
        Scanner sc = new Scanner(System.in);
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        while(sc.hasNext()){
            String input = sc.nextLine();
            if("end".equals(input))       //客户端写入“end”可以退出聊天
                break;
            buffer.put(input.getBytes());
            buffer.flip();
            channel.write(buffer);
            buffer.clear();
        }
    }

    //用于客户端（或服务端）读取消息
    public void doRead(SelectionKey key) throws IOException{
        SocketChannel channel = (SocketChannel)key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        while (channel.read(buffer) >0) {
            buffer.flip();
            byte[] bytes = buffer.array();
            String msg = new String(bytes, "UTF-8");
            System.out.println(msg);
            buffer.clear();
        }
    }

    //用于服务端接受连接请求
    public SocketChannel doAccept(SelectionKey key) throws IOException{
        ServerSocketChannel serverChannel = (ServerSocketChannel)key.channel();
        //与客户端三次握手，建立连接通道
        SocketChannel channel = serverChannel.accept();
        //设置为非阻塞
        channel.configureBlocking(false);
        //注册到selector上，并监听读事件
        channel.register(key.selector(),SelectionKey.OP_READ);

        return channel;
    }

}
