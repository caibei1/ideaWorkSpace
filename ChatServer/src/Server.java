import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    public static CrazyitMap<String,PrintStream> clients = new CrazyitMap<>();
    //public  static LinkedList<Socket> socketList = new LinkedList<Socket>();
    public void init(){
        try {
            ServerSocket ss = new ServerSocket(30000);
            while(true){
                Socket socket = ss.accept();
                //socketList.add(socket);
                //每当客户端连接后，启动新线程为客户端服务
                new Thread(new ServerThread(socket)).start();
            }
        }catch (IOException e){
           System.out.println("服务器启动失败，是否端口被占用？");
        }
    }
    public static void main(String [] args){
        Server server = new Server();
        server.init();

    }
}
