package com.itmo.pavel;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;

public class ServerRunnable implements Runnable {
    private DatagramSocket socket;
    private DatagramPacket receivePacket;
    private ExecutorService executorService;
    private int threads;
    private String prefix;

    public ServerRunnable(DatagramSocket socket, DatagramPacket receivePacket, ExecutorService executorService, int threads, String prefix) {
        this.socket = socket;
        this.receivePacket = receivePacket;
        this.executorService = executorService;
        this.threads = threads;
        this.prefix = prefix;
    }

    @Override
    public void run() {
        while (true) {
            try {
                socket.receive(receivePacket);
            } catch (IOException e) {
                //System.out.println("Unable to receive request");
                //e.printStackTrace();
                continue;
            }
            String message = new String(receivePacket.getData(), 0, receivePacket.getLength(), StandardCharsets.UTF_8);
            byte[] requestBytes = (prefix + message).getBytes();
            DatagramPacket sendPacket = new DatagramPacket(requestBytes, requestBytes.length, receivePacket.getAddress(), receivePacket.getPort());
            if (threads == 1) {
                sendResponce(sendPacket, socket);
            } else {
                executorService.submit(new Runnable() {
                    @Override
                    public void run() {
                        sendResponce(sendPacket, socket);
                    }
                });
            }
        }
    }

    protected void sendResponce(DatagramPacket packet, DatagramSocket socket) {
        try {
            socket.send(packet);
        } catch (IOException e) {
            //System.out.println("Unable to send data. Try again.");
            //e.printStackTrace();
        }
    }
}