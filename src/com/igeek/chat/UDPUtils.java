package com.igeek.chat;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * @ClassName: UDPUtils
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @date 2018年7月27日 上午10:57:01 Company www.igeekhome.com
 * 
 */
public class UDPUtils {
	
	//本软件的通讯端口
	public final static int PORT=54321;

	public static void send(String receiveIP,String content) {
		// 1、建立发送端，空参构造；
		DatagramSocket datagramSocket=null;
		try {
			datagramSocket = new DatagramSocket();
			// 2、发送的数据
			byte[] buf = content.getBytes();

			// 是接收方的ip地址
			InetAddress address = InetAddress.getByName(receiveIP);
			// 是接收方的端口

			// 3、建立数据包
			DatagramPacket datagramPacket = new DatagramPacket(buf, 0, buf.length, address, PORT);

			// 4、通过socket发送数据包
			datagramSocket.send(datagramPacket);
			
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			if(datagramSocket!=null){
				// 5、关闭socket
				datagramSocket.close();
			}
		}

	}

}
