package com.itmo.pavel;

import info.kgeorgiy.java.advanced.hello.HelloServer;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UDPServer implements HelloServer {
    private ExecutorService executorService;
    private DatagramSocket socket;

    public static void main(String[] args) {
        UDPServer server = new UDPServer();
        server.start(Integer.valueOf(args[0]), Integer.valueOf(args[1]));
    }

    @Override
    public void start(int i, int i1) {
        synchronized (this) {
            executorService = Executors.newFixedThreadPool(i1);
            try {
                socket = new DatagramSocket(i);
                DatagramPacket packet = new DatagramPacket(new byte[socket.getReceiveBufferSize()], socket.getReceiveBufferSize());
                executorService.execute(new ServerRunnable(socket, packet, executorService, i1, "Hello, "));
            } catch (SocketException e) {
                System.out.println("Unable to create socket: ");
                e.printStackTrace();
            }
        }
    }

    @Override
    public void close() {
        synchronized (this) {
            executorService.shutdownNow();
            socket.close();
        }
    }
}