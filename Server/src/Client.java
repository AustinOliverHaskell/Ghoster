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


	// The form of communication between the server and the client 
	// is through a JSON object that is sent over the network. So 
	// this class should be able to talk to another class that will 
	// generate valid JSON and be able to parse recieved JSON. 	

	// The client  object should be able to create and talk to another 
	// class called DataBase (Which I havent made yet), that safely talks 
	// to the database. This needs to make sure that there isnt SQL code in
	// the sent JSON object. Additionally, the database will store passwords
	// with a salt and hash algorithm. Need to figure out how to transmit image
	// and video data over a network, I think that we can just send the file as
	// is since its all bytes in the end.s
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
