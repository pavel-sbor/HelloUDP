package com.itmo.pavel;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;


public class ClientRunnable implements Runnable {
    private int orderNumber;
    private int sendingsCount;
    private String prefix;
    private String responsePrefix;
    private int serverPort;
    private InetAddress address;

    private final int SO_TIMEOUT = 200;

    public ClientRunnable(int orderNumber, int sendingsCount, String prefix, int serverPort, InetAddress address, String respPrefix) {
        this.orderNumber = orderNumber;
        this.sendingsCount = sendingsCount;
        this.prefix = prefix;
        this.serverPort = serverPort;
        this.address = address;
        this.responsePrefix = respPrefix;
    }

    public void run() {
        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setSoTimeout(SO_TIMEOUT);
            byte[] receiveBuffer = new byte[socket.getReceiveBufferSize()];
            for (int i = 0; i < sendingsCount; i++) {
                StringBuilder requestMessage = new StringBuilder().append(prefix)
                        .append(orderNumber).append("_").append(i);
                byte[] requestBytes = requestMessage.toString().getBytes();
                DatagramPacket sendPacket = new DatagramPacket(requestBytes, requestBytes.length, address, serverPort);
                try {
                    socket.send(sendPacket);
                } catch (IOException e) {
                    //System.out.println("Unable to send data. Try again.");
                    //e.printStackTrace();
                    --i;
                    continue;
                }
                try {
                    DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                    socket.receive(receivePacket);
                    String message = new String(receivePacket.getData(), 0, receivePacket.getLength(), StandardCharsets.UTF_8);
                    if (message.equals(responsePrefix + requestMessage)) {
                        System.out.println(message);
                    } else {
                        //System.out.println("Wrong format of response message! Got \""+ message + "\""
                        //+ "\n need \"" + responsePrefix + requestMessage + "\"");
                        --i;
                        continue;
                    }

                } catch (IOException e) {
                    //System.out.println("Unable to receive data. Try to send again.");
                    //e.printStackTrace();
                    --i;
                    continue;
                }

            }
        } catch (SocketException e) {
            System.out.println("Unable to create socket: ");
            e.printStackTrace();
        }
    }
}