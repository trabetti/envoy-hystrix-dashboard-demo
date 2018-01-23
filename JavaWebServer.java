/*
copied from http://library.sourcerabbit.com/v/?id=19
*/

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
 
public class JavaWebServer
{
 
    private static final int fNumberOfThreads = 100;
    private static final Executor fThreadPool = Executors.newFixedThreadPool(fNumberOfThreads);
 
    public static void main(String[] args) throws IOException
    {
    	if (args.length == 0) {
    		errMessage();
    		return;
    	}
    	
    	final int portNumber = Integer.parseInt(args[0]);
    	final int errorPercantage = (args.length > 1) ? Integer.parseInt(args[1]) : 0;
    	final int timeoutPercantage = (args.length > 2) ? Integer.parseInt(args[2]) : 0;
    	final String serviceNum = (args.length > 3) ? args[3] : "";
       ServerSocket socket = new ServerSocket(portNumber);
 //        ServerSocket socket = new ServerSocket(81);

       while (true)
        {
            final Socket connection = socket.accept();
            Runnable task = new Runnable()
            {
                @Override
                public void run()
                {
                    HandleRequest(connection, portNumber, errorPercantage, timeoutPercantage, serviceNum);
                }
            };
            fThreadPool.execute(task);
        }
    }
 
    private static void HandleRequest(Socket s, int portNumber, 
    		int errorPercantage, int timeoutPercantage, String serviceNum)
    {
        BufferedReader in;
        PrintWriter out;
        String request;

        try
        {
            String webServerAddress = s.getInetAddress().toString();
            System.out.println("New Connection on service " + serviceNum + ":" + webServerAddress);
            in = new BufferedReader(new InputStreamReader(s.getInputStream()));
 
            request = in.readLine();
            System.out.println("--- Client request: " + request + ":" + portNumber);
 
            out = new PrintWriter(s.getOutputStream(), true);
//            out.println("HTTP/1.0 200");
            out.println(getHttpReturnCode(errorPercantage, timeoutPercantage));            
	    out.println("Content-type: text/html");
            out.println("Server-name: myserver");
            String response = "Welcome to Random Web Server #" + serviceNum + "\n";
//            out.println("Content-length: " + response.length());
            out.println("Content-length: " + 0);
            out.println("");
            //out.println(response);
            out.flush();
            out.close();
            s.close();
        }
        catch (IOException e)
        {
            System.out.println("Failed respond to client request: " + e.getMessage());
        }
        finally
        {
            if (s != null)
            {
                try
                {
                    s.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
        return;
    }


     // return random code
     // based on weights
     // currently hard coded:
     // 60% 200 OK
     // 30% 503 SERVICE UNAVAILABLE
     // 10% 10s delay - to trigger timeout
     private static String getHttpReturnCode(int errorPercantage, int timeoutPercantage) {
    	//String[] returnCodes = {"HTTP/1.1 200 OK", "HTTP/1.1 503 SERVICE UNAVAILABLE", "HTTP/1.1 504 GATEWAY TIMEOUT"};
        Random randomGenerator = new Random();
    	int randomInt = randomGenerator.nextInt(100); 
    	
	if (randomInt < timeoutPercantage) {
    		// timeout
    		try {
				TimeUnit.SECONDS.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		return "HTTP/1.1 200 OK";
 	} 
 	else if (randomInt < (errorPercantage+timeoutPercantage))
    		return "HTTP/1.1 503 SERVICE UNAVAILABLE";
	else
   		return "HTTP/1.1 200 OK";
    }
     
     private static void errMessage() {
    	 System.out.println("Usage: JavaWebServer portNumber [errorPercantage] [ServiceNumber]");
     }
 
}