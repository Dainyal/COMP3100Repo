import java.net.*;  
import java.io.*; 
public class Client {
    //create a socket
    Socket mySocket;
    //initialise input and output streams
    DataOutputStream outputStream;
    BufferedReader inputStream;
    //initialise server and job ID variables
    int serverID = 0;
    int nRecs = 0;
    int currentServerID = 0;
    int largestServerID = 0;
    int currentCore = 0;
    int largestCore = 0;
    int serverAmount = 0;
    int largestServerAmount = 0;
    int numJobs = 0;
    boolean firstTime = true;
    String sMessage = "";
    String sMessagePrev = ""; 
    String sMessageSecond = "";
    String strnRecs = ""; 
    String serverMessage = "";
    String largestServerName = "";
    String currentServerName = "";
    String jobType = "";
    String loopMessage = "";
    String[] arrJobType;
    String[] arrsMessage;
    String[] arrstrnRecs;
   

    public Client(String address, int port) throws Exception{
          mySocket = new Socket(address, port);
          outputStream = new DataOutputStream(mySocket.getOutputStream());
          inputStream = new BufferedReader(new InputStreamReader(mySocket.getInputStream()));  
    }

    public static void main(String[] args) throws Exception{
        Client c = new Client("192.168.0.69",50000);
        c.byClient();
        c.mySocket.close();
        c.inputStream.close();
        c.outputStream.close();
    }

    public void byClient() throws Exception{
        //send HELO to server
        send("HELO");
        //receive OK
        sMessage = this.inputStream.readLine();
        //authorise user
        String username = System.getProperty("user.name"); 
        send("AUTH " + username);
        sMessage = this.inputStream.readLine();
 
        while (!jobType.equals("NONE")) { 
            send("REDY"); 
            //server response
            loopMessage = this.inputStream.readLine(); //JOBN 37 0 653 3 700 3800
            arrJobType = loopMessage.split(" ");
            jobType = arrJobType[0]; 

            if (jobType.equals("JCPL")) { //If job complete, continue
                continue;
            }          

            if (firstTime == true) { //Only on first runthrough
                firstTime = false;
                //Request server state information
                send("GETS All");
                //Server response
                strnRecs = this.inputStream.readLine(); //DATA nRecs recLen
                //Converts to array of strings
                arrstrnRecs = strnRecs.split(" ");
                //Convert nRecs to int
                nRecs = Integer.parseInt(arrstrnRecs[1]); 
                //Acknowledge server
                send("OK");

                for (int i = 0; i < nRecs; i++) {
                    sMessage = this.inputStream.readLine();
                    arrsMessage = sMessage.split(" ");
                    currentServerName = arrsMessage[0];
                    currentServerID = Integer.parseInt(arrsMessage[1]);
                    currentCore = Integer.parseInt(arrsMessage[4]);
                    serverAmount++; 
                    //Replacing largest server type
                    if (!currentServerName.equals(largestServerName)) {
                        if (currentCore > largestCore) {
                            largestCore = currentCore;
                            largestServerName = currentServerName;
                            largestServerID = currentServerID;
                            serverAmount = 1;
                        }
                    }
                }          
                send("OK"); 
                read();    
            } 
            if (jobType.equals("JOBN")) {  
                send("SCHD " + numJobs + " " + largestServerName + " " + serverID);
                read();
                numJobs++;       
                serverID = (serverID + 1)%serverAmount;
            }          
        }
        send("QUIT");
        //receive QUIT
        read();
    }
    //Send Message
    public void send(String message) throws Exception{
        this.outputStream.write((message + "\n").getBytes("UTF-8"));
    }

    public void read() throws Exception {
        this.inputStream.readLine();
    }
}
