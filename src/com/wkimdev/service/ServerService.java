package com.wkimdev.service;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class ServerService extends Application {
	
	ExecutorService executorService;
	ServerSocket serverSocket; // 클라이언트 연결 수락 
	List<Client> connections = new Vector<Client>(); // 연결된 클라이언트를 저장, 스레드 초기화 
	
	// 서버 시작 메서드 
	void startServer() {
		executorService = Executors.newFixedThreadPool(
				Runtime.getRuntime().availableProcessors()
		);
		
		try {
			serverSocket = new ServerSocket();
			serverSocket.bind(new InetSocketAddress("localhost", 5001));
		} catch(Exception e){
			if(!serverSocket.isClosed()) {
				stopServer();
			}
			return;
		}
		
		
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				Platform.runLater(()->{
					displayText("[서버 시작]");
					btnStartStop.setText("stop");
				});
				while(true) {
					try {
						Socket socket = serverSocket.accept();
						String message = "[연결수락: " + socket.getRemoteSocketAddress()  + ": " + Thread.currentThread().getName() + "]";
						Platform.runLater(()->displayText(message));
						
						Client client = new Client(socket);
						connections.add(client);
						Platform.runLater(()->displayText("[client 객체 수 : " + connections.size() + "]"));
					} catch (Exception e) {
						if(!serverSocket.isClosed()) {
							stopServer();
						}
						break;
					}
				}
			}
		};
		executorService.submit(runnable);	// 작업스레드의 연결 작업 처리 
	}
	
	
	/*
	 * 서버 종료 코드 
	 * 연결된 모든 socket 닫기
	 * serversocket닫기
	 * executorService종료  
	 */
	void stopServer() {
		try {
			// 반복된 요소가 있는 경우 true를 리턴한다. 
			Iterator<Client> iterator = connections.iterator();
			// 모든 socket를 닫는다. 
			while(iterator.hasNext()) {
				Client client = iterator.next();
				client.socket.close();
				iterator.remove();
			}
			
			if(serverSocket!=null && !serverSocket.isClosed()) {
				serverSocket.close();
			}
			if(executorService!=null && !executorService.isShutdown()) {
				executorService.shutdown();
			}
			Platform.runLater(()->{
				displayText("[서버 멈춤]");
				btnStartStop.setText("start");
			});
		} catch(Exception e) {
		}
	}
	
	// 데이터 통신 코드, 연결된 클라이언트를 표현  
	class Client {
		Socket socket;
		
		Client(Socket socket){
			this.socket = socket;
			receive();
		}
		
		// 클라이언트 데이터를 받는 receive코드 
		void receive() {
			Runnable runnable = new Runnable() {
				@Override
				public void run() {
					try {
						while(true) {
							byte[] bytes = new byte[100];
							InputStream io = socket.getInputStream();
							
							int readByteCount = io.read(bytes);
							
							// 클라이언트 비정상적 종료, IOException 발생  
							if(readByteCount == -1) {
								throw new IOException();
							}
							
							String message = "[요청 처리: " + socket.getRemoteSocketAddress() + ": " + Thread.currentThread().getName() + "]";
							Platform.runLater(()->displayText(message));
							
							// 요청을 받고 서버를 닫지 않으면 블로킹됨.. 
							//풀이 두개로 찍힌느데. 
							
							String data = new String(bytes, 0, readByteCount, "UTF-8");
							
							// 모든 클라리언트 들에게 data 보냄
							for(Client client : connections) {
								client.send(data); 
							}
						}
					} catch (Exception e) {
						// 클라이언트에서 통신이 안될 경우 처리 
						try {
							connections.remove(Client.this);
							String message = "[클라이언트 통신 안됨: " + socket.getRemoteSocketAddress() + ": " + Thread.currentThread().getName() + "]";
							Platform.runLater(()->displayText(message));
							socket.close();
						} catch (Exception e2) {
						}
					}
				}
			};
			executorService.submit(runnable);
		}
		
		// 클라이언트로 메세지를 보내는 send 코드 
		void send(String data) {
			Runnable runnable = new Runnable() {
				@Override
				public void run() {
					try {
						byte[] bytes = data.getBytes("UTF-8");
						OutputStream os = socket.getOutputStream();
						os.write(bytes);
						os.flush();
					} catch (Exception e) {
						// 클라이언트에서 통신이 안될 경우 처리 
						try {
							String message = "[클라이언트 통신 안됨: " + socket.getRemoteSocketAddress() + ": " + Thread.currentThread().getName() + "]";
							Platform.runLater(()->displayText(message));
							connections.remove(Client.this);
							socket.close();
						} catch (Exception e2) {
						}
					}
				}
			};
			executorService.submit(runnable);
		}
	}
	
	/////UI코드/////////////////////////////////////////////////
	TextArea txtDisplay;
	Button btnStartStop;

	@Override
	public void start(Stage primaryStage) throws Exception {
		BorderPane root = new BorderPane();
		root.setPrefSize(500, 300);
		
		txtDisplay = new TextArea();
		txtDisplay.setEditable(false);
		BorderPane.setMargin(txtDisplay, new Insets(0,0,2,0));
		root.setCenter(txtDisplay);
		
		btnStartStop = new Button("start");
		btnStartStop.setPrefHeight(30);
		btnStartStop.setMaxWidth(Double.MAX_VALUE);
		btnStartStop.setOnAction(e->{
			if(btnStartStop.getText().equals("start")) {
				startServer();
			} else if(btnStartStop.getText().equals("stop")){
				stopServer();
			}
		});
		root.setBottom(btnStartStop);
		
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("app.css").toString());
		primaryStage.setScene(scene);
		primaryStage.setTitle("Server");
		primaryStage.setOnCloseRequest(event->stopServer());
		primaryStage.show();
	}
	
	void displayText(String text) {
		txtDisplay.appendText(text + "\n");
	}	
	
	public static void main(String[] args) {
		launch(args);
	}
}