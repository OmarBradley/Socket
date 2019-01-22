package nonblocking;

import nonblocking.server.NServer;

public class NServerRunner {

    public static void main(String[] args) {
        NServer server = new NServer();
        try {
            server.initChannel();
        } catch (Exception e) {
            try {
                server.stopServer();
            } catch (Exception ignore) {
                ignore.printStackTrace();
            }
            return;
        }

        Thread serverThread = new Thread(() -> {
            try {
                server.runServer();
            } catch (Exception e) {
                try {
                    server.stopServer();
                } catch (Exception ignore) {
                    ignore.printStackTrace();
                }
            }
        });
        serverThread.start();
    }
}