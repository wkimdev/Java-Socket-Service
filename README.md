## java socket service
- tcp network, Serversocket, Socket, io package(DataInputStream, DataOutputStream), multi thread programming  
- network학습 더
- 발전 방향 node.js socket 채팅창 (웹채팅프로그래밍)

#### 실행
 
- client server는 내 cpu코어수만큼 띄운다.  
	* 스레드수를 제한해 놓았기 때문에 코어수 이상으로 클라이언트 어플을 띄우면 요청처리가 블로킹되서 처리 안된다.    
	
	
```
executorService = Executors.newFixedThreadPool(
				Runtime.getRuntime().availableProcessors()
);		
```

