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
			ServerSocket socket = new ServerSocket(port);
			int clientCount = 0;
			
			System.out.println("Bound Socket, Waiting for connections...");
			
			
			while(true)
			{
				// Socket is created, now we just need to wait for a request
				
				
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
