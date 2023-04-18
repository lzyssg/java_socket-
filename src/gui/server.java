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
    //��ʾ����״̬
    private JLabel stateLB;
    private JLabel portlable;
    //��ʾ�����¼
    public JTextArea centerTestArea;
    //�ײ����
    private JPanel southPanel;
    //���������
    private JTextArea inputTextArea;
    private JPanel bottomPanel;
    //�˿ں������
    private JTextField portTextFiled;
    //���Ͱ�ť
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

        //ͬʱ���ն���ͻ��˵���Ϣ -- ���߳̽�������
        @Override
        public void run() {
            try {
                DataInputStream dis = new DataInputStream(s.getInputStream());
              //  DataOutputStream out=new DataOutputStream(s.getOutputStream());
                ObjectInputStream bis = new ObjectInputStream(s.getInputStream());
                    while (isStart) {
                        String str = dis.readUTF();
                        System.out.println(s.getInetAddress() + "/" + s.getPort() +"˵: " + str);
                        centerTestArea.append(s.getInetAddress() + "/" + s.getPort() +"˵: " + str +"\n");
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
                System.out.println("һ���ͻ��������ˣ�" + s.getInetAddress() + "/" + s.getPort());
                centerTestArea.append("һ���ͻ��������ˣ�" + s.getInetAddress() + "/" + s.getPort() + "\n");
            }
            catch (IOException e) {
                System.out.println("�������ж���!");
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
        setTitle("������");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(650, 550);
        setLocationRelativeTo(null);

        //������
        stateLB = new JLabel("����������", SwingConstants.CENTER);
        portlable = new JLabel("PORT");

        //�����¼
        centerTestArea = new JTextArea();
        centerTestArea.setEditable(false);
        centerTestArea.setBackground(new Color(211, 211, 211));

        //�ײ�
        southPanel = new JPanel(new BorderLayout());
        inputTextArea = new JTextArea(10, 10);
        bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        portTextFiled = new JTextField(8);
        sendBT = new JButton("������Ϣ");
        startport = new JButton("����");
        sound = new JButton("��������");
        serverend=new JButton("�ر�");
        soundend=new JButton("�ر�����");
        screen=new JButton("��Ļ���");
        receivefile=new JButton("�����ļ�");

        //������
        bottomPanel.add(portlable);
        bottomPanel.add(portTextFiled);
        bottomPanel.add(sendBT);
        bottomPanel.add(startport);
        bottomPanel.add(sound);
        bottomPanel.add(serverend);
        bottomPanel.add(soundend);
        bottomPanel.add(screen);
        bottomPanel.add(receivefile);
        southPanel.add(new JScrollPane(inputTextArea), BorderLayout.NORTH);//���������ӹ�����
        southPanel.add(bottomPanel, BorderLayout.CENTER);


        //jframe�����������λ
        add(stateLB, BorderLayout.NORTH);
        add(new JScrollPane(centerTestArea), BorderLayout.CENTER);
        add(southPanel, BorderLayout.SOUTH);
        setVisible(true);

        try {
            portTextFiled.setText(String.valueOf(6666));//Ĭ�϶˿��ı������Ϊ6666
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
                    System.out.println("������ֹͣ!\n");
                    centerTestArea.append("�������Ͽ�!\n");
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
                    //�ر�����������ٹر�socket����
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
                            //���Խ��ܶ���ͻ��˵�����
                            while (isStart) {
                                s = ss.accept();
                                ccList.add(new ClientConn(s));
                                System.out.println("һ���ͻ������ӷ�������" + s.getInetAddress() + "/" + s.getPort());
                                centerTestArea.append("һ���ͻ������ӷ�������" + s.getInetAddress() + "/" + s.getPort() + "\n");
                            }
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                        centerTestArea.append("�����������ɹ���\n");
                    }
                }.start();


            }

        });
        sendBT.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String strSend = inputTextArea.getText();
                //����ccList������send����
                Iterator<ClientConn> it = ccList.iterator();
                while (it.hasNext()) {
                    ClientConn o = it.next();
                    o.send(strSend + "\n");
                    centerTestArea.append("�����˵��" + strSend + "\n");
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
