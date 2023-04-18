package voice;

import javax.sound.sampled.*;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class ThreadSoundServer extends Thread{
    boolean socketend=false;
    public void finish(boolean socketend){
        this.socketend=socketend;
    }


    public void run() {

            AudioFormat.Encoding encoding = AudioFormat.Encoding.PCM_SIGNED;
            float rate = 44100.0f;
            int sampleSize = 16;
            int channels = 2;
            int frameSize = 4;
            boolean bigEndian = true;

            AudioFormat format = new AudioFormat(encoding, rate, sampleSize, channels, (sampleSize / 8)
                    * channels, rate, bigEndian);

            SourceDataLine speakers = null;
            DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, format);

            try {
                speakers = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
            } catch (LineUnavailableException e) {
                e.printStackTrace();
            }
            try {
                speakers.open(format);
            } catch (LineUnavailableException e) {
                e.printStackTrace();
            }

            DatagramSocket socket = null;

            try {
                socket = new DatagramSocket(8080);
            } catch (SocketException e) {
                e.printStackTrace();
            }
            byte[] data = new byte[speakers.getBufferSize() / 5];
            speakers.start();
            while (!socketend) {
                DatagramPacket receivePacket = new DatagramPacket(data, data.length);
                try {

                    socket.receive(receivePacket);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                speakers.write(data, 0, data.length);
            }


        }

}
