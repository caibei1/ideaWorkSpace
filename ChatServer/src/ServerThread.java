import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.LinkedList;

public class ServerThread implements Runnable {

    private Socket s;
    BufferedReader br = null;
    PrintStream ps = null;
    public ServerThread(Socket s) throws IOException{
        this.s=s;
        br = new BufferedReader(new InputStreamReader(s.getInputStream()));
    }

    @Override
    public void run() {
        try{
            br = new BufferedReader(new InputStreamReader(s.getInputStream()));
            ps = new PrintStream(s.getOutputStream());
            String context = null;
            while ((context=br.readLine())!=null){
                //如果读到的行是以USER_ROUND开始，并以其结束，则可以确定读到的是用户登陆的用户名
                if(context.startsWith(CrazyitProtocol.USER_ROUND)&&context.endsWith(CrazyitProtocol.USER_ROUND)){
                    //得到真实消息
                    String userName = getRealMsg(context);
                    //如果用户名重复
                    if(Server.clients.map.containsKey(userName)){
                        System.out.println("用户名重复");
                        ps.println(CrazyitProtocol.NAME_REP);
                    }else{
                        System.out.println("登陆成功");
                        ps.println(CrazyitProtocol.LOGIN_SUCCESS);
                        Server.clients.put(userName,ps);
                    }
                }
                //私聊
                else if(context.startsWith(CrazyitProtocol.PRIVATE_ROUND)&&context.endsWith(CrazyitProtocol.PRIVATE_ROUND)){
                    //得到真实消息
                    String userAndMsg = getRealMsg(context);
                    String user = userAndMsg.split(CrazyitProtocol.SPLIT_SIGN)[0];
                    String msg = userAndMsg.split(CrazyitProtocol.SPLIT_SIGN)[1];
                    //获取私聊用户对应的输出流，并发送消息
                    Server.clients.map.get(user).println(Server.clients.getKeyByValue(ps)+"悄悄对你说："+msg);
                }
                //群聊
                else{
                    //得到真实消息
                    String msg = getRealMsg(context);
                    for (PrintStream ps1:Server.clients.valueSet()) {
                        ps1.println(Server.clients.getKeyByValue(ps)+"说:"+msg);
                    }
                }
            }
        //捕获异常，说明该socket对应的客户端已经出现问题，所有从map删除
        }catch (IOException e){
            Server.clients.removeByValue(ps);
            System.out.println(Server.clients.map.size());
            //关闭网络IO资源
            try{
                if(br!=null){
                    br.close();
                }
                if (ps!=null){
                    ps.close();
                }
                if (s!=null){
                    s.close();
                }
            }catch (IOException e1){
                e1.printStackTrace();
            }
        }
    }


    //将读取到的内容去掉前后的协议字符，恢复成真实数据
    private String getRealMsg(String context) {
        return context.substring(CrazyitProtocol.PROTOCOL_LEN,context.length()-CrazyitProtocol.PROTOCOL_LEN);
    }


}
