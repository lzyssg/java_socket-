package qiandao;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

public class GetLocalIP {

    public String GetLocalIP() {
        Process process;
        try {
            process = Runtime.getRuntime().exec("cmd.exe /c \"@Echo off&for /f \"tokens=4\" %a in ('route print^|findstr 0.0.0.0.*0.0.0.0') do (echo.%a)\""); //DOS命令取IP
            InputStreamReader r = new InputStreamReader(process.getInputStream(), "UTF-8");
            LineNumberReader returnData = new LineNumberReader(r);
            String returnMsg = "";
            String line = "";
            while ((line = returnData.readLine()) != null) {
                System.out.println(line);
                return line; //取第一行获得的IP值
//System.out.println(returnData.getLineNumber()+" "+line);
//returnMsg += line;
            }

        } catch (IOException e) {
// TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }
}
