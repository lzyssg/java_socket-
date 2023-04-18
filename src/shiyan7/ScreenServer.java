package shiyan7;

import com.sun.image.codec.jpeg.JPEGCodec;

import com.sun.image.codec.jpeg.JPEGImageEncoder;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
public class ScreenServer extends Thread{
    private Dimension screenSize;
    private Rectangle rectangle;
    private Robot robot;
    private JPEGImageEncoder encoder;
    String IP;

    public ScreenServer(String ip) {
        screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        rectangle = new Rectangle(screenSize);
         IP=ip;
        try {
            robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }
    public void run(){
        ZipOutputStream os = null;
        Socket socket=null;
        while (true){
            try{
                socket = new Socket(IP,5000);// 连接远程IP
                BufferedImage image = robot.createScreenCapture(rectangle);// 捕获制定屏幕矩形区域
                os = new ZipOutputStream(socket.getOutputStream());//加入压缩流
                os.setLevel(9);//压缩级别
                os.putNextEntry(new ZipEntry("1.jpg"));
                JPEGCodec.createJPEGEncoder(os).encode(image);//写入处理后的jpg图片的输出流
                os.close();
                Thread.sleep(1000);//每秒20帧
            }catch(Exception e){
                e.printStackTrace();
            }finally{
                if(os!=null){
                    try{
                        os.close();
                    }catch(Exception ioe){}
                }
                if(socket!=null){
                    try {
                        socket.close();
                    } catch (IOException e) {
                    }
                }
            }
        }
    }
//public static void main(String[] args){
    //    new ScreenServer().start();
//}

}
