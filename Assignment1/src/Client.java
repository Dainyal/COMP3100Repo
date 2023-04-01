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
    String[] job;
    String loopMessage = "";
    int currentServerID = 0;
    int firstServerID = 0;
    int currentCores = 0;
    int firstCore = 0;
    int serverAmount = 0;
    int count = 0;

   
    public Client(String address, int port) throws Exception{
          mySocket = new Socket(address, port);
          outStream = new DataOutputStream(mySocket.getOutputStream());
          inputStream = new BufferedReader(new InputStreamReader(mySocket.getInputStream()));  
    }

    public static void main(String[] args) throws Exception{
        Client c = new Client("192.168.120.197",50000);
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
            job = loopMessage.split(" ");
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
            System.out.println("line 68");

            for (int i = 0; i < nRecs; i++) {
                sMessage = this.inputStream.readLine();
                arrsMessage = sMessage.split(" ");
                currentServerName = arrsMessage[0];
                //joon
                currentServerID = Integer.parseInt(arrsMessage[1]);
                //2
                currentCores = Integer.parseInt(arrsMessage[4]);
                //6
                serverAmount++;
                if (!currentServerName.equals(largestServerName)) {
                    serverAmount = 1;
                }
                if (!currentServerName.equals(largestServerName) || (currentCores != firstCore)) {
                    largestServerName = currentServerName; //keep track of largest server name
                    //joon
                    firstServerID = currentServerID;
                    //1
                    firstCore = currentCores;
                    //6
                }
                // count++;
                // System.out.println("For loop: " + count);
                // System.out.println("job type: " + jobType[0]);
            }
            send("OK");
            if (job[0].equals("JOBN")) {
                send("SCHD" + job[2] + " " + largestServerName + " " + firstCore);
            }
        }

            
        //} 
            //server sends job list
             

            //Receive message: JOBN, JCPL, NONE
            //identify largest server type
            // while (serverID <= 9 && jobID <= 9){
            //     send("SCHD "+ jobID + " xlarge " + serverID);
            //     jobID ++; 
            //     serverID ++;      
            // }

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

//HELO
//AUTH DANIEL
//REDY
//GETS All
//OK
//QUIT


//  compile                  javac src\*.java -d bin
//  run java project         java -cp bin Client
//  run server               ./ds-server -c ../../configs/sample-configs/ds-sample-config01.xml -n -v all