import java.util.Scanner;

public class ServerAdmin implements Runnable {
    private final ChatServer server;

    public ServerAdmin(ChatServer chatServer) {
        this.server = chatServer;
        new Thread(this).start();
    }

    public void run() {
        while (true) {
            Scanner scanner = new Scanner(System.in);
            String clientName;
            // введя в консоль ChatServer имя клиента, он будет отключен
            if (server.getClientsNames().contains(clientName = scanner.nextLine())) {
                for (Client client : server.getClients()) {
                    if (client.getName() != null && client.getName().equals(clientName))
                        client.getThread().interrupt();
                }
            }
        }
    }
}
