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

	// 整个窗体的布局面板
	private JPanel contentPane;
	// 消息输入框
	private JTextField txtSendContent;
	// 发送按钮
	private JButton btnSend;
	// 消息显示框
	private JTextArea txtReceivedContent;
	// 消息显示框的滚动面板
	private JScrollPane scrollPaneContent;

	// 接收消息的子线程
	private UDPReceiveThread udpReceiveThread;
	// 接收消息的子线程是否继续工作的标记
	private boolean isRecevie = true;

	// 第一次发送登录消息
	private boolean isFirstSend = true;

	// 用户列表的滚动面板
	private JScrollPane scrollPaneUsers;
	// 才是JList中的实际数据
	private DefaultListModel<String> defaultListModel;
	// 用户列表控件
	private JList<String> listUsers;

	// 获取本机地址信息
	private InetAddress localHostIP = null;
	private JButton btnSendFile;

	/**
	 * Create the frame.
	 */
	public ChatFrame() {

		// 获取到当前计算机屏幕的分辨率尺寸
		int screenWidth = getToolkit().getScreenSize().width;
		int screenHeight = getToolkit().getScreenSize().height;

		// 本窗口的尺寸
		int width = 800;
		int height = 600;

		// 窗口启动时在屏幕中显示的坐标值
		int pointX = (screenWidth - width) / 2;
		int pointY = (screenHeight - height) / 2;

		try {
			// 窗口启动时，获取本机id地址信息
			localHostIP = InetAddress.getLocalHost();
		} catch (UnknownHostException ex) {
			ex.printStackTrace();
		}

		// 设置窗体标题
		setTitle("IGeekHome\u804A\u5929\u5BA4-" + localHostIP);
		// 设置窗体的关闭事件，在关闭窗体的同时，程序结束退出
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// 设置窗体的大小及坐标位置
		setBounds(pointX, pointY, width, height);

		// 设置窗体中控件的布局方式
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel lblSendContent = new JLabel("\u804A\u5929\u5185\u5BB9\uFF1A");
		// 提示标签的大小
		lblSendContent.setBounds(10, 328, 100, 15);
		contentPane.add(lblSendContent);

		txtSendContent = new JTextField();
		// 设置输入框的大小
		txtSendContent.setBounds(110, 325, 300, 21);
		txtSendContent.setColumns(10);
		contentPane.add(txtSendContent);

		btnSend = new JButton("\u53D1\u9001\u804A\u5929\u5185\u5BB9");
		// 设置发送按钮的大小
		btnSend.setBounds(110, 360, 150, 25);
		contentPane.add(btnSend);

		scrollPaneContent = new JScrollPane();
		// 设置消息显示滚动面板的大小
		scrollPaneContent.setBounds(10, 10, 400, 307);
		contentPane.add(scrollPaneContent);

		txtReceivedContent = new JTextArea();
		// 设置消息显示框的内容自动换行
		txtReceivedContent.setLineWrap(true);
		// 设置消息显示框不可编辑
		txtReceivedContent.setEditable(false);
		// 把消息显示框加入到滚动面板中，才能有滚动功能
		scrollPaneContent.setViewportView(txtReceivedContent);

		scrollPaneUsers = new JScrollPane();
		// 设置用户列表滚动面板的大小
		scrollPaneUsers.setBounds(420, 10, 200, 336);
		contentPane.add(scrollPaneUsers);

		defaultListModel = new DefaultListModel<String>();
		listUsers = new JList<String>(defaultListModel);
		
		// 设置用户列表为单选模式
		listUsers.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		// 把用户列表加入到滚动面板中
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
			       
			       //下面开始文件的传输
			       //可以再写一个发送文件的子线程类的对象
			    }

			}
		});
		
		listUsers.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				String localHostAddress=localHostIP.getHostAddress();
				//用户列表框的点击事件，看看是否有用户被选中，选中了才能p2p发送文件    
				String selectedValue = listUsers.getSelectedValue();
				if(selectedValue!=null&&!"".equals(selectedValue)&&!localHostAddress.equals(selectedValue)){
					btnSendFile.setEnabled(true);					
				}else{
					btnSendFile.setEnabled(false);			
				}
			}
		});

		// 输入框的回车事件
		txtSendContent.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// 发送消息的操作
				sendMessage();
			}
		});

		// 注册添加按钮的点击事件，发送消息
		btnSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// 发送消息的操作
				sendMessage();
			}
		});

		// 注册窗口的关闭事件
		addWindowListener(new WindowAdapter() {
			/**
			 * @Title: windowClosing
			 * @Description: TODO(这里用一句话描述这个方法的作用)
			 * @param e
			 * @see java.awt.event.WindowAdapter#windowClosing(java.awt.event.WindowEvent)
			 */
			@Override
			public void windowClosing(WindowEvent e) {
				System.out.println("聊天程序退出！");

				String loginContent = "login:0";
				// 向局域网中发送一个退出的广播消息
				UDPUtils.send("255.255.255.255", loginContent);

				// 让监听接收消息的线程也正常接收
				ChatFrame.this.isRecevie = false;
			}

		});

		// 把本用户加入到用户列表中
		defaultListModel.addElement(localHostIP.getHostAddress());
		// 设置列表的标题框
		listUsers.setBorder(BorderFactory.createTitledBorder("在线人数：" + defaultListModel.size()));

		// 启动消息接收线程
		udpReceiveThread = new UDPReceiveThread();
		udpReceiveThread.start();

		// 启动接收文件线程
	}

	// 发送消息的操作
	private void sendMessage() {
		// 如果没有实际消息，不能发生
		String txtSendContentValue = ChatFrame.this.txtSendContent.getText();
		if (txtSendContentValue == null || "".equals(txtSendContentValue)) {
			// 弹出一个提示对话框,模式对话框
			JOptionPane.showMessageDialog(ChatFrame.this, "请输入聊天内容！");

			// 聊天输入框得到光标，以便输入聊天内容
			ChatFrame.this.txtSendContent.grabFocus();
			return;
		}

		// 发送聊天内容
		// 得到控件的文本内容
		String receiveIP = "255.255.255.255";// 相当于群发,默认广播群发
		String content = "content:" + txtSendContentValue;

		String selectIP = listUsers.getSelectedValue();
		// 本机地址
		String localHostAddress = localHostIP.getHostAddress();
		if (selectIP != null && !"".equals(selectIP) && !localHostAddress.equals(selectIP)) {
			// 代表的是私聊
			receiveIP = selectIP;
			content = "privatecontent:" + txtSendContentValue;

			String message = "私聊信息[发送给-" + receiveIP + "] : " + txtSendContentValue;

			// 同步自己把聊天内容放入到自己的消息显示框中
			// 把最新的消息内容追加到消息框中
			ChatFrame.this.txtReceivedContent.append(message + "\r\n");
		}

		// 发送方法
		UDPUtils.send(receiveIP, content);

		// 清空输入框
		ChatFrame.this.txtSendContent.setText("");
	}

	// 接收消息的线程
	public class UDPReceiveThread extends Thread {

		@Override
		public void run() {
			// 1、建立接收端
			DatagramSocket datagramSocket = null;
			try {
				datagramSocket = new DatagramSocket(UDPUtils.PORT);
				System.out.println("消息接收线程启动......");

				while (ChatFrame.this.isRecevie) {

					byte[] buf = new byte[1024];
					// 2、建立数据包，用于接收数据
					DatagramPacket datagramPacket = new DatagramPacket(buf, buf.length);

					System.out.println("等待接收。。。。。。");

					if (isFirstSend) {
						// 发送登录消息的子线程
						Thread sendLoginThread = new Thread() {

							@Override
							public void run() {
								String loginContent = "login:1";
								// 启动时向局域网中发送一个登录的广播消息
								UDPUtils.send("255.255.255.255", loginContent);
								System.out.println("已经发送登录消息");
								// 发送登录消息完毕，标记更改
								isFirstSend = false;
							}

						};
						// 启动发送登录消息的子线程
						sendLoginThread.start();
					}

					// 3、通过socket接收数据包
					datagramSocket.receive(datagramPacket);

					// 4、解析数据包
					byte[] data = datagramPacket.getData();
					int length = datagramPacket.getLength();

					// 也可以获取发送端的相关信息
					InetAddress sendAddress = datagramPacket.getAddress();

					// 把字节数据转成能够理解的字符串数据
					String content = new String(data, 0, length);
					System.out.println("接收到的数据：[" + sendAddress + "]" + content);

					// 解析消息内容
					parseContent(content, sendAddress.getHostAddress());

				}
			} catch (SocketException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (datagramSocket != null) {
					// 5、关闭socket
					datagramSocket.close();
				}
			}

			System.out.println("消息接收线程结束工作......");
		}

		/**
		 * @Title: parseContent
		 * @Description: 解析消息内容
		 * @param content
		 */
		private void parseContent(String content, String sendAddressIP) {

			String[] split = content.split(":");

			// 消息格式如果未按照指定格式发送，则认为此消息为非法消息，忽略此消息
			if (split == null || split.length != 2) {
				return;
			}

			String message = null;

			if ("login".equals(split[0])) {
				// 登录和退出消息
				if ("1".equals(split[1])) {

					if (!defaultListModel.contains(sendAddressIP)) {
						// 登录
						message = "系统信息[" + sendAddressIP + "] : 进入聊天室！";
						// 把此用户加入到用户列表中
						defaultListModel.addElement(sendAddressIP);
					}

					//接收方，接收到有人登录，则延迟1秒钟发送反馈消息，以便对方的消息监听确保启动
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					// 本机接收到有人新登录进入，则发送一个反馈消息，告诉新登录进入的用户，在之前他已经登录了。
					UDPUtils.send(sendAddressIP, "login:2");

				} else if ("2".equals(split[1])) {
					// 是已经登录的用户发送回来的反馈消息，代表以前就有用户已经登录了
					// 把以前已经登录的用加入到自己的用户列表中
					if (!defaultListModel.contains(sendAddressIP)) {
						// 把此用户加入到用户列表中
						defaultListModel.addElement(sendAddressIP);
					}

				} else if ("0".equals(split[1])) {
					// 退出
					message = "系统信息[" + sendAddressIP + "] : 退出聊天室！";

					defaultListModel.removeElement(sendAddressIP);
				}

				// 设置列表的标题框
				listUsers.setBorder(BorderFactory.createTitledBorder("在线人数：" + defaultListModel.size()));

			} else if ("content".equals(split[0])) {
				// 聊天内容消息
				message = "聊天信息[" + sendAddressIP + "] : " + split[1];
			} else if ("privatecontent".equals(split[0])) {
				// 私聊内容
				message = "私聊信息[" + sendAddressIP + "] : " + split[1];
			}

			// 有效消息才追加
			if (message != null) {
				// 把最新的消息内容追加到消息框中
				ChatFrame.this.txtReceivedContent.append(message + "\r\n");
			}

			String txtReceivedContentText = ChatFrame.this.txtReceivedContent.getText();
			// 为了控制本程序中实际显示的聊天内容不能过多，否则内存会溢出
			if (txtReceivedContentText.length() >= 1000) {
				// 超过1500个字符，则截取掉最前面的500个
				txtReceivedContentText = txtReceivedContentText.substring(txtReceivedContentText.length() - 500,
						txtReceivedContentText.length());

				// 为了截取掉的内容比较完整，把最后一条不完整的消息再次截取掉
				txtReceivedContentText = txtReceivedContentText.substring(txtReceivedContentText.indexOf("\r\n") + 2);

				ChatFrame.this.txtReceivedContent.setText(txtReceivedContentText);
			}

			// 获取垂直方向实际的最大高度
			int maximum = ChatFrame.this.scrollPaneContent.getVerticalScrollBar().getMaximum();

			// 效果就是自动把滚动条滚动到最下方
			ChatFrame.this.scrollPaneContent.getVerticalScrollBar().setValue(maximum);
		}

	}

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		// 启动主程序
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
