package voice;

import javax.sound.sampled.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UdpSoundClient
{
    boolean voiceend=true;
    public void finish(boolean s){
        this.voiceend=s;
    }
    private void init() throws Exception{
        AudioFormat.Encoding encoding = AudioFormat.Encoding.PCM_SIGNED;
        float rate = 44100.0f;
        int sampleSize = 16;
        int channels = 2;
        int frameSize = 4;
        boolean bigEndian = true;

        AudioFormat format = new AudioFormat(encoding, rate, sampleSize, channels, (sampleSize / 8)
                * channels, rate, bigEndian);//做一个音频数据格式

        TargetDataLine line;//声音输入线
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);//构建数据行的信息对象，其中包含单个音频格式
        if(!AudioSystem.isLineSupported(info)){//如果没打开麦克风，则匹配不到数据行 没设备支持的意思
            System.out.println("Not Supported");
            System.exit(1);
        }
        //udp连接
        DatagramSocket socket = new DatagramSocket(8081);
        //InetAddress IPAddress = InetAddress.getLocalHost();
        InetAddress IPAddress = InetAddress.getByName("127.0.0.1");

        try
        {
            line = (TargetDataLine) AudioSystem.getLine(info);//获取声音输入数据行
            line.open(format);//打开声音数据行，让它的资源可让系统运行

            //ByteArrayOutputStream out = new ByteArrayOutputStream();
            int numBytesRead;
            byte[] data = new byte [line.getBufferSize() / 5];
            int totalBytesRead = 0;

            line.start();//音频开始捕捉

            while(true){
                DatagramPacket sendPacket = new DatagramPacket(data, data.length, IPAddress, 8080);
                // totalBytesRead += numBytesRead;
                socket.send(sendPacket);
                //out.write(data, 0, numBytesRead);
                // System.out.println("Debug");
            }

        }
        catch(LineUnavailableException e)
        {
            e.printStackTrace();
        }
    }

    public UdpSoundClient() {
        try {
            if(voiceend){
            init();
                    }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void main(String[]args)
    {
        new UdpSoundClient();
}
    }