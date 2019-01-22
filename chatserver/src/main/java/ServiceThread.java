import java.io.*;
import java.net.*;

public class ServiceThread extends Thread {

    private ChatServer server;
    private Socket socket;
    String UserName;
    PrintWriter out;
    BufferedReader in;

    public ServiceThread(ChatServer server, Socket socket) {

        this.server = server;
        this.socket = socket;
    }

    public void sendMessage(String msg) throws IOException {

        if (out != null) {
            out.println(msg);
        }
    }

    @Override
    public void run() {

        try {
            System.out.println("클라이언트\n" + socket + "\n에서 접속하였습니다.");

            // 메시지 입출력 객체를 소켓에서 받아온다.
            out = new PrintWriter(socket.getOutputStream(), true);   // 소켓에다 써준다
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));   // 소켓에서 읽는다

            // 줄바꿈
            out.println();
            out.println("UserName : ");
            UserName = in.readLine();
            sendMessage("♡" + UserName);
            server.sendMessageAll("# " + UserName + " 님이 들어오셨습니다.");
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                server.sendMessageAll("[" + UserName + "]" + inputLine);
            }
            out.close();
            in.close();
            socket.close();

        } catch (IOException e) {
            server.removeClient(this);
            server.sendMessageAll("# " + UserName + " 님이 나가셨습니다.");
            System.out.println("클라이언트\n" + socket + "\n에서 접속이 끊겼습니다...");
        }
    }
}