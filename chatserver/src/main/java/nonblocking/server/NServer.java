package nonblocking.server;

import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;

public class NServer {

    private ServerSocketChannel serverSocket;
    private NSelectorManager selectorManager;

    public NServer() {
        selectorManager = new NSelectorManager();
    }

    public void initChannel() throws Exception {
        selectorManager.openSelector();
        serverSocket = ServerSocketChannel.open();
        serverSocket.configureBlocking(false);
        serverSocket.bind(new InetSocketAddress(9999));
        serverSocket.register(selectorManager.getSelector(), SelectionKey.OP_ACCEPT);
    }

    public void runServer() throws Exception {
        while (true) {
            if (selectorManager.isNotConnectClient()) {
                continue;
            }
            selectorManager.processChannelJob();
        }
    }

    public void stopServer() throws Exception {
        selectorManager.closeAllClients();

        if (serverSocket != null && serverSocket.isOpen()) {
            serverSocket.close();
        }

        selectorManager.closeSelector();
    }
}
