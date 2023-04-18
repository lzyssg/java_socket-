package FileUpload;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TCPServer
{

    public TCPServer() throws IOException
    {
        init();
    }

    private void init() throws IOException
    {
        final int PORT = 8800;
        final String receive_file_path = "receiveFile";

        ServerSocket server = new ServerSocket(PORT);
        while (true)
        {
            Socket socket = server.accept();

            System.out.println("来访问客户端信息:" + "客户端IP：" + socket.getInetAddress()
                    + " 客户端端口:" + socket.getInetAddress().getLocalHost() + "已连接服务器");
//            BufferedReader bReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            //读取客户端发送来的消息
//            String receive_file_name = bReader.readLine();
//            System.out.println("客户端发来的消息：" + msg);
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            String receive_file_name = dis.readUTF();

            new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    try
                    {
                        InputStream is = socket.getInputStream();
                        File file = new File(receive_file_path);
                        if (!file.exists())
                        {
                            file.mkdir();
                        }
                        String receive_file_path = file + File.separator + receive_file_name;

                        System.out.println("receive_file_name:" + receive_file_name);
                        System.out.println("receive_file_path:" + receive_file_path);

                        FileOutputStream fos = new FileOutputStream(receive_file_path);
                        int len = 0;
                        byte[] bytes = new byte[1024];
                        while ((len = is.read(bytes)) != -1)
                        {
                            fos.write(bytes, 0, len);
                        }
                        socket.getOutputStream().write("上传成功".getBytes());
                        fos.close();
                        socket.close();
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
//        server.close();
    }

    public static void main(String[] args) throws IOException
    {
        TCPServer server = new TCPServer();
    }
}
