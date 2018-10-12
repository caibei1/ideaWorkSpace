import java.io.BufferedReader;
import java.io.IOException;


public class ClientThread implements Runnable {

    BufferedReader br = null;

    public ClientThread(BufferedReader br) {
        this.br=br;
    }

    @Override
    public void run() {
        try {
            String context = null;
            while((context=br.readLine())!=null){
                System.out.println(context);
            }

        }catch (IOException e){
            e.printStackTrace();
        }
        //使用finally块来关闭该线程对应的输入流
        finally {
            try {
                if(br!=null){
                    br.close();
                }
            }catch (IOException ex){
                ex.printStackTrace();
            }
        }

    }
}
