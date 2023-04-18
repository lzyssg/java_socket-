package shiyan7;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;

public class test {
public test(){
        JFrame jf=new JFrame("屏幕监控");
        jf.setSize(800,800);
        jf.setVisible(true);
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Toolkit tk=Toolkit.getDefaultToolkit();
        Dimension dm=tk.getScreenSize();
        JLabel imagelabel=new JLabel();
        jf.add(imagelabel);

        //robot

        try {
                Robot robot = new Robot();
                while (true) {
                        Rectangle rec = new Rectangle(0,0,(int)dm.getWidth(),(int)dm.getHeight());
                        BufferedImage buflmg = robot.createScreenCapture(rec);
                        imagelabel.setIcon(new ImageIcon(buflmg));
                        //Thread.sleep(100);
                }
        } catch (AWTException e) {
                e.printStackTrace();
        }


}
public static void main(String[] args){
    new test();
}
        }