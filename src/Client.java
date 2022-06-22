import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;

class Client implements Runnable {
    private final Socket socket;

    private Scanner in;
    private PrintStream out;
    private final ChatServer server;
    private final Thread thread = new Thread(this);
    private String name = null;


    public Client(Socket socket, ChatServer server) {

        this.socket = socket;
        this.server = server;
        // запускаем поток
        this.thread.start();
    }

    void receive(String message) {
        out.println(message);
    }

    public Thread getThread() {
        return thread;
    }

    public String getName() {
        return name;
    }

    public void run() {
        try {
            // запускаем scanner отдельным потоком
            // необходимо для отключения клиента администратором
            threadForScanner.start();
            while (!thread.isInterrupted()) ;
            if (name != null) server.sendAll(name + " left chat");
            server.getClientsNames().remove(name);
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    Thread threadForScanner = new Thread() {
        public void run() {
            try {
                // получаем потоки ввода и вывода
                InputStream is = socket.getInputStream();
                OutputStream os = socket.getOutputStream();

                // создаем удобные средства ввода и вывода
                in = new Scanner(is);
                out = new PrintStream(os);

                // читаем из сети и пишем в сеть
                out.println("Welcome to chat!");
                out.println("Enter your name");
                // проверяет, свободно ли имя килента
                while (server.getClientsNames().contains(name = in.nextLine())) {
                    out.println("This name is busy");
                    out.println("Enter your name");
                }
                server.getClientsNames().add(name);
                String input = name + " join chat!";
                while (!input.equals(name + ": " + "bye")) {
                    server.sendAll(input);
                    try {
                        input = name + ": " + in.nextLine();
                    } catch (Exception e) {
                        break;
                    }
                }
                // завершаем работу клиента
                thread.interrupt();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NoSuchElementException e) {} // ошибка scanner при закрытии окна клиента
        }
    };
}