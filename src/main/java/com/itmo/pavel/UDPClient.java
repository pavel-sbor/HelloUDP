package com.itmo.pavel;

import info.kgeorgiy.java.advanced.hello.HelloClient;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class UDPClient implements HelloClient, Closeable {
    private ExecutorService executorService;
    public static void main(String[] args) {
        UDPClient client = new UDPClient();
        client.start(args[0], Integer.valueOf(args[1]),
                args[2], Integer.valueOf(args[4]), Integer.valueOf(args[3]));
    }

    @Override
    public void start(String s, int i, String s1, int i1, int i2) {
        try {
            executorService = Executors.newFixedThreadPool(i2);
            InetAddress address = InetAddress.getByName(s);
            for (int j = 0; j < i2; j++) {
                executorService.submit(new ClientRunnable(j, i1, s1, i, address, "Hello, "));
            }
            executorService.shutdown();
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
        } catch (UnknownHostException e) {
            System.out.println("Host is unknown!");
            e.printStackTrace();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        } finally {
            executorService.shutdownNow();
        }
    }

    @Override
    public void close() throws IOException {
        executorService.shutdownNow();
    }
}