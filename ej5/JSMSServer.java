import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// Ejercicio 5: servidor completo del chat (JSMS).
// Pool de hilos + RoomManager compartido + Shutdown Hook.
public class JSMSServer {
    private static final int PUERTO = 8080;
    private static final ExecutorService pool = Executors.newFixedThreadPool(10);
    private static final RoomManager roomManager = new RoomManager(); // UNO solo, compartido

    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(PUERTO);
        System.out.println("Servidor JSMS listo en puerto " + PUERTO + "...");

        // Shutdown Hook: la JVM lo ejecuta justo antes de cerrarse (Ctrl+C)
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Apagando servidor...");
            roomManager.notificarATodos("SERVER: el servidor se esta apagando");
            pool.shutdown();
            try {
                server.close(); // hace que accept() lance SocketException
            } catch (IOException ignored) {
            }
        }));

        try {
            while (true) {
                Socket socket = server.accept(); // Bloqueante
                pool.execute(new FullClientHandler(socket, roomManager));
            }
        } catch (SocketException e) {
            // El ServerSocket se cerro desde el Shutdown Hook: salida normal
        }
    }
}
