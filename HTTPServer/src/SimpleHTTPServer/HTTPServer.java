/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package SimpleHTTPServer;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 *
 * @author Aleksandar Milicevic
 */
public class HTTPServer {
    
    public static void main(String[] args) throws IOException {
        
        System.out.println("HTTP Server je poceo sa radom...");
        
        String request;
        String fileName;
        
        ServerSocket serverSocket = new ServerSocket(80);
        
        while(true) {
            Socket communicationSocket = serverSocket.accept();
            System.out.println("HTTP Server je prihvatio zahtev...");
            
            BufferedReader tokOdKlijenta = new BufferedReader(new InputStreamReader(communicationSocket.getInputStream()));
            DataOutputStream tokKaKlijentu = new DataOutputStream(communicationSocket.getOutputStream());
            
            request = tokOdKlijenta.readLine();
            System.out.println(request);
            
            StringTokenizer tokenizovanaLinijaZahteva = new StringTokenizer(request);
            
            if(tokenizovanaLinijaZahteva.nextToken().equals("GET")) {
                fileName = tokenizovanaLinijaZahteva.nextToken();
                if(fileName.startsWith("/")) {
                    fileName = fileName.substring(1);
                }
                
                File file = new File(fileName);
                
                if(file.exists()) {
                    int fileSizeInBytes = (int) file.length();
                    
                    FileInputStream inFile = new FileInputStream(fileName);
                    
                    byte[] fileContent = new byte[fileSizeInBytes];
                    inFile.read(fileContent);
                    
                    tokKaKlijentu.writeBytes("HTTP/1.0 200 Document Follows\r\n");
                    
                    if(fileName.endsWith(".jpg")) {
                        tokKaKlijentu.writeBytes("Content-Type: image/jpeg\r\n");
                    }
                    if(fileName.endsWith(".dif")) {
                        tokKaKlijentu.writeBytes("Content-Type: image/gif\r\n");
                    }
                    
                    tokKaKlijentu.writeBytes("Content-Length: " + fileSizeInBytes + "\r\n");
                    
                    tokKaKlijentu.writeBytes("\r\n");
                    
                    tokKaKlijentu.write(fileContent, 0, fileSizeInBytes);
                }
                else {
                    tokKaKlijentu.writeBytes("HTTP/1.0 404 Not Found\r\n");
                    
                    tokKaKlijentu.writeBytes("\r\n");
                    
                    tokKaKlijentu.writeBytes("Fajl koji trazite ne postoji na serveru");
                }
                
                communicationSocket.close();
            }
            else {
                System.out.println("Bad Request Message");
            }
        }
        
    }

}
