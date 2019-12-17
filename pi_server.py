import sounddevice as sd
import threading
import struct
import numpy as np
import time
import os
import socket
from TCP import TCP_sender

sd.default.channels = 1

def allocate_port(ip, port):
    s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    _port = 1 + port
    while True:
        try:
            s.bind((ip, _port))
            break
        except:
            _port += 1
    return (s, _port)

def record(rcs, time_length, port, i):
    s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    s.connect(('127.0.0.1', port))
    
    def cb(indata, outdata, frames, time, status):
        d = indata[:, 0].tobytes()
        s.send(d)
        rcs.send(d)
        return
    
    with sd.Stream(callback=cb, blocksize=8400, dtype='float32', device=i):
        time.sleep(time_length)
    
    s.close()
    return

def thread_record(ML_ip, ML_port, s, time_length, train_test, title, i):
    s.listen(2)
    cs, addr = s.accept()
    
    rs, rport = allocate_port('127.0.0.1', 14310 + i)
    #rs = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    #rs.bind(('127.0.0.1', 14310 + i))
    rs.listen(2)
    
    data = b''
    t = threading.Thread(target=record, args=(cs, time_length, rport, i, ))
    t.start()
    
    rcs, raddr = rs.accept()
    rcs.settimeout(2)
    while True:
        try:
            d = rcs.recv(1024)
            if not d:
                break
            data += d
        except:
            break
    rcs.close()
    rs.close()
    t.join()
    
    signal = []
    for j in range(len(data)//4):
        signal.append(struct.unpack('<f', data[4*j:4*j+4])[0])
    np.save('record_data{}'.format(i), np.array(signal))
    
    cs.close()
    s.close()

    ss = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    ss.connect((ML_ip, ML_port))
    ss.send('{}+{}'.format(train_test, title+'ch{}'.format(i)).encode())
    TCP = TCP_sender(ss)
    TCP.run('record_data{}.npy'.format(i))
    ss.close()

    return

def main():
    try:
        Pi_ip = '192.168.0.8'#input('> Pi ip: ')
        Pi_port = 10612 #int(input('> Pi port: '))
        ML_ip = '110.76.74.51'#input('> ML ip: ')
        ML_port = 14310#int(input('> ML port: '))
    except:
        print('> ip or port is wrong.')
        return
    
    s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    s.bind((Pi_ip, Pi_port))
    s.listen(2)
    
    while True:
        try:
            cs, addr = s.accept()
        except:
            print('Force to close server.')
            try:
                cs.close()
            except:
                None
            s.close()
            return
        try:
            cmd = cs.recv(1024).decode().lower().split('+')
            print(cmd)

            if cmd[0] == 'quit':
                cs.close()
                s.close()
                return

            if cmd[0] == 'connect':
                cs.send(b'Connected')
                cs.close()

            if cmd[0] == 'record':
                train_test = cmd[1]
                time_length = int(cmd[2])
                title = cmd[3]

                s1, p1 = allocate_port(Pi_ip, Pi_port)
                s2, p2 = allocate_port(Pi_ip, p1)

                cs.send('{}+{}'.format(p1, p2).encode())
                cs.close()

                t1 = threading.Thread(target=thread_record, args=(ML_ip, ML_port, s1, time_length, train_test, title, 1,))
                t2 = threading.Thread(target=thread_record, args=(ML_ip, ML_port, s2, time_length, train_test, title, 2,))
                t1.start()
                t2.start()
               # t1.join()
               # t2.join()
                
        except Exception as e:
            print('> Error {}'.format(type(e)))
            cs.close()
            s.close()
            return
        
main()
