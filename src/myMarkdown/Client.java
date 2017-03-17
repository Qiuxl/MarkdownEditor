package myMarkdown;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.InetAddress;
import java.net.Socket;

import javax.swing.tree.DefaultTreeCellEditor.EditorContainer;

import support.BlankLabel;


public class Client {

	String host_url;
	String host_addr;
	int serverport;
	public Socket client;
	private InetAddress address;
	BufferedReader Mread = null;
	Writer mwWriter;
	private int ret,index;
	private StringBuffer stringBuffer = new StringBuffer();
	private String str;
	private char [] recvBuffer = new char[4096];
	public Client(String ip,int port) {
		this.host_addr = ip;
		this.host_addr = ip;
		this.serverport = port;
		// TODO Auto-generated constructor stub
		try {	
			client = new Socket(host_addr,port);
			Mread = new BufferedReader(new InputStreamReader(client.getInputStream(), "UTF-8"));
			mwWriter = new OutputStreamWriter(client.getOutputStream(),"UTF-8");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			new MessegeDialog(null, "Connect Failed");
		}
		if(client !=null)
		{
			new MessegeDialog(null, "Connect Success");
	//		System.out.println("This client:");
	//		System.out.println(client.getLocalPort()+" "+client.getInetAddress()+" "+client.getLocalAddress());
		}
	}
	public boolean IsAlive() {
		
		if(client == null)
			return false;
		else
		{
			try {
				mwWriter.write("==~~HELLO~~==");
				mwWriter.flush();
				String string = Recv();
			//	System.out.println(string);
				if(string.indexOf("==OK==")< 0)
					return false;
			} 
			catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				return false;
			}
			return true;
		}
	}
	//往服务器发送消息
	public boolean send(String str)
	{
		if(IsAlive())
		{
			try {
				mwWriter.write(str);
				mwWriter.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else{
			new MessegeDialog(null, "Connection has been closed.Please connect again");
			return false;
		}
		return true;
	}
	public void close() {
		try {
			send("==~~EXIT~~==");
			client.close();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public String Recv()
	{
	//获取服务器端的文档
		if(stringBuffer.length()>0)
			stringBuffer.delete(0, stringBuffer.length());
		while(true)
		{
			try {
				ret = Mread.read(recvBuffer);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return "ERROR";
			}
			str = new String(recvBuffer,0,ret);
			if((index=str.indexOf("==~~OK~~=="))!=-1)
			{
				if(index != 0)
					stringBuffer.append(str.substring(0,index));
				break;
			}
			else if((index=str.indexOf("==OK=="))!=-1)
				{
					stringBuffer.append(str);
					break;  //回复还活着的消息直接跳出
				}
			else{
				stringBuffer.append(str);	
			}
		}
		return stringBuffer.toString();
	}

}
