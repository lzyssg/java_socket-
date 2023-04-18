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
    private JLabel signnumLabel; //ǩ������
    private JLabel signnameLabel; //ǩ����ǩ
    private JTextField signnameText; //ǩ���༭��
    private JTextField signnumText;  //ǩ��ѧ�ſ�
    private JButton signB; //ǩ����ť
    private JPanel jPanelSign;
    private JPanel jPanelSign1;
    private JPanel jPanelSign2;
    public int port = new java.util.Random().nextInt(300) * new java.util.Random().nextInt(10); //�����ǩ���˿ں�
    private boolean connectionflag = false;
    private PrintWriter fileout = null;

    public static void main(String args[]) throws UnknownHostException {
        System.out.println(Inet4Address.getLocalHost().getHostAddress());
        Client aClient = new Client();
        aClient.startUp();
    }

    // ��ʼ����� 
    public Client() {
        clientFrame = new JFrame();
        jPanelNorth = new JPanel();
        jPanelSouth_sign = new JPanel(); //�ļ�����Panel
        jPanelSign = new JPanel();
        jPanelSign1 = new JPanel();
        jPanelSign2 = new JPanel();
        IPLabel = new JLabel("������IP", JLabel.LEFT);
        IPText = new JTextField(8);
        PortLabel = new JLabel("�������˿�", JLabel.LEFT);
        PortText = new JTextField(8);
        nameLabel = new JLabel("����", JLabel.LEFT);
        nameText = new JTextField(5);
        //PortLabel = new JLabel("���ط������˿ڣ�", JLabel.LEFT); 
        connectButton = new JButton("����");
        clientTextArea = new JTextArea();
        scroller = new JScrollPane(clientTextArea);
        jPanelSouth0 = new JPanel();
        jPanelSouth1 = new JPanel();
        jPanelSouth2 = new JPanel();
        sayLabel = new JLabel("��Ϣ", JLabel.LEFT);
        sayText = new JTextField(30);
        sayButton = new JButton("ȷ��");
        signButton = new JButton("ǩ��");


        signButton.setPreferredSize(new Dimension(120, 30));

        sign = new JFrame();
        signnameLabel = new JLabel("������"); //ǩ����ǩ
        signnameText = new JTextField(15); //ǩ���༭��
        signnumLabel = new JLabel("ѧ�ţ�"); //ǩ����ǩ
        signnumText = new JTextField(15); //ǩ���༭��
        signB = new JButton("ǩ��"); //ǩ����ť  

    }

    public class Mytcp {

        private BufferedReader reader;
        private ServerSocket server;
        private Socket socket;

        void getServer(int port) {
            try {
                server = new ServerSocket(port);        //ǩ��Socket
                while (true) {
                    socket = server.accept();           //accept()�����᷵��һ���Ϳͻ���Socket����������Socket����
                    System.out.println(socket.getPort());
                    reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
                    getClientMessage();
                }
            } catch (Exception e) {
                e.printStackTrace();

            }

        }

        //��ȡ�ͻ��˷��͹�������Ϣ
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

    // ����GUI 
    private void buildGUI() {
        // ���ڵ����� 
        clientFrame.setTitle("�ͻ���");
        clientFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        clientFrame.setSize(550, 550);
        clientFrame.setResizable(false); //�����ڲ��ɵ���

        // ��������� 
        jPanelNorth.add(IPLabel);
        jPanelNorth.add(IPText);
        jPanelNorth.add(PortLabel);
        jPanelNorth.add(PortText);
        jPanelNorth.add(nameLabel);
        jPanelNorth.add(nameText);
        jPanelNorth.add(connectButton);
        clientFrame.getContentPane().add(BorderLayout.NORTH, jPanelNorth);

        // �м����� 
        clientTextArea.setFocusable(false);
        scroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        clientFrame.getContentPane().add(BorderLayout.CENTER, scroller);

        // ��������� 
        jPanelSouth2.add(sayLabel);
        jPanelSouth2.add(sayText);
        jPanelSouth2.add(sayButton);

        jPanelSouth_sign.add(signButton);


        jPanelSouth1.add(jPanelSouth_sign);
        jPanelSouth0.setLayout(new BoxLayout(jPanelSouth0, BoxLayout.Y_AXIS));
        jPanelSouth0.add(jPanelSouth1);
        jPanelSouth0.add(jPanelSouth2);
        clientFrame.getContentPane().add(BorderLayout.SOUTH, jPanelSouth0);

        // ���ô��ڿɼ� 
        clientFrame.setVisible(true);
        clientFrame.setLocationRelativeTo(null); //�ô�������Ļ�м�
    }

    private void signGUI() {
        sign.setTitle("ǩ��");
        //sign.setDefaultCloseOperation(); 
        sign.setSize(260, 140);
        jPanelSign.add(signnumLabel);
        jPanelSign.add(signnumText);
        jPanelSign1.add(signnameLabel);
        jPanelSign1.add(signnameText);
        jPanelSign2.add(signB);
        sign.getContentPane().add(BorderLayout.NORTH, jPanelSign);
        sign.getContentPane().add(BorderLayout.CENTER, jPanelSign1);
        sign.getContentPane().add(BorderLayout.SOUTH, jPanelSign2); //�Ű�
        //sign.setVisible(true); 
        sign.setLocationRelativeTo(clientFrame);
        sign.setResizable(false);
    }

    // �ͻ������� 
    public void startUp() {
        buildGUI();
        signGUI();
        Filter filter = new Filter();
        // ���շ�������Ϣ���߳� 
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

        // ����Connect��ť��ʵ�ַ����������� 
        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String aServerIP = IPText.getText();
                String aServerPort = PortText.getText();
                name = nameText.getText();
                if (nameText.getText().equals("")) {
                    name = "Ĭ���û�";
                }
                if (aServerIP.equals("") || aServerPort.equals("")) {
                    JOptionPane.showMessageDialog(clientFrame, "������������ IP�Ͷ˿ڣ�");
                } else {
                    try {
                        @SuppressWarnings("resource")
                        Socket clientSocket = new Socket(aServerIP, Integer.parseInt(aServerPort));

                        reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), "UTF-8"));
                        writer = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream(), "UTF-8"), true);
                        clientTextArea.append("������������...\n");
                        connectionflag = true;
                        connectButton.setEnabled(false);
                        Thread readerThread = new Thread(incomingReader);
                        readerThread.start();

                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(clientFrame, "���Ӳ��Ϸ�����!\n��ȷ�� IP �� �˿� ������ȷ��");
                    }

                }
            }
        });

        // ������Ϣ��������
        ActionListener SayListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String aText = sayText.getText();
                if (aText.equals("")) {
                    JOptionPane.showMessageDialog(clientFrame, "���ݲ���Ϊ�գ�");
                } else {
                    try {
                        writer.println(name + "��" + aText);
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
                if (signnameText.getText().equals("") | signnumText.getText().equals("")) { //���ǩ����Ϣ�Ƿ�����
                    JOptionPane.showMessageDialog(clientFrame, "ǩ����Ϣ������д������");
                } else {
                    Thread t = new Thread(new Runnable() {
                        public void run() {
                            // run����������д
                            Mytcp s = new Mytcp();
                            s.getServer(port);
                        }
                    });
                    t.start();
                    port = port++ + new java.util.Random().nextInt(10);
                    //�˿ںŸı�
                    GetLocalIP ipConent = new GetLocalIP();
                    String ip = ipConent.GetLocalIP(); //��ȡ����IP
                    writer.println("**!!**sign**!!**" + "|@" + ip + "#$" + signnameText.getText() + "%^" + signnumText.getText() + "&]" + port + "~"); //�ݳ���Ϣ ����+ѧ��+�˿ں�
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
                //JOptionPane.showMessageDialog(clientFrame, "ǩ����");
                if (!connectionflag) {
                    JOptionPane.showMessageDialog(clientFrame, "ǩ��ʧ�ܣ�δ���ӷ�������");
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

