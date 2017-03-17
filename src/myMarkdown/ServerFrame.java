package myMarkdown;

import java.awt.Font;
import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class ServerFrame extends JFrame {

	private Gui mGui;
	private JLabel portlabel = new JLabel("Input local port: ");
	private JButton okButton = new JButton("OK");
	private JButton cancelBtn = new JButton("Cancel");
	Font mFont = new Font("Seri", Font.PLAIN, 9);
	private JTextField addrField = new JTextField();
	private JTextField portField = new JTextField();
	public ServerFrame(Gui ui)
	{
		this.mGui = ui;
		setSize(300,200);
		setVisible(true);
		setLayout(null);
		portField.setBounds(112, 50, 150, 20);
		portlabel.setBounds(10, 50, 100, 20);
		okButton.setBounds(50, 90, 70, 30);
		cancelBtn.setBounds(150, 90, 70, 30);
		cancelBtn.setFont(mFont);
		okButton.setFont(mFont);
		add(portField);
		add(addrField);
		add(portlabel);
		add(okButton);
		add(cancelBtn);
		setTitle("Connect to remote");
		setLocationRelativeTo(null);
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				int port = Integer.parseInt(portField.getText().toString().trim());
				mGui.openServer(port);
		//		System.out.println(str+" "+port);
				ServerFrame.this.dispose();
				
			}
		});
		cancelBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				ServerFrame.this.dispose();
			}
		});
	}
	
	public ServerFrame() throws HeadlessException {
		// TODO Auto-generated constructor stub
	}

	public ServerFrame(GraphicsConfiguration arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	public ServerFrame(String arg0) throws HeadlessException {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	public ServerFrame(String arg0, GraphicsConfiguration arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

}
