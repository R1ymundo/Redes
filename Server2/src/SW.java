/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.*;
import java.net.Socket;
import java.util.*;

public class SW extends Thread{
    public Socket cliente;
    public PrintWriter pw;
    public BufferedOutputStream bos;
    public BufferedReader br;
    public File archivo;    
    public String FileName;
    public DataOutputStream dataOutputStream;
    public SW(Socket client) throws Exception {
        this.cliente = client;
    }
    @Override
    public void run(){
        try{
            br = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
            dataOutputStream= new DataOutputStream(cliente.getOutputStream());
            bos = new BufferedOutputStream(cliente.getOutputStream());
            pw = new PrintWriter(new OutputStreamWriter(bos));            
            String line = br.readLine();
            
           
            if(line == null){
                                        pw.print("<html><head><title>Servidor web ");
	  				pw.print("</title> <BODY bgcolor=\"#AACCFF\"> <br>Linea vacia</br>");
	  				pw.print("</BODY></html>");
                                        pw.flush();
	  				cliente.close();
	  				return;
            } 
            System.out.println("-----------------------------");
            System.out.println("Cliente conectado desde: "+cliente.getInetAddress()+"\nPor el puerto: "+cliente.getPort());
            System.out.println("Datos: "+line);
            String peticion=line;
            StringTokenizer st = new StringTokenizer(line);
            line = st.nextToken();
            switch(line){
                case "GET":
                    String mGet = st.nextToken();
                    if(mGet.equals("/") && st.nextToken().equals("HTTP/1.1")){
                        enviarRecurso("index.htm");
                    }else
                        if(mGet.startsWith("/") && mGet.contains("?")){
                            String parametros = "";
                            st = new StringTokenizer(mGet, "?");
                            mGet = st.nextToken();
                            if(st.hasMoreTokens()){
                                parametros = parametros + st.nextToken("?");
                            }
                            imprimirResultado(mGet, parametros);
                        }else{
                            st = new StringTokenizer(mGet);
                            mGet = st.nextToken();
                            enviarRecurso(mGet);
                        }
                    break;
                case "POST":
                    String archivo = st.nextToken();
                    String linea;
                    int pd = -1;
                    while((linea = br.readLine()) != null && (linea.length() != 0)){
                        if(linea.contains("Content-Length:")){
                            pd = Integer.parseInt(linea.substring(linea.indexOf("Content-Length:") + 16, linea.length()));
                        }
                    }
                    String parametros = "";
                    if(pd > 0){
                        char[] arreglo = new char[pd];
                        br.read(arreglo, 0, pd);
                        parametros = new String(arreglo);
                    }
                    imprimirResultado(archivo, parametros);
                    break;
                case "HEAD":
                    String nombre = st.nextToken();
                    try{
                        File file = new File("C:\\Users\\aldos\\Downloads\\Server\\Server2\\"+nombre);
                        if(file.exists()){
                            String extension = "";
                            if (file.getName().lastIndexOf(".") != -1 && file.getName().lastIndexOf(".") != 0) {
                                extension = file.getName().substring(file.getName().lastIndexOf(".") + 1);
                            }
                            
                            String msj = "HTTP/1.1 200 ok\n";
                            msj += "Server: C-C\n";
                            msj += "Date: " + new Date() + "\n";
                            switch(extension){
                                case "html": msj += "Content-Type: text/html \n";
                                    break;
                                case "jpg": msj += "Content-Type: image/jpeg \n";
                                case "jpeg":
                                    break;
                                case "pdf": msj += "Content-Type: application/pdf \n";
                                    break;
                            }
                            msj += "\n";
                            System.out.print(msj);
                            dataOutputStream.write(msj.getBytes());
                            dataOutputStream.flush();
                            dataOutputStream.close();
                        }else{
                            System.err.println("Archivo " + nombre + " no encontrado");
                            mensajeBorrar("404");
                            String mensaje = "HTTP/1.1 ";
                            mensaje += "Server: C-C\n";
            mensaje += "Date: " + new Date() + "\n";
            mensaje += "Content-Type: text/html \n";
            mensaje += "\n";
            bos.write(mensaje.getBytes());
            bos.flush();
                        }
                    }catch(IOException e){
                        System.err.println("Error en HEAD");
                    }
                    break;
                case "DELETE":
                    String l = st.nextToken();
                    if(l.equals("/") && st.nextToken().equals("HTTP/1.1")){
                        enviarRecurso("Inicio.html");
                    }else{
                        st = new StringTokenizer(l);
                        l = st.nextToken();
                        File file = new File("C:\\Users\\aldos\\Downloads\\Server\\Server2\\"+l);
                        if(file.exists()){
                            if(file.delete()){
                                System.out.println("Archivo "+l+" borrado");
                                 
                                 String sb = "";
                                sb = sb + "HTTP/1.1 200 ok\n";
                                sb = sb + "Server: C-cn \n";
                                sb = sb + "Date: " + new Date() + " \n";
                                sb = sb + "Content-Type: text/html \n";
                                sb = sb + "\n";
                                System.out.print(sb);
                                dataOutputStream.write(sb.getBytes());
                                dataOutputStream.flush();
                                
                                paginaBorrado("Archivo Eliminado", "El archivo " + l + " ha sido eliminado");}
                            else{ 
                                String sb = "";
                                sb = sb + "HTTP/1.1 500 internal Server Error \n";
                                sb = sb + "Server: C-cn \n";
                                sb = sb + "Date: " + new Date() + " \n";
                                sb = sb + "Content-Type: text/html \n";
                                sb = sb + "\n";
                                bos.write(sb.getBytes());
                                System.out.print(sb);
                                System.out.println("Archivo "+l+" no pudo ser borrado");
                                mensajeBorrar("500");
                                paginaBorrado("500 Internal Server Error", "Error del servidor");
                            }
                        }else{
                            System.out.println("Archivo no encontrado");
                             String sb = "";
                                sb = sb + "HTTP/1.1 404 Not Found\n";
                                sb = sb + "Server: C-cn \n";
                                sb = sb + "Date: " + new Date() + " \n";
                                sb = sb + "Content-Type: text/html \n";
                                sb = sb + "\n";
                                System.out.print(sb);
                                bos.write(sb.getBytes());
                            mensajeBorrar("404");
                            paginaBorrado("404 Not Found", "El archivo " + l + " no se encuentra en el servidor");
                        }
                    }                    
                    break;
                case "PUT":
                    System.out.println("PUT");
                    String linea2;
                    linea2 = peticion;

                     int aux=linea2.indexOf("Content-Type");
                     aux=linea2.indexOf("\n",aux);
                     aux=linea2.indexOf("\n",aux);
                     String stb=linea2.substring(aux+1,linea2.length()-1);
                     
                     getFileName(linea2);
                     DataOutputStream ar=new DataOutputStream(new FileOutputStream(FileName));
                     DataInputStream ar2=new DataInputStream(new ByteArrayInputStream(stb.getBytes()));
                     int aux2=0;
                     byte []bm=new byte[1024];
                     while((aux2=ar2.read(bm))!= -1){
                         ar.write(bm,0,aux2);
                         ar.flush();
                     }
                     StringBuffer respuesta=new StringBuffer();
                     respuesta.append("HTTP/1.0 201 Created \n");
                     String fecha="Date: "+new Date()+"\n";
                     respuesta.append(fecha);
                     String tipom="Content-Type:text/html \n\n";
                     respuesta.append(tipom);
                     respuesta.append("<html><head><title>SERVIDOR WEB</title></head>\n");
                     respuesta.append("<body bgcolor=\"#AACCFF\"><center><h1><br>Archivo Creado</br></h1>");
                     respuesta.append("</center></body></htlm>\n\n");
                     ar.close();
                     ar2.close();
                     System.out.print(respuesta);
                     bos.write(respuesta.toString().getBytes());
                     bos.flush();
                    break;
                default: mensajeBorrar("501");
                    break;
            }
        }catch(IOException e){
            System.err.println("Error en el hilo: "+e);
        }
        pw.close();
    }
    public void enviarRecurso(String nombre){
        try{
            archivo = new File("C:\\Users\\aldos\\Downloads\\Server\\Server2\\"+nombre);
            if(archivo.exists()){
                System.out.println("Archivo: "+nombre);
                int b_leidos = 0;
                BufferedInputStream bis = new BufferedInputStream(new FileInputStream(archivo));
                byte[] buf = new byte[1024];
                int tam_bloque = 0;
                if (bis.available() >= 1024) {
                    tam_bloque = 1024;
                } else {
                    tam_bloque = bis.available();
                }

                String extension = "";
                if (archivo.getName().lastIndexOf(".") != -1 && archivo.getName().lastIndexOf(".") != 0) {
                    extension = archivo.getName().substring(archivo.getName().lastIndexOf(".") + 1);
                }

                String sb = "";
                sb = sb + "HTTP/1.1 200 ok\n";
                sb = sb + "Server: PRACTICA5/1.0 \n";
                sb = sb + "Date: " + new Date() + " \n";

                switch (extension) {
                    case "html":
                        sb = sb + "Content-Type: text/html \n";
                        break;
                    case "jpg":
                    case "jpeg":
                        sb = sb + "Content-Type: image/jpeg \n";
                        break;
                    case "pdf":
                        sb = sb + "Content-Type: application/pdf \n";
                        break;
                }
                sb = sb + "Content-Length: " + bis.available() + " \n";
                sb = sb + "\n";
                bos.write(sb.getBytes());
                bos.flush();

                while ((b_leidos = bis.read(buf, 0, buf.length)) != -1) {
                    bos.write(buf, 0, b_leidos);
                }
                bos.flush();
                bis.close();
            }else{
                System.err.println("El archivo " + nombre + " no fue encontrado");
                String sb = "";
                sb = sb + "HTTP/1.1 404 Not Found\n";
                sb = sb + "Server: PRACTICA3/1.0 \n";
                sb = sb + "Date: " + new Date() + " \n";
                sb = sb + "Content-Type: text/html \n";
                sb = sb + "\n";
                bos.write(sb.getBytes());
                bos.flush();
            }
        }catch(IOException e){
            System.err.println("Erron en enviarRecurso "+e);
        }
    }
    public void getFileName(String cont){
        int i=cont.indexOf("/");
        int f=cont.indexOf(" ",i);
        FileName=cont.substring(i+1,f);
    }
    public void imprimirResultado(String nombre, String parametros){
        if(parametros == null){
            enviarRecurso(nombre);
        }
        try{
            archivo = new File("C:\\Users\\aldos\\Downloads\\Server\\Server2\\"+nombre);
            if(archivo.exists()){
                System.out.println("Parametros: "+parametros);
                try{
                    String linea = "";
                    BufferedReader fichero = new BufferedReader(new FileReader(archivo));
                    StringTokenizer pr = new StringTokenizer(parametros);

                    String sb = "";
                    sb = sb + "HTTP/1.1 200 ok\n";
                    sb = sb + "Server: PRACTICA5/1.0 \n";
                    sb = sb + "Date: " + new Date() + " \n";
                    sb = sb + "Content-Type: text/html \n";
                    sb = sb + "\n";
                    System.out.print(sb);
                    bos.write(sb.getBytes());
                    bos.flush();

                    String codigo = "";
                    while (true) {
                        linea = fichero.readLine();
                        if (linea.contains("<body")) {
                                
                            codigo = codigo + linea + ("<table id='param'>");

                            while (pr.hasMoreTokens()) {
                                linea = pr.nextToken("=");
                                linea = linea.replace("&", " ");
                                codigo = codigo + ("<tr><td>" + linea + "</td>");
                                linea = pr.nextToken("&");
                                linea = linea.replace("+", " ");
                                linea = linea.replace("=", " ");

                                codigo = codigo + ("<td>" + linea + "</td></tr>");
                                pw.print(codigo);
                                codigo = "";
                                pw.flush();
                            }
                            codigo = codigo + ("</table>");
                            linea = "";
                        }
                        if (linea == null) {
                            break;
                        }
                        pw.print(linea);
                        pw.flush();
                    }
                    System.out.println("archivo enviado");
                }catch(Exception ex){
//                    System.err.println("Error despues de que el archivo existe");
                }
            }else {
                System.err.println("Archivo " + nombre + " no encontrado");
                String sb = "";
                sb = sb + "HTTP/1.1 404 Not Found\n";
                sb = sb + "Server: PRACTICA5/1.0 \n";
                sb = sb + "Date: " + new Date() + " \n";
                sb = sb + "Content-Type: text/html \n";
                sb = sb + "\n";
                System.out.print(sb);
                bos.write(sb.getBytes());
                bos.flush();
            }
        }catch(IOException e){
            System.err.println("Error en imprimirRecurso "+e);
        }        
    }
    public void mensajeBorrar(String tipo){
        try{
            String mensaje = "HTTP/1.1 ";
            switch(tipo){
                case "200": mensaje += tipo + "ok\n";
                    break;
                case "404": mensaje += tipo + "404 Not Found\n";
                    break;
                case "500": mensaje += tipo + "Internal Server Error\n";
                    break;
                case "501": mensaje += tipo + "Not Implemented\n";
                    break;
            }
            mensaje += "Server: C-C\n";
            mensaje += "Date: " + new Date() + "\n";
            mensaje += "Content-Type: text/html \n";
            mensaje += "\n";
            bos.write(mensaje.getBytes());
            bos.flush();
        }catch(IOException e){
            System.err.println("Error en página mensaje");
        }
    }
    public void paginaBorrado(String A, String B){
        pw.write("<html><head><title>" + A + "</title><meta charset=\"UTF-8\">");
        pw.flush();
        pw.write("<style>body{color: #cccccc} text{color: white} body{background-color: #222222}</style>");
        pw.flush();
        pw.write("</head><body>");
        pw.flush();
        pw.write("<h1>" + A + "</h1><hr>");
        pw.flush();
        pw.write("<h2>" + B + "</h2>");
        pw.flush();
        pw.write("</body></html>");
        pw.flush();
    }
}