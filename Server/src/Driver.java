import java.io.IOException;
import java.net.*;

// Main
public class Driver
{
	
	private static final int port = 8080;
	
	public static void main(String[] args)
	{
		try
		{
			// TODO Make this secure with SSL
			ServerSocket socket = new ServerSocket(port);
			int clientCount = 0;
			
			System.out.println("Bound Socket, Waiting for connections...");
			
			
			while(true)
			{				
				// The client object handles client requests and is also multithreaded
				// Need to make sure that Client object also communicates through SSL	
				new Client(socket.accept(), clientCount).start();
				clientCount++;
			}

		} 
		catch (UnknownHostException error) 
		{
			error.printStackTrace();
		}
		catch (IOException error)
		{
			error.printStackTrace();
		}
		

		
		
		
		
	}
}
