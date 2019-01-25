package com.wkimdev.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutorService;

import javafx.application.Application;
import javafx.stage.Stage;

public class ServerService extends Application {
	
	ExecutorService executorService;
	ServerSocket serverSocket;
	List<Client> connections = new Vector<Client>();
	
	// 서버 시작 메서드 
	void startServer() {
		
	}
	
	// 서버 종료 메서드
	void stopServer() {
		
	}
	
	// 데이터 통신 코드 
	class Client {
		
	}
	
	// ServerSocket ==> 클라의 연결 요청을 기다리면서, 연결 수락 담당 
	// Socket	==> 연결된 클라와 통신을 담당. 
	public static void main(String[] args) throws IOException {
		
		// socket 연결 accept 코드 
		// 서버에서는 바이트 데이터를 읽어야 한다. 
		// 나중에 try구문을 나와 serversocket을 close시켜야 한다. 그래서 글로벌 변수로 선언?
		ServerSocket serverSocket = null;
		
		try {
			serverSocket = new ServerSocket();
			// 바인딩 포트 
			serverSocket.bind(new InetSocketAddress("localhost", 5001));
			while(true) {
				// 서버는 가동중이기 때문에 while구문이 들어간다!!!!!! (연결을 기다림...)
				System.out.println("[server 연결 기다림 시작]");
				Socket socket = serverSocket.accept();
				InetSocketAddress isa = (InetSocketAddress) socket.getRemoteSocketAddress();
				
				System.out.println("연결 수락 성공!");
				
				byte[] bytes = null;
				String message = null;
				
				// byte로 인풋데이터를 디코딩 한다. 
				// 클라에서 byte쏜 메세지를 string으로 변환하기ㅏ
				InputStream is = socket.getInputStream();
				// 바이트가 null 배열이기 때문에 공간을 할당해놔야 한다. 
				bytes = new byte[100];
				int readByteCount = is.read(bytes); // bytes를 받아 is의 요소를 읽는 것 같다.
				message = new String(bytes, 0, readByteCount, "UTF-8"); // 특정한 캐릭터셋으로 byte array값을 디코딩한다.
				
				System.out.println("[클라로부터 받은 데이터 :]"+message);
				
				// 보내는 부분이기 때문에 string으로 보낸..
				OutputStream os = socket.getOutputStream();
				message = "hello test from server~";
				bytes = message.getBytes("UTF-8");
				os.write(bytes);
				os.flush(); // 이 출력 스트림을 플래시 해, 버퍼에 포함 된 모든 출력 바이트를 강제적으로 출력합니다.
				// outputstream에서 자원을 close하기 위해 수행한다. 
				System.out.println("데이터 보내기 성공");
			}
		} catch (Exception e) {
		}
		
		if(!serverSocket.isClosed()) {
			try {
				serverSocket.close();
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}

	@Override
	public void start(Stage arg0) throws Exception {
		// TODO Auto-generated method stub
		
	}
}