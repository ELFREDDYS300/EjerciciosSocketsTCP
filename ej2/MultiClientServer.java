import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// Ejercicio 2: servidor multihilo. El hilo principal solo acepta clientes
// y cada conexión se delega a un hilo del pool.
public class MultiClientServer {
    private static final int PUERTO = 8080;
    private static final ExecutorService pool = Executors.newFixedThreadPool(10);

    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(PUERTO);
        System.out.println("Servidor Multihilo listo en puerto " + PUERTO + "...");

        while (true) {
            Socket socket = server.accept();        // Bloqueante: espera un cliente
            pool.execute(new ClientHandler(socket)); // Asigna un hilo del pool
        }
    }
}
