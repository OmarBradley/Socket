package nonblocking.client;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.function.Consumer;


/**
 * NClient -> SocketChannel 로 이름 바꾸기
 */
public class NClient {

    private SocketChannel socketChannel;
    private String sendData;
    private Consumer<String> readMessageListener;
    private String userName;

    public NClient(SocketChannel socketChannel, String userName) {
        this.socketChannel = socketChannel;
        this.userName = userName;
    }

    public void initClient(Selector selector) throws Exception {
        socketChannel.configureBlocking(false);
        SelectionKey selectionKey = socketChannel.register(selector, SelectionKey.OP_READ);
        selectionKey.attach(this);
    }

    public void receive() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.allocate(100);
        int byteCount = socketChannel.read(byteBuffer);
        if (byteCount == -1) {
            throw new IOException();
        }
        byteBuffer.flip();

        String data = createUtf8Charset().decode(byteBuffer).toString();
        readMessageListener.accept(userName + " : " + data);
        System.out.println("read");
    }

    public void send(SelectionKey selectionKey) throws Exception {
        System.out.println("send");
        ByteBuffer byteBuffer = createUtf8Charset().encode(sendData);
        socketChannel.write(byteBuffer);
        selectionKey.interestOps(SelectionKey.OP_READ);
    }

    public void closeSocketChannel() throws Exception {
        socketChannel.close();
    }

    public SelectionKey getSelectionKey(Selector selector) {
        return socketChannel.keyFor(selector);
    }

    public void setSendData(String sendData) {
        this.sendData = sendData;
    }

    public void setReadMessageListener(Consumer<String> readMessageListener) {
        this.readMessageListener = readMessageListener;
    }

    public String getUserName() {
        return userName;
    }

    private Charset createUtf8Charset() {
        return Charset.forName("UTF-8");
    }

}
