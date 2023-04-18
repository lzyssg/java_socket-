package ii;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArrayList;

public class ChatServer {
    public static CopyOnWriteArrayList<Socket> list = new CopyOnWriteArrayList<>();
    //接收客户端的主线程
    public static void main(String[] args) {
        try {
            ServerSocket server = new ServerSocket(8888);
            System.out.println("服务器已启动");
            while (true) {
                //获取连接对象、每获取一个连接对象，就进行添加
                Socket socket = server.accept();
                System.out.println("有一个客户端进行了连接");
                list.add(socket);
                new ChatServerThread(socket).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
