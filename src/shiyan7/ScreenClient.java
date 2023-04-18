package shiyan7;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.zip.ZipInputStream;
public class ScreenClient {
    Dimension screenSize;

    public ScreenClient() {
        JFrame jf=new JFrame("屏幕监控");
        JTextField a=new JTextField();
        jf.setSize(800,800);
        jf.setVisible(true);
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Toolkit tk=Toolkit.getDefaultToolkit();
        Dimension dm=tk.getScreenSize();
        JLabel imagelabel=new JLabel();
        jf.add(imagelabel);
        new Thread(){
            Image image;
            public void run(){
                try{
                    ServerSocket ss=new ServerSocket(5000);
                    while(true){
                        Socket s=null;
                        s=ss.accept();
                        ZipInputStream zis=new ZipInputStream(s.getInputStream());//ZIP流
                        zis.getNextEntry();//获得ZIP下一个条目 代表一个压缩文件或目录
                        image=ImageIO.read(zis);//
                        imagelabel.setIcon(new ImageIcon(image));
                        sleep(100);
                    }
                }catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }
    public static void main(String[] args){
        new ScreenClient();
    }
}
