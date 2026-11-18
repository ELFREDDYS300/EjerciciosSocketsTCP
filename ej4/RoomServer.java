import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// Ejercicio 4: servidor con salas y broadcast.
// Crea UN solo RoomManager y lo comparte con todos los handlers.
// (Sin Shutdown Hook: eso se agrega en el ejercicio 5, ver JSMSServer.)
public class RoomServer {
    private static final int PUERTO = 8080;
    private static final ExecutorService pool = Executors.newFixedThreadPool(10);
    private static final RoomManager roomManager = new RoomManager();

    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(PUERTO);
        System.out.println("Servidor con salas listo en puerto " + PUERTO + "...");

        while (true) {
            Socket socket = server.accept();
            pool.execute(new FullClientHandler(socket, roomManager));
        }
    }
}
