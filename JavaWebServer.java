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
 
public class JavaWebServer
{
 
    private static final int fNumberOfThreads = 100;
    private static final Executor fThreadPool = Executors.newFixedThreadPool(fNumberOfThreads);
 
    public static void main(String[] args) throws IOException
    {
    	final int portNumber = Integer.parseInt(args[0]);
    	final int serviceNum = (args.length > 1) ? Integer.parseInt(args[1]) : 0;
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
                    HandleRequest(connection, portNumber, serviceNum);
                }
            };
            fThreadPool.execute(task);
        }
    }
 
    private static void HandleRequest(Socket s, int portNumber, int serviceNum)
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
            out.println(getHttpReturnCode());            
	    out.println("Content-type: text/html");
            out.println("Server-name: myserver");
            String response = "Welcome to Random Web Server #" + serviceNum + "\n";
            out.println("Content-length: " + response.length());
            out.println("");
            out.println(response);
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

     private static String getHttpReturnCode() {
    	//String[] returnCodes = {"HTTP/1.1 200 OK", "HTTP/1.1 503 SERVICE UNAVAILABLE", "HTTP/1.1 504 GATEWAY TIMEOUT"};
        Random randomGenerator = new Random();
    	int randomInt = randomGenerator.nextInt(10); 
    	
    	if (randomInt < 6)
    		return "HTTP/1.1 200 OK";
    	else if (randomInt < 9)
    		return "HTTP/1.1 503 SERVICE UNAVAILABLE";
    	else 
    		return "HTTP/1.1 504 GATEWAY TIMEOUT";
    }
 
}