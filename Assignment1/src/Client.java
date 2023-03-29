import java.net.*;  
import java.io.*; 
public class Client {
    Socket mySocket;
    DataOutputStream outStream;
    BufferedReader inputStream;

    int serverID = 0;
    int jobID = 0;

    
    public Client(String address, int port) throws Exception{
          mySocket = new Socket(address, port);
          outStream = new DataOutputStream(mySocket.getOutputStream());
          inputStream = new BufferedReader(new InputStreamReader(mySocket.getInputStream()));  

    }

    public static void main(String[] args) throws Exception{
        Client c = new Client("192.168.0.69",50000);
        c.byClient();
        c.mySocket.close();
        c.inputStream.close();
        c.outStream.close();
    }


    public void byClient() throws Exception{
        sendMessage("HELO");
        System.out.println("Server says: "+this.inputStream.readLine());
        String username = System.getProperty("user.name"); //systemcall to opsys to give me the user executing this program
        sendMessage("AUTH " +username);
        System.out.println("Server says: "+ this.inputStream.readLine());

        //we closed the connection with ds-sim without telling it that I am going to quit 
        sendMessage("REDY"); //when we send ready server sends us an update, usually a job from the USER side of the server
                                     //if I need get JCPL -> message from the Server Side of the simulator 
        while (serverID <= 9 && jobID <= 9){
            sendMessage("SCHD "+ jobID + " xlarge " + serverID);
            serverID ++; //global variable for the server ID
            jobID ++; //global variable for the job ID
        }

        System.out.println("Server says: "+ this.inputStream.readLine());
        sendMessage("QUIT");
        System.out.println("Server says: "+ this.inputStream.readLine());

    }

    public void sendMessage(String message ) throws Exception{
        this.outStream.write( (message + "\n").getBytes("UTF-8"));
    }
}