import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;

public class ChatServer {

    private final ArrayList<Client> clients = new ArrayList<>();
    private final ServerSocket serverSocket;
    private final Set<String> clientsNames = new HashSet<>();

    public ChatServer() throws IOException {
        // создаем серверный сокет на порту 1234
        serverSocket = new ServerSocket(1234);
    }

    void sendAll(String message) {
        // отправляем сообщение всем клиентам
        for (Client client : clients) {
            client.receive(message);
        }
    }

    public ArrayList<Client> getClients() {
        return clients;
    }

    public void run() {
        new ServerAdmin(this);
        while (true) {
            System.out.println("Waiting...");

            try {
                // ждем клиента из сети
                Socket socket = serverSocket.accept();
                System.out.println("Client connected!");
                // добавляем клиента на своей стороне в список клиентов
                clients.add(new Client(socket, this));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Set<String> getClientsNames() {
        return clientsNames;
    }

    public static void main(String[] args) throws IOException {
        ChatServer chatServer = new ChatServer();
        chatServer.run();
    }
}
