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
    //��ʾ����״̬
    private JLabel stateLB;
    private JLabel iplable;
    private JLabel portlable;
    private JLabel udpiplable;
    private JLabel udpportlable;
    //��ʾ�����¼
    private JTextArea centerTestArea;
    //�ײ����
    private JPanel southPanel;
    //���������
    private JTextArea inputTextArea;
    //����IP��ַ�����,��ť
    private JPanel bottomPanel;
    private JPanel bottompanel2;
    //IP�����
    private JTextField ipTextFiled;
    //�˿ں������
    private JTextField portTextFiled;
    //���Ͱ�ť
    private JTextField UDPiptextfiled;
    private JTextField UDPporttextfiled;
    private JButton sendBT;
    //�ļ����Ͱ�ť
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
        setTitle("������");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(650, 550);
        setLocationRelativeTo(null);

        //������
        stateLB = new JLabel("����������", SwingConstants.CENTER);
        iplable = new JLabel("ip");
        portlable = new JLabel("PORT");



        //�����¼
        centerTestArea = new JTextArea();
        centerTestArea.setEditable(false);
        centerTestArea.setBackground(new Color(211, 211, 211));

        //�ײ�
        southPanel = new JPanel(new BorderLayout());
        inputTextArea = new JTextArea(10, 20);
        bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        bottompanel2=new JPanel(new FlowLayout(FlowLayout.CENTER,2,2));

        portTextFiled = new JTextField(8);
        ipTextFiled = new JTextField(8);
        sendBT = new JButton("������Ϣ");
        selectFile = new JButton("ѡ���ļ�");
        sendFile = new JButton("���ļ�");
        qiandao = new JButton("ǩ��");
        JButton connect = new JButton("����");
        sound = new JButton("��������");
        soundend = new JButton("�ж���������");
        screen = new JButton("��Ļ���");

        //������
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
        southPanel.add(new JScrollPane(inputTextArea),BorderLayout.CENTER);//���������ӹ�����
        southPanel.add(bottomPanel, BorderLayout.SOUTH);
        //bottomPanel.add(bottompanel2,BorderLayout.SOUTH);

        //jframe�����������λ
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

        //������ʵ�ְ�Send��ť������Ϣ�Ĺ���
        sendBT.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String strSend = inputTextArea.getText();
                if (strSend.trim().length() == 0) {
                    return;
                }
                send(strSend);
                centerTestArea.append(s.getInetAddress() + "/" + s.getPort() + strSend + "\n");
                inputTextArea.setText("");  //����ı���
            }
        });

        connect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                try {
                    s = new Socket(ipTextFiled.getText(), Integer.parseInt(portTextFiled.getText()));
                    //��ʾ�����Ϸ�����
                    isConn = true;
                    centerTestArea.append("�������Ϸ�����!\n");
                } catch (SocketException e) {
                    System.out.println("�Ҳ���������!");
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
                String strSend = "����ǩ����";
                send(strSend);
                centerTestArea.append("ǩ���ɹ�!" + "\n");
                inputTextArea.setText("");  //����ı���
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
                    dos.writeUTF(send_file_name);//���������
                    System.out.println("���͵��ļ�:" + send_file_name);

                    int len = 0;
                    byte[] bytes = new byte[1024];
                    // �Ѷ�ȡ�����ļ��ϴ���������
                    while ((len = fis.read(bytes)) != -1) {
                        os.write(bytes, 0, len);
                    }
                    // ����
                    socket.shutdownOutput();
                    // ��д
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

    //���̵߳��࣬ʵ����Runnable�ӿ�
    class Receive implements Runnable {

        @Override
        public void run() {
            try {
                while (isConn) {
                    DataInputStream dis = new DataInputStream(s.getInputStream());
                    String str = dis.readUTF();
                    centerTestArea.append("�����˵:"+str);
                }
            } catch (SocketException e) {
                System.out.println("��������ֹ");
                centerTestArea.append("��������ֹ!\n");
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

