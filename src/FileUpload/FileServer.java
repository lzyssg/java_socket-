package FileUpload;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FileServer {

    /**
     * @param args
     */
    public static void main(String[] args) {

        ExecutorService threadpool = Executors.newFixedThreadPool( 10 );
        try {
            ServerSocket ss = new ServerSocket( 8800 );
            while ( true ) {
                threadpool.execute( new UploadThread(ss.accept()));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class UploadThread implements Runnable {
    private Socket s;
    UploadThread(Socket s) {
        this .s = s;
    }

    @Override
    public void run() {
        System.out.println(s.getInetAddress().getHostAddress());
        try {
            ObjectInputStream bis = new ObjectInputStream(s.getInputStream());
            byte [] info = new byte [ 256 ];
            bis.read(info);
            String file_name = new String(info).trim();
            BufferedOutputStream bos = new BufferedOutputStream( new FileOutputStream( "e:/" +file_name));

            byte [] buf = new byte [ 1024 ];
            int len = 0 ;
            while ((len = bis.read(buf)) != - 1 ) {
                bos.write(buf, 0 , len);
            }
            bos.close();
            bis.close();
            s.close();
            System.out.println(file_name+ "  文件传输成功" );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}