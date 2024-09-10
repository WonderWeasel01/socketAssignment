import java.io.*;
import java.net.*;

public class TCPClient {
    public static void main(String[] args) {
        String serverAddress = "localhost"; // Change to server IP if needed
        int port = 5000;
        int fileCounter = 0;

        try (Socket socket = new Socket(serverAddress, port)) {
            System.out.println("Connected to server");

            while(receiveFile(socket)){
                fileCounter++;
            }
            System.out.println("Files recieved " + fileCounter);

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    // Method to receive file from server and save it locally
    private static boolean receiveFile(Socket socket) {
        String saveDir = "src/resources/receivedFiles";

        try {
            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());


            String fileName = dataInputStream.readUTF();

            if(fileName.equals("END_OF_FILES")){
                return false; // No more files to read
            }

            String filePath = saveDir + File.separator + fileName;
            long fileSize = dataInputStream.readLong();


            FileOutputStream fos = new FileOutputStream(filePath);
            BufferedOutputStream bos = new BufferedOutputStream(fos);

            byte[] byteArray = new byte[1024];
            int bytesRead;
            long bytesRemaining = fileSize;

            // Read data from server until end of stream
            while ((bytesRead = dataInputStream.read(byteArray, 0, (int) Math.min(byteArray.length, bytesRemaining))) != -1) {
                bos.write(byteArray, 0, bytesRead);
                bytesRemaining -= bytesRead;
                if (bytesRemaining <= 0) {
                    break;
                }
            }
            
            bos.flush();
            bos.close();

            System.out.println("File " + fileName + " saved to " + saveDir);
            return true;
        } catch (EOFException eof) {
            // End of stream reached;
            return false;
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
    }
}
