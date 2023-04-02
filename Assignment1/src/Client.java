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
   
    int nRecs = 0;
    String sMessage = "";
    String sMessagePrev = "";
    String[] arrsMessage;
    String sMessageSecond = "";
    String strnRecs = "";
    String[] arrstrnRecs;
    String serverMessage = "";
    String largestServerName = "";
    String currentServerName = "";
    String[] arrJobType;
    String jobType = "";
    String loopMessage = "";
    int currentServerID = 0;
    int largestServerID = 0;
    int currentCore = 0;
    int largestCore = 0;
    int serverAmount = 0;
    int count = 0;
    int largestServerAmount = 0;
   
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
        send("HELO");
        System.out.println("Client message: HELO");

        //receive OK
        sMessage = this.inputStream.readLine();
        System.out.println("Server message: " + sMessage);

        //authorise user
        String username = System.getProperty("user.name"); 
        send("AUTH " + username);
        System.out.println("Client message: AUTH");

        //receive OK
        sMessage = this.inputStream.readLine();
        System.out.println("Server message: " + sMessage);
        System.out.println("Before while loop");


        while (!sMessage.equals("NONE")) {
           
            send("REDY"); 
            System.out.println("Client message: REDY");
            //server response
            loopMessage = this.inputStream.readLine();
            System.out.println("Server message: " + loopMessage); //JOBN 37 0 653 3 700 3800
            arrJobType = loopMessage.split(" ");
            jobType = arrJobType[0]; //JOBN

            //request server state information
            send("GETS All");
            System.out.println("Client message: GETS All"); 
            //server response
            strnRecs = this.inputStream.readLine();
            System.out.println("Server message: string is " + strnRecs); //e.g. DATA nRecs recLen
           
            arrstrnRecs = strnRecs.split(" "); //converts to array of strings
            nRecs = Integer.parseInt(arrstrnRecs[1]); //convert nRecs to int
            
            //acknowledge server
            send("OK");
            System.out.println("Client message: OK");
            //xxlarge 0 inactive -1 16
            for (int i = 0; i < nRecs; i++) {
                sMessage = this.inputStream.readLine();
                arrsMessage = sMessage.split(" ");
                currentServerName = arrsMessage[0];
                //xxlarge
                currentServerID = Integer.parseInt(arrsMessage[1]);
                //0
                currentCore = Integer.parseInt(arrsMessage[4]);
                //16
                serverAmount++; 

                //if new server name
                if (!currentServerName.equals(largestServerName)) {
                    largestServerAmount = serverAmount-1; //find amount of largest server type
                    if (currentCore > largestCore) {
                        largestCore = currentCore;
                        largestServerName = currentServerName;
                        largestServerID = currentServerID;
                        serverAmount = 1;
                    }
                }
            }
            send("OK");
            if (jobType.equals("JOBN")) {
                send("SCHD " + arrJobType[2] + " " + largestServerName + " " + largestServerAmount);
            }
        }

            
        send("OK");
        System.out.println("Client says: OK");

        System.out.println("Server message: "+ this.inputStream.readLine());
        //send QUIT
        send("QUIT");
        System.out.println("Client message: QUIT");
        //receive QUIT
        System.out.println("Server message: "+ this.inputStream.readLine());
    }

    public void send(String message) throws Exception{
        this.outStream.write((message + "\n").getBytes("UTF-8"));
    }
}

//job: type,id,submitTime,estRunTime,cores,memory,disk


//  compile                  javac src\*.java -d bin
//  run java project         java -cp bin Client
//  run server               ./ds-server -c ../../configs/sample-configs/ds-sample-config01.xml -n -v all