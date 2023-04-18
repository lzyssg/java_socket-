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
    JMenuItem signlog; //ǩ����־
    JMenuItem filelog; // �ļ���־
    JMenuItem copy; // �ļ�����
    ////////////////////////////////////

    public static void main(String[] args) {
        Server aServer = new Server();
        aServer.startUp();
    }

    // ��ʼ����� 
    public Server() {
        serverFrame = new JFrame();
        jPanelNorth = new JPanel();
        portLabel = new JLabel("�˿ں�", JLabel.LEFT);
        portText = new JTextField(25);
        startButton = new JButton("��ʼ");
        signButton = new JButton("��ʼǩ���");
        serverTextArea = new JTextArea();
        scroller = new JScrollPane(serverTextArea);
        jPanelSouth0 = new JPanel();
        jPanelSouth1 = new JPanel();
        jPanelSouth2 = new JPanel();
        sayLabel = new JLabel("��Ϣ", JLabel.LEFT);
        sayText = new JTextField(30);
        sayButton = new JButton("ȷ��");

        //////////////////�˵���
        bar = new JMenuBar();
        menu = new JMenu("��־��ѯ"); //��n
        signlog = new JMenuItem("ǩ����־");
        filelog = new JMenuItem("�ϴ���־");
        menu.add(signlog);
        menu.add(filelog);
        bar.add(menu);
        serverFrame.setJMenuBar(bar);
        ///////////////////////�˵���
    }

    private PrintWriter writer1;
    Socket socket1;
    Filter filter = new Filter(); //��Ϣ����

    private void connect(String name, String num, String ip, int port) {
        while (true) {
            try {
                socket1 = new Socket(ip, port);
                writer1 = new PrintWriter(new OutputStreamWriter(socket1.getOutputStream(), "UTF-8"), true);
                if (signboolean) {
                    Runtime.getRuntime().exec("cmd.exe /c \"echo. IP: " + ip + " ѧ��: " + num + " ����: " + name + " ���û��� %date% %time% ������ǩ���� >>signlog.log"); //д��ǩ����־
                    writer1.println("ѧ�ţ�" + num + "--������" + name + " ʹ��IP�� " + ip + " ������ǩ����");
                } else {
                    writer1.println(name + ", ����˹���Աδ��ʼǩ��������Ժ����ԡ�");
                }
                break;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // ����GUI 
    private void buildGUI() {
        // ���ڵ����� 
        serverFrame.setTitle("������");
        serverFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        serverFrame.setSize(550, 550);
        serverFrame.setResizable(false); //�����ڲ��ɵ���

        // ��������� 
        jPanelNorth.add(portLabel);
        jPanelNorth.add(portText);
        jPanelNorth.add(startButton);
        jPanelNorth.add(signButton);
        serverFrame.getContentPane().add(BorderLayout.NORTH, jPanelNorth);

        // �м����� 
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

        // ���ô��ڿɼ� 
        serverFrame.setVisible(true);
        serverFrame.setLocationRelativeTo(null); //�ô�������Ļ�м�
    }

    // ���������� 
    public void startUp() {
        buildGUI();

        // ����Start��ť�������˿� 
        ActionListener startListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clientOutputStreams = new ArrayList<PrintWriter>();
                String aPort = portText.getText();

                if (aPort.equals("")) {
                    JOptionPane.showMessageDialog(serverFrame, "��������ȷ�Ķ˿ںţ�");
                } else {
                    try {
                        // �ȴ��ͻ������ӵ��߳� 
                        Runnable serverRunnable = new Runnable() {
                            @Override
                            public void run() {
                                ServerSocket serverSocket;
                                try {
                                    serverSocket = new ServerSocket(Integer.parseInt(aPort));
                                    connectionboolean = true;
                                    serverTextArea.append("���ڵȴ��ͻ�������...\n");
                                    while (true) {
                                        Socket clientSocket = serverSocket.accept();
                                        serverTextArea.append("�ͻ���������...\n");
                                        // �ڷ������˶Կͻ��˿����ļ�������߳�
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

        // ����Say��ť��������Ϣ
        ActionListener SayListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String aText = sayText.getText();
                if (!aText.equals("")) {
                    aText = "����ˣ�" + filter.Filter(aText);
                    sendToEveryClient(aText);
                    serverTextArea.append(aText + "\n");
                    sayText.setText("");
                } else {
                    JOptionPane.showMessageDialog(serverFrame, "���ݲ���Ϊ�գ�");
                }
            }
        };

        ActionListener signListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // run����������д
                if (connectionboolean == true) {
                    if (signboolean == false) {
                        signboolean = true;
                        signButton.setText("����ǩ���");
                    }else {
                        signboolean = false;
                        signButton.setText("��ʼǩ���");
                    }
                } else {
                    JOptionPane.showMessageDialog(serverFrame, "����������������");
                }
            }
        };

        Desktop desktop = Desktop.getDesktop(); //�ļ��򿪶���
        signlog.addActionListener(new ActionListener() { //�˵����¼�
            @Override
            public void actionPerformed(ActionEvent arg0) {
                try {
                    try {
                        desktop.open(new File("signlog.log"));
                        //Runtime.getRuntime().exec("signlog.log"); //��ǩ����־ (DOS)
                    } catch (IOException ex) {
                        Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(serverFrame, "����ǩ����־��");
                }
            }
        });
        
        
          signlog.addActionListener(new ActionListener() { //�˵����¼�
            @Override
            public void actionPerformed(ActionEvent arg0) {
                try {
                    try {
                        desktop.open(new File("signlog.log"));
                        //Runtime.getRuntime().exec("signlog.log"); //��ǩ����־ (DOS)
                    } catch (IOException ex) {
                        Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(serverFrame, "����ǩ����־��");
                }
            }
        });
        


        signButton.addActionListener(signListener);
        sayButton.addActionListener(SayListener);
        sayText.addActionListener(SayListener);
    }

    // ��ͻ��˵��߳� 
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
                        if (message.substring(message.indexOf("��") + 1, message.indexOf("|")).equals("**!!**sign**!!**")) {
                            String ip = message.substring(message.indexOf("@") + 1, message.indexOf("#")); //����IP
                            String name = message.substring(message.indexOf("$") + 1, message.indexOf("%")); //����
                            String num = message.substring(message.indexOf("^") + 1, message.indexOf("&")); //ѧ��
                            int port = Integer.parseInt(message.substring(message.indexOf("]") + 1, message.indexOf("~"))); //�˿ں�

                            System.out.println("IP�ǣ�" + ip + " ���û� " + name + " ����ǩ����");
                            //Check(message);
                            System.out.println(port);
                            Thread tc = new Thread(new Runnable() {
                                public void run() {
                                    connect(name, num, ip, port); //ǩ�������IP
                                }
                            });
                            tc.start();

                        }
                    } catch (StringIndexOutOfBoundsException e) { //Catch����
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
        String name = message.substring(message.indexOf("&") + 1, message.indexOf("|")); //����
        String num = message.substring(message.indexOf("|") + 1, message.indexOf("#")); //ѧ��
        String ip = message.substring(message.indexOf("|") + 1, message.indexOf("&")); //����IP
        int port = Integer.parseInt(message.substring(message.indexOf("#") + 1, message.indexOf("@"))); //�˿ں�
        //   **!!**sign**!!**|192.168.56.1IP&123����|123ѧ��#294�˿ں�@
        // 127.0.0.1IP&LZY|132133#7878@
        System.out.println("ע��");
        Thread tc = new Thread(new Runnable() {
            public void run() {
                // run����������д

                connect(name, num, ip, port); //ǩ�������IP
            }
        });
        tc.start();

    }

    // ������Ϣ�����пͻ��˵ķ��� 
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
