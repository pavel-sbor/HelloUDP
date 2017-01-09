package com.itmo.pavel;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

/**
 * Created by Pavel on 10.01.2017.
 */
public class ClientRunnable implements Runnable {
    private int orderNumber;
    private int sendingsCount;
    private String prefix;
    private int serverPort;
    private InetAddress address;

    private final int SO_TIMEOUT = 300;
    private final int MAX_RETRIES = 3;

    public ClientRunnable(int orderNumber, int sendingsCount, String prefix, int serverPort, InetAddress address) {
        this.orderNumber = orderNumber;
        this.sendingsCount = sendingsCount;
        this.prefix = prefix;
        this.serverPort = serverPort;
        this.address = address;
    }

    public void run() {
        try (DatagramSocket socket = new DatagramSocket(serverPort, address)) {
            socket.setSoTimeout(SO_TIMEOUT);
            for (int i = 0; i < sendingsCount; i++) {
                int retryCount = 0;
                StringBuilder requestMessage = new StringBuilder().append(prefix)
                        .append(orderNumber).append(" ").append(i);
                byte[] requestBytes = requestMessage.toString().getBytes();
                DatagramPacket sendPacket = new DatagramPacket(requestBytes, requestBytes.length, address, serverPort);
                try {
                    socket.send(sendPacket);
                } catch (IOException e) {
                    System.out.println("Unable to send data. Try again.");
                    e.printStackTrace();
                    if (++retryCount < MAX_RETRIES) {
                        --i;
                    }
                }

            }
        } catch (SocketException e) {
            System.out.println("Unable to create socket: ");
            e.printStackTrace();
        }

    }
}
