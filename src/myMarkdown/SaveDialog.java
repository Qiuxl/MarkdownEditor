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


//点击关闭的时候弹窗
public class SaveDialog extends JDialog {

	public SaveDialog(Frame owner, String title) {
		super(owner, title);
		
		setSize(400, 150);
		setVisible(true);
		setLocationRelativeTo(null);
		this.getContentPane().setBackground(Color.white);
		Gui temp = (Gui)owner;
		
		setLayout(null);

		JLabel info = new JLabel("The current file has been modified\nto save or not?");
		info.setFont(new Font("SansSerif", Font.PLAIN, 12));
		info.setBounds(40,0,400,60);
		getContentPane().add(info);
		
		JButton yes=new JButton("Yes");
		yes.setBounds(100,70,60,25);
		yes.setFont(new Font("SansSerif", Font.PLAIN, 12));
		yes.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				SaveDialog.this.dispose();
				temp.save();
				temp.setText("");
			}
			
		});
		getContentPane().add(yes);
		
		JButton no=new JButton("No");
		no.setBounds(220,70,60,25);
		no.setFont(new Font("SansSerif", Font.PLAIN, 12));
		no.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				SaveDialog.this.dispose();
				temp.setText("");
			}
			
		});
		getContentPane().add(no);
		// TODO Auto-generated constructor stub
	}

}
