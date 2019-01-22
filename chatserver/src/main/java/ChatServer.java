import java.io.*;
import java.net.*;
import java.nio.channels.SelectionKey;
import java.util.*;

public class ChatServer {

    Vector<ServiceThread> Clients;

    public ChatServer() {

        Clients = new Vector<>();
    }

    public void addClient(ServiceThread st) {   // 클라이언트가 접속하면 추가

        Clients.add(st);
    }

    public void removeClient(ServiceThread st) {   // 클라이언트가 종료하면 삭제

        Clients.remove(st);
    }

    public void sendMessageAll(String msg) {   // 모든 클라이언트에게 받은 메시지를 출력

        try {

            for (ServiceThread st : Clients) {
                st.sendMessage(msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        ChatServer server;
        ServerSocket serverSocket = null;
        int port = 9999;
        server = new ChatServer();   // 클라이언트를 관리하는 객체(추가, 삭제, 메시지 전달)
        try {
            serverSocket = new ServerSocket(port);   // 나 서버야.

        } catch (Exception e) {
            System.err.println("연결 실패입니다.");
            System.exit(1);
        }
        System.out.println("서버\n" + serverSocket + "\n에서 연결을 기다립니다.");

        try {
            while (true) {



                Socket serviceSocket = serverSocket.accept();   // 클라이언트 접속
                // 또 다른 클라이언트가 접속할 때 까지 대기함 -> blocking 된다

                // 클라이언트 관리하는 객체, 클라이언트를 가진 객체를 생성한 후 Thread를 통해서
                // 읽고 쓰는 작업을 Run 메서드를 명시한 후 실행.

                ServiceThread thread = new ServiceThread(server, serviceSocket);
                thread.start();
                server.addClient(thread);
            }
        } catch (Exception e) {
            try {
                serverSocket.close(); // 서버종료
            } catch (IOException ie) {
                ie.printStackTrace();
            }
        }
    }
}