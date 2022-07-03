package org.sumitkrrastogi;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {

        System.out.println("Hello world!");
        SocketListener socketListener = new SocketListener();
        try {
            socketListener.startListening();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}