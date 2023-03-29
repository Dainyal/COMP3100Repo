import java.net.*;  
import java.io.*; 
public class Client {
    //create a socket
    Socket mySocket;
    //initialise input and output streams
    DataOutputStream outStream;
    BufferedReader inputStream;
    //initialise server and job ID variables
    int serverID = 0;
    int jobID = 0;
    int nRecs = 0;

   
    public Client(String address, int port) throws Exception{
          mySocket = new Socket(address, port);
          outStream = new DataOutputStream(mySocket.getOutputStream());
          inputStream = new BufferedReader(new InputStreamReader(mySocket.getInputStream()));  
    }

    public static void main(String[] args) throws Exception{
        Client c = new Client("192.168.0.69",50000);
        c.byClient();
        //close socket
        c.mySocket.close();
        c.inputStream.close();
        c.outStream.close();
    }


    public void byClient() throws Exception{
        //send HELO to server
        sendMessage("HELO");
        //receive OK
        System.out.println("Server says: "+this.inputStream.readLine());
        //authorise user
        String username = System.getProperty("user.name"); 
        sendMessage("AUTH " + username);
        //receive OK
        System.out.println("Server says: "+ this.inputStream.readLine());

        //while (message !NONE) {

            //jobs 1-n

            //send REDY
            sendMessage("REDY"); //when we send ready server sends us an update, usually a job from the USER side of the server
                                        //if I need get JCPL -> message from the Server Side of the simulator 


            //Receive message: JOBN, JCPL, NONE
            //identify largest server type
            while (serverID <= 9 && jobID <= 9){
                sendMessage("SCHD "+ jobID + " xlarge " + serverID);
                jobID ++; 
                serverID ++;      
            }

            //send gets message             e.g. GETS All
            //receive DATA nRecs recSize    e.g. DATA 5 124
            //send OK

            // for (int i = 0; i < nRecs; i++) {
                //receive each record
                //keep track of largest server type and number of servers of that type
            // }
            //send OK
            //receive .
            // if (message == JOBN) {
                //sendMessage("SCHD ")  schedule job
            //}
        //}
    
        System.out.println("Server says: "+ this.inputStream.readLine());
        //send QUIT
        sendMessage("QUIT");
        //receive QUIT
        System.out.println("Server says: "+ this.inputStream.readLine());
    }

    public void sendMessage(String message ) throws Exception{
        this.outStream.write( (message + "\n").getBytes("UTF-8"));
    }
}

//  compile java project        java -cp bin DsClient
//  run server                  ./ds-server -c ../../configs/sample-configs/ds-sample-config01.xml -n -v all