import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AIOServer {
    static final int PORT = 30000;
    final static String UTF_8 = "utf-8";
    static List<AsynchronousSocketChannel> channelList = new ArrayList<>();
    public void startListen() throws IOException {
        //创建线程池
        ExecutorService executor = Executors.newFixedThreadPool(20);
        //以指定线程池创建AsynchronousChannelGroup
        AsynchronousChannelGroup channelGroup = AsynchronousChannelGroup.withThreadPool(executor);
        //以指定线程池创建AsynchronousServerSocketChannel
        AsynchronousServerSocketChannel serverSocketChannel =
                AsynchronousServerSocketChannel.open(channelGroup).bind(new InetSocketAddress(PORT));
        //使用CompletionHandler接收来自客户端的连接请求
        serverSocketChannel.accept(null, new AcceptHandler(serverSocketChannel));

    }
        public static void main(String[]args) throws IOException {
            AIOServer server = new AIOServer();
            server.startListen();
        }


        //实现自己的completionHandler
        class AcceptHandler implements CompletionHandler<AsynchronousSocketChannel,Object>{

            private AsynchronousServerSocketChannel serverSocketChannel;
            public AcceptHandler(AsynchronousServerSocketChannel sc){
                this.serverSocketChannel=sc;
            }

            //d定义一个bytebuffer准备读取数据
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            //当实际IO操作完成时触发该方法
            @Override
            public void completed(final AsynchronousSocketChannel sc, Object attachment) {
                //记录新连接进来的Channel
                AIOServer.channelList.add(sc);
                //准备接收客户端的下一次连接
                serverSocketChannel.accept(null,this);
                sc.read(buffer, null, new CompletionHandler<Integer, Object>() {
                    @Override
                    public void completed(Integer result, Object attachment) {
                        buffer.flip();
                        String content = StandardCharsets.UTF_8.decode(buffer).toString();
                        //遍历，群发消息
                        for(AsynchronousSocketChannel c : AIOServer.channelList){
                            try {
                                c.write(ByteBuffer.wrap(content.getBytes(AIOServer.UTF_8))).get();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            buffer.clear();
                            //读取下一次数据
                            sc.read(buffer,null,this);
                        }
                    }

                    @Override
                    public void failed(Throwable exc, Object attachment) {
                        System.out.println("读取数据失败："+exc);
                        AIOServer.channelList.remove(sc);
                    }
                });
            }

            @Override
            public void failed(Throwable exc, Object attachment) {
                System.out.println("连接失败");
            }
        }

}
