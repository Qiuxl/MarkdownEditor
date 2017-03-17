package myMarkdown;

import java.awt.Font;
import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class connectFrame extends JFrame {

	private Gui mGui;
	private JLabel hostLabel = new JLabel("Host Address: ");
	private JLabel portlabel = new JLabel("Port: ");
	private JButton okButton = new JButton("Connect");
	private JButton cancelBtn = new JButton("Cancel");
	Font mFont = new Font("Seri", Font.PLAIN, 9);
	private JTextField addrField = new JTextField();
	private JTextField portField = new JTextField();
	public connectFrame(Gui ui) throws HeadlessException {
		// TODO Auto-generated constructor stub
		this.mGui = ui;
		setSize(300,200);
		setVisible(true);
		setLayout(null);
		addrField.setBounds(112, 10, 150, 20);
		portField.setBounds(112, 50, 150, 20);
		hostLabel.setBounds(10, 10, 100, 20);
		portlabel.setBounds(60, 50, 50, 20);
		okButton.setBounds(50, 90, 70, 30);
		cancelBtn.setBounds(150, 90, 70, 30);
		cancelBtn.setFont(mFont);
		okButton.setFont(mFont);
		add(portField);
		add(addrField);
		add(hostLabel);
		add(portlabel);
		add(okButton);
		add(cancelBtn);
		setTitle("Connect to remote");
		setLocationRelativeTo(null);
		okButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				String str = addrField.getText().toString().trim();
				int port = Integer.parseInt(portField.getText().toString().trim());
				mGui.connect(str, port);
		//		System.out.println(str+" "+port);
				connectFrame.this.dispose();
				
			}
		});
		cancelBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				connectFrame.this.dispose();
			}
		});
		addWindowListener(new WindowListener() {
			
			@Override
			public void windowOpened(WindowEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowIconified(WindowEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowDeiconified(WindowEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowDeactivated(WindowEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowClosing(WindowEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowClosed(WindowEvent arg0) {
				// TODO Auto-generated method stub
				connectFrame.this.dispose();
			}
			
			@Override
			public void windowActivated(WindowEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	

}
