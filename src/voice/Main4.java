package voice;

public class Main4 extends Thread{

        private int i = 0;

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                //业务代码
                i++;
                System.out.println(i);
             //   new UdpSoundClient();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    System.out.println("线程在sleep期间被打断了");
                    e.printStackTrace();
                    //再次打断，设置中断标志，则之后的isInterrupted为true
                    this.interrupt();
                }
            }
        }

}