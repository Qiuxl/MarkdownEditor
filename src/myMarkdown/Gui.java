package myMarkdown;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.MenuShortcut;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.geom.FlatteningPathIterator;
import java.awt.image.ByteLookupTable;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.KeyStore.PrivateKeyEntry;
import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.lang.model.element.Element;
import javax.naming.spi.DirStateFactory.Result;
import javax.sql.rowset.spi.SyncFactoryException;
import javax.swing.Action;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.TableModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.StyledEditorKit.BoldAction;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;


import org.markdown4j.Markdown4jProcessor;

import layout.TableLayout;

/**
 *
 *   /Icon/app_icon.png
 *   /Icon/app_icon.png
 *   /Icon/icon_open.png
 * 
 * 
 */
public class Gui extends JFrame {
	
	
	public boolean isEdited = false; //作为编辑与否的标记 
	public boolean isSave = false;  // 作为保存与否的标记
	private boolean isConnected = false;
//	public  boolean isRefreshEnable = true;  //这里设置为public，接收到的时候设置不刷新
	private boolean isAutoSend = false;  //设置开启自动刷新功能与否
	//private boolean isUploadInfoEnable = true;
	private boolean isServerServiceOpen = false;
	private boolean DoubleScrollEnable = true;  //标记同步滚动的功能开启与否，编辑的时候同步滚动设置为false，当用户自己滚动的时候才开启
	
	private int serverPort = -1;  //记录服务器所开端口
	private String remoteHost = null;
	private Server mServer=null;
	private Lock EditLock = new ReentrantLock();   //编辑框设置的时候的lock
	private Lock ReadLock = new ReentrantLock();    //读取栏的lock
	private Lock ChangeLock = new ReentrantLock();
	
	//用于匹配的正则表达式 <h?> </h?>
	private String regx1 = "<h1>(.*)</h1>|<h2>(.*)</h2>|<h3>(.*)</h3>|<h4>(.*)</h4>|<h5>(.*)</h5>";
	private String regx2 = "#{1}.*\n|#{2}.*\n|#{3}.*\n|#{4}.*\n|#{5}.*\n";
	
	
	private String style = null; // 作为记录用户导入的css文件的一个设置，如果不为空的时候，必须
	private Pattern r = Pattern.compile(regx1);
	private Pattern s = Pattern.compile(regx2);
	private JScrollBar jscrollBar1;
	private ArrayList<Integer> recordline = new ArrayList<Integer>();
	private int titlenum=0;  //记录多少个
	private String oldtext = null;
	private String currentFile;  //这里指的是完整的路径名称
	private String path = "/Icon/app_icon.png";
    private Image mainIcon;
    private int totallines = 0;
    private int totalchars = 0;
    private int indexOfCursor = 0;  //移动的时候用的位置
    private Document document = null;
    
    private boolean enableDocListener = true;  //打开文件的 时候这俩设置为false，结束之后再刷新
    private boolean enableEditlistern = true;  
    private Client mClient = null;
    private int sroll1Pos,sroll1Max;  //滚动条1的位置和最大值
    private int sroll2Pos=0,sroll2Max=0;  //滚动条2的位置和最大值
    
    Markdown4jProcessor totalpro = new Markdown4jProcessor();
	JMenuBar topMenu;
	Container c;  
	Font textStyle = new Font("微软雅黑",Font.BOLD, 12);
	Font textStyle1 = new Font("SansSerif", Font.BOLD, 11);
	Icon icon_open = new ImageIcon("./Icon/icon_open1.png");
	JToolBar topTool;
	
	JLabel numofchar = new JLabel("Characters: "+"0");
	JLabel cols = new JLabel("Column: "+"1");
	JLabel lines = new JLabel("Lines: "+ "1");
	JLabel warn = new JLabel();
	
	DefaultListModel<String> defaultListModel = new DefaultListModel<String>();   
    
	JList<String> mList = new JList<String>();
	
	TextArea naviArea = new TextArea(1000,40);
	JEditorPane areaShow = new JEditorPane();
	JTextArea textedit  = new JTextArea();
	JTextArea lineArea = new JTextArea();
	
	public Gui() {
		// TODO Auto-generated constructor stub
		try {
			mainIcon = ImageIO.read(this.getClass().getResource("/Icon/app_icon.png"));
			setIconImage(mainIcon);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		areaShow.setEditable(false);// 右边不可编辑
		InitializeMenu();
//		InitializeEdit();
		
		mList.setModel(defaultListModel);
		mList.setFont(new Font("华文楷体",Font.BOLD, 14));
		InitializeTable();
		InitiaBottom();
		
		
	//	textedit.setColumns(35);
	 //   textedit.setLineWrap(true);        //激活自动换行功能 
	//    textedit.setWrapStyleWord(true);            // 激活断行不断字功能
		lineArea.setText("1\n"); 
		lineArea.setEditable(false);    //不能编辑
		
		lineArea.setBackground(new Color(0xe0,0xee,0xe0));   //E0EEE0
		lineArea.setFont(new Font("楷体", Font.PLAIN, 16));
		lineArea.setForeground(new Color(0x4F,0x4F,0x4F)); //7FFF00  4F4F4F
		lineArea.setCaretColor(new Color(0x99,0x99,0x99)); //C6E2FF 
	    this.setJMenuBar(topMenu);
		this.add(topTool,BorderLayout.NORTH);
		this.pack();
	//	Server mServer = new Server(this);
		//导航栏双击的时候的反应函数
		mList.addMouseListener(new MouseAdapter() {
		    public void mouseClicked(MouseEvent e){  
		        if(e.getClickCount()==2){   //When double click JList  
		            On_double_click();
		        }  
		    }  
		});
		addWindowListener(new WindowAdapter(){
			   @Override
			   public void windowClosing(WindowEvent e) {
			     //关闭文件的时候最好使用file->close
				   System.exit(0);  //直接关闭不保存的
	/*			  if(isEdited == true){
					  
					  int option= JOptionPane.showConfirmDialog( 
				                  Gui.this, "The current file has been modified\nto save or not? ", "提示 ",JOptionPane.YES_NO_OPTION); 
				      if(option==JOptionPane.YES_OPTION) 
				         if(e.getWindow()   ==   Gui.this) 
				         { 
				        	 save();
			//	        	 mServer.close();
				        	 System.exit(0); 
				         } 
				         else 
				         { 
				        	 return; 
				         
				         } 
				   } */
			   }
			  });
//这里新开的询问是否关闭的线程开始运行就结束，没有等到用户选择
//		this.addWindowListener(new WindowListener() {
//			
//			@Override
//			public void windowOpened(WindowEvent e) {
//				// TODO Auto-generated method stub
//				
//			}
//			
//			@Override
//			public void windowIconified(WindowEvent e) {
//				// TODO Auto-generated method stub
//				
//			}
//			
//			@Override
//			public void windowDeiconified(WindowEvent e) {
//				// TODO Auto-generated method stub
//				
//			}
//			
//			@Override
//			public void windowDeactivated(WindowEvent e) {
//				// TODO Auto-generated method stub
//				
//			}
//			
//			@Override
//			public void windowClosing(WindowEvent e) {
//				// TODO Auto-generated method stub		
//				if(!isEdited)
//				{
//					System.exit(0);
//				}
//				if(isEdited)
//				{
//					System.out.println("111");
//					save_on_close();
//				}
//					
//			}
//			
//			@Override
//			public void windowClosed(WindowEvent e) {
//				// TODO Auto-generated method stub
//			}
//			
//			@Override
//			public void windowActivated(WindowEvent e) {
//				// TODO Auto-generated method stub
//				
//			}
//		});
		document = textedit.getDocument();
		document.addDocumentListener(new DocumentListener() {
		
			@Override
			public void removeUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				if(isConnected&&isAutoSend)  //连接成功并且自动刷新开启
				{
					upload(false);
				}
				   if(totalchars<=200000)
				   {
						isEdited = true;
						on_Conten_change();
				   }
				   else {
					new Thread(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							isEdited = true;
							System.out.println("getting change lock");
							on_Conten_change();
						}
					});	
				   }
				
			}
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				//只有从事件派发线程才能更新组件。
				// TODO Auto-generated method stub
					if(isConnected&&isAutoSend)  //连接成功并且自动刷新开启
					{
						upload(false);
					}
				   if(totalchars<=200000)
				   {
						isEdited = true;
						on_Conten_change();
				   }
				   else {
					new Thread(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							isEdited = true;
							System.out.println("getting change lock");
							on_Conten_change();
						}
					});	
				   }
			}
			
			@Override
			public void changedUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				if(isConnected&&isAutoSend)  //连接成功并且自动刷新开启
				{
					upload(false);
				}
			   if(totalchars<=200000)
			   {
					isEdited = true;
					on_Conten_change();
			   }
			   else {
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						isEdited = true;
						System.out.println("getting change lock");
						on_Conten_change();
					}
				});	
			   }
			}
		});
		
	//   设置事件监听器，动态显示行数和列数  
		textedit.addCaretListener(new CaretListener() {
			
			@Override
			public void caretUpdate(CaretEvent e) {
				if(enableEditlistern==false)
				{
					System.out.println("Lock by user");
					return;
				}
				// TODO Auto-generated method stub
				try {
					int pos = textedit.getCaretPosition();
	                int lines = textedit.getLineOfOffset(pos) + 1; 
	                //获取列数 
	                int col = pos - textedit.getLineStartOffset(lines-1);
	                cols.setText("Column "+col);
	                Gui.this.lines.setText("Line "+lines);

				} catch (Exception e2) {
					// TODO: handle exception
					warn.setText("No cursor info");
				}
			}
		});
	}
	
	
	public void setAutoSend(boolean bool)
	{
		this.isAutoSend = bool;
	}
	
	public String getText()
	{
		return textedit.getText();
	}
	public void setText(String str)
	{
		EditLock.lock();
		textedit.setText(str);
		EditLock.unlock();
	}
	
	public void caretUpdate() {
		// TODO Auto-generated method stub
		try {
			int pos = textedit.getCaretPosition();
            int lines = textedit.getLineOfOffset(pos) + 1; 
            //获取列数 
            int col = pos - textedit.getLineStartOffset(lines-1);
            cols.setText("Column "+col);
            Gui.this.lines.setText("Line "+lines);
            int line2 = lineArea.getLineCount();
            if(line2>=999)
            	;
            if(line2<=lines)
            {
            	for(;line2<=lines;line2++)
            		lineArea.append(line2+"\n");
            }
            else{
            	//重新绘制图案
            	lineArea.setText("");
            	for(int i=1;i<=lines;i++)
            		lineArea.append(i+"\n");
            }
		} catch (Exception e2) {
			// TODO: handle exception
			warn.setText("No cursor info");
		}
		
	}
	public void changeCSS () throws IOException {
		FileDialog fileDialog = new FileDialog(this, "choose a css file", FileDialog.LOAD);
		fileDialog.setVisible(true);
		if (fileDialog.getDirectory() == null || fileDialog.getFile() == null)
			return;
		File css = new File(fileDialog.getDirectory() + fileDialog.getFile());
		
		BufferedReader in = 
			new BufferedReader(
				new FileReader(css));
		String line;
		StringBuilder stringBuilder = new StringBuilder();
		while ((line=in.readLine()) != null) {
			stringBuilder.append(line);
		}
		in.close();
		style = "<style>"+stringBuilder.toString()+"</style>";
		areaShow.setText(style+areaShow.getText().toString());  //重新刷新格式，如果之前设置过不知道是不是会存在冲突，待定
		// editorPane.setText(style + html);
	}
	//初始化窗体的菜单栏
	private void InitializeMenu()
	{
        //创建内容面板  
        
		KeyStroke openKS = KeyStroke.getKeyStroke("ctrl O");// 定义一个ctrl + b的快捷键 
		KeyStroke saveAsKS = KeyStroke.getKeyStroke("shift s");// 定义一个ctrl + b的快捷键 
        c=this.getContentPane();  
        
		topMenu = new JMenuBar();  //最顶层的菜单
		topTool = new JToolBar();  //工具栏
		JButton NewBtn = new JButton(new ImageIcon(this.getClass().getResource("/Icon/icon_new.png")));
		JButton OpenBtn = new JButton(new ImageIcon(this.getClass().getResource("/Icon/icon_open1.png")));
		JButton SaveBtn = new JButton(new ImageIcon(this.getClass().getResource("/Icon/icon_save.png")));
		JButton exportBtn = new JButton(new ImageIcon(this.getClass().getResource("/Icon/icon_export.png")));
		JButton connectBtn = new JButton(new ImageIcon(this.getClass().getResource("/Icon/connect_icon.png")));
		JButton uploadBtn = new JButton(new ImageIcon(this.getClass().getResource("/Icon/upload_icon.png")));
		JButton downloadBtn = new JButton(new ImageIcon(this.getClass().getResource("/Icon/download_icon.png")));
		JButton disconnectBtn = new JButton(new ImageIcon(this.getClass().getResource("/Icon/disconnect_icon.png")));		
		
		
		
		NewBtn.setBorderPainted(false);
		OpenBtn.setBorderPainted(false);
		exportBtn.setBorderPainted(false);
		SaveBtn.setBorderPainted(false);
		connectBtn.setBorderPainted(false);
		uploadBtn.setBorderPainted(false);
		downloadBtn.setBorderPainted(false);
		disconnectBtn.setBorderPainted(false);
		
		JMenu file = new JMenu("File");
		JMenu help = new JMenu("Help");
		JMenu remote = new JMenu("Remote");
		JMenu ServerMenu = new JMenu("Server");
		JMenu CssOption = new JMenu("CssOption");  //导入css文件或者使用默认文件的接口
		JMenu SyncOption = new JMenu("实时同步选项");
		
		
		JMenuItem connectItem = new JMenuItem("Connect");
		JMenuItem uploadItem = new JMenuItem("Upload");
		JMenuItem downItem = new JMenuItem("Pull");
		JMenuItem DisconItem = new JMenuItem("Disconnect");
		
		JMenuItem openItem = new JMenuItem("Open",icon_open);	
		JMenuItem saveItem = new JMenuItem("Save",new ImageIcon("./Icon/icon_save.png"));
		JMenuItem saveAsItem = new JMenuItem("Save as");
		JMenuItem closeItem = new JMenuItem("Close");
		JMenuItem NewFileItem = new JMenuItem("New File",new ImageIcon("./Icon/icon_new.png"));
		JMenuItem ImportCss = new JMenuItem("Import Css File");   // 提供给用户导入自己的css文件的按钮
		
		JMenuItem serverOpenItem = new JMenuItem("Open as a Server");
		JMenuItem serverCloseItem = new JMenuItem("Close Server service");
		
		JMenuItem defaultCssItem = new JMenuItem("Use Default");
		
		JMenu exportItem = new JMenu("Export file");
		JMenu submenu = new JMenu();
		JMenuItem export_dox = new JMenuItem("1.Docx File");
		JMenuItem export_html = new JMenuItem("2.Html File");
		
		JMenuItem AutoRefresh = new JMenuItem("开启");
		JMenuItem RefreshByhand = new JMenuItem("关闭");
		AutoRefresh.setFont(textStyle);
		RefreshByhand.setFont(textStyle);
		
		SyncOption.add(AutoRefresh);
		SyncOption.addSeparator();
		SyncOption.add(RefreshByhand);
		
		
		JMenuItem About = new JMenuItem("About this");
		submenu.add(export_dox);
		submenu.add(export_html);
		
		exportItem.add(submenu);
		
		saveAsItem.setAccelerator(saveAsKS);
		openItem.setAccelerator(openKS);
		uploadItem.setAccelerator(KeyStroke.getKeyStroke('R', InputEvent.CTRL_MASK));  //刷新的功能
		NewFileItem.setAccelerator(KeyStroke.getKeyStroke('N', InputEvent.CTRL_MASK));
		saveItem.setAccelerator(KeyStroke.getKeyStroke('S', InputEvent.CTRL_MASK));
	//	exportItem.setAccelerator(KeyStroke.getKeyStroke('W', InputEvent.CTRL_MASK));
//添加监听事件
		
		AutoRefresh.addActionListener(e->{
			this.isAutoSend = true;
			
		});
		RefreshByhand.addActionListener(e->{
			this.isAutoSend = false;
			
		});
		
		//断开与远程的连接函数
		disconnectBtn.addActionListener(e->{
			
			DisConnect();
		}
		);
		DisconItem.addActionListener(e->{
			DisConnect();
			
		});
		defaultCssItem.addActionListener(e->{
			if(style == null)
				return;
			else
			{
				InitializeDefaultCss();
				style = null;  //值为空
				try {
					areaShow.setText(totalpro.process(textedit.getText().toString()));
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					new MessegeDialog(Gui.this, "Reuse default Css failed");
					e1.printStackTrace();
				}
			}			
		});
		ImportCss.addActionListener(e->{
			try {
				changeCSS();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				new MessegeDialog(Gui.this, "Import css failed");
			}
		});
		openItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				Open_file();
			}
		});
