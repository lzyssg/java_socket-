package ii;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArrayList;

public class ChatServerThread extends Thread {
    //服务器与客户端交互功能实现的线程
    private Socket socket;
    private CopyOnWriteArrayList<Socket> list = ChatServer.list;

    ChatServerThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {//3、发送消息给其他客户端
        PrintStream ps = null;
        BufferedReader br = null;
        while (true) {
            try {
                br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String line = br.readLine();
                for (Socket socket1 : list) {//将服务器接收到的消息转发给其他人
                    if (socket1 == socket) {
                        continue;
                    }
                    ps = new PrintStream(socket.getOutputStream());
                    ps.println(line);
                }
            } catch ( IOException e) {
                e.printStackTrace();
            }
        }
    }
}
