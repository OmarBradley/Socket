import java.awt.Frame;
import java.awt.event.*;
import java.net.*;
import java.io.*;
import java.awt.*;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

public class ChatClient extends Frame implements Runnable {

    SocketChannel clientSocket = null;

    ByteBuffer byteBuffer = null;

    TextArea outputArea;
    TextField inputField;

    public ChatClient(String title) {
        super(title);

        setLayout(new BorderLayout());

        outputArea = new TextArea();
        outputArea.setEditable(false);

        add(outputArea, "Center");
        inputField = new TextField();
        add(inputField, "South");

        inputField.addActionListener(new InputListener());
    }

    public void addMessage(String msg) {
        outputArea.append(msg);
    }

    public void connect(String host, int port) {
        try {
            clientSocket = SocketChannel.open();
            clientSocket.configureBlocking(true);
            clientSocket.connect(new InetSocketAddress(host, port));
        } catch (Exception e) {
            System.err.println("입출력 에러입니다.");
            System.exit(1);
        }
    }

    public void disconnect() {
        try {
            clientSocket.close();
        } catch (IOException e) {
        }
    }

    public void run() {
        while (true) {
            try {
                ByteBuffer byteBuffer = ByteBuffer.allocate(100);
                clientSocket.read(byteBuffer);
                byteBuffer.flip();
                Charset charset = Charset.forName("UTF-8");
                String data = charset.decode(byteBuffer).toString() + "\n";
                addMessage(data);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public static void main(String[] args) {

        ChatClient mf = new ChatClient("자바 채팅 클라이언트");

        mf.pack();
        mf.setSize(500, 300);
        mf.setVisible(true);

        mf.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                mf.disconnect();
                System.exit(0);
            }
        });
        mf.connect("127.0.0.1", 9999);

        Thread thread = new Thread(mf);
        thread.start();
    }

    class InputListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            String input = inputField.getText();
            inputField.setText("");
            try {
                Charset charset = Charset.defaultCharset();
                byteBuffer = charset.encode(input);
                clientSocket.write(byteBuffer);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }

    }
}