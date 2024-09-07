import java.net.*;
import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
 
public class server{
    public static void main (String[] args){
 
    ServerSocket server;
    Socket connection;
 
    //DataOutputStream output;
    BufferedInputStream bis;
    BufferedOutputStream bos;
 
    byte[] receivedData;
    int in;
    String file;
 
    try{
        //Servidor Socket en el puerto 5000
        server = new ServerSocket( 5000 );
        while ( true ) {
            //Aceptar conexiones
            connection = server.accept();
            //Buffer de 1024 bytes
            receivedData = new byte[6500];
            bis = new BufferedInputStream(connection.getInputStream());
            DataInputStream dis=new DataInputStream(connection.getInputStream());
            //Recibimos el nombre del fichero
            file = dis.readUTF();
            file = file.substring(file.indexOf('\\')+1,file.length());

            System.out.println(file);
            //Para guardar fichero recibido
            bos = new BufferedOutputStream(new FileOutputStream(file));
            while ((in = bis.read(receivedData)) != -1){
                //System.out.println(in);
                bos.write(receivedData,0,in);
            }  

            bos.close();
            dis.close();


            Path origenPath = FileSystems.getDefault().getPath("C:\\Users\\raymu\\OneDrive\\Escritorio\\DatagramaECO (1)\\DatagramaECO\\" + file);
            Path destinoPath = FileSystems.getDefault().getPath("C:\\Users\\raymu\\Downloads\\"+file);

            try {
                Files.move(origenPath, destinoPath, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                System.err.println(e);
            }
        
         
        }
     }catch (Exception e ) {
        System.err.println(e);
     }
   }
}


