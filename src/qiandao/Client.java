package qiandao;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import static java.lang.System.out;
import java.net.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Client {

    private JFrame clientFrame;
    private JFrame sign;
    private JLabel IPLabel;
    private JLabel PortLabel;
    private JLabel sayLabel;
    private JLabel nameLabel;
    private JTextField IPText;
    private JTextField PortText;
    private JTextField sayText;
    private JTextField nameText;
    private JButton connectButton;
    private JButton sayButton;
    private JButton signButton;
    
    private JPanel jPanelNorth;
    private JPanel jPanelSouth0;
    private JPanel jPanelSouth1;
    private JPanel jPanelSouth2;
    private JPanel jPanelSouth_sign;
    private JTextArea clientTextArea;
    private JScrollPane scroller;
    private BufferedReader reader;
    private PrintWriter writer;
    private String name;
    private ArrayList<PrintWriter> clientOutputStreams;
    private JLabel signnumLabel; //签到窗口
    private JLabel signnameLabel; //签到标签
    private JTextField signnameText; //签到编辑框
    private JTextField signnumText;  //签到学号框
    private JButton signB; //签到按钮
    private JPanel jPanelSign;
    private JPanel jPanelSign1;
    private JPanel jPanelSign2;
    public int port = new java.util.Random().nextInt(300) * new java.util.Random().nextInt(10); //随机的签到端口号
    private boolean connectionflag = false;
    private PrintWriter fileout = null;

    public static void main(String args[]) throws UnknownHostException {
        System.out.println(Inet4Address.getLocalHost().getHostAddress());
        Client aClient = new Client();
        aClient.startUp();
    }

    // 初始化组件 
    public Client() {
        clientFrame = new JFrame();
        jPanelNorth = new JPanel();
        jPanelSouth_sign = new JPanel(); //文件传输Panel
        jPanelSign = new JPanel();
        jPanelSign1 = new JPanel();
        jPanelSign2 = new JPanel();
        IPLabel = new JLabel("服务器IP", JLabel.LEFT);
        IPText = new JTextField(8);
        PortLabel = new JLabel("服务器端口", JLabel.LEFT);
        PortText = new JTextField(8);
        nameLabel = new JLabel("姓名", JLabel.LEFT);
        nameText = new JTextField(5);
        //PortLabel = new JLabel("本地服务器端口：", JLabel.LEFT); 
        connectButton = new JButton("连接");
        clientTextArea = new JTextArea();
        scroller = new JScrollPane(clientTextArea);
        jPanelSouth0 = new JPanel();
        jPanelSouth1 = new JPanel();
        jPanelSouth2 = new JPanel();
        sayLabel = new JLabel("消息", JLabel.LEFT);
        sayText = new JTextField(30);
        sayButton = new JButton("确认");
        signButton = new JButton("签到");


        signButton.setPreferredSize(new Dimension(120, 30));

        sign = new JFrame();
        signnameLabel = new JLabel("姓名："); //签到标签
        signnameText = new JTextField(15); //签到编辑框
        signnumLabel = new JLabel("学号："); //签到标签
        signnumText = new JTextField(15); //签到编辑框
        signB = new JButton("签到"); //签到按钮  

    }

    public class Mytcp {

        private BufferedReader reader;
        private ServerSocket server;
        private Socket socket;

        void getServer(int port) {
            try {
                server = new ServerSocket(port);        //签到Socket
                while (true) {
                    socket = server.accept();           //accept()方法会返回一个和客户端Socket对象相连的Socket对象
                    System.out.println(socket.getPort());
                    reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
                    getClientMessage();
                }
            } catch (Exception e) {
                e.printStackTrace();

            }

        }

        //读取客户端发送过来的信息
        private void getClientMessage() {
            try {
                while (true) {
                    clientTextArea.append(reader.readLine() + "\n");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (reader != null) {
                    reader.close();
                }
                if (socket != null) {
                    socket.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // 构建GUI 
    private void buildGUI() {
        // 窗口的设置 
        clientFrame.setTitle("客户端");
        clientFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        clientFrame.setSize(550, 550);
        clientFrame.setResizable(false); //主窗口不可调整

        // 北区的组件 
        jPanelNorth.add(IPLabel);
        jPanelNorth.add(IPText);
        jPanelNorth.add(PortLabel);
        jPanelNorth.add(PortText);
        jPanelNorth.add(nameLabel);
        jPanelNorth.add(nameText);
        jPanelNorth.add(connectButton);
        clientFrame.getContentPane().add(BorderLayout.NORTH, jPanelNorth);

        // 中间的组件 
        clientTextArea.setFocusable(false);
        scroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        clientFrame.getContentPane().add(BorderLayout.CENTER, scroller);

        // 南区的组件 
        jPanelSouth2.add(sayLabel);
        jPanelSouth2.add(sayText);
        jPanelSouth2.add(sayButton);

        jPanelSouth_sign.add(signButton);


        jPanelSouth1.add(jPanelSouth_sign);
        jPanelSouth0.setLayout(new BoxLayout(jPanelSouth0, BoxLayout.Y_AXIS));
        jPanelSouth0.add(jPanelSouth1);
        jPanelSouth0.add(jPanelSouth2);
        clientFrame.getContentPane().add(BorderLayout.SOUTH, jPanelSouth0);

        // 设置窗口可见 
        clientFrame.setVisible(true);
        clientFrame.setLocationRelativeTo(null); //置窗口于屏幕中间
    }

    private void signGUI() {
        sign.setTitle("签到");
        //sign.setDefaultCloseOperation(); 
        sign.setSize(260, 140);
        jPanelSign.add(signnumLabel);
        jPanelSign.add(signnumText);
        jPanelSign1.add(signnameLabel);
        jPanelSign1.add(signnameText);
        jPanelSign2.add(signB);
        sign.getContentPane().add(BorderLayout.NORTH, jPanelSign);
        sign.getContentPane().add(BorderLayout.CENTER, jPanelSign1);
        sign.getContentPane().add(BorderLayout.SOUTH, jPanelSign2); //排版
        //sign.setVisible(true); 
        sign.setLocationRelativeTo(clientFrame);
        sign.setResizable(false);
    }

    // 客户端运行 
    public void startUp() {
        buildGUI();
        signGUI();
        Filter filter = new Filter();
        // 接收服务器消息的线程 
        Runnable incomingReader = new Runnable() {
            @Override
            public void run() {
                String message;
                try {
                    while ((message = reader.readLine()) != null) {
                        clientTextArea.append(filter.Filter(message) + "\n");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };

        // 监听Connect按钮，实现服务器的连接 
        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String aServerIP = IPText.getText();
                String aServerPort = PortText.getText();
                name = nameText.getText();
                if (nameText.getText().equals("")) {
                    name = "默认用户";
                }
                if (aServerIP.equals("") || aServerPort.equals("")) {
                    JOptionPane.showMessageDialog(clientFrame, "请输入完整的 IP和端口！");
                } else {
                    try {
                        @SuppressWarnings("resource")
                        Socket clientSocket = new Socket(aServerIP, Integer.parseInt(aServerPort));

                        reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), "UTF-8"));
                        writer = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream(), "UTF-8"), true);
                        clientTextArea.append("服务器已连接...\n");
                        connectionflag = true;
                        connectButton.setEnabled(false);
                        Thread readerThread = new Thread(incomingReader);
                        readerThread.start();

                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(clientFrame, "连接不上服务器!\n请确认 IP 和 端口 输入正确。");
                    }

                }
            }
        });

        // 发送消息到服务器
        ActionListener SayListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String aText = sayText.getText();
                if (aText.equals("")) {
                    JOptionPane.showMessageDialog(clientFrame, "内容不能为空！");
                } else {
                    try {
                        writer.println(name + "：" + aText);
                        writer.flush();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    sayText.setText("");
                }
            }
        };

        ActionListener signListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (signnameText.getText().equals("") | signnumText.getText().equals("")) { //检查签到信息是否完整
                    JOptionPane.showMessageDialog(clientFrame, "签到信息必须填写完整。");
                } else {
                    Thread t = new Thread(new Runnable() {
                        public void run() {
                            // run方法具体重写
                            Mytcp s = new Mytcp();
                            s.getServer(port);
                        }
                    });
                    t.start();
                    port = port++ + new java.util.Random().nextInt(10);
                    //端口号改变
                    GetLocalIP ipConent = new GetLocalIP();
                    String ip = ipConent.GetLocalIP(); //获取本机IP
                    writer.println("**!!**sign**!!**" + "|@" + ip + "#$" + signnameText.getText() + "%^" + signnumText.getText() + "&]" + port + "~"); //递出消息 名字+学号+端口号
                    writer.flush();
                    signnameText.setText("");
                    signnumText.setText("");
                    sign.setVisible(false);
                }
            }
        };

        ActionListener sListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //JOptionPane.showMessageDialog(clientFrame, "签到！");
                if (!connectionflag) {
                    JOptionPane.showMessageDialog(clientFrame, "签到失败，未连接服务器！");
                } else {

                    sign.setVisible(true);
                }
            }
        };



        sayButton.addActionListener(SayListener);
        sayText.addActionListener(SayListener);
        signButton.addActionListener(sListener);
        signB.addActionListener(signListener);
    }}

