package com.wkimdev.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ClientService {

	public static void main(String[] args) throws IOException {
		// 연결을 시도하는 코드 
		// 그리고 데이터는 바이트 스트링으로 전달.
		// 소켓에 바인딩 하기까지는 try
		// socket이 제대로 끝나지 않았을땐, closed 
		
		Socket socket = null;
		
		try {
			System.out.println("[client 연결 시도 시작!]");
			socket = new Socket(); //close를 시켜버리니까 인스턴스를 새로 만듬?
			socket.connect(new InetSocketAddress("localhost", 5001));
			System.out.println("[client 연결 성공!]");
			byte[] bytes = null;
			String message = null;
			
			// output to server
			// string to byte
			// 연결한 socket에 보내야 되기 때문에 socket에서 가져온다. 
			OutputStream os = socket.getOutputStream();
			message = "hello server~~";
			os.write(message.getBytes("UTF-8"));
			os.flush();
			System.out.println("[서버에게 메세지를 보냄]");
			
			// input from server
			// byte to string
			InputStream is = socket.getInputStream();
			bytes = new byte[100];
			int readcount = is.read(bytes);
			message = new String(bytes, 0, readcount, "UTF-8");
			System.out.println("[서버로부터 메세지를 받았습니다. ]"+message);
			
			os.close();
			is.close();
		}catch(Exception e){
		}
	}
}
