package myMarkdown;

import java.awt.Color;
import java.awt.Dialog;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextArea;

public class MessegeDialog extends JDialog {

	public MessegeDialog(Frame owner, String msg) {
		super(owner, "Information");
		
		setSize(380, 150);
		setVisible(true);
		setLayout(null);
		setLocationRelativeTo(null);
		this.getContentPane().setBackground(Color.white);
		
		JTextArea info = new JTextArea(msg);
		info.setEditable(false);
		info.setColumns(8);
		info.setLineWrap(true);
		info.setWrapStyleWord(true);
		info.setFont(new Font("SansSerif", Font.BOLD, 13));
		info.setBounds(40,0,300,60);
		getContentPane().add(info);
		
		JButton yes=new JButton("OK");
		yes.setBounds(140,70,60,25);
		yes.setFont(new Font("SansSerif", Font.PLAIN, 12));
		yes.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				MessegeDialog.this.dispose();
			}
			
		});
		// TODO Auto-generated constructor stub
		getContentPane().add(yes);
	}

}
