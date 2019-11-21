import socket

class TCP_sender:
    def __init__(self, s):
        self.s = s

    def run(self, filename):
        if not self.s.recv(1024).decode().lower() == 'start':
            return
        f = open(filename,'rb')
        while True:
            data = f.read(1024)
            if data:
                self.s.send(data)
            else:
                break
        f.close()

class TCP_raw_sender:
    def __init__(self, ip, port):
        self.ip   = ip
        self.port = port

    def run(self, filename):
        s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        s.connect((self.ip, self.port))
        f = open(filename,'rb')
        while True:
            data = f.read(1024)
            if data:
                s.send(data)
                print(data)
            else:
                break
        f.close()
        s.close()

class TCP_receiver:
    def __init__(self, cs):
        self.cs = cs

    def run(self, filename):
        self.cs.send(b'start')
        f = open(filename,'wb')
        while True:
            data = self.cs.recv(1024)
            if not data:
                break
            f.write(data)
        f.close()
        return
    
class TCP_raw_receiver:
    def __init__(self, ip, port):
        self.ip   = ip
        self.port = port

    def run(self, filename):
        s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        s.bind((self.ip, self.port))
        s.listen(1)
        
        cs, addr = s.accept()
        f = open(filename,'wb')
        while True:
            data = cs.recv(1024)
            if not data:
                break
            f.write(data)
        f.close()
        cs.close()
        s.close()