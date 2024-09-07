from multiprocessing.connection import answer_challenge
import os
import shutil
import socket
import struct
import json
import zipfile

dir_name = "/Users/raymu/OneDrive/Escritorio/py/carpeta_c/"

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
    os.makedirs(dir_name, exist_ok=True)
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

def send_file(sck: socket.socket, file_name):
    
    global dir_name
    
    dirname = dir_name + file_name
    
    filesize = os.path.getsize(dirname)

    sck.sendall(struct.pack("<Q", filesize))
    
    with open(dirname, "rb") as f:
        while read_bytes := f.read(1024):
            sck.sendall(read_bytes)
        f.close()

def file_list():
    global dir_name
    files = os.listdir(dir_name)
    
    return files 

def remove_file(file_name): 
    global dir_name
    os.remove(dir_name + file_name)
    
def remove_folder(dirPath):
    global dir_name
    try:
        shutil.rmtree(dir_name+ dirPath)
    except OSError as e:
        print(f"Error:{ e.strerror}")
        
def  recv_folder(sck: socket.socket):
    
    global dir_name

    filename = sck.recv(1024).decode()
    receive_file(sck,filename)

    path = os.path.join(dir_name, filename.strip(".zip"))

    with zipfile.ZipFile(filename, 'r') as file:
        file.extractall(path)
    
    os.remove(filename)
        
    
with socket.create_server(("localhost", 6190)) as server:
    
    while True:
        print("Esperando al cliente...")
        conn, address = server.accept()
        print(f"{address[0]}:{address[1]} conectado.")
        
        while True:
            answer_client = int(conn.recv(1024))
            
            print(answer_client)
            
            if (answer_client == 1):
                
                files_list = file_list()
                send_list = json.dumps(files_list)
                conn.send(send_list.encode())
                
            elif(answer_client == 3):
                
                recv_option = int(conn.recv(1024))
                
                if (recv_option == 1):
                    print("Recibiendo archivo...")
                    file_name = conn.recv(1024).decode()
                    receive_file(conn, file_name)    
                    print("Archivo recibido.")   
                    
                elif (recv_option == 2):
                    recv_folder(conn)
                
            elif(answer_client == 4):
                
                file_name = conn.recv(1024).decode()
                send_file(conn, file_name)
                print("Enviado.")
                
            elif(answer_client == 5):
                
                files_list = file_list()
                send_list = json.dumps(files_list)
                conn.send(send_list.encode())
                
                recv_option = int(conn.recv(1024))
                
                if (recv_option == 1):
                    recv_name = conn.recv(1024)
                    remove_file(recv_name.decode())    
                elif (recv_option == 2):
                    recv_folder_name = conn.recv(1024)
                    remove_folder(recv_folder_name.decode())
                
            elif(answer_client == 7):
                conn.close()
                print("Conexión cerrada.\n\n")
                break
            
            