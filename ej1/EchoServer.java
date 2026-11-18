import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

// Ejercicio 1: servidor de eco mono-hilo (atiende a UN cliente)
public class EchoServer {
    public static void main(String[] args) throws IOException {
        int puerto = 8080;

        try (ServerSocket serverSocket = new ServerSocket(puerto)) {
            System.out.println("Servidor iniciado en puerto " + puerto);

            Socket clientSocket = serverSocket.accept(); // Bloqueante: espera un cliente
            System.out.println("Cliente conectado: " + clientSocket.getRemoteSocketAddress());

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true); // autoflush

            String mensaje;
            while ((mensaje = in.readLine()) != null) { // null = el cliente cerró la conexión
                if ("CHAO".equalsIgnoreCase(mensaje)) break;
                out.println("ECO: " + mensaje);
            }

            clientSocket.close();
            System.out.println("Cliente desconectado");
        }
    }
}
