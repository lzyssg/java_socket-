package voice;

import java.util.Timer;
import java.util.TimerTask;

public class TimeOutTask extends TimerTask {
    private Thread t;
    private Timer timer;

    TimeOutTask(Thread t, Timer timer){
        this.t = t;
        this.timer = timer;
    }

    // 用于结束工作线程
    public void run() {
        if (t != null && t.isAlive()) {
            t.interrupt();
            timer.cancel();
        }
    }
}