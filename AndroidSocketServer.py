import socket
import io
import threading
from PIL import Image
import os
import sys



def client(socket, addr):
    print('Client connected by', addr)
    
    try:
        # receive data from android client.
        # data : 'translatedInput|style|quality'
        dataFromClient = socket.recv(1024)
        data = dataFromClient.decode('UTF-8')
        print('received data:', data)

        translatedInput = data.split('|')[0]
        style = data.split('|')[1]
        quality = data.split('|')[2]
        print('translatedInput:', translatedInput)
        print('style:', style)
        print('quality:', quality)
        # run
        if style == 'none':
            os.system(f"python generate.py -p '{translatedInput}' -i {quality}")
        else:
            os.system(f"python generate.py -p '{translatedInput} in the style of {style}' -i {quality}")
        result_image = Image.open('output.png')
        
        # convert image to bytes
        imgByteArr = io.BytesIO()
        result_image.save(imgByteArr, format='PNG')
        imgByteArr = imgByteArr.getvalue()
        # send data to android client.
        socket.send(imgByteArr)
        socket.close()
        print('client disconnected by', addr)
        print()

    except:
        print('Connection failure')

        

ip = '999.999.999.999' # sample
port = 1111 # sample

serverSocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
serverSocket.bind((ip, port))
serverSocket.listen()
print('listening on', port, 'port')

try:
    while(True):
        socket, addr = serverSocket.accept()
        client_thread = threading.Thread(target = client, args = (socket, addr))
        client_thread.start()
        print('continue')
except:
    print('Connection failure')
finally:
    serverSocket.close()