//save as的监听事件
		saveAsItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				save_as();
			}
		});
//save 的监听事件
		saveItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				save();
			}
		});
//new file的监听事件
		NewFileItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				CreateFile();
			}
//程序总窗体的监听事件
		
		});
//远程连接的监听事件
		connectItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				if(!isConnected) //尚未连接
				{
					new connectFrame(Gui.this);
				}
				else{
					  int option= JOptionPane.showConfirmDialog( 
			                  Gui.this, "You have connected to "+Gui.this.remoteHost+" Disconnect it and reconnect?", "提示 ",JOptionPane.YES_NO_OPTION); 
			      if(option==JOptionPane.YES_OPTION) 
			      {
			    	  DisConnect();
			    	  new connectFrame(Gui.this);
			      } 
				}
			}
		});
//更新远程监听事件
		uploadItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				upload(true);
			}
		});
//断开连接的监听事件
		disconnectBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				DisConnect();
				new MessegeDialog(Gui.this, "Disconnect success");
			}
		});
// 获取服务器端的文档
		downItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				GetRemote();
			}
		});
//以下是几个工具栏按钮的响应函数
		NewBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				CreateFile();
			}
		});
		SaveBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				save();
			}
		});
		OpenBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				Open_file();
			}
		});
		connectBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if(!isConnected) //尚未连接
				{
					new connectFrame(Gui.this);
				}
				else{
					  int option= JOptionPane.showConfirmDialog( 
			                  Gui.this, "You have connected to "+Gui.this.remoteHost+"Disconnect it and reconnect?", "提示 ",JOptionPane.YES_NO_OPTION); 
			      if(option==JOptionPane.YES_OPTION) 
			      {
			    	  DisConnect();
			    	  new connectFrame(Gui.this);
			      } 
				}
			}
		});
		uploadBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				upload(true);
			}
		});
		downloadBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				GetRemote();
			}
		});
		disconnectBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				DisConnect();
			}
		});
