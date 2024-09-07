from twisted.internet import reactor, protocol
import os

# Clase para manejar el índice local de archivos
class LocalIndex:
    def __init__(self, directory='.'):
        self.directory = directory
        self.index = {}
        self.build_index()

    def build_index(self):
        # Construye el índice recorriendo todos los archivos en el directorio y subdirectorios
        for root, dirs, files in os.walk(self.directory):
            for filename in files:
                path = os.path.join(root, filename)
                self.index[filename] = path

    def search(self, keyword):
        # Busca archivos que contengan la palabra clave en el nombre
        return [filename for filename in self.index if keyword in filename]

# Protocolo del servidor P2P
class P2PServerProtocol(protocol.Protocol):
    def __init__(self):
        self.local_index = LocalIndex()  # Cambia el directorio aquí si es necesario

    def dataReceived(self, data):
        # Maneja los datos recibidos del cliente
        message = data.decode('utf-8')
        command, *args = message.split()

        if command == 'SEARCH':
            self.handle_search(args)
        elif command == 'DOWNLOAD':
            self.handle_download(args)

    def handle_search(self, args):
        # Maneja la búsqueda de archivos
        keyword = args[0]
        matching_files = self.local_index.search(keyword)
        response = 'SEARCH_RESULT ' + ' '.join(matching_files)
        self.transport.write(response.encode('utf-8'))

    def handle_download(self, args):
        # Maneja la descarga de archivos
        filename = args[0]
        if filename in self.local_index.index:
            with open(self.local_index.index[filename], 'rb') as f:
                data = f.read()
                response = f'DOWNLOAD_RESULT {len(data)} '.encode('utf-8') + data
                self.transport.write(response)
        else:
            self.transport.write(b'ERROR File not found')

# Fábrica del servidor P2P
class P2PServerFactory(protocol.Factory):
    def buildProtocol(self, addr):
        return P2PServerProtocol()

# Función principal para iniciar el servidor
def main():
    reactor.listenTCP(8000, P2PServerFactory())
    print('P2P Server started on port 8000')
    reactor.run()

if __name__ == '__main__':
    main()
