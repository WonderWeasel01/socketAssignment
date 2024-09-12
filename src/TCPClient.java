import java.io.*;
import java.net.*;

public class TCPClient {
    public static void main(String[] args) {
        String serverAddress = "192.168.0.101"; // Change to server IP if needed
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

            //Create path- & output stream to the file.
            String filePath = saveDir + File.separator + fileName;
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));


            //We use the filesize to know when to separate files
            long fileSize = dataInputStream.readLong();
            long bytesRemaining = fileSize;

            byte[] buffer = new byte[1024];
            int bytesRead;

            //Read data from server until end of stream. DataInputStream.read returns -1 when there is no more data to read.
            while ((bytesRead = dataInputStream.read(buffer, 0, (int) Math.min(buffer.length, bytesRemaining))) != -1) {
                //Write the data from the inputstream to the file
                bos.write(buffer, 0, bytesRead);
                //Calculate when the file is fully read.
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
