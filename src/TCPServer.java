import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class TCPServer {
    public static void main(String[] args) {
        int port = 5000; // Port number
        String folderPath = "src/resources/sendFiles"; // Files to be sent

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server is listening on port " + port);

            // Wait for client connection
            Socket socket = serverSocket.accept();
            System.out.println("New client connected");

            File folder = new File(folderPath);
            List<File> files = getFilesInFolder(folder);

            // Send each file
            for (File file : files) {
                sendFile(socket, file);
            }

            //Sending a string to let the client know that there are no more files to be read.
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
            dataOutputStream.writeUTF("END_OF_FILES");
            dataOutputStream.flush();

            socket.close();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private static List<File> getFilesInFolder(File folder) {
        File[] files = folder.listFiles();
        List<File> fileList = new ArrayList<>();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    fileList.add(file);
                }
            }
        }
        return fileList;
    }

    private static void sendFile(Socket socket, File file) {
        try {

            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());

            //Send file data to differentiate between files
            String fileName = file.getName();
            long fileSize = file.length();
            dataOutputStream.writeUTF(fileName);
            dataOutputStream.writeLong(fileSize);
            dataOutputStream.flush();

            BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file));
            OutputStream outputStream = socket.getOutputStream();

            byte[] buffer = new byte[1024];
            int bytesRead;

            while ((bytesRead = bufferedInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            outputStream.flush();
            bufferedInputStream.close();

            System.out.println("File " + fileName + " sent successfully");

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
