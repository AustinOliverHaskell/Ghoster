import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client extends Thread
{
	// ----- Private Data -----
	private Socket socket;
	private long clientNumber;
	// ------------------------

	// ----- Constructors -----
	Client(Socket socket, long clientNumber)
	{
		this.socket = socket;
		this.clientNumber = clientNumber;
	}
	// ------------------------

	
	@Override
	public void run()
	{
		System.out.println("Client Created and Threaded: #"+clientNumber);
		
		try 
		{
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintWriter   out = new PrintWriter(socket.getOutputStream(), true);
			
			while(true)
			{
				String input = in.readLine();
				
				if (input == null || input.equals("END"))
				{
					System.out.println("Sending Kill signal to the client");
					out.println("END");
					break;
				}
				
				System.out.println("Recieved: " + input);
			}
			
			
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		finally 
		{
			try 
			{
				System.out.println("Closing connection to " + clientNumber);
				socket.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
}
