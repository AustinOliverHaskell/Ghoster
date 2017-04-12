import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.*;


public class Driver extends JFrame
{
	private JLabel recived;
	private JButton submit;
	private JTextField send;
	
	Driver()
	{
		recived = new JLabel("");
		submit = new JButton("Send");
		send = new JTextField();
		
		submit.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
			}
			
		});
		
		this.setSize(400, 400);
		this.setLayout(new BorderLayout());
		
		this.add(recived, BorderLayout.NORTH);
		this.add(submit, BorderLayout.SOUTH);
		this.add(send, BorderLayout.CENTER);
		
		this.setVisible(true);
	}
	
	
	public static void main(String[] args) {
		
		Driver d = new Driver();
		
		String serverAddress = "127.0.0.1";
		
		try 
		{
			Socket socket = new Socket(serverAddress, 8080);
			
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintWriter out = new PrintWriter(socket.getOutputStream());
			
			out.println("Hello,");
			out.println("World!");
			
			out.flush();
			
			while(true)
			{
				String str = in.readLine();
				
				if (str == null || str.equals("END"))
				{
					System.out.println("Recieved Kill Signal");
					out.println("END");
					break;
				}
			}
			
		} 
		catch (UnknownHostException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
