import java.net.*;
import java.io.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class cliente {

    public static void main(String[] args) {
  
        ArrayList<String> canciones = new ArrayList<String>();

        Scanner resp_cancion = new Scanner(System.in);
        Scanner compra = new Scanner(System.in);
        Scanner eliminar = new Scanner(System.in);
        Scanner eliminarCancion = new Scanner(System.in);

        int resp = 0, opc = 0, com = 0, borrar = 0;
        boolean x = true;
                
        while (x){
            opc = menu();
            

            switch (opc) {
                case 1:

                    do {

                        String cancion = Eleccion_cancion();
                        canciones.add(cancion);

                        System.out.println("\nDeseas agregar otra cancion? \n1.-SI \n2.-NO");
                        resp = resp_cancion.nextInt();
                        
                        System.out.println("\n\n");

                    } while (resp != 2);

                    break;

                case 2:

                    while (true){
                        imprimir(canciones);

                        System.out.println("Deseas eliminar una cancion de tu carrito?");
                        System.out.println("1.-Si \n2.-No");
                        borrar = eliminar.nextInt();

                        if(borrar == 1){
                            System.out.println("Selecciona la cancion a eliminar");
                            imprimir(canciones);
                            borrar = eliminarCancion.nextInt();
                            removeCancion(canciones, borrar);
                            System.out.println("\n\n");

                        }else if(borrar > 2 || borrar < 1){
                            System.out.println("Por favor seleccione una opcion valida");
                            System.out.println("\n\n");
                        }

                        System.out.println("Deseas finalizar tu compra");
                        System.out.println("1.-Si \n2.-No");
                        com = compra.nextInt();

                        if(com == 1){
                            
                            enviarMusica(canciones);
                            
                        }else if(com == 2 ){
                            break;

                        }else if(com > 2 || com < 1){
                            System.out.println("Por favor seleccione una opcion valida");
                            System.out.println("\n\n");

                        }

                    }

                    break;

                case 3:
                    
                enviarMusica(canciones);

                break;

                case 4:
                    
                    canciones.clear();
                    x = false;
                    
                    break;
                default:
                    System.out.println("Por favor seleccione una opcion valida");
                    System.out.println("\n\n");

            }
            
            System.out.println("\n\n\n");
        }

        

    }

    static String Eleccion_cancion() {

        int i = 1;
        int name_song = 0;

        File directorio = new File("C:\\Users\\raymu\\Music\\prueba\\"); //directorio a listar                                             
        String[] lista = directorio.list();
        Arrays.sort(lista);

        Scanner name_cancion = new Scanner(System.in);

        System.out.println("Estas son las cacniones disponibles: \n");

        for (String lista1 : lista) {
            System.out.println(i + ".- " + lista1);
            i++;
        }

        System.out.println("\nSeleccione el numero de la cancion a descargar?");

        name_song = name_cancion.nextInt();

        String cancion = lista[name_song - 1];

        return cancion;

    }

    static int menu() {

        System.out.println("*****Bienvenido a su tienda de musica*****");
        System.out.println("Por favor seleccione una opcion");
        System.out.println("1.-Canciones \n2.-Carrito \n3.-Finalizar Compra \n4.-Salir");

        Scanner opcion = new Scanner(System.in);

        int opc = opcion.nextInt();
        
        System.out.println("\n\n");

        return opc;

    }

    static void imprimir(ArrayList list) {

        int i = 0, j = 1;
        
        if (list.size() == 0) {
            System.out.println("Aun no hay canciones elegidas");
        } else {
            
            for (i = 0, j =1; i < list.size(); i++, j++) {
                System.out.println(j + ".- " + list.get(i));
                
            }
        }
    }
    
    static void removeCancion(ArrayList list, int num){
        list.remove(num - 1);
    }
    
    static void enviarMusica(ArrayList canciones){
        
        BufferedInputStream bis;
        BufferedOutputStream bos;
        byte[] byteArray;
        int i, in;
        
        try {

            for (i = 0; i < canciones.size(); i++) {

                //Fichero a transferir
                final String filename = "C:\\Users\\raymu\\Music\\prueba\\" + canciones.get(i);
                final File localFile = new File(filename);

                Socket client = new Socket("localhost", 5000);

                bis = new BufferedInputStream(new FileInputStream(localFile));
                bos = new BufferedOutputStream(client.getOutputStream());

                //Enviamos el nombre del fichero
                DataOutputStream dos = new DataOutputStream(client.getOutputStream());
                dos.writeUTF(localFile.getName());

                //Enviamos el fichero
                byteArray = new byte[8192];

                while ((in = bis.read(byteArray)) != -1) {
                    bos.write(byteArray, 0, in);
                }

                bis.close();
                bos.close();
            }
            
            canciones.clear();

        } catch (Exception e) {
            System.err.println(e);
        }
    }

}
