package gui;

import shiyan7.ScreenClient;
import shiyan7.ScreenServer;
import voice.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import javax.swing.*;
public class client extends JFrame {
    //显示监听状态
    private JLabel stateLB;
    private JLabel iplable;
    private JLabel portlable;
    private JLabel udpiplable;
    private JLabel udpportlable;
    //显示聊天记录
    private JTextArea centerTestArea;
    //底层面板
    private JPanel southPanel;
    //聊天输入框
    private JTextArea inputTextArea;
    //放置IP地址输入框,按钮
    private JPanel bottomPanel;
    private JPanel bottompanel2;
    //IP输入框
    private JTextField ipTextFiled;
    //端口号输入框
    private JTextField portTextFiled;
    //发送按钮
    private JTextField UDPiptextfiled;
    private JTextField UDPporttextfiled;
    private JButton sendBT;
    //文件发送按钮
    private JButton sendFile;
    private JButton selectFile;
    private JButton qiandao;
    private JButton sound;
    private JButton soundend;
    private JButton soundkai;
    private JButton screen;
    private BufferedReader br;
    private Socket s = null;
    File file=null;
    private String IP;
    private int PORT;
    private String send_file_name;
    private String send_file_path;
    private DataOutputStream dos = null;
    private boolean isConn = false;
    private boolean kai = true;

    public void send(String str) {
        try {
            dos = new DataOutputStream(s.getOutputStream());
            dos.writeUTF(str);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public  client() throws HeadlessException {
        super();
    }
    
    public void init() {
        setTitle("聊天室");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(650, 550);
        setLocationRelativeTo(null);

        //窗口上
        stateLB = new JLabel("尼轩聊天室", SwingConstants.CENTER);
        iplable = new JLabel("ip");
        portlable = new JLabel("PORT");



        //聊天记录
        centerTestArea = new JTextArea();
        centerTestArea.setEditable(false);
        centerTestArea.setBackground(new Color(211, 211, 211));

        //底部
        southPanel = new JPanel(new BorderLayout());
        inputTextArea = new JTextArea(10, 20);
        bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        bottompanel2=new JPanel(new FlowLayout(FlowLayout.CENTER,2,2));

        portTextFiled = new JTextField(8);
        ipTextFiled = new JTextField(8);
        sendBT = new JButton("发送消息");
        selectFile = new JButton("选择文件");
        sendFile = new JButton("发文件");
        qiandao = new JButton("签到");
        JButton connect = new JButton("连接");
        sound = new JButton("语音聊天");
        soundend = new JButton("中断语音聊天");
        screen = new JButton("屏幕监控");

        //添加组件
        bottomPanel.add(iplable);
        bottomPanel.add(ipTextFiled);
        bottomPanel.add(portlable);
        bottomPanel.add(portTextFiled);
        bottomPanel.add(connect);
        bottomPanel.add(sendBT);
        bottomPanel.add(selectFile);
        //bottomPanel.add(sendFile);
        bottomPanel.add(qiandao);
        bottomPanel.add(sound);
        bottomPanel.add(soundend);
        bottomPanel.add(screen);
        southPanel.add(new JScrollPane(inputTextArea),BorderLayout.CENTER);//给输入框添加滚动条
        southPanel.add(bottomPanel, BorderLayout.SOUTH);
        //bottomPanel.add(bottompanel2,BorderLayout.SOUTH);

        //jframe加入各个面板调位
        add(stateLB, BorderLayout.NORTH);
        add(new JScrollPane(centerTestArea));
        add(southPanel, BorderLayout.SOUTH);
        setVisible(true);
        try {
            InetAddress address = InetAddress.getLocalHost();
            ipTextFiled.setText(address.getHostAddress());
            portTextFiled.setText(String.valueOf(6666));
        } catch (Exception e) {
            e.printStackTrace();
        }

        //监听，实现按Send按钮发送信息的功能
        sendBT.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String strSend = inputTextArea.getText();
                if (strSend.trim().length() == 0) {
                    return;
                }
                send(strSend);
                centerTestArea.append(s.getInetAddress() + "/" + s.getPort() + strSend + "\n");
                inputTextArea.setText("");  //清空文本框
            }
        });

        connect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                try {
                    s = new Socket(ipTextFiled.getText(), Integer.parseInt(portTextFiled.getText()));
                    //表示连接上服务器
                    isConn = true;
                    centerTestArea.append("已连接上服务器!\n");
                } catch (SocketException e) {
                    System.out.println("找不到服务器!");
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                new Thread(new Receive()).start();

            }
        });

        qiandao.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String strSend = "我来签到了";
                send(strSend);
                centerTestArea.append("签到成功!" + "\n");
                inputTextArea.setText("");  //清空文本框
            }
        });
        JFileChooser fileDialog = new JFileChooser();
        selectFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                FileInputStream fis = null;
                try {
                    // TODO add your handling code here:
                    int returnVal = fileDialog.showOpenDialog(null);
                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        File file = fileDialog.getSelectedFile();
                        send_file_name = file.getName();
                        send_file_path = file.getAbsolutePath();
                    }
                    fis = new FileInputStream(send_file_path);
                    Socket socket = new Socket(ipTextFiled.getText(), 6000);
                    OutputStream os = socket.getOutputStream();
                    InputStream is = socket.getInputStream();
                    DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                    dos.writeUTF(send_file_name);//发给服务端
                    System.out.println("发送的文件:" + send_file_name);

                    int len = 0;
                    byte[] bytes = new byte[1024];
                    // 把读取到到文件上传到服务器
                    while ((len = fis.read(bytes)) != -1) {
                        os.write(bytes, 0, len);
                    }
                    // 阻塞
                    socket.shutdownOutput();
                    // 回写
                    fis.close();
                    socket.close();

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        ThreadSoundClient t1 = new ThreadSoundClient();

        sound.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                t1.start();
            }
        });
        soundend.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                t1.finish(true);
                // t1.interrupt();


            }
        });
        /*
        screen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                Thread t=new ScreenServer(ipTextFiled.getText());
                t.start();
            }
        });
    }*/
        screen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                new ScreenClient();
            }
        });
    }

    //多线程的类，实现了Runnable接口
    class Receive implements Runnable {

        @Override
        public void run() {
            try {
                while (isConn) {
                    DataInputStream dis = new DataInputStream(s.getInputStream());
                    String str = dis.readUTF();
                    centerTestArea.append("服务端说:"+str);
                }
            } catch (SocketException e) {
                System.out.println("服务器终止");
                centerTestArea.append("服务器终止!\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
                    client client = new client();
                    client.init();

    }
}

