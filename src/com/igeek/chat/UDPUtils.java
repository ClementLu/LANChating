package com.igeek.chat;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * @ClassName: UDPUtils
 * @Description: TODO(������һ�仰��������������)
 * @date 2018��7��27�� ����10:57:01 Company www.igeekhome.com
 * 
 */
public class UDPUtils {
	
	//�������ͨѶ�˿�
	public final static int PORT=54321;

	public static void send(String receiveIP,String content) {
		// 1���������Ͷˣ��ղι��죻
		DatagramSocket datagramSocket=null;
		try {
			datagramSocket = new DatagramSocket();
			// 2�����͵�����
			byte[] buf = content.getBytes();

			// �ǽ��շ���ip��ַ
			InetAddress address = InetAddress.getByName(receiveIP);
			// �ǽ��շ��Ķ˿�

			// 3���������ݰ�
			DatagramPacket datagramPacket = new DatagramPacket(buf, 0, buf.length, address, PORT);

			// 4��ͨ��socket�������ݰ�
			datagramSocket.send(datagramPacket);
			
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			if(datagramSocket!=null){
				// 5���ر�socket
				datagramSocket.close();
			}
		}

	}

}
