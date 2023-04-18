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

public class Server {

    private JFrame serverFrame;
    private JLabel sayLabel;
    private JLabel portLabel;
    private JTextField portText;
    private JTextField sayText;
    private JButton startButton;
    private JButton sayButton;
    private JButton signButton;
    private JPanel jPanelNorth;
    private JPanel jPanelSouth0;
    private JPanel jPanelSouth1;
    private JPanel jPanelSouth2;
    private JScrollPane scroller;
    private JTextArea serverTextArea;
    private ArrayList<PrintWriter> clientOutputStreams;
    private boolean signboolean = false;
    private boolean connectionboolean = false;

    ////////////////////////////////////
    JMenuBar bar;
    JMenu menu;
    JMenuItem signlog; //签到日志
    JMenuItem filelog; // 文件日志
    JMenuItem copy; // 文件拷贝
    ////////////////////////////////////

    public static void main(String[] args) {
        Server aServer = new Server();
        aServer.startUp();
    }

    // 初始化组件 
    public Server() {
        serverFrame = new JFrame();
        jPanelNorth = new JPanel();
        portLabel = new JLabel("端口号", JLabel.LEFT);
        portText = new JTextField(25);
        startButton = new JButton("开始");
        signButton = new JButton("开始签到活动");
        serverTextArea = new JTextArea();
        scroller = new JScrollPane(serverTextArea);
        jPanelSouth0 = new JPanel();
        jPanelSouth1 = new JPanel();
        jPanelSouth2 = new JPanel();
        sayLabel = new JLabel("消息", JLabel.LEFT);
        sayText = new JTextField(30);
        sayButton = new JButton("确认");

        //////////////////菜单栏
        bar = new JMenuBar();
        menu = new JMenu("日志查询"); //再n
        signlog = new JMenuItem("签到日志");
        filelog = new JMenuItem("上传日志");
        menu.add(signlog);
        menu.add(filelog);
        bar.add(menu);
        serverFrame.setJMenuBar(bar);
        ///////////////////////菜单栏
    }

    private PrintWriter writer1;
    Socket socket1;
    Filter filter = new Filter(); //信息舆情

