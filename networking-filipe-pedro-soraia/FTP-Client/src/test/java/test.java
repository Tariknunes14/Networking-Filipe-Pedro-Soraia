import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import static org.mockito.Mockito.*;

//import org.mockserver.*;

import org.junit.Assert;
import org.junit.contrib.java.lang.system.SystemOutRule;
import org.junit.contrib.java.lang.system.TextFromStandardInputStream;
import org.junit.rules.ExpectedException;
import org.w3c.dom.Text;

public class test {

    Client client;

    ServerSocket server;

    public PrintWriter mockWriter;
    BufferedReader serverMessageReceive;

    //Sending message in SystemIn
    @Rule
    public final TextFromStandardInputStream systemIn = TextFromStandardInputStream.emptyStandardInputStream();

    //Receibing mesage systemout
    @Rule
    public final SystemOutRule systemOutRule = new SystemOutRule().enableLog();

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

@Before
    public void setup() throws IOException {

        server = new ServerSocket(9000);
        //server = mock(ServerSocket.class);
        mockWriter = mock(PrintWriter.class);
        serverMessageReceive = mock(BufferedReader.class);
        client = new Client();
        //client = server.accept();
    }

    @Test
    public void SetUpStreamTests () throws IOException {
        Assert.assertNotNull(client.getOutputBufferWriter());
        Assert.assertNotNull((client.getInputBufferReader()));
        Assert.assertNotNull(client.getIn());
    }

    @Test
    public void notSetupStreamsTest() throws IOException {
        Assert.assertNull(client.getOutputBufferWriter());
        Assert.assertNull(client.getInputBufferReader());
        Assert.assertNull(client.getIn());
    }

    @Test(expected = IOException.class)
    public void CannotConnectToServer() {

        Assert.assertFalse(client.getClientSocket().isConnected());

    }

    @Test
    public void canConnectToServer() throws IOException {
    //confirm if theres a connection
        Assert.assertTrue(client.getClientSocket().isConnected());
    }
    @Test
    public void receiveSystemInMessage() throws IOException {
    //Send messate in terminal
        systemIn.provideLines("help");
        //client.getInputBufferReader();
        //we confirme if the input buffer received the message
        Assert.assertEquals(client.getInputBufferReader().readLine(), "help");
    }
    @Test
    public void sendMessage() throws IOException {
        //Simualate sending a message
        systemIn.provideLines("help");
        String message = "";
        //Simulate receiveing
        when(serverMessageReceive.readLine()).thenReturn("help");
        message = serverMessageReceive.readLine();

        //confirm if its the same
        Assert.assertEquals(message, "help");

    }
    @Test
    public void notSendMessage() throws IOException {
    //simulate sending a message
        systemIn.provideLines("help");
        //Simulate receiving wrong message
        when(serverMessageReceive.readLine()).thenReturn(" ");
        String message = serverMessageReceive.readLine();
        //Confirming
        Assert.assertNotEquals(message, "help");
    }

    @Test
    public void canReceiveMessages() throws IOException {
        systemIn.provideLines("QUIT");
        when(client.getIn().readLine()).thenReturn(serverMessageReceive.readLine());
        mockWriter.println(serverMessageReceive.readLine());
        String receive = client.getIn().readLine();
        Assert.assertEquals(receive, "QUIT");
    }
    @Test
    public void canNotReceiveMessage(){
        systemIn.provideLines("help");
        Assert.assertNotEquals("help", client.getIn());
    }







}
