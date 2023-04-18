package gui;


import FileUpload.TCPServer;
import shiyan7.ScreenClient;
import shiyan7.ScreenServer;
import voice.ThreadSoundServer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.List;

public class server extends JFrame {
    private static final int DEFAULT_PORT = 8899;
    //显示监听状态
    private JLabel stateLB;
    private JLabel portlable;
    //显示聊天记录
    public JTextArea centerTestArea;
    //底层面板
    private JPanel southPanel;
    //聊天输入框
    private JTextArea inputTextArea;
    private JPanel bottomPanel;
    //端口号输入框
    private JTextField portTextFiled;
    //发送按钮
    private JButton sendBT;
    private JButton startport;
    private JButton sound;
    private JButton soundend;
    private JButton serverend;
    private JButton screen;
    ServerSocket server;
    public static List<Socket> sockets=new Vector<>();
    boolean isstart=false;
    private ArrayList<ClientConn> ccList = new ArrayList<>();
    private Socket socket;
    private PrintWriter pw1=null;
    private ServerSocket ss = null;

    private boolean isStart = false;
    private boolean kaiguan=false;
    private Socket s=null;
    private DataOutputStream dos = null;
    private JButton receivefile;

    class ClientConn implements Runnable{
        Socket s = null;
        public ClientConn(Socket s) {
            this.s = s;
            (new Thread(this)).start();
        }

        //同时接收多个客户端的信息 -- 多线程接收数据
        @Override
        public void run() {
            try {
                DataInputStream dis = new DataInputStream(s.getInputStream());
              //  DataOutputStream out=new DataOutputStream(s.getOutputStream());
                ObjectInputStream bis = new ObjectInputStream(s.getInputStream());
                    while (isStart) {
                        String str = dis.readUTF();
                        System.out.println(s.getInetAddress() + "/" + s.getPort() +"说: " + str);
                        centerTestArea.append(s.getInetAddress() + "/" + s.getPort() +"说: " + str +"\n");
                        bis = new ObjectInputStream(s.getInputStream());
                        byte [] info = new byte [ 256 ];
                        bis.read(info);
                        String file_name = new String(info).trim();
                        BufferedOutputStream bos = new BufferedOutputStream( new FileOutputStream( "e:/" +file_name));

                        byte [] buf = new byte [ 1024 ];
                        int len = 0 ;
                        while ((len = bis.read(buf)) != - 1 ) {
                            bos.write(buf, 0 , len);
                        }

                }
            } catch (SocketException e) {
                System.out.println("一个客户端下线了：" + s.getInetAddress() + "/" + s.getPort());
                centerTestArea.append("一个客户端下线了：" + s.getInetAddress() + "/" + s.getPort() + "\n");
            }
            catch (IOException e) {
                System.out.println("服务器中断了!");
                e.printStackTrace();
            }
        }
        public void send(String str) {
            try {
                DataOutputStream dos = new DataOutputStream(this.s.getOutputStream());
                dos.writeUTF(str);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }



    public server() {
        setTitle("聊天室");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(650, 550);
        setLocationRelativeTo(null);

        //窗口上
        stateLB = new JLabel("尼轩聊天室", SwingConstants.CENTER);
        portlable = new JLabel("PORT");

        //聊天记录
        centerTestArea = new JTextArea();
        centerTestArea.setEditable(false);
        centerTestArea.setBackground(new Color(211, 211, 211));

        //底部
        southPanel = new JPanel(new BorderLayout());
        inputTextArea = new JTextArea(10, 10);
        bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        portTextFiled = new JTextField(8);
        sendBT = new JButton("发送消息");
        startport = new JButton("开启");
        sound = new JButton("语音聊天");
        serverend=new JButton("关闭");
        soundend=new JButton("关闭语音");
        screen=new JButton("屏幕监控");
        receivefile=new JButton("接受文件");

        //添加组件
        bottomPanel.add(portlable);
        bottomPanel.add(portTextFiled);
        bottomPanel.add(sendBT);
        bottomPanel.add(startport);
        bottomPanel.add(sound);
        bottomPanel.add(serverend);
        bottomPanel.add(soundend);
        bottomPanel.add(screen);
        bottomPanel.add(receivefile);
        southPanel.add(new JScrollPane(inputTextArea), BorderLayout.NORTH);//给输入框添加滚动条
        southPanel.add(bottomPanel, BorderLayout.CENTER);


        //jframe加入各个面板调位
        add(stateLB, BorderLayout.NORTH);
        add(new JScrollPane(centerTestArea), BorderLayout.CENTER);
        add(southPanel, BorderLayout.SOUTH);
        setVisible(true);

        try {
            portTextFiled.setText(String.valueOf(6666));//默认端口文本框填充为6666
        } catch (Exception e) {
            e.printStackTrace();
        }


        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                isStart = false;
                try {
                    if (ss != null) {
                        ss.close();
                    }
                    System.out.println("服务器停止!\n");
                    centerTestArea.append("服务器断开!\n");
                    System.exit(0);
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }

        });


        serverend.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    //关闭输入输出流再关闭socket连接
                    s.shutdownOutput();
                    s.shutdownInput();
                    ss.close();
                    isstart=false;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        startport.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Thread() {
                    public void run() {
                        try {
                            try {
                                ss = new ServerSocket(Integer.parseInt(portTextFiled.getText()));
                                isStart = true;
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                            //可以接受多个客户端的连接
                            while (isStart) {
                                s = ss.accept();
                                ccList.add(new ClientConn(s));
                                System.out.println("一个客户端连接服务区：" + s.getInetAddress() + "/" + s.getPort());
                                centerTestArea.append("一个客户端连接服务区：" + s.getInetAddress() + "/" + s.getPort() + "\n");
                            }
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                        centerTestArea.append("服务器启动成功！\n");
                    }
                }.start();


            }

        });
        sendBT.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String strSend = inputTextArea.getText();
                //遍历ccList，调用send方法
                Iterator<ClientConn> it = ccList.iterator();
                while (it.hasNext()) {
                    ClientConn o = it.next();
                    o.send(strSend + "\n");
                    centerTestArea.append("服务端说：" + strSend + "\n");
                    inputTextArea.setText("");
                }
            }
        });
        ThreadSoundServer t2=new ThreadSoundServer();
        sound.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                t2.start();

            }
        });

        soundend.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                t2.finish(true);

            }
        });
      /*  screen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                new ScreenClient();

            }
        });*/
        screen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                Thread t=new ScreenServer("10.200.149.152");
                t.start();
            }});
        receivefile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                new Thread(){
                    public void run(){
                        try {
                            new TCPServer();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
        });
    }

    public static void main(String[] args) {
            server server = new server();
    }
}
