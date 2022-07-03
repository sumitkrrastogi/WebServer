package org.sumitkrrastogi;

import org.jclouds.http.HttpResponse;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ProtocolFamily;
import java.net.http.HttpRequest;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;

public class SocketListener {



    public void startListening() throws IOException {
        ServerSocketChannel serverSocket = ServerSocketChannel.open();
        serverSocket.configureBlocking(false);
        serverSocket.bind(new InetSocketAddress(8080));
        Selector webTrafficSelector = Selector.open();
        SelectionKey register = serverSocket.register(webTrafficSelector, SelectionKey.OP_ACCEPT);
        while(true)
        {
            int select = webTrafficSelector.select();
            for(SelectionKey selectionKey: webTrafficSelector.selectedKeys())
            {
                if(selectionKey.isAcceptable())
                {
                    System.out.println("Server is accepting connection");
                    SocketChannel accept;
                    while(( accept = serverSocket.accept())!=null)
                    {
                        System.out.println("waiting...");
                        accept.configureBlocking(false);
                        accept.register(webTrafficSelector, SelectionKey.OP_READ);
                    }
                }

                else if(selectionKey.isReadable())
                {
                    System.out.println("Reading from channel");
                    Object attachment = selectionKey.attachment();
                    if(attachment!=null)
                    {
                        System.out.println("Attachement : "+attachment.getClass().getCanonicalName());

                    }
                    ByteBuffer buffer = ByteBuffer.allocate(25600);
                    SocketChannel channel = (SocketChannel) selectionKey.channel();
                    int read = channel.read(buffer);
                    String s = new String(buffer.array()).trim();
                    HttpParser httpParser = new HttpParser(new ByteArrayInputStream(buffer.array()));
                    String method = httpParser.getMethod();
                    System.out.println("Message : "+s);
                    buffer.clear();

                    HttpResponse build = HttpResponse.builder()
                            .addHeader("Content-Type","text/html; charset=utf-8")
                            .addHeader("Content-Type","text/html; charset=utf-8")
                            .statusCode(200).message("Hi There..").build();
                    ByteBuffer byteBuffer = ByteBuffer.allocate(25600);
                    byteBuffer.put(build.toString().getBytes(StandardCharsets.UTF_8));
                    channel.write(byteBuffer);
                    byteBuffer.clear();
                    selectionKey.cancel();
                    //channel.close();
                }

            }


        }

    }
}