    private void connect(String name, String num, String ip, int port) {
        while (true) {
            try {
                socket1 = new Socket(ip, port);
                writer1 = new PrintWriter(new OutputStreamWriter(socket1.getOutputStream(), "UTF-8"), true);
                if (signboolean) {
                    Runtime.getRuntime().exec("cmd.exe /c \"echo. IP: " + ip + " 学号: " + num + " 姓名: " + name + " 的用户在 %date% %time% 进行了签到。 >>signlog.log"); //写出签到日志
                    writer1.println("学号：" + num + "--姓名：" + name + " 使用IP： " + ip + " 进行了签到。");
                } else {
                    writer1.println(name + ", 服务端管理员未开始签到活动。请稍后再试。");
                }
                break;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // 构建GUI 
    private void buildGUI() {
        // 窗口的设置 
        serverFrame.setTitle("服务器");
        serverFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        serverFrame.setSize(550, 550);
        serverFrame.setResizable(false); //主窗口不可调整

        // 北区的组件 
        jPanelNorth.add(portLabel);
        jPanelNorth.add(portText);
        jPanelNorth.add(startButton);
        jPanelNorth.add(signButton);
        serverFrame.getContentPane().add(BorderLayout.NORTH, jPanelNorth);

        // 中间的组件 
        serverTextArea.setFocusable(false);
        scroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        serverFrame.getContentPane().add(BorderLayout.CENTER, scroller);

        jPanelSouth2.add(sayLabel);
        jPanelSouth2.add(sayText);
        jPanelSouth2.add(sayButton);
        jPanelSouth0.setLayout(new BoxLayout(jPanelSouth0, BoxLayout.Y_AXIS));
        jPanelSouth0.add(jPanelSouth1);
        jPanelSouth0.add(jPanelSouth2);
        serverFrame.getContentPane().add(BorderLayout.SOUTH, jPanelSouth0);

        // 设置窗口可见 
        serverFrame.setVisible(true);
        serverFrame.setLocationRelativeTo(null); //置窗口于屏幕中间
    }

    // 服务器运行 
    public void startUp() {
        buildGUI();

        // 监听Start按钮，建立端口 
        ActionListener startListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clientOutputStreams = new ArrayList<PrintWriter>();
                String aPort = portText.getText();

                if (aPort.equals("")) {
                    JOptionPane.showMessageDialog(serverFrame, "请输入正确的端口号！");
                } else {
                    try {
                        // 等待客户端连接的线程 
                        Runnable serverRunnable = new Runnable() {
                            @Override
                            public void run() {
                                ServerSocket serverSocket;
                                try {
                                    serverSocket = new ServerSocket(Integer.parseInt(aPort));
                                    connectionboolean = true;
                                    serverTextArea.append("正在等待客户端连接...\n");
                                    while (true) {
                                        Socket clientSocket = serverSocket.accept();
                                        serverTextArea.append("客户端已连接...\n");
                                        // 在服务器端对客户端开启文件传输的线程
                                        //

                                        PrintWriter writer = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream(), "UTF-8"), true);
                                        clientOutputStreams.add(writer);
                                        Thread t = new Thread(new ClientHandler(clientSocket));
                                        t.start();

                                    }
                                } catch (NumberFormatException | IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        };
                        Thread serverThread = new Thread(serverRunnable);
                        serverThread.start();

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        };
        startButton.addActionListener(startListener);
        portText.addActionListener(startListener);

        // 监听Say按钮，发送消息
        ActionListener SayListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String aText = sayText.getText();
                if (!aText.equals("")) {
                    aText = "服务端：" + filter.Filter(aText);
                    sendToEveryClient(aText);
                    serverTextArea.append(aText + "\n");
                    sayText.setText("");
                } else {
                    JOptionPane.showMessageDialog(serverFrame, "内容不能为空！");
                }
            }
        };

        ActionListener signListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // run方法具体重写
                if (connectionboolean == true) {
                    if (signboolean == false) {
                        signboolean = true;
                        signButton.setText("结束签到活动");
                    }else {
                        signboolean = false;
                        signButton.setText("开始签到活动");
                    }
                } else {
                    JOptionPane.showMessageDialog(serverFrame, "请先启动服务器！");
                }
            }
        };

        Desktop desktop = Desktop.getDesktop(); //文件打开对象
        signlog.addActionListener(new ActionListener() { //菜单栏事件
            @Override
            public void actionPerformed(ActionEvent arg0) {
                try {
                    try {
                        desktop.open(new File("signlog.log"));
                        //Runtime.getRuntime().exec("signlog.log"); //打开签到日志 (DOS)
                    } catch (IOException ex) {
                        Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(serverFrame, "暂无签到日志！");
                }
            }
        });
        
        
          signlog.addActionListener(new ActionListener() { //菜单栏事件
            @Override
            public void actionPerformed(ActionEvent arg0) {
                try {
                    try {
                        desktop.open(new File("signlog.log"));
                        //Runtime.getRuntime().exec("signlog.log"); //打开签到日志 (DOS)
                    } catch (IOException ex) {
                        Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(serverFrame, "暂无签到日志！");
                }
            }
        });
        


        signButton.addActionListener(signListener);
        sayButton.addActionListener(SayListener);
        sayText.addActionListener(SayListener);
    }

    // 多客户端的线程 
    public class ClientHandler implements Runnable {

        BufferedReader bReader;
        Socket aSocket;

        public ClientHandler(Socket clientSocket) {
            try {
                aSocket = clientSocket;

                bReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), "UTF-8"));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        @Override
        public void run() {
            String message;
            try {
                while ((message = bReader.readLine()) != null) {

                    try {
                        if (message.substring(message.indexOf("：") + 1, message.indexOf("|")).equals("**!!**sign**!!**")) {
                            String ip = message.substring(message.indexOf("@") + 1, message.indexOf("#")); //主机IP
                            String name = message.substring(message.indexOf("$") + 1, message.indexOf("%")); //姓名
                            String num = message.substring(message.indexOf("^") + 1, message.indexOf("&")); //学号
                            int port = Integer.parseInt(message.substring(message.indexOf("]") + 1, message.indexOf("~"))); //端口号

                            System.out.println("IP是：" + ip + " 的用户 " + name + " 尝试签到！");
                            //Check(message);
                            System.out.println(port);
                            Thread tc = new Thread(new Runnable() {
                                public void run() {
                                    connect(name, num, ip, port); //签到对象的IP
                                }
                            });
                            tc.start();

                        }
                    } catch (StringIndexOutOfBoundsException e) { //Catch错误
                        System.out.println(message);
                        sendToEveryClient(message);
                        serverTextArea.append(filter.Filter(message) + "\n");
                    }

                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void Check(String message) {
        String name = message.substring(message.indexOf("&") + 1, message.indexOf("|")); //姓名
        String num = message.substring(message.indexOf("|") + 1, message.indexOf("#")); //学号
        String ip = message.substring(message.indexOf("|") + 1, message.indexOf("&")); //主机IP
        int port = Integer.parseInt(message.substring(message.indexOf("#") + 1, message.indexOf("@"))); //端口号
        //   **!!**sign**!!**|192.168.56.1IP&123姓名|123学号#294端口号@
        // 127.0.0.1IP&LZY|132133#7878@
        System.out.println("注明");
        Thread tc = new Thread(new Runnable() {
            public void run() {
                // run方法具体重写

                connect(name, num, ip, port); //签到对象的IP
            }
        });
        tc.start();

    }

    // 发送消息给所有客户端的方法 
    private void sendToEveryClient(String message) {
        Iterator<PrintWriter> it = clientOutputStreams.iterator();
        while (it.hasNext()) {
            try {
                PrintWriter writer = (PrintWriter) it.next();
                writer.println(message);
                writer.flush();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
