import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServidorWeb
{
    

    public static int puerto = 5500;
    public static void main(String[] args) throws IOException, Exception{
        ExecutorService pool = Executors.newFixedThreadPool(3);
        ServerSocket server = new ServerSocket(puerto);
        System.out.println("Escuchando por el puerto "+puerto);
        for(;;){
            try{
                Socket cliente = server.accept();
                SW  analisis = new SW(cliente);
                pool.execute(analisis);
            }catch(Exception e){
                System.err.println("Error en el pool: "+e);
            }
        }
    }    
}
    

