package ii;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.CopyOnWriteArrayList;
public class ChatUser {//客户端做到读写分离
    private static CopyOnWriteArrayList<Socket> list = ChatServer.list;
    private Scanner scan = new Scanner(System.in);
    public static void main(String[] args) throws IOException {
        Scanner scan = new Scanner(System.in);
        Socket socket = new Socket("localhost",8888);
        System.out.println("已连接服务器");
        new Thread(()->{//接收信息线程
            while (true){
                try {
                    BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String text = br.readLine();
                    System.out.println(text);
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }).start();
        new Thread(()->{//发送信息线程
            while (true){
                PrintStream ps = null;
                try {
                    ps = new PrintStream(socket.getOutputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
                ps.println(scan.nextLine());
            }
        }).start();
    }
}