//导出docx文件
		export_dox.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				export_docx();
			}
		});
//导出html文件
		export_html.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				Ex_Html();
			}
		});
		closeItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				if(!isEdited)
				{
					textedit.setText("");
					areaShow.setText("");
					isEdited = false;
				}
				else{
					new SaveDialog(Gui.this, "Save or Not?");
				}
			}
		});
		About.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				new MessegeDialog(Gui.this, "Any Question or bug Please send email to xlq1120@yahoo.com");
			}
		});
	//打开作为服务器功能
		
		serverOpenItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				if(Gui.this.serverPort > 0)
				{
					new MessegeDialog(Gui.this, "Server with local port "+ Gui.this.serverPort +" has already opened");
				}
				else
					new ServerFrame(Gui.this);	
			}
		});
		serverCloseItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				if(mServer!=null)
					mServer.close();
				Gui.this.serverPort = -1; //关闭了
				//更新标题
				String currentTitle = Gui.this.getTitle();
				int index = currentTitle.indexOf("As a server");
				if(index<0)
				{
					//Gui.this.setTitle(currentTitle+" As a server local Port "+port);
				}
				else 
				{
					String substr = currentTitle.substring(0, index);
					Gui.this.setTitle(substr);
				}
			}
		});
		
		file.setMnemonic('F');
		file.add(NewFileItem);
		file.addSeparator();
		file.add(openItem);
		file.addSeparator();
		file.add(saveItem);
		file.addSeparator();
		file.add(saveAsItem);
		file.addSeparator();
		file.add(exportItem);
		file.addSeparator();
		file.add(closeItem);
		
		
		CssOption.add(defaultCssItem);
		CssOption.addSeparator();
		CssOption.add(ImportCss);
		
		remote.add(connectItem);
		remote.add(uploadItem);
		remote.add(downItem);
		remote.add(DisconItem);
		
		ServerMenu.add(serverOpenItem);
		ServerMenu.add(serverCloseItem);
		
		help.add(About);
		
		CssOption.setFont(textStyle);
		file.setFont(textStyle);
		help.setFont(textStyle);
		remote.setFont(textStyle);
		ServerMenu.setFont(textStyle);
		SyncOption.setFont(textStyle);
		topMenu.setFont(textStyle);
		topMenu.add(file);
		topMenu.add(CssOption);
		topMenu.add(remote);
		topMenu.add(ServerMenu);
		topMenu.add(SyncOption);
		topMenu.add(help);
		topTool.setBackground(new Color(234, 234, 234));
		topTool.add(NewBtn);
		topTool.addSeparator();
		topTool.add(OpenBtn);
		topTool.addSeparator();
		topTool.add(SaveBtn);

		topTool.addSeparator();
		topTool.add(connectBtn);
		topTool.addSeparator();
		topTool.add(uploadBtn);
		topTool.addSeparator();
		topTool.add(downloadBtn);
		topTool.addSeparator();
		topTool.add(disconnectBtn);
		
		
	}
	public void setConnectBool(boolean TorF)
	{
		this.isConnected = TorF;
	}
	public void setHost(String hostip) {
		this.remoteHost = hostip;
	}
//将数据发送	
	public void upload(boolean bool) //bool值用来设置是否出现弹窗提示
	{
		if(isConnected == false)
		{
			new MessegeDialog(this, "Connect closed.Please connect again");
		}
		else
		{		
			EditLock.lock();  //锁住资源
			if(mClient.send("==~~BEGIN~~=="+textedit.getText()+"==~~OK~~=="))
			{
				if(bool)
					new MessegeDialog(this, "upload Success");	
			}
			else{
				new MessegeDialog(this, "upload Failed,may be remote server close");
				isConnected = false;
				//更新标题
				String title = this.getTitle();
				int index = title.indexOf("connect");  //寻找是否已经连接上了服务器
				if(index < 0)
				{
					//不用刷新
				}
				else
				{
					String subStr = title.substring(0, index-1);
				    setTitle(subStr);
				 }
			}
			EditLock.unlock();
			
		}
	}
	
