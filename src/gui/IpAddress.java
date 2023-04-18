package gui;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

public class IpAddress {

    public static void main(String[] args) {
        String ip = IpAddress.getIpAddress();
        System.out.println("ip地址 : " + ip);

    }

    public static String getIpAddress() {
        String cmd = String.format("ipconfig");
        String result = "";
        try {
            Process process = Runtime.getRuntime().exec(cmd);
            InputStreamReader ioReader = new InputStreamReader(process.getInputStream(),"GBK");
            LineNumberReader input = new LineNumberReader(ioReader);
            String temp = input.readLine();
            String pattern = new String("^\\s.*IPv4.*:.*\\d");
            while(temp != null) {
                if (temp.matches(pattern)) {
                    result = temp.substring(temp.indexOf(":") + 2);
                } else{
                }
                temp = input.readLine();
            }

        } catch (IOException ioe) {
            System.out.println(ioe);
            return null;
        }
        return result;
    }
}