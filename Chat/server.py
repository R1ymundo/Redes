import socket
import time

# Crear socket UDP
svrsocket = socket.socket(socket.AF_INET,socket.SOCK_DGRAM)
# Unión
svrsocket.bind(('',8080))
# Usuario en la sala de chat, almacenado en un diccionario, {(ip: puerto): Nombre de usuario}
users = {}


def send_record(msg):
    '''
         Recibir una cadena
         Imprimir y pasar a write2file y difundir
         A través de este método, puede ver al usuario entrando / saliendo de la sala de chat y la información del chat del usuario en el servidor y el usuario.
         Al mismo tiempo, escriba la información anterior en el archivo (a través de write2file)
    '''
    print(msg)
    broadcast(msg)

def broadcast(msg):
    '''
         Difusión en el socket (enviar msg a cada usuario)
         Enviar la información enviada por cada usuario a otros usuarios
    '''
    # Recorre los usuarios del diccionario de usuario
    for address in users:
        svrsocket.sendto(msg.encode(), address)

while True:
    try:
        # Recibir información del socket UDP
        user_data, user_addr = svrsocket.recvfrom(1024)
                
        # Nuevo usuario se une a la sala de chat
        if not user_addr in users:
            # El usuario ingresó al mensaje de la sala de chat
            enter_msg = time.asctime() + "\n" + user_data.decode() + " acaba de entrar en la sala del chat ..."
            # El usuario ingresa a la sala de chat, envía un recordatorio y lo graba en un archivo
            send_record(enter_msg)
            # Agregar el usuario a los usuarios del diccionario de usuario
            users[user_addr] = user_data.decode('utf-8')
            # Nuevos usuarios ingresan a la sala de chat, no es necesario juzgar user_data
            continue


        # El usuario ingresa "--EXIT" para salir del chat
        if '--EXIT' in user_data.decode('utf-8'):
            # Msg el usuario salió de la sala de chat
            leave_msg = time.asctime() + "\n" + users[user_addr] + "Sal de la sala de chat ..."
            # Diccionario de usuario eliminar este usuario
            users.pop(user_addr)
            # El usuario sale de la sala de chat, envía un mensaje y lo graba en un archivo
            send_record(leave_msg)

        # El usuario ingresa "--USERLIST" para solicitar una lista de usuarios en línea en la sala de chat
        elif '--USERLIST' in user_data.decode('utf-8'):
            # Recorre el diccionario almacenando usuarios usuarios
            for user in users.keys():
                # list_msg = username+ip+port
                list_msg = (users[user]+': '+user[0]+":"+str(user[1])).encode()
                # Se envía por separado a los usuarios que solicitan la lista de usuarios
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
            # Chat normal en la sala de chat
            msg = time.asctime() + '\n' + user_data.decode('utf-8')
            # Enviar mensaje y grabar
            send_record(msg)

    except ConnectionResetError:
        print("ConnectionResetError")
