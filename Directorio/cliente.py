from genericpath import isdir, isfile
import json
import os
import pathlib
import shutil
import socket
import struct
from tkinter import Tk, Tcl
from tkinter import filedialog
from getpass import getuser
import zipfile

usuario = getuser()
dir_name = "/Users/"+ usuario +"/Downloads/"

def send_file(sck: socket.socket, filename):

    filesize = os.path.getsize(filename)

    sck.sendall(struct.pack("<Q", filesize))
    
    with open(filename, "rb") as f:
        while read_bytes := f.read(1024):
            sck.sendall(read_bytes)
        f.close()

def receive_file_size(sck: socket.socket):
    fmt = "<Q"
    expected_bytes = struct.calcsize(fmt)
    received_bytes = 0
    stream = bytes()
    while received_bytes < expected_bytes:
        chunk = sck.recv(expected_bytes - received_bytes)
        stream += chunk
        received_bytes += len(chunk)
    filesize = struct.unpack(fmt, stream)[0]
    return filesize

def receive_file(sck: socket.socket, filename):
    
    global dir_name
    os.chdir(dir_name)
    
    filesize = receive_file_size(sck)
    with open( filename, "wb") as f:
        received_bytes = 0
        while received_bytes < filesize:
            chunk = sck.recv(1024)
            if chunk:
                f.write(chunk)
                received_bytes += len(chunk)
        f.close()

def name_file(sck: socket.socket):
    Tk().withdraw()
    file_path = filedialog.askopenfilename()
    file_name = os.path.basename(file_path)
    return (file_path, file_name)

def list_content():
    Tk().withdraw()
    
    filename = filedialog.askdirectory(
        initialdir="C://"
    )
    
    path = pathlib.Path(filename).absolute()
    files = os.listdir(path)
    show_files(files)

def menu():
    print("1.-Listar contenido de carpeta remota")
    print("2.-Listar contenido de carpeta local")
    print("3.-Subir archivos/carpetas")
    print("4.-Descargar archivos/carpetas")
    print("5.-Eliminar archivos/carpetas remotas")
    print("6.-Eliminar archivos/carpetas locales")
    print("7.-Salir")
    option = input("Selecciona una opcion: ")
    
    return option

def show_files (files):
    for index, content in enumerate(files):
        print(f"{index}: {content}" )
    print("\n\n")

def remove_file(): 
    Tk().withdraw()
    file_path = filedialog.askopenfilename()
    os.remove(file_path)
        
def remove_folder():
    Tk().withdraw()
    
    filename = filedialog.askdirectory(
        initialdir="C://"
    )
    
    try:
        shutil.rmtree(filename)
    except OSError as e:
        print(f"Error:{ e.strerror}")

def send_folder(sck: socket.socket):
    Tk().withdraw()
    
    filename = filedialog.askdirectory(
        initialdir="C://"
    )
    
    path = pathlib.Path(filename).absolute()
    path_name = pathlib.PurePath(path).name
    folder = os.listdir(path)
    os.chdir(path)
    
    zip_name = path_name + ".zip"

    with zipfile.ZipFile(zip_name, 'w') as file:
        for i in range(len(folder)):
            file.write(folder[i])
            print(os.path.join(path, folder[i]))

    sck.send(zip_name.encode())
    send_file(sck, zip_name)

    os.remove(zip_name)

with socket.create_connection(("localhost", 6190)) as conn:
    print("Conectado al servidor.")
    
    while True:
        
        answer = menu()
        conn.send(answer.encode())

        answer = int(answer)
        
        if (answer == 1):
            recv_files = conn.recv(1024)
            files_list = json.loads(recv_files.decode())
            show_files(files_list)
            
            
        elif(answer == 2):
            
            list_content()
            
        elif(answer == 3):
            
            option = input("\n \n ¿Que deseas enviar? \n 1.- Archivo \n 2.-Carpeta \n")
            conn.send(option.encode())
            
            if (option == "1" ):
                print("Enviando archivo...")
                file_path, file_name = name_file(conn)
                conn.send("".join(file_name).encode())
                send_file(conn, file_path)
                print("Enviado.")
                
            elif (option == "2"):
                 send_folder (conn)   
            else:
                print("Opción no valida")
            
        elif(answer == 4):
            
            file_name = input("\n Nombre del archivo a descargar")
            conn.send(file_name.encode())
            
            receive_file(conn, file_name)    
            print("Archivo descargado.")
            
        elif(answer == 5):
            
            print("Estos son los archivos y carpetas")
            recv_files = conn.recv(1024)
            files_list = json.loads(recv_files.decode())
            show_files(files_list)
            
            option = input("\n \n ¿Que deseas eliminar? \n 1.- Archivo \n 2.-Carpeta \n")
            conn.send(option.encode())
            
            if (option == "1" ):
                send_name = input("¿Que archivo quiere eliminar?\n")
                conn.send(send_name.encode())
            elif (option == "2"):
                send_folder_name = input("¿Que carpeta quiere eliminar?\n")
                conn.send(send_folder_name.encode())    
            else:
                print("Opción no valida")
            
        elif(answer == 6):
            
            option = input("\n \n ¿Que deseas eliminar? \n 1.- Archivo \n 2.-Carpeta \n")
            
            if (option == "1" ):
                remove_file()
                
            elif (option == "2"):
                remove_folder()
                
            else:
                print("Opción no valida")
            
        elif(answer == 7):
            conn.close()
            break
        else:
            print("Seleccione una opcion correcta")
    
print("Conexión cerrada.")