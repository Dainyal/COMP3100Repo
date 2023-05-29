import java.net.*;  
import java.io.*; 
public class Client {
    //create a socket
    Socket mySocket;
    //initialise input and output streams
    DataOutputStream outputStream;
    BufferedReader inputStream;
    //initialise server and job ID variables
    int nRecs = 0;
    int numJobs = 0;
    String sMessage = "";
    String strnRecs = ""; 
    String jobType = "";
    String loopMessage = "";
    String firstServerType = "";
    String firstServerID = "";
    String[] arrJobType;
    String[] arrstrnRecs;
    String[] arrFirstServer;
    
    public Client(String address, int port) throws Exception{
          mySocket = new Socket(address, port);
          outputStream = new DataOutputStream(mySocket.getOutputStream());
          inputStream = new BufferedReader(new InputStreamReader(mySocket.getInputStream()));  
    }

    public static void main(String[] args) throws Exception{
        Client c = new Client("localhost",50000);
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

            if (jobType.equals("JCPL")|| jobType.equals("NONE")) { //If job complete, continue
                continue;
            }          

                //Request server state information
                send("GETS Avail " + arrJobType[4] + " " + arrJobType[5] + " " + arrJobType[6]); //GETS Avail core memory disk
                //Server response
                strnRecs = this.inputStream.readLine(); //DATA nRecs recLen
                //Converts to array of strings
                arrstrnRecs = strnRecs.split(" ");
                //Convert nRecs to int
                nRecs = Integer.parseInt(arrstrnRecs[1]); 
                //Acknowledge server
                send("OK");
                
                if (nRecs != 0) {     
                    //Choosing first/best server
                    sMessage = this.inputStream.readLine();
                    arrFirstServer = sMessage.split(" ");
                    firstServerType = arrFirstServer[0]; //juju
                    firstServerID = arrFirstServer[1]; //3
                                   
                    //reading the rest of the servers
                    for (int i = 0; i < nRecs-1; i++) {
                        read();
                    }          
                    send("OK"); 
                    read();   
                } 
                else if (nRecs == 0){
                    read();
                    send("GETS Capable " + arrJobType[4] + " " + arrJobType[5] + " " + arrJobType[6]); //GETS Capable core memory disk
                   
                    strnRecs = this.inputStream.readLine(); //DATA nRecs recLen
                    //Converts to array of strings
                    arrstrnRecs = strnRecs.split(" ");
                    //Convert nRecs to int
                    nRecs = Integer.parseInt(arrstrnRecs[1]); 
                    //Acknowledge server
                    send("OK");

                    //Choosing first/best server
                    sMessage = this.inputStream.readLine();
                    arrFirstServer = sMessage.split(" ");
                    firstServerType = arrFirstServer[0]; //juju
                    firstServerID = arrFirstServer[1]; //3

                    //reading the rest of the servers
                    for (int i = 0; i < nRecs-1; i++) {
                        read();
                    }     
                    send("OK");
                    read();
                }
            if (jobType.equals("JOBN")) {  
                send("SCHD " + numJobs + " " + firstServerType + " " + firstServerID);
                read();
                numJobs++;       
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