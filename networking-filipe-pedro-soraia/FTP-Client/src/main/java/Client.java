import java.io.*;
import java.net.Socket;
import java.util.Arrays;

public class Client {

    private Socket clientSocket;
    private BufferedReader inputBufferReader;
    private BufferedWriter outputBufferWriter;
    BufferedReader in;
    public Client(){
        try {
            clientSocket = new Socket("127.0.0.1", 9000);

        }
        catch (IOException e){
            System.out.println("client cannot connect to server");
            //throw new RuntimeException("client cannot connect to server");
        }
        String line = "";

        try {
            setUpSocketStreams();
            while  (!line.equals("QUIT") && !line.equals("DISCONNECT")  && !line.equals("BYE")){

                line = inputBufferReader.readLine();
                outputBufferWriter.write(line);
                outputBufferWriter.newLine();
                outputBufferWriter.flush();

                //reading message from server
                String serverEcho;

                //when server sends exit it means that we are not waiting for a message that the servers sends
                while ((serverEcho = in.readLine()) != null && !serverEcho.equals("exit")){
                    String type;
                    type = serverEcho;

                    if(type.equals("get")){

                        getCommand(line);
                        System.out.println("Download Complete");
                        break;
                    }
                    if(type.equals("put")){
                        putCommand(line);

                    }
                    if(!serverEcho.equals("exit") && !serverEcho.equals("get") && !serverEcho.equals("put"))
                            System.out.println(serverEcho);

                }
                line = line.toUpperCase();

            }
            outputBufferWriter.close();
        }
        catch (IOException e){
            throw new RuntimeException("");
        }catch (Exception e){
            System.out.println("we got an error");
        }
    }

    public void setUpSocketStreams() throws IOException {
        inputBufferReader = new BufferedReader(new InputStreamReader(System.in));
        outputBufferWriter = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }

    public void getCommand(String line){
        try {

            //Max byte size
            byte fileSize[] = new byte[clientSocket.getReceiveBufferSize()];
            //receive input
            InputStream is = clientSocket.getInputStream();

            //Files information
            //Where to write the data
            FileOutputStream download = new FileOutputStream("clientRoot/" + line);
            //convert file to bytes
            BufferedOutputStream downloadFile = new BufferedOutputStream(download);

            //gets the bytesread to know the size
            int bytesRead ;
            bytesRead = is.read(fileSize);

            //If theres no more bytes to read we get out
            while (bytesRead != -1){
                //Writing the file
                downloadFile.write(fileSize, 0, bytesRead);
                if(is.available() == 0){
                    break;
                }
                bytesRead = is.read(fileSize);
            }
            //Push bytes to the filee
            downloadFile.flush();
            downloadFile.close();
            download.close();

        } catch (IOException e) {
            System.out.println("error - Can't copy");
        }

    }

    public void putCommand(String line){
        try {
            //Gets the file
            File getFile = new File("clientRoot/" + line);

            FileInputStream file = new FileInputStream("clientRoot/" + line);

            //get the bytes that we need to give to client
            byte buffer[] = new byte[(int) getFile.length()];

            int bytesRead = 0;
            int current = 0;
            //Send file to the client
            OutputStream uploadFile = clientSocket.getOutputStream();

            while (file.available() != 0){
                //Getting bytes we need to read, start where current is. We start at 0
                bytesRead = file.read(buffer, current, (buffer.length-current));
                if(bytesRead >= 0){
                    current += bytesRead;
                }
                uploadFile.write(buffer,0, current);
            }
            //close streams
            uploadFile.flush();
            file.close();
        }catch (FileNotFoundException e){
            System.out.println("Error - File not found");
        }catch (Exception e){
            System.out.println("Error - There was a problem");
        }
    }

    public BufferedReader getIn() {
        return in;
    }

    public BufferedReader getInputBufferReader() {
        return inputBufferReader;
    }

    public BufferedWriter getOutputBufferWriter() {
        return outputBufferWriter;
    }

    public InputStream getInPutStream() throws IOException {
        return clientSocket.getInputStream();

    }

    public OutputStream getOutPutStream() throws IOException {
        return clientSocket.getOutputStream();

    }

    public Socket getClientSocket(){
        return clientSocket;
    }

    public static void main(String[] args) {
        new Client();
    }
}