//断开连接
	public void DisConnect() {
		if(isConnected==false)
			return ;
		String title = this.getTitle();
		int index = title.indexOf(" connect");  //寻找是否已经连接上了服务器
		if(index < 0)
		{
			//不用刷新
		}
		else   //去掉connect字段
		{
			String subStr = title.substring(0, index-1);
		    setTitle(subStr);
		 }
		mClient.close();
		isConnected = false;
	}
//获取服务器端的文档
	public void GetRemote() {
		if(isConnected == true)
		{
			if(mClient.send("==~~GET~~==")==false)
			{
				new MessegeDialog(Gui.this, "Could not open data transfer!");
				return;
			}
			String temp = mClient.Recv();
			if(temp.equals("ERROR"))
			{
				new MessegeDialog(Gui.this, "Remote close connection while receiving");
			}
			else{
				new TextFrame().SetText(temp);
			}
		}
		else{
			new MessegeDialog(Gui.this, "Connect closed.Please connect again");
		}
	}
	//开启服务器功能的执行函数
	public void openServer(int port)
	{
		try {
			mServer = new Server(Gui.this,port);
			this.serverPort = port;  //更新port
			new MessegeDialog(Gui.this, "Now your Markdown is servered as a server");
			isServerServiceOpen = true;
			//改变标题
			String currentTitle = Gui.this.getTitle();
			int index = currentTitle.indexOf("As a server");
			if(index<0)
			{
				Gui.this.setTitle(currentTitle+" As a server local Port "+port);
			}
			else 
			{
				String substr = currentTitle.substring(0, index);
				Gui.this.setTitle(substr+" As a server local Port "+port);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			new MessegeDialog(Gui.this, "开启服务器功能失败，可能端口被占用");
		}
	}
	
	//初始化默认的css显示样式
	public void  InitializeDefaultCss() {
	    HTMLEditorKit kit = new HTMLEditorKit();
//添加规则
	    StyleSheet styleSheet = kit.getStyleSheet();
	     
	    
	    styleSheet.addRule("table {border-collapse: collapse; border: solid #000000; border-width: 1px 0 0 1px;}");
	    styleSheet.addRule("table caption {font-size: 12px; font-weight: bolder;}");
	    styleSheet.addRule("table td {white-space: word-wrap; font-size: 10px; height: 10px; border: solid #000000; border-width: 0 1px 1px 0; padding: 2px; text-align: left; vertical-align: center}");
	    styleSheet.addRule("code, tt {margin: 0 0px; padding: 0px 0px; white-space: nowrap; border: 1px solid #eaeaea; background-color: #f8f8f8;border-radius: 3px;}");
	    styleSheet.addRule("pre>code {margin: 0;padding: 0;white-space: pre;border: none;background: transparent;}");
	    styleSheet.addRule("pre, code, tt {font-size: 12px;font-family: Consolas, \"Liberation Mono\", Courier, monospace;}");
	    styleSheet.addRule("pre {background-color: #f8f8f8;border: 1px solid #ccc;font-size: 13px;line-height: 19px;overflow: auto;padding: 6px 10px;border-radius: 3px;}");
	    
	    
	    areaShow.setEditorKit(kit);
		areaShow.setEditorKit(new HTMLEditorKit());
	}
	
// 这里使用table laytout来初始化	
	public void InitializeTable()
	{
	
	//创建一个一行一列的tablelayout
		
		
		double size[][] ={
				{0.20,0.39,5,TableLayout.FILL}, 
				{TableLayout.FILL}};
		double size1[][] ={     //用于显示中间的行号
				{30,TableLayout.FILL}, 
				{TableLayout.FILL}};
		TableLayout mtTableLayout = new TableLayout(size);
		TextArea editArea = new TextArea();
		
		
		JPanel mainpane = new JPanel(mtTableLayout);
		JPanel middlepane = new JPanel(new TableLayout(size1));
		
		lineArea.setColumns(4);
	
	    lineArea.setLineWrap(true);        //激活自动换行功能 
		middlepane.add(lineArea, "0,0");
		middlepane.add(textedit,"1,0");
		String after = null;
		String title = "标题导航";
		
		JLabel labeltitle = new JLabel(title);
		labeltitle.setFont(new Font("微软雅黑", Font.BOLD,14));
		labeltitle.setBounds(0, 0, 133, 15);

//		Toolkit kit = Toolkit.getDefaultToolkit(); //定义工具包  		  
//		Dimension screenSize = kit.getScreenSize(); //获取屏幕的尺寸  		  
//		int screenWidth = this.getWidth(); //获取屏幕的宽  		  
//		int screenHeight = this.getBounds().height; //获取屏幕的高  
//		System.out.println(screenHeight+"\n"+screenWidth);		
			//panel.setLayout(myGridLayout);
//		TextArea mArea = new TextArea();

		InitializeDefaultCss();
		
		areaShow.setAutoscrolls(true);
		
		areaShow.addHyperlinkListener(new HyperlinkListener() {
			
			@Override
			public void hyperlinkUpdate(HyperlinkEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
//		Markdown4jProcessor myProcessor = new Markdown4jProcessor();
//		try {
//			after = myProcessor.process(primary);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		textedit.setText(after);
		mList.add(labeltitle);
//		areaShow.setText(after);
		Container container =this.getContentPane();
		//c.add

	//	JSplitPane pane1 = new 
		textedit.setFont(new Font("微软雅黑", Font.PLAIN, 14));
		JScrollPane sroll1 = new JScrollPane(mList);
		JScrollPane sroll2 = new JScrollPane(middlepane);
		JScrollPane sroll3 = new JScrollPane(areaShow);
		sroll2.setVerticalScrollBarPolicy( 
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);  //设置总是出现，便于监听
		
		/**
		 *Function:实现了两个view的同步滚动 
		 *@author qzh
		 *date:2016-12-07
		 *@version:1.0
		 **/
		jscrollBar1 = sroll2.getVerticalScrollBar();
		if (jscrollBar1 != null) 
		jscrollBar1.addAdjustmentListener(new AdjustmentListener() {
			
			@Override
			public void adjustmentValueChanged(AdjustmentEvent e) {
				// TODO Auto-generated method stub
				JScrollBar jscrollBar2 = sroll3.getVerticalScrollBar();
				if(jscrollBar2 == null)
					return ;
				if(DoubleScrollEnable == false)
					return;
				sroll1Pos = jscrollBar1.getValue();
				sroll1Max = jscrollBar1.getMaximum();
				sroll2Pos = jscrollBar2.getValue();
				sroll2Max = jscrollBar2.getMaximum();
				if(sroll1Pos == 0)
					jscrollBar2.setValue(0);
				else{
					int temp= (int) ((long)sroll2Max*(long)sroll1Pos/sroll1Max);
					jscrollBar2.setValue(temp);
				}
			}
		});
		JSplitPane rightpane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,true);
		rightpane.setLeftComponent(sroll2);
		rightpane.setRightComponent(sroll3);
		rightpane.setDividerLocation(300);
		rightpane.setDividerSize(2);
		JSplitPane mainSpilt = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,true);
		mainSpilt.setLeftComponent(sroll1);
		mainSpilt.setRightComponent(rightpane);
		mainSpilt.setDividerLocation(200);
		mainSpilt.setDividerSize(5);
		rightpane.setVisible(true);
//		mainpane.add(sroll1,"0,0");
//		mainpane.add(sroll2,"1,0");
//		mainpane.add(sroll3,"3,0");
	//	mainpane.add(labeltitle,"0,1");
//		this.add(mainpane,BorderLayout.CENTER);
		this.getContentPane().add(mainSpilt);
		//	panel2.add(mList);
				
	}
	public void InitiaBottom() {
		
		double size[][] ={
				{130,100,100,100,TableLayout.FILL}, 
				{TableLayout.FILL}};
		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new TableLayout(size));
		bottomPanel.setBounds(0, 290, 400, 5);
		bottomPanel.setFont(textStyle1);
		
		numofchar.setFont(textStyle1);
		bottomPanel.add(numofchar,"0,0");
		bottomPanel.add(cols,"2,0");
		bottomPanel.add(lines,"1,0");
		bottomPanel.add(warn, "3,0");
		bottomPanel.setBackground(new Color(194,194,194));
		this.getContentPane().add(bottomPanel,BorderLayout.SOUTH);	
		
	}
	
	
/**
 *Function: open file Function 
 *@author qzh 
 *@return null
 *@version 1.0
 */
	public void Open_file() {
		if(!isEdited)
		{
			enableDocListener = false;
			enableEditlistern = false;
			StringBuffer contextBuffer = new StringBuffer();
			FileDialog OpenDia = new FileDialog(this, "Open File"); // 重要
			OpenDia.setVisible(true);
			String path = OpenDia.getDirectory() + OpenDia.getFile(); // 重要
			if (!path.equals("nullnull")) 
			{
				currentFile = path;
				String context = "";
				BufferedReader fReader = null;
				try {
			//这里仅仅用于处理文本文件，因此直接使用reader		
					
					try {
						fReader = new BufferedReader(new InputStreamReader(new FileInputStream(path),"UTF-8"));
					} catch (UnsupportedEncodingException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} 
					String message = null;
					try {
						while ((message = fReader.readLine()) != null) {
							contextBuffer.append(message);
							contextBuffer.append("\n");
						}
					} 
					catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						}
					
					}
				catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				context = contextBuffer.toString();
				textedit.setText(context);
				enableDocListener = true;  //打开后使能
				enableEditlistern = true;
				try {
					if(style!= null){
						areaShow.setText(style+totalpro.process(context));
						System.out.println("style is not null");
					}
					else 
						areaShow.setText(totalpro.process(context));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				finally {
					if(fReader!=null)
						try {
							fReader.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				}
			//	textedit.setCaretPosition(context.length());
		//		update_lineTag();
				Point mousepoint = textedit.getMousePosition();
				Rectangle curPosition = null;
				try {
					curPosition = textedit.modelToView(textedit.getCaretPosition());
				} catch (BadLocationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				textedit.setCaretPosition(0);
		//		System.out.println(curPosition);
				isEdited = false;
			//	this.setTitle(path);
				String currentTitle = Gui.this.getTitle();
				int index = currentTitle.indexOf("As a server");
				if(index<0)
				{
					Gui.this.setTitle(path);
				}
				else 
				{
					String substr = currentTitle.substring(index, currentTitle.length());
					Gui.this.setTitle(path+" "+substr);
				}
				int index1 = currentTitle.indexOf("connect");
				if(index1<0&&index<0)
				{
					Gui.this.setTitle(path);
				}
				else if(index1>0)  //已经连接到远程
				{
					String substr = currentTitle.substring(index1, currentTitle.length());
					Gui.this.setTitle(path+" "+substr);
				}
				on_Conten_change();
			}
		}
		else
		{
			new OpenDialog(this, "Save or Not?");
		}
// 打开文件,只有成功打开了才能修改isEdited变量

		
	}
	
/**
 * Function: fetch the content out of area show,and save
 * @author qzh
 * @date: 2016-12-05
 * @version: 1.0
 */	
   public void save() {
	   
	   if(isEdited == false)
		   return;
	   if(currentFile == null)
	   {
		   save_as();
	   }
	 //否则直接覆盖重写
	   else{
	   BufferedOutputStream buffout;
	   try {
		   buffout = new BufferedOutputStream(new FileOutputStream(currentFile));
		   try {
			   buffout.write(textedit.getText().getBytes());
			   buffout.close();
		   } catch (IOException e) {
			   // TODO Auto-generated catch block
			   e.printStackTrace();
		   }
		   try {
			buffout.close();
		   }
		   catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		   }
		   isEdited = false;
	   	} 
	   catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
	   		e.printStackTrace();
	   	}
	   }
   }
   
   /**
    * save as another file
    * 
    * */
   public void save_as() {
	
		FileDialog dia2 = new FileDialog(this, "Save", FileDialog.SAVE);
		dia2.setFile("unTitled.md");
	//	dia2.setFilenameFilter((FilenameFilter)new Filter());
		dia2.setVisible(true);
		String path = dia2.getDirectory() + dia2.getFile();
		if (!path.equals("nullnull")) {
			try {
				BufferedOutputStream buffout = new BufferedOutputStream(new FileOutputStream(path));
				buffout.write(textedit.getText().getBytes());
				buffout.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			isEdited = false;
			if(currentFile == null)
				currentFile = path;
		
			this.setTitle(currentFile);
		}
			
   }
 /**
  * @Function:create a new file
  * @author qzh
  * @Date:2016-12-05
  * @version: 1.0
  * 
  **/
   public void CreateFile()
   {
	   if(!isEdited){
			String currentTitle = Gui.this.getTitle();
			int index = currentTitle.indexOf("As a server");
			if(index<0)
			{
				Gui.this.setTitle("untitled.md");
			}
			else 
			{
				String substr = currentTitle.substring(index, currentTitle.length());
				Gui.this.setTitle("untitled.md"+" "+substr);
			}
			int index1 = currentTitle.indexOf("connect");
			if(index1<0&&index<0)
			{
				Gui.this.setTitle("untitled.md");
			}
			else if(index1> 0)  //已经连接到远程
			{
				String substr = currentTitle.substring(index1, currentTitle.length());
				Gui.this.setTitle("untitled.md"+" "+substr);
			}
		   currentFile = null;
		   oldtext = "";
		   this.isEdited = false;
		   areaShow.setText("");
		   textedit.setText("");
	   }
	   else
	   {//保存文件
		   new NewFileDialog(this, "Save or Not?");
	   }
	   numofchar.setText("Characters: "+"0");
	   textedit.setCaretPosition(0);  //定位到最开始
	   
	//还要增加一些状态栏的变化
	 // 稍后补充
   }
// 关闭的时候的响应函数，当被编辑过的时候
   public void save_on_close()
   {
	  // new OpenDialog(this, "Save or Not");
	//   new CloseDialog(this, "Save or Not?");
   }
 
//监听编辑栏中是否发生变化
   public void on_Conten_change() 
   {

	   if(enableDocListener == false)
	    	return ;
	    DoubleScrollEnable = false;
		boolean index = false;
		EditLock.lock();
	    String tempStr = textedit.getText().toString();
	    EditLock.unlock();
	    totalchars = tempStr.length();  //更新
	 //   System.out.println(tempStr);
		try {
		String strProces = totalpro.process(tempStr);
	//	System.out.println(strProces);
		ReadLock.lock();
		areaShow.setText(strProces);
		ReadLock.unlock();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Matcher matcher1 = s.matcher(tempStr);
	//"#{1}.*\n|#{2}.*\n|#{3}.*\n|#{4}.*\n|#{5}.*\n"	
		while(matcher1.find())  //匹配到
		{
			if(index==false)
			{
				recordline.clear();
				index = true;
				defaultListModel.clear();
				defaultListModel.add(0," ");
				titlenum = 0;
			}
			String tempgroup = matcher1.group(0);
			int start = matcher1.start();	
			if(start!=0&&tempStr.charAt(start-1)!='\n')
				continue;     //#号前面不是空格证明不是标题
			recordline.add(start);
		//	System.out.println(start);
			if(tempgroup.contains("#####"))
			{
				defaultListModel.add(titlenum+1,"    "+tempgroup.substring(5));
			}
			else if(tempgroup.contains("####"))
			{
				defaultListModel.add(titlenum+1,"   "+tempgroup.substring(4));
			}
			else if(tempgroup.contains("###"))
			{
				defaultListModel.add(titlenum+1,"  "+tempgroup.substring(3));
			}
			else if(tempgroup.contains("##"))
			{
				defaultListModel.add(titlenum+1," "+tempgroup.substring(2));
			}
			else if(tempgroup.contains("#"))
			{
				defaultListModel.add(titlenum+1,tempgroup.substring(1));
			}

			titlenum++;
		}
	//尝试用css样式解析
		Matcher matcher2 = r.matcher(tempStr);
		while(matcher2.find())  //匹配到
		{
			if(index==false)
			{
				recordline.clear();
				index = true;
				defaultListModel.clear();
				defaultListModel.add(0," ");
				titlenum = 0;
			}
			String tempgroup = matcher2.group(0);
			int star = matcher2.start();
			recordline.add(star);
			if(tempgroup.contains("<h5>"))
			{
				defaultListModel.add(titlenum+1,"    "+tempgroup.substring(4, tempgroup.length()-6));
			}
			else if(tempgroup.contains("<h4>"))
			{
				defaultListModel.add(titlenum+1,"   "+tempgroup.substring(4, tempgroup.length()-6));
			}
			else if(tempgroup.contains("<h3>"))
			{
				defaultListModel.add(titlenum+1,"  "+tempgroup.substring(4, tempgroup.length()-6));
			}
			else if(tempgroup.contains("<h2>"))
			{
				defaultListModel.add(titlenum+1," "+tempgroup.substring(4, tempgroup.length()-6));
			}
			else if(tempgroup.contains("<h1>"))
			{
				defaultListModel.add(titlenum+1,tempgroup.substring(4, tempgroup.length()-6));
			}
			titlenum++;
		}
		if(index ==false)
		{//没有找到标题，清空
			defaultListModel.clear();
		}
		if(tempStr.length()<=200000&&textedit.getLineCount()<=999)
			update_lineTag();
		numofchar.setText("Characters: "+tempStr.length());
		if(!tempStr.equals(oldtext))
			isEdited=true;
		DoubleScrollEnable = true;
		//System.out.println(areaShow.getCaretPosition());
	  
   }
	   
   public void update_lineTag()
   {
//       int line2 = textedit.getLineCount();
//       
//       if(totallines <= line2)
//       {
//       	for(;totallines<=line2-1;totallines++)
//       		lineArea.append(totallines+2+"\n");
//       }
//       else{
       	//重新绘制图案
       	lineArea.setText("");
       	for(int i=1;i<=textedit.getLineCount();i++)
       		lineArea.append(i+"\n");
//       }
   }
 /*
  * @date 2016-12-06
  * do when double click the left panel
  * @author:qzh
  * 
  **/
   public void  On_double_click() {
	   int currentCursor = textedit.getCaretPosition();
	   indexOfCursor = recordline.get(mList.getSelectedIndex()-1);
	//   System.out.println(indexOfCursor);
	   sroll_sreen(indexOfCursor);
	   if(currentCursor<=indexOfCursor){
		   jscrollBar1.setValue(jscrollBar1.getValue()+1000);
	   }
	   else{
	   }
	//   System.out.println(mList.getSelectedIndex()+" index "+indexOfCursor);
   }
   
   /*
    * funtion：点击导航栏的时候中间屏幕的移动
    * 
    * 
    **/
   public void sroll_sreen(int position) {
	
	   textedit.setCaretPosition(position);
   }
   
   public void export_docx()
   {
	    boolean to_save = true;
		FileDialog dia2 = new FileDialog(this, "Save", FileDialog.SAVE);
		dia2.setFile("unTitled.docx");
	//	dia2.setFilenameFilter((FilenameFilter)new Filter());
		dia2.setVisible(true);
		String path = dia2.getDirectory() + dia2.getFile();
		File file = new File(path);
		if(file.exists())
		{
			int option= JOptionPane.showConfirmDialog( 
	                  Gui.this, "File"+dia2.getFile()+"exist! Cover or not?", "提示 ",JOptionPane.YES_NO_OPTION); 
	         if(option==JOptionPane.NO_OPTION) 
	         { 
	        	 to_save = false;
	         } 
		}
		if(to_save)
		{
			boolean result=false; 
			try {
				BufferedOutputStream buffout = new BufferedOutputStream(new FileOutputStream("cache.html"));
				buffout.write(areaShow.getText().toString().getBytes());
				buffout.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				new MessegeDialog(Gui.this, "           保存失败");
				return ;
			}
			if(to_save)
			{
				String content = areaShow.getText();
				String absolutePath = System.getProperty("user.dir")+"/cache.html";
				result = new Html2Doc().HtmlToWord(absolutePath,path);
			}
			if(result)
			{
				new MessegeDialog(Gui.this, "           保存成功");
			}
			else{
				new MessegeDialog(Gui.this, "           保存失败");
			}
		}
   }
   public void Ex_Html()
   {
	    boolean to_save = true;
		FileDialog dia2 = new FileDialog(this, "Save", FileDialog.SAVE);
		dia2.setFile("unTitled.html");
	//	dia2.setFilenameFilter((FilenameFilter)new Filter());
		dia2.setVisible(true);
		String path = dia2.getDirectory() + dia2.getFile();
		File file = new File(path);
		if(file.exists())
		{
			int option= JOptionPane.showConfirmDialog( 
	                  Gui.this, "File"+dia2.getFile()+"exist!\nCover or not?", "提示 ",JOptionPane.YES_NO_OPTION); 
	         if(option==JOptionPane.NO_OPTION) 
	         { 
	        	 to_save = false;
	         } 
		}
		if(to_save)
		{
			boolean result=false; 
			if(to_save)
			{
				try {
					BufferedOutputStream buffout = new BufferedOutputStream(new FileOutputStream(path));
					buffout.write(areaShow.getText().getBytes());
					buffout.close();
					new MessegeDialog(Gui.this, "           保存成功");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					new MessegeDialog(Gui.this, "           保存失败");
				}
			}
		}
   }
   public void connect(String ipaddr,int port)
   {
	   mClient = new Client(ipaddr, port);
	   this.remoteHost = ipaddr;
	   isConnected = mClient.IsAlive();
	   if(isConnected)
	   {
		   String title = this.getTitle();
		   int index = title.indexOf(" connect");  //寻找是否已经连接上了服务器
		   if(index < 0)
			   setTitle(title+"   connect to remote host "+this.remoteHost);
		   else
		   {
			   String subStr = title.substring(0, index-1);
			   setTitle(subStr+"   connect to remote host "+this.remoteHost);
		   }
	   }
   }
   public static void main(String[] args) {
		// TODO Auto-generated method stub
		Gui myGui = new Gui();
		myGui.setTitle("My markdown");
		myGui.setSize(800,600);
		myGui.setLocationRelativeTo(null);
		myGui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		myGui.setVisible(true);
	}
   
	private String readFile(String path)
	{
		StringBuffer temp = new StringBuffer();
		int count;
		byte[] buffer = new byte[1024];
	//	System.out.println(path);
		try {
			
			BufferedInputStream bufread = new BufferedInputStream(new FileInputStream(path));
			try {
				while((count = bufread.read(buffer, 0, 1024)) != -1)
				{
				//	System.out.println(buffer);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return "";
	}
}
