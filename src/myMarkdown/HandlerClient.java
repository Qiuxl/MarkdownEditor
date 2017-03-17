package myMarkdown;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.Socket;
import java.nio.Buffer;

public class HandlerClient implements Runnable {
	private Socket sock;
	private boolean isConnected = true;
	StringBuffer stringBuffer = new StringBuffer();
	byte[] Sendbuffer = new byte[4096];
	char [] recvBuffer = new char[4096];
	int ret=0;
	Gui MainUi;
	BufferedReader Mread = null;
	Writer mwWriter;
	public HandlerClient(Gui Ui,Socket sock) {
		super();
		this.MainUi = Ui;
		this.sock = sock;
	}
	public void SetContent() {
		MainUi.setAutoSend(false);
		MainUi.setText(stringBuffer.toString());
		MainUi.setAutoSend(true);
	}
	public void sendText() {
		try {
			mwWriter.write(MainUi.getText()+"==~~OK~~==");
			mwWriter.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			Mread = new BufferedReader(new InputStreamReader(sock.getInputStream(),"UTF-8"));
			mwWriter = new OutputStreamWriter(sock.getOutputStream(),"UTF-8");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("could not get information from server");
		}
		String str;
		int index1;
		int index2;
		while(isConnected)
		{
			try {
				ret = Mread.read(recvBuffer);
				str = new String(recvBuffer, 0, ret);
				if((index1 = str.indexOf("==~~HELLO~~=="))!=-1)
				{
					mwWriter.write("==OK==");
					mwWriter.flush();
				}
				else if((index1 = str.indexOf("==~~BEGIN~~=="))!=-1&&
					(index2 = str.indexOf("==~~OK~~=="))!=-1)  //结束符号和开始符号都存在
				{
					if(stringBuffer.length()>0)
					{
						stringBuffer.delete(0, stringBuffer.length());
					}
					stringBuffer.append(str.substring(13, str.length()-10));
					SetContent();
				}
				else if((index1 = str.indexOf("==~~BEGIN~~=="))!=-1&&
						(index2 = str.indexOf("==~~OK~~==")) == -1)  //只有开始符号
				{
					if(stringBuffer.length()>0)
					{
						stringBuffer.delete(0, stringBuffer.length());
					}
					stringBuffer.append(str.substring(13, str.length()));
				}
				else if((index1 = str.indexOf("==~~BEGIN~~=="))==-1&&
						(index2 = str.indexOf("==~~OK~~==")) != -1)	   //只有结束符号
				{
					if(str.length()>10)
						stringBuffer.append(str.substring(10, str.length()));
					SetContent();
				}
				else{
					if((index1 = str.indexOf("==~~EXIT~~=="))!=-1)  //退出
					{
						isConnected = false;
						break;
					} 
					if((index1 = str.indexOf("==~~GET~~=="))!=-1)
					{
						sendText();
					}
					else{
						stringBuffer.append(str);
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				isConnected = false;
			}
		}
		try {
			Mread.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			sock.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

}
