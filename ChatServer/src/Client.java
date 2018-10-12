import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;


public class Client {
    private Socket socket;
    private  PrintStream ps;
    private  BufferedReader brServer;
    private BufferedReader ketIn;

    public void init(){
        try{
            //初始化键盘输入流
            ketIn = new BufferedReader(new InputStreamReader(System.in));
            //连接服务器
            socket = new Socket("127.0.0.1",30000);
            ps = new PrintStream(socket.getOutputStream());
            brServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String tip = "";
            while(true){
                String userName = JOptionPane.showInputDialog(tip+"输入用户名");
                //在用户输入的用户名前后增加协议字符串后发送
                ps.println(CrazyitProtocol.USER_ROUND+userName+CrazyitProtocol.USER_ROUND);
                //读取服务器响应
                String result = brServer.readLine();
                //如果用户名重复，则开始下次循环
                if (result.equals(CrazyitProtocol.NAME_REP)){
                    tip="用户名重复，请重新";
                    continue;
                }
                //如果服务器返回登陆成功，则结束循环
                if(result.equals(CrazyitProtocol.LOGIN_SUCCESS)){
                    break;
                }
            }
        //捕获到异常，关闭资源并退出
        }catch (UnknownHostException e){
            System.out.println("找不到服务器");
            closeRs();
        }catch (IOException e1){
            System.out.println("网络异常，请重新登陆");
            closeRs();
            System.exit(1);
        }
        //以该socket对应的输入流启动ClientThread线程  用于读取信息
        new Thread(new ClientThread(brServer)).start();

    }

    //定义一个读取键盘输出，并向网络发送的方法
    private void readAndSend(){
        try{
            String context = null;
            while ((context=ketIn.readLine())!=null){
                //如果发送的消息有冒号，并以//开头，则认为想发送私聊信息
                if(context.indexOf(":")>0&&context.startsWith("//")){
                    context=context.substring(2);
                    ps.println(CrazyitProtocol.PRIVATE_ROUND+context.split(":")[0]+CrazyitProtocol.SPLIT_SIGN
                            +context.split(":")[1]+CrazyitProtocol.PRIVATE_ROUND);
                }else{
                    ps.println(CrazyitProtocol.MSG_ROUND+context+CrazyitProtocol.MSG_ROUND);
                }
            }
        //捕获到异常  关闭网络资源并退出
        }catch (IOException ex){
            System.out.println("网络异常，请重新登陆");
            closeRs();
            System.exit(1);
        }
    }

    //关闭资源
    private void closeRs() {
        try {
            if(ketIn!=null){
                ps.close();
            }
            if(brServer!=null){
                ps.close();
            }
            if(ps!=null){
                ps.close();
            }
            if(socket!=null){
                ketIn.close();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }


    public static  void main(String[]args){
       Client client = new Client();
       client.init();
       client.readAndSend();

    }
}
