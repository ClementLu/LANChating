package com.igeek.chat;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ChatFrame extends JFrame {

	// ��������Ĳ������
	private JPanel contentPane;
	// ��Ϣ�����
	private JTextField txtSendContent;
	// ���Ͱ�ť
	private JButton btnSend;
	// ��Ϣ��ʾ��
	private JTextArea txtReceivedContent;
	// ��Ϣ��ʾ��Ĺ������
	private JScrollPane scrollPaneContent;

	// ������Ϣ�����߳�
	private UDPReceiveThread udpReceiveThread;
	// ������Ϣ�����߳��Ƿ���������ı��
	private boolean isRecevie = true;

	// ��һ�η��͵�¼��Ϣ
	private boolean isFirstSend = true;

	// �û��б�Ĺ������
	private JScrollPane scrollPaneUsers;
	// ����JList�е�ʵ������
	private DefaultListModel<String> defaultListModel;
	// �û��б�ؼ�
	private JList<String> listUsers;

	// ��ȡ������ַ��Ϣ
	private InetAddress localHostIP = null;
	private JButton btnSendFile;

	/**
	 * Create the frame.
	 */
	public ChatFrame() {

		// ��ȡ����ǰ�������Ļ�ķֱ��ʳߴ�
		int screenWidth = getToolkit().getScreenSize().width;
		int screenHeight = getToolkit().getScreenSize().height;

		// �����ڵĳߴ�
		int width = 800;
		int height = 600;

		// ��������ʱ����Ļ����ʾ������ֵ
		int pointX = (screenWidth - width) / 2;
		int pointY = (screenHeight - height) / 2;

		try {
			// ��������ʱ����ȡ����id��ַ��Ϣ
			localHostIP = InetAddress.getLocalHost();
		} catch (UnknownHostException ex) {
			ex.printStackTrace();
		}

		// ���ô������
		setTitle("IGeekHome\u804A\u5929\u5BA4-" + localHostIP);
		// ���ô���Ĺر��¼����ڹرմ����ͬʱ����������˳�
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// ���ô���Ĵ�С������λ��
		setBounds(pointX, pointY, width, height);

		// ���ô����пؼ��Ĳ��ַ�ʽ
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel lblSendContent = new JLabel("\u804A\u5929\u5185\u5BB9\uFF1A");
		// ��ʾ��ǩ�Ĵ�С
		lblSendContent.setBounds(10, 328, 100, 15);
		contentPane.add(lblSendContent);

		txtSendContent = new JTextField();
		// ���������Ĵ�С
		txtSendContent.setBounds(110, 325, 300, 21);
		txtSendContent.setColumns(10);
		contentPane.add(txtSendContent);

		btnSend = new JButton("\u53D1\u9001\u804A\u5929\u5185\u5BB9");
		// ���÷��Ͱ�ť�Ĵ�С
		btnSend.setBounds(110, 360, 150, 25);
		contentPane.add(btnSend);

		scrollPaneContent = new JScrollPane();
		// ������Ϣ��ʾ�������Ĵ�С
		scrollPaneContent.setBounds(10, 10, 400, 307);
		contentPane.add(scrollPaneContent);

		txtReceivedContent = new JTextArea();
		// ������Ϣ��ʾ��������Զ�����
		txtReceivedContent.setLineWrap(true);
		// ������Ϣ��ʾ�򲻿ɱ༭
		txtReceivedContent.setEditable(false);
		// ����Ϣ��ʾ����뵽��������У������й�������
		scrollPaneContent.setViewportView(txtReceivedContent);

		scrollPaneUsers = new JScrollPane();
		// �����û��б�������Ĵ�С
		scrollPaneUsers.setBounds(420, 10, 200, 336);
		contentPane.add(scrollPaneUsers);

		defaultListModel = new DefaultListModel<String>();
		listUsers = new JList<String>(defaultListModel);
		
		// �����û��б�Ϊ��ѡģʽ
		listUsers.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		// ���û��б���뵽���������
		scrollPaneUsers.setViewportView(listUsers);
		
		btnSendFile = new JButton("\u53D1\u9001\u6587\u4EF6");
		btnSendFile.setEnabled(false);
		btnSendFile.setBounds(420, 356, 95, 25);
		contentPane.add(btnSendFile);
		
		btnSendFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
//			    FileNameExtensionFilter filter = new FileNameExtensionFilter(
//			        "JPG & GIF Images", "jpg", "gif");
//			    chooser.setFileFilter(filter);
			    int returnVal = chooser.showOpenDialog(ChatFrame.this);
			    if(returnVal == JFileChooser.APPROVE_OPTION) {
			       File selectedFile = chooser.getSelectedFile();
			       System.out.println("You chose to open this file: " +
			    		   selectedFile.getName());
			       System.out.println(selectedFile.getAbsolutePath());
			       
			       //���濪ʼ�ļ��Ĵ���
			       //������дһ�������ļ������߳���Ķ���
			    }

			}
		});
		
		listUsers.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				String localHostAddress=localHostIP.getHostAddress();
				//�û��б��ĵ���¼��������Ƿ����û���ѡ�У�ѡ���˲���p2p�����ļ�    
				String selectedValue = listUsers.getSelectedValue();
				if(selectedValue!=null&&!"".equals(selectedValue)&&!localHostAddress.equals(selectedValue)){
					btnSendFile.setEnabled(true);					
				}else{
					btnSendFile.setEnabled(false);			
				}
			}
		});

		// �����Ļس��¼�
		txtSendContent.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// ������Ϣ�Ĳ���
				sendMessage();
			}
		});

		// ע����Ӱ�ť�ĵ���¼���������Ϣ
		btnSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// ������Ϣ�Ĳ���
				sendMessage();
			}
		});

		// ע�ᴰ�ڵĹر��¼�
		addWindowListener(new WindowAdapter() {
			/**
			 * @Title: windowClosing
			 * @Description: TODO(������һ�仰�����������������)
			 * @param e
			 * @see java.awt.event.WindowAdapter#windowClosing(java.awt.event.WindowEvent)
			 */
			@Override
			public void windowClosing(WindowEvent e) {
				System.out.println("��������˳���");

				String loginContent = "login:0";
				// ��������з���һ���˳��Ĺ㲥��Ϣ
				UDPUtils.send("255.255.255.255", loginContent);

				// �ü���������Ϣ���߳�Ҳ��������
				ChatFrame.this.isRecevie = false;
			}

		});

		// �ѱ��û����뵽�û��б���
		defaultListModel.addElement(localHostIP.getHostAddress());
		// �����б�ı����
		listUsers.setBorder(BorderFactory.createTitledBorder("����������" + defaultListModel.size()));

		// ������Ϣ�����߳�
		udpReceiveThread = new UDPReceiveThread();
		udpReceiveThread.start();

		// ���������ļ��߳�
	}

	// ������Ϣ�Ĳ���
	private void sendMessage() {
		// ���û��ʵ����Ϣ�����ܷ���
		String txtSendContentValue = ChatFrame.this.txtSendContent.getText();
		if (txtSendContentValue == null || "".equals(txtSendContentValue)) {
			// ����һ����ʾ�Ի���,ģʽ�Ի���
			JOptionPane.showMessageDialog(ChatFrame.this, "�������������ݣ�");

			// ���������õ���꣬�Ա�������������
			ChatFrame.this.txtSendContent.grabFocus();
			return;
		}

		// ������������
		// �õ��ؼ����ı�����
		String receiveIP = "255.255.255.255";// �൱��Ⱥ��,Ĭ�Ϲ㲥Ⱥ��
		String content = "content:" + txtSendContentValue;

		String selectIP = listUsers.getSelectedValue();
		// ������ַ
		String localHostAddress = localHostIP.getHostAddress();
		if (selectIP != null && !"".equals(selectIP) && !localHostAddress.equals(selectIP)) {
			// �������˽��
			receiveIP = selectIP;
			content = "privatecontent:" + txtSendContentValue;

			String message = "˽����Ϣ[���͸�-" + receiveIP + "] : " + txtSendContentValue;

			// ͬ���Լ����������ݷ��뵽�Լ�����Ϣ��ʾ����
			// �����µ���Ϣ����׷�ӵ���Ϣ����
			ChatFrame.this.txtReceivedContent.append(message + "\r\n");
		}

		// ���ͷ���
		UDPUtils.send(receiveIP, content);

		// ��������
		ChatFrame.this.txtSendContent.setText("");
	}

	// ������Ϣ���߳�
	public class UDPReceiveThread extends Thread {

		@Override
		public void run() {
			// 1���������ն�
			DatagramSocket datagramSocket = null;
			try {
				datagramSocket = new DatagramSocket(UDPUtils.PORT);
				System.out.println("��Ϣ�����߳�����......");

				while (ChatFrame.this.isRecevie) {

					byte[] buf = new byte[1024];
					// 2���������ݰ������ڽ�������
					DatagramPacket datagramPacket = new DatagramPacket(buf, buf.length);

					System.out.println("�ȴ����ա�����������");

					if (isFirstSend) {
						// ���͵�¼��Ϣ�����߳�
						Thread sendLoginThread = new Thread() {

							@Override
							public void run() {
								String loginContent = "login:1";
								// ����ʱ��������з���һ����¼�Ĺ㲥��Ϣ
								UDPUtils.send("255.255.255.255", loginContent);
								System.out.println("�Ѿ����͵�¼��Ϣ");
								// ���͵�¼��Ϣ��ϣ���Ǹ���
								isFirstSend = false;
							}

						};
						// �������͵�¼��Ϣ�����߳�
						sendLoginThread.start();
					}

					// 3��ͨ��socket�������ݰ�
					datagramSocket.receive(datagramPacket);

					// 4���������ݰ�
					byte[] data = datagramPacket.getData();
					int length = datagramPacket.getLength();

					// Ҳ���Ի�ȡ���Ͷ˵������Ϣ
					InetAddress sendAddress = datagramPacket.getAddress();

					// ���ֽ�����ת���ܹ������ַ�������
					String content = new String(data, 0, length);
					System.out.println("���յ������ݣ�[" + sendAddress + "]" + content);

					// ������Ϣ����
					parseContent(content, sendAddress.getHostAddress());

				}
			} catch (SocketException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (datagramSocket != null) {
					// 5���ر�socket
					datagramSocket.close();
				}
			}

			System.out.println("��Ϣ�����߳̽�������......");
		}

		/**
		 * @Title: parseContent
		 * @Description: ������Ϣ����
		 * @param content
		 */
		private void parseContent(String content, String sendAddressIP) {

			String[] split = content.split(":");

			// ��Ϣ��ʽ���δ����ָ����ʽ���ͣ�����Ϊ����ϢΪ�Ƿ���Ϣ�����Դ���Ϣ
			if (split == null || split.length != 2) {
				return;
			}

			String message = null;

			if ("login".equals(split[0])) {
				// ��¼���˳���Ϣ
				if ("1".equals(split[1])) {

					if (!defaultListModel.contains(sendAddressIP)) {
						// ��¼
						message = "ϵͳ��Ϣ[" + sendAddressIP + "] : ���������ң�";
						// �Ѵ��û����뵽�û��б���
						defaultListModel.addElement(sendAddressIP);
					}

					//���շ������յ����˵�¼�����ӳ�1���ӷ��ͷ�����Ϣ���Ա�Է�����Ϣ����ȷ������
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					// �������յ������µ�¼���룬����һ��������Ϣ�������µ�¼������û�����֮ǰ���Ѿ���¼�ˡ�
					UDPUtils.send(sendAddressIP, "login:2");

				} else if ("2".equals(split[1])) {
					// ���Ѿ���¼���û����ͻ����ķ�����Ϣ��������ǰ�����û��Ѿ���¼��
					// ����ǰ�Ѿ���¼���ü��뵽�Լ����û��б���
					if (!defaultListModel.contains(sendAddressIP)) {
						// �Ѵ��û����뵽�û��б���
						defaultListModel.addElement(sendAddressIP);
					}

				} else if ("0".equals(split[1])) {
					// �˳�
					message = "ϵͳ��Ϣ[" + sendAddressIP + "] : �˳������ң�";

					defaultListModel.removeElement(sendAddressIP);
				}

				// �����б�ı����
				listUsers.setBorder(BorderFactory.createTitledBorder("����������" + defaultListModel.size()));

			} else if ("content".equals(split[0])) {
				// ����������Ϣ
				message = "������Ϣ[" + sendAddressIP + "] : " + split[1];
			} else if ("privatecontent".equals(split[0])) {
				// ˽������
				message = "˽����Ϣ[" + sendAddressIP + "] : " + split[1];
			}

			// ��Ч��Ϣ��׷��
			if (message != null) {
				// �����µ���Ϣ����׷�ӵ���Ϣ����
				ChatFrame.this.txtReceivedContent.append(message + "\r\n");
			}

			String txtReceivedContentText = ChatFrame.this.txtReceivedContent.getText();
			// Ϊ�˿��Ʊ�������ʵ����ʾ���������ݲ��ܹ��࣬�����ڴ�����
			if (txtReceivedContentText.length() >= 1000) {
				// ����1500���ַ������ȡ����ǰ���500��
				txtReceivedContentText = txtReceivedContentText.substring(txtReceivedContentText.length() - 500,
						txtReceivedContentText.length());

				// Ϊ�˽�ȡ�������ݱȽ������������һ������������Ϣ�ٴν�ȡ��
				txtReceivedContentText = txtReceivedContentText.substring(txtReceivedContentText.indexOf("\r\n") + 2);

				ChatFrame.this.txtReceivedContent.setText(txtReceivedContentText);
			}

			// ��ȡ��ֱ����ʵ�ʵ����߶�
			int maximum = ChatFrame.this.scrollPaneContent.getVerticalScrollBar().getMaximum();

			// Ч�������Զ��ѹ��������������·�
			ChatFrame.this.scrollPaneContent.getVerticalScrollBar().setValue(maximum);
		}

	}

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		// ����������
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ChatFrame frame = new ChatFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
