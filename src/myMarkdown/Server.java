package myMarkdown;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.PublicKey;
import java.util.ArrayList;

public class Server {

	private Gui mgGui;
	private static int ThreadCount = 0;
	static int ServerPort = 3000;
	ArrayList<Socket> clentInfo = new ArrayList<Socket>();
	
	private ServerSocket MyServer;
	public Server(Gui ui,int port) throws IOException {
		// TODO Auto-generated constructor stub
		this.mgGui = ui;
		this.ServerPort = port;
			MyServer = new ServerSocket(ServerPort);
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				while(true)
				{
					try {
						Socket temp = MyServer.accept();  //异常退出了告诉各个连接端服务器断开了
						clentInfo.add(temp);
				//		System.out.println("This is Server:");
				//		System.out.println(temp.getLocalPort()+" "+temp.getPort()+" ");
						ThreadCount++;
						new Thread(new HandlerClient(mgGui, temp)).start();
					} catch (IOException e) {   //socket退出了，
						// TODO Auto-generated catch block
						e.printStackTrace();
						break;
					}
					

				}
			}
		}).start();
		
	}
	public void close()
	{
		if(MyServer.isClosed())
			return;
		for(Socket e:clentInfo)
		{
			if(e !=null &&!e.isClosed())
				{
					try {
						e.close();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
						System.out.println("Error happen when closing client connecting");
					}
				}
		}
		try {
			MyServer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Already close!");
			e.printStackTrace();
		}
	}
}
