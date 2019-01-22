package nonblocking.server;

import nonblocking.client.NClient;

import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

public class NSelectorManager {

    private Selector selector;
    private List<NClient> clients = new Vector<>();

    void openSelector() throws Exception {
        selector = Selector.open();
    }

    boolean isNotConnectClient() throws Exception {
        return selector.select() == 0;
    }

    Selector getSelector() {
        return selector;
    }

    void processChannelJob() throws Exception {
        Set<SelectionKey> selectedKeys = selector.selectedKeys();
        Iterator<SelectionKey> iterator = selectedKeys.iterator();
        while (iterator.hasNext()) {
            SelectionKey selectionKey = iterator.next();
            if (selectionKey.isAcceptable()) {
                NClient client = registerClient(selectionKey);
                sendDataToAllClients(client.getUserName() + "유저 입장");
            } else if (selectionKey.isReadable()) {
                NClient client = (NClient) selectionKey.attachment();
                try {
                    client.setReadMessageListener(this::sendDataToAllClients);
                    client.receive();
                    selector.wakeup();
                } catch (Exception e) {
                    clients.remove(client);
                    client.closeSocketChannel();
                }
            } else if (selectionKey.isWritable()) {
                NClient client = (NClient) selectionKey.attachment();
                try {
                    client.send(selectionKey);
                    selector.wakeup();
                } catch (Exception e) {
                    clients.remove(client);
                    client.closeSocketChannel();
                }
            }
            iterator.remove();
        }
    }

    void closeAllClients() throws Exception {
        Iterator<NClient> iterator = clients.iterator();
        while (iterator.hasNext()) {
            NClient client = iterator.next();
            client.closeSocketChannel();
            iterator.remove();
        }
    }

    void closeSelector() throws Exception {
        if (selector != null && selector.isOpen()) {
            selector.close();
        }
    }

    private NClient registerClient(SelectionKey selectionKey) throws Exception {
        ServerSocketChannel serverSocket = (ServerSocketChannel) selectionKey.channel();
        SocketChannel clientChannel = serverSocket.accept();
        NClient client = new NClient(clientChannel, "UserName [" + clients.size() + "] ");
        client.initClient(selector);
        clients.add(client);
        return client;
    }

    private void sendDataToAllClients(String sendData) {
        for (NClient client : clients) {
            client.setSendData(sendData);
            SelectionKey key = client.getSelectionKey(selector);
            key.interestOps(SelectionKey.OP_WRITE);
        }
    }

}
