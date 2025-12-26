/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package HTTPServer;

import java.io.*;
import java.net.*;
import javax.swing.*;

/**
 *
 * @author Aleksandar Milicevic
 */
public class HTTPServer extends Thread {
    
    Socket communicationSocket;
    
    public HTTPServer(Socket communicationSocket) {
        this.communicationSocket = communicationSocket;
        start();
    }
    
    public static void main(String[] args) throws IOException {
        System.out.println("HTTP Server je poceo sa radom...");
        
        ServerSocket serverSocket = new ServerSocket(80);
        
        while(true) {
            new HTTPServer(serverSocket.accept());
            System.out.println("HTTP Server prihvatio zahtev...");
        }
    }
    
    JTextArea console = new JTextArea();
    
    public void InitConsole() {
        JFrame f = new JFrame();
        f.setContentPane(new JScrollPane(console));
        f.setSize(640, 480);
        f.setVisible(true);
    }
    
    void println(String s) {
        console.append(s + "\n");
    }

    @Override
    public void run() {
        InitConsole();
        
        String request;
        String fileName;
        
        try {
            BufferedReader tokOdKlijenta = new BufferedReader(new InputStreamReader(communicationSocket.getInputStream()));
            PrintStream tokKaKlijentu = new PrintStream(communicationSocket.getOutputStream());
            
            while(true) {
                request = tokOdKlijenta.readLine();
                println(request);
                
                if(request == null) {
                    communicationSocket.close();
                    return;
                }
                
                String headerLine;
            
                while(true) {
                    headerLine = tokOdKlijenta.readLine();
                    println(headerLine);
                    if(headerLine == null || headerLine.isEmpty() || headerLine.equals("")) break;
                }
                
                String[] requestParts = request.split(" ");
                
                if(requestParts[0].equals("GET") || requestParts[0].equals("HEAD")) {
                    fileName = requestParts[1];
                    
                    fileName = decodeURL(fileName);
                    
                    if(fileName.startsWith("/")) {
                        fileName = fileName.substring(1);
                    }
                    
                    File file = new File(fileName);
                    
                    if(file.exists()) {
                        int numOfBytes = (int) file.length();
                        
                        tokKaKlijentu.print("HTTP/1.1 200 OK\r\n");
                        tokKaKlijentu.print("Connection: keep-alive\r\n");
                        println("\tOK file exists");
                        
                        if (fileName.endsWith(".html") || fileName.endsWith(".htm")) {
                            tokKaKlijentu.print("Content-Type: text/html\r\n");
                        }
                        if(fileName.endsWith(".jpg")) {
                            tokKaKlijentu.print("Content-Type: image/jpeg\r\n");
                        }
                        if(fileName.endsWith(".gif")) {
                            tokKaKlijentu.print("Content-Type: image/gif\r\n");
                        }
                        
                        tokKaKlijentu.print("Content-Length: " + numOfBytes + "\r\n");
                        
                        tokKaKlijentu.print("\r\n");
                        
                        if(requestParts[0].equals("GET")) {
                            byte[] buffer = new byte[10240];
                            
                            RandomAccessFile randomAccessFile = new RandomAccessFile(fileName, "r");
                            
                            int n;
                            while(true) {
                                n = randomAccessFile.read(buffer);
                                
                                if(n == -1) {
                                    break;
                                }
                                
                                tokKaKlijentu.write(buffer, 0, n);
                            }
                            randomAccessFile.close();
                        }
                    } else {
                        tokKaKlijentu.print("HTTP/1.1 404 Not Found\r\n");
                        
                        String message = "File koji trazite ne postoji na serveru";
                        
                        tokKaKlijentu.print("Content-length: " + message.length() + "\r\n");
                        
                        tokKaKlijentu.print("\r\n");
                        
                        tokKaKlijentu.print(message);
                    }
                } else {
                    println("HTTP/1.1 400 Bad Request: " + request);
                }
            }
            
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    private static String decodeURL(String fileName) {
        try {
            return URLDecoder.decode(fileName, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            return fileName;
        }
    }

}
