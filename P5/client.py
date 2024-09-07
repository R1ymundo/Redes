import socket
import threading
import selectors

def send(sock,addr):
   while True:
      string = input()
      message = name.upper() + ": " + string
      sock.sendto(message.encode('utf-8'),addr)
      if string == '--EXIT':
         break

def recv(sock,addr):
    sock.sendto(name.upper().encode('utf-8'), addr)
    # Crear un selector
    sel = selectors.DefaultSelector()
    # Registrar el socket para la lectura
    sel.register(sock, selectors.EVENT_READ, data=addr)
    while True:
        # Bloquear hasta que haya algún evento de lectura disponible
        events = sel.select()
        for key, mask in events:
            if mask & selectors.EVENT_READ:
                data, _ = sock.recvfrom(1024)
                print(data.decode('utf-8'))
     
print('____WELCOME____\n'+'EXIT -> exits\nUSERLIST -> get users')
name = input('Introduce tu nombre:')
socket = socket.socket(socket.AF_INET,socket.SOCK_DGRAM)
server = ('localhost',8080)
socket.setblocking(False)
tr = threading.Thread(target=recv,args=(socket,server),daemon=True)
ts = threading.Thread(target=send,args=(socket,server))
tr.start()
ts.start()
ts.join()
socket.close()
