import socket
import time

svrsocket = socket.socket(socket.AF_INET,socket.SOCK_DGRAM)
svrsocket.bind(('',8080))
svrsocket.setblocking(False)
users = {}

def send_record(msg):
    print(msg)
    broadcast(msg)

def broadcast(msg):
    for address in users:
        svrsocket.sendto(msg.encode(), address)

while True:
    try:
        user_data, user_addr = svrsocket.recvfrom(1024)
        if not user_addr in users:
            enter_msg = time.asctime() + "\n" + user_data.decode() + " acaba de entrar en la sala del chat ..."
            send_record(enter_msg)
            users[user_addr] = user_data.decode('utf-8')
            continue
        if '--EXIT' in user_data.decode('utf-8'):
            leave_msg = time.asctime() + "\n" + users[user_addr] + " salio de la sala de chat ..."
            users.pop(user_addr)
            send_record(leave_msg)

        elif '--USERLIST' in user_data.decode('utf-8'):
            for user in users.keys():
                list_msg = (users[user]+': '+user[0]+":"+str(user[1])).encode()
                svrsocket.sendto(list_msg,user_addr)

        elif '@' in user_data.decode('utf-8') :
            usuario = user_data.decode('utf-8')

            dos_puntos = usuario.find(':')
            arroba = usuario.find('@') 
                       
            frase = usuario[arroba + 1:]
            espacio = frase.find(' ')
            
            mensaje = frase[espacio + 1:]
            user_send = usuario[:dos_puntos]
            name_usuario = frase[:espacio].upper()
            
            for user in users.keys():
                if users[user] == name_usuario:
                    msg_private = (user_send + ': ' + mensaje).encode()
                    svrsocket.sendto(msg_private, (user[0], user[1]) )
                    break
        else:
            msg = time.asctime() + '\n' + user_data.decode('utf-8')
            send_record(msg)

    except ConnectionResetError:
        print("ConnectionResetError")
    
    except BlockingIOError:
        pass
