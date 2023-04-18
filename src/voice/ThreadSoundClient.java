package voice;

import javax.sound.sampled.*;
import java.io.IOException;
import java.net.*;

public class ThreadSoundClient extends Thread{
    boolean voiceend=false;
    private String IP;
    //boolean socketend;
    DatagramPacket sendPacket;
  //  public void finish(boolean socketend){
    //    //this.voiceend=voiceend;
    //    this.socketend=socketend;
   // }
    public void finish(boolean voiceend){
        this.voiceend=voiceend;
    }
    public String getip(String ip){
       return IP=ip;
    }
   // public boolean finishvalue(){
   //     return socketend;
    //}
    @Override

    public void run(){
            AudioFormat.Encoding encoding = AudioFormat.Encoding.PCM_SIGNED;
            float rate = 44100.0f;
            int sampleSize = 16;
            int channels = 2;
            int frameSize = 4;
            boolean bigEndian = true;

            AudioFormat format = new AudioFormat(encoding, rate, sampleSize, channels, (sampleSize / 8)
                    * channels, rate, bigEndian);

            TargetDataLine line;
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
            if (!AudioSystem.isLineSupported(info)) {
                System.out.println("Not Supported");
                System.exit(1);
            }

            DatagramSocket socket = null;
            try {
                socket = new DatagramSocket(8081);
           //     if(socketend){

              //      socket.close();
               // }
            } catch (SocketException e) {
                e.printStackTrace();
            }

        //InetAddress IPAddress = InetAddress.getLocalHost();
            InetAddress IPAddress = null;
            try {
                IPAddress = InetAddress.getByName("192.168.253.128");
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }

            try {
                line = (TargetDataLine) AudioSystem.getLine(info);
                line.open(format);
                if(voiceend){
                    line.close();
                }
                //ByteArrayOutputStream out = new ByteArrayOutputStream();
                int numBytesRead;
                byte[] data = new byte[line.getBufferSize() / 5];
                int totalBytesRead = 0;
                line.start();

                while (!voiceend) {
                    numBytesRead = line.read(data, 0, data.length);
                     sendPacket = new DatagramPacket(data, data.length, IPAddress, 8080);
                    // totalBytesRead += numBytesRead;
                    socket.send(sendPacket);
                    //out.write(data, 0, numBytesRead);
                    // System.out.println(interrupted());
                }


            } catch (LineUnavailableException | IOException e) {
                e.printStackTrace();
            }

        }

}
