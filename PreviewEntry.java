package com.splitPanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Label;
import java.awt.MenuItem;
import java.awt.Panel;
import java.awt.PopupMenu;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.border.Border;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import com.HCNet.HCNetSDK;
import com.HCNet.PlayCtrl;
import com.cetc34.view.newTabPane;
import com.common.CommonInfor;
import com.common.CommonMethods;
import com.common.DeviceInfo;
import com.common.SysInfo;
import com.database.SystemSettingDao;
import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.examples.win32.W32API.HWND;
import com.view.JFramePTZControl;

public class PreviewEntry extends javax.swing.JFrame implements ActionListener {

	private int iChannelNum;
	public List<CommonInfor> commonfors = new ArrayList<CommonInfor>(16);

	private String CaptureVedio = "CaptureVedio/";

	private NativeLong lUserID;
	private HCNetSDK.NET_DVR_DEVICEINFO_V30 m_strDeviceInfo;
	private HCNetSDK.NET_DVR_CLIENTINFO m_strClientInfo;
	private HCNetSDK.NET_DVR_IPPARACFG m_strIpparaCfg;

	private CommonInfor infor; //封装数据，通道口，面板，clientinfo等信息

	//菜单项
	private MenuItem mCaptureImage = new MenuItem("截图");
	private MenuItem mCaptureVedio = new MenuItem("录像");
	private MenuItem PlayControl = new MenuItem("云台控制");
	private MenuItem Full = new MenuItem("全屏");

	private String CaptureImage = "CaptureImage/"; //捕获图像存储路径
	private DefaultMutableTreeNode m_DeviceRoot;
	private panel_4 playAlarmPanel;
	int videoNum = 0;
	private int currentChn;
	private Panel currentPanel;
	public ArrayList<Integer> channelCount = new ArrayList<Integer>();


	private DeviceInfo deviceInfo = new DeviceInfo();
	private HCNetSDK hCNetSDK = HCNetSDK.INSTANCE;
	private PlayCtrl playCtrl = PlayCtrl.INSTANCE;

	private JFrame playFrame;
	private panel_4 p4; //四屏幕
	private panel_9 p9; //九屏幕
	private panel_16 p16; //十六屏幕

	private PopupMenu popupMenu1 = new PopupMenu(); //右键弹出菜单

	private List<NativeLong> lPreviewHandles = new ArrayList<NativeLong>();

	private int currentScreen = 4; //当前屏幕个数，作用于监控轮询，currentCamera/currentScreen

	private final int ONE = 1; //单屏幕
	private final int FOUR = 4; //四屏幕
	private final int NIGHT = 9; //九屏幕
	private final int SIXTEEN = 16; //十六屏幕
	private final int MAX_CAMERA = 16; //预设最大摄像头数

	private PlayThread playThread;
	private int prevScreen;
	String recordSettingString = "不录像";

	
	
	/*
	 * 调用的构造函数
	 */
	public PreviewEntry(String ip, short port, String name, String passwd,
			Map<Integer, List<Integer>> relation) {
		initComponents();
		
		SysInfo info = new SystemSettingDao().getSystemInfo();
		CaptureImage = info.getPicStorePath() + "/";
		CaptureVedio = info.getVideoStorePath() +"/";
		recordSettingString = info.getRecordSetting();
		
		jPanel5.setBorder( BorderFactory.createEtchedBorder(0, Color.RED, Color.RED));
		this.relation = relation;
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.userName = name;
		this.passwd = passwd;
		this.ip = ip;
		this.port = port;
		doLogin();
		this.jTree1.setModel(initialTreeModel());
		creatTree();
		popupMenu1.add(mCaptureImage);
		popupMenu1.add(mCaptureVedio);
		popupMenu1.add(PlayControl);
		popupMenu1.add(Full);
		popupMenu1.addActionListener(this);
		m_strClientInfo = new HCNetSDK.NET_DVR_CLIENTINFO();
		playThread = new PlayThread();
		initPanel();
		playThread.start();
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				hCNetSDK.NET_DVR_Logout(lUserID);
				hCNetSDK.NET_DVR_Cleanup();
				playThread.stop();
				System.out.println("logout success!");

			}
		});

		panel_4 screen4 = new panel_4();
		screen4.removePop();
		screen4.getLabel1().setText("Alarm1");
		screen4.getLabel2().setText("Alarm2");
		screen4.getLabel3().setText("Alarm3");
		screen4.getLabel4().setText("Alarm4");
		jPanel5.setBackground(Color.black);
		jPanel5.setLayout(new BorderLayout());
		jPanel5.add(screen4);
		jPanel3.setBackground(Color.black);
		playAlarmPanel = screen4;
	}

	
	/**
	 * 告警页面录像
	 */
	private int alarmTime = 3 * 60*1000;
	public void playAlarm() {

		new Thread(new Runnable() {

			//录像
			public void Record(NativeLong lPreviewHandle) {
				//String CaptureVedio = "CaptureVideo/";
				long begin = System.currentTimeMillis();
				Date date = new Date(begin);

				String year = Integer.valueOf(date.getYear()).toString();
				String month = Integer.valueOf(date.getMonth()).toString();
				String day = Integer.valueOf(date.getDay()).toString();
				String hour = Integer.valueOf(date.getHours()).toString();
				String minute = Integer.valueOf(date.getMinutes()).toString();
				String second = Integer.valueOf(date.getSeconds()).toString();
				Long time = System.currentTimeMillis();
				String fileName =String.valueOf(currentChn)+time + ".mp4";
				File f = new File(CaptureVedio + fileName);
				if (!f.exists()) {
					try {
						f.createNewFile();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					hCNetSDK.NET_DVR_SaveRealData(lPreviewHandle, CaptureVedio
							+ fileName);
				}

			}

			@Override
			public void run() {
				// TODO Auto-generated method stub

				Panel p = null;
				p = currentPanel;
				Integer chn = currentChn;
				HWND hwnd = new HWND(Native.getComponentPointer(p));
				m_strClientInfo.lChannel = new NativeLong(currentChn);
				m_strClientInfo.hPlayWnd = hwnd;

				NativeLong lPreviewHandle = hCNetSDK.NET_DVR_RealPlay_V30(
						lUserID, m_strClientInfo, null, null, true);
				
				if(recordSettingString.equals("录像"))
				{
					Record(lPreviewHandle);
				}
				try {
					Thread.sleep(alarmTime);
					//Thread.sleep(15000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				hCNetSDK.NET_DVR_StopRealPlay(lPreviewHandle);
				channelCount.remove(chn);

				if (channelCount.size() == 0)
					jTabbedPane1.setSelectedIndex(0);

				playAlarmPanel.repaint();
				p.repaint();
			}
		}).start();
	}

	public void changeTab() {
		jTabbedPane1.setSelectedIndex(1);
	}

	public void setCurrentPanel() {
		if (videoNum % 4 == 0)
			currentPanel = playAlarmPanel.getPanel1();
		if (videoNum % 4 == 1)
			currentPanel = playAlarmPanel.getPanel2();
		if (videoNum % 4 == 2)
			currentPanel = playAlarmPanel.getPanel3();
		if (videoNum % 4 == 3)
			currentPanel = playAlarmPanel.getPanel4();
	}

	public void doAlarm(int chn) {
		doLogin();
		currentChn = chn;
		if (!channelCount.contains(chn)) {

			setCurrentPanel();
			videoNum++;
			channelCount.add(chn);
			System.out.println(chn);
			playAlarm();
			if (videoNum == 4)
				videoNum = 0;
		}
	}

	//初始化面板

	private DefaultTreeModel initialTreeModel() {
		m_DeviceRoot = new DefaultMutableTreeNode(this.ip);
		DefaultTreeModel myDefaultTreeModel = new DefaultTreeModel(m_DeviceRoot);//使用根节点创建模型
		return myDefaultTreeModel;
	}

	private void creatTree() {
		int m_iTreeNodeNum = 0;
		DefaultTreeModel TreeModel = ((DefaultTreeModel) jTree1.getModel());//获取树模型

		this.m_strIpparaCfg = new HCNetSDK.NET_DVR_IPPARACFG();
		this.m_strIpparaCfg = deviceInfo.doGetIPList(this.lUserID);
		for (int iChannum = 0; iChannum < HCNetSDK.MAX_IP_CHANNEL; iChannum++) {
			if (m_strIpparaCfg.struIPChanInfo[iChannum].byEnable == 1) {
				DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(
						"IPCamera" + (iChannum + m_strDeviceInfo.byStartChan));
				TreeModel.insertNodeInto(newNode, m_DeviceRoot,
						m_iTreeNodeNum++);
			}
		}
		TreeModel.reload();//将添加的节点显示到界面
		jTree1.setSelectionInterval(1, 1);//选中第一个节点
	}

	public void initPanel() {

		p4 = new panel_4();
		p9 = new panel_9();
		p16 = new panel_16();
		Dimension full = Toolkit.getDefaultToolkit().getScreenSize();
		this.setBounds(0, 0, full.width, (full.height));
		jPanel3.setLayout(new BorderLayout());
		jPanel3.add(p4);
		p4.popupMenu1.addActionListener(this);
		p9.popupMenu1.addActionListener(this);
		p16.popupMenu1.addActionListener(this);
		jPanel3.setVisible(true);
		for (int i = 0; i < 16; i++)
			lPreviewHandles.add(new NativeLong());
		jButton1.setEnabled(false);
		currentScreen = 4;
		jPanel3.setSize(500, 500);
	}

	public void pMouseClicked(MouseEvent e) {
		if (e.isMetaDown()) {
			((javax.swing.JPanel) e.getSource()).add(popupMenu1);
			popupMenu1.show((Component) e.getSource(), e.getX(), e.getY());
		}

	}

	//登录

	public void doLogin() {
		deviceInfo.doLogin(this.ip, this.port, this.userName, this.passwd);
		this.m_strDeviceInfo = deviceInfo.getM_strDeviceInfo();
		this.lUserID = deviceInfo.getUserID();
		System.out.println(this.userName+"-=========================");
	}

	//播放

	public void play(int chn) {
		Panel p = null;

		if (currentScreen == 4) {
			if (chn - 33 < currentScreen)
				p = p4.panels.get(chn - 33);
			else
				return;
		} else if (currentScreen == 9) {

			if (chn - 33 < currentScreen)
				p = p9.panels.get(chn - 33);
			else
				return;
		} else if (currentScreen == 16) {

			if (chn - 33 < currentScreen)
				p = p16.panels.get(chn - 33);
			else
				return;
		}

		System.out.println("ChannelNum: " + chn);
		p.requestFocus();

		HWND hwnd = new HWND(Native.getComponentPointer(p));
		m_strClientInfo.lChannel = new NativeLong(chn);
		m_strClientInfo.hPlayWnd = hwnd;
		CommonInfor infor = new CommonInfor();
		NativeLong lPreviewHandle = hCNetSDK.NET_DVR_RealPlay_V30(lUserID,
				m_strClientInfo, null, null, true);
		lPreviewHandles.add(chn - 33, lPreviewHandle);

		infor.setiChannelNum(chn);
		infor.setlRealHandle(lPreviewHandle);
		infor.setPanel(p);
		infor.setM_strClientInfo(m_strClientInfo);

		commonfors.add(chn - 33, infor);
		p.repaint();
	}

	/*
	 * 播放控制
	 */
	private void play() {
		initPanel();
		playThread.start();
	}

	//////////////////////////////////////////////////////////////////////////////////
	/** Creates new form PreviewEntry */
	private String ip;
	private short port;
	private String userName;
	private String passwd;
	private Map<Integer, List<Integer>> relation;

	
	
	//GEN-BEGIN:initComponents
	// <editor-fold defaultstate="collapsed" desc="Generated Code">
	private void initComponents() {

		jTabbedPane1 = new javax.swing.JTabbedPane();
		jPanel1 = new javax.swing.JPanel();
		jPanel3 = new javax.swing.JPanel();
		jScrollPane1 = new javax.swing.JScrollPane();
		jTree1 = new javax.swing.JTree();
		jPanel4 = new javax.swing.JPanel();
		jButton1 = new javax.swing.JButton();
		jButton2 = new javax.swing.JButton();
		jButton3 = new javax.swing.JButton();
		jLabel1 = new javax.swing.JLabel();
		jComboBox2 = new javax.swing.JComboBox();
		jLabel2 = new javax.swing.JLabel();
		jComboBox1 = new javax.swing.JComboBox();
		jPanel2 = new javax.swing.JPanel();
		jPanel5 = new javax.swing.JPanel();

		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

		javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(
				jPanel3);
		jPanel3.setLayout(jPanel3Layout);
		jPanel3Layout.setHorizontalGroup(jPanel3Layout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 866,
				Short.MAX_VALUE));
		jPanel3Layout.setVerticalGroup(jPanel3Layout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 430,
				Short.MAX_VALUE));

		jTree1.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				jTree1MouseClicked(evt);
			}
		});
		jScrollPane1.setViewportView(jTree1);

		jPanel4.setLayout(null);

		jButton1.setText("\u56db\u5c4f\u5e55");
		jButton1.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButton1ActionPerformed(evt);
			}
		});
		jPanel4.add(jButton1);
		jButton1.setBounds(30, 10, 69, 25);

		jButton2.setText("\u4e5d\u5c4f\u5e55");
		jButton2.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButton2ActionPerformed(evt);
			}
		});
		jPanel4.add(jButton2);
		jButton2.setBounds(130, 10, 69, 25);

		jButton3.setText("\u5341\u516d\u5c4f\u5e55");
		jButton3.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButton3ActionPerformed(evt);
			}
		});
		jPanel4.add(jButton3);
		jButton3.setBounds(220, 10, 81, 25);

		jLabel1.setText("\u8f6e\u8be2\u65f6\u95f4");
		jPanel4.add(jLabel1);
		jLabel1.setBounds(410, 0, 50, 40);

		jComboBox2.setModel(new javax.swing.DefaultComboBoxModel(new String[] {
				"20秒", "40秒", "1分钟", "3分钟", "5分钟" }));
		jComboBox2.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jComboBox2ActionPerformed(evt);
			}
		});
		jPanel4.add(jComboBox2);
		jComboBox2.setBounds(490, 10, 59, 20);

		jLabel2.setText("\u544a\u8b66\u5f55\u50cf\u65f6\u957f");
		jPanel4.add(jLabel2);
		jLabel2.setBounds(620, 0, 100, 40);

		jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] {
				"3分钟", "5分钟", "10分钟", "15分钟", "30分钟" }));
		jComboBox1.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jComboBox1ActionPerformed(evt);
			}
		});
		jPanel4.add(jComboBox1);
		jComboBox1.setBounds(720, 10, 66, 23);

		javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(
				jPanel1);
		jPanel1.setLayout(jPanel1Layout);
		jPanel1Layout
				.setHorizontalGroup(jPanel1Layout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								jPanel1Layout
										.createSequentialGroup()
										.addComponent(
												jScrollPane1,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												140,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addGroup(
												jPanel1Layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addGroup(
																jPanel1Layout
																		.createSequentialGroup()
																		.addComponent(
																				jPanel4,
																				javax.swing.GroupLayout.DEFAULT_SIZE,
																				854,
																				Short.MAX_VALUE)
																		.addContainerGap())
														.addComponent(
																jPanel3,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																Short.MAX_VALUE))));
		jPanel1Layout
				.setVerticalGroup(jPanel1Layout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addComponent(jScrollPane1,
								javax.swing.GroupLayout.DEFAULT_SIZE, 480,
								Short.MAX_VALUE)
						.addGroup(
								javax.swing.GroupLayout.Alignment.TRAILING,
								jPanel1Layout
										.createSequentialGroup()
										.addComponent(
												jPanel3,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												Short.MAX_VALUE)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(
												jPanel4,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												42,
												javax.swing.GroupLayout.PREFERRED_SIZE)));

		jTabbedPane1.addTab("\u5b9e\u65f6\u76d1\u63a7 ", jPanel1);

		javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(
				jPanel5);
		jPanel5.setLayout(jPanel5Layout);
		jPanel5Layout.setHorizontalGroup(jPanel5Layout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 1013,
				Short.MAX_VALUE));
		jPanel5Layout.setVerticalGroup(jPanel5Layout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 480,
				Short.MAX_VALUE));

		javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(
				jPanel2);
		jPanel2.setLayout(jPanel2Layout);
		jPanel2Layout.setHorizontalGroup(jPanel2Layout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addComponent(
				jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE,
				javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
		jPanel2Layout.setVerticalGroup(jPanel2Layout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addComponent(
				jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE,
				javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));

		jTabbedPane1.addTab("\u8b66\u544a\u754c\u9762", jPanel2);

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(
				getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				layout.createSequentialGroup()
						.addComponent(jTabbedPane1,
								javax.swing.GroupLayout.DEFAULT_SIZE, 1018,
								Short.MAX_VALUE).addContainerGap()));
		layout.setVerticalGroup(layout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addComponent(
				jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 511,
				Short.MAX_VALUE));

		pack();
	}// </editor-fold>
		//GEN-END:initComponents

	private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {
		// TODO add your handling code here:
		if(jComboBox1.getSelectedIndex()==0)
			alarmTime=3*60*1000;
		if(jComboBox1.getSelectedIndex()==1)
			alarmTime=5*60*1000;
		if(jComboBox1.getSelectedIndex()==2)
			alarmTime=10*60*1000;
		if(jComboBox1.getSelectedIndex()==3)
			alarmTime=15*60*1000;
		if(jComboBox1.getSelectedIndex()==4)
			alarmTime=30*60*1000;
	}

	//GEN-END:initComponents

	//GEN-BEGIN:variables
	// Variables declaration - do not modify
	private javax.swing.JButton jButton1;
	private javax.swing.JButton jButton2;
	private javax.swing.JButton jButton3;
	private javax.swing.JComboBox jComboBox1;
	private javax.swing.JComboBox jComboBox2;
	private javax.swing.JLabel jLabel1;
	private javax.swing.JLabel jLabel2;
	private javax.swing.JPanel jPanel1;
	private javax.swing.JPanel jPanel2;
	private javax.swing.JPanel jPanel3;
	private javax.swing.JPanel jPanel4;
	private javax.swing.JPanel jPanel5;
	private javax.swing.JScrollPane jScrollPane1;
	private javax.swing.JTabbedPane jTabbedPane1;
	private javax.swing.JTree jTree1;
	// End of variables declaration//GEN-END:variables

	private int loopTime = 20;

	private void jComboBox2ActionPerformed(java.awt.event.ActionEvent evt) {
		// TODO add your handling code here:
		if (jComboBox2.getSelectedIndex() == 0)
			loopTime = 5;
		if (jComboBox2.getSelectedIndex() == 1)
			loopTime = 10;
		if (jComboBox2.getSelectedIndex() == 2)
			loopTime = 15;
		if (jComboBox2.getSelectedIndex() == 3)
			loopTime = 20;
		if (jComboBox2.getSelectedIndex() == 4)
			loopTime = 25;
		System.out.println(loopTime);
	}

	private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {
		// TODO add your handling code here:
	}

	/*
	 * 四屏幕显示
	 * 
	 */
	private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {

		for (int j = 0; j < currentScreen; j++) {

			hCNetSDK.NET_DVR_StopRealPlay(lPreviewHandles.get(j));

		}
		playThread.stop();
		lPreviewHandles.clear();
		lPreviewHandles = new ArrayList<NativeLong>(currentScreen);
		jPanel3.setLayout(new BorderLayout());
		jPanel3.removeAll();
		jButton1.setEnabled(false);
		jButton2.setEnabled(true);
		jButton3.setEnabled(true);
		jPanel3.add(p4);
		currentScreen = 4;
		playThread = new PlayThread();
		playThread.start();
		this.repaint();

	}

	/*
	 * 
	 * 十六屏幕显示
	 * 
	 */
	private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {

		for (int j = 0; j < currentScreen; j++) {

			hCNetSDK.NET_DVR_StopRealPlay(lPreviewHandles.get(j));
		}
		playThread.stop();

		lPreviewHandles.clear();

		lPreviewHandles = new ArrayList<NativeLong>(currentScreen);

		jPanel3.setLayout(new BorderLayout());
		//		if (currentScreen == 4) {
		//			jButton1.setEnabled(true);
		//			jPanel3.remove(p4);
		//		}
		//
		//		else if (currentScreen == 9) {
		//			jButton2.setEnabled(true);
		//			jPanel3.remove(p9);
		//		}
		jButton1.setEnabled(true);
		jButton2.setEnabled(true);
		jButton3.setEnabled(false);
		jPanel3.removeAll();
		jPanel3.add(p16);

		currentScreen = 16;
		playThread = new PlayThread();
		playThread.start();
		this.repaint();
		//		JButton bt = (JButton) evt.getSource();
		//		bt.setEnabled(false);
	}

	/*
	 * 
	 * 九屏幕显示
	 * 
	 */
	private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {
		// TODO add your handling code here:
		//	prevScreen = currentScreen;

		for (int j = 0; j < currentScreen; j++) {

			hCNetSDK.NET_DVR_StopRealPlay(lPreviewHandles.get(j));

		}

		playThread.stop();
		lPreviewHandles.clear();
		lPreviewHandles = new ArrayList<NativeLong>(currentScreen);
		jPanel3.removeAll();
		jButton2.setEnabled(false);
		jButton1.setEnabled(true);
		jButton3.setEnabled(true);
		jPanel3.add(p9);
		currentScreen = 9;
		playThread = new PlayThread();
		playThread.start();
		this.repaint();
		//		JButton bt = (JButton) evt.getSource();
		//		bt.setEnabled(false);

	}

	//全屏轮训事件处理

	private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

		jPanel3.setSize(screenSize);

	}

	private void jTree1MouseClicked(java.awt.event.MouseEvent evt) {
		if (evt.getClickCount() == 2) {
			TreePath tp = jTree1.getSelectionPath();//获取选中节点的路径
			String sChannelName = ((DefaultMutableTreeNode) tp
					.getLastPathComponent()).toString();
			if (sChannelName.charAt(0) == 'I') {
				iChannelNum = Integer.parseInt(sChannelName.substring(8)) + 32;
				System.out.println(iChannelNum);
				ImageVideoInfor image = new ImageVideoInfor(iChannelNum);
				image.setVisible(true);
			} else
				iChannelNum = -1;
		}
	}

	/*
	 * 截图
	 */
	private void CatchImage(Panel panel) {

		CommonInfor infor = null;
		int i = 0;
		for (i = 0; i < commonfors.size(); i++) {
			infor = commonfors.get(i);
			if (infor.getPanel().equals(panel)) {
				break;
			}
		}
		if (i == commonfors.size())
			return;

		HCNetSDK.NET_DVR_CLIENTINFO m_strClientInfo = infor
				.getM_strClientInfo();

		final NativeLong lRealHandle = hCNetSDK.NET_DVR_RealPlay_V30(lUserID,
				m_strClientInfo, null, null, true);

		SimpleDateFormat df = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");//设置日期格式
		String fileName = Integer.valueOf(infor.getiChannelNum()).toString()
				+ df.format(new Date()) + ".jpg";// new Date()为获取当前系统时间

		System.out.println(fileName);
		//创建文件
		File f = new File(CaptureImage + fileName);
		System.out.println(f.toString());
		if (!f.exists()) {
			try {
				f.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		NativeLong handle = infor.getlRealHandle();
		HCNetSDK.INSTANCE.NET_DVR_CapturePicture(handle, CaptureImage
				+ fileName);
		JOptionPane.showMessageDialog(this, "截图成功");
	}

	//////////////////////////////////////////////////////////////////////////////-------------

	private JOptionPane alaJOptionPane;
	private JOptionPane overJOptionPane;
	private JFrame frame = this;

	/**
	 * 录像
	 * @param chn
	 * @param time
	 */
	public void playRecord(final int chn, final int time) {
		new Thread(new Runnable() {
			public void Record(NativeLong lPreviewHandle) {
				//String CaptureVedio = "CaptureVideo/";
				long begin = System.currentTimeMillis();
				Date date = new Date(begin);

				String year = Integer.valueOf(date.getYear()).toString();
				String month = Integer.valueOf(date.getMonth()).toString();
				String day = Integer.valueOf(date.getDay()).toString();
				String hour = Integer.valueOf(date.getHours()).toString();
				String minute = Integer.valueOf(date.getMinutes()).toString();
				String second = Integer.valueOf(date.getSeconds()).toString();
				Long time = System.currentTimeMillis();
				String fileName = String.valueOf(chn) + time + ".mp4";
				File f = new File(CaptureVedio + fileName);
				if (!f.exists()) {
					try {
						f.createNewFile();
					} catch (IOException e) {
						e.printStackTrace();
					}
					hCNetSDK.NET_DVR_SaveRealData(lPreviewHandle, CaptureVedio
							+ fileName);
				}

			}

			@Override
			public void run() {
				// TODO Auto-generated method stub
				alaJOptionPane = new JOptionPane();
				overJOptionPane = new JOptionPane();
				HCNetSDK.NET_DVR_CLIENTINFO m_strClientInfo = new HCNetSDK.NET_DVR_CLIENTINFO();
				Panel p = new Panel();
				p.setSize(500, 500);
				p.setVisible(true);
				m_strClientInfo.lChannel = new NativeLong(chn);
				NativeLong lPreviewHandle = hCNetSDK.NET_DVR_RealPlay_V30(
						lUserID, m_strClientInfo, null, null, true);
				if (lPreviewHandle.intValue() == -1) {
					alaJOptionPane.showMessageDialog(frame, "该通道无法录像");
					video.dispose();
					return;
				}
				Record(lPreviewHandle);
				try {
					Thread.sleep(time);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				hCNetSDK.NET_DVR_StopRealPlay(lPreviewHandle);
				overJOptionPane.showMessageDialog(frame, "录像完成");
				video.dispose();
			}
		}).start();
	}

	public void playFull(int chn) {

		JFrame jf = new JFrame();
		jf.setBounds(0, 0, 1000, 800);
		jf.setLayout(new BorderLayout());
		Panel panel = new Panel();
		jf.add(panel);
		jf.setVisible(true);
		alaJOptionPane = new JOptionPane();
		overJOptionPane = new JOptionPane();
		HCNetSDK.NET_DVR_CLIENTINFO m_strClientInfo = new HCNetSDK.NET_DVR_CLIENTINFO();
		HWND hwnd = new HWND(Native.getComponentPointer(panel));
		m_strClientInfo.lChannel = new NativeLong(chn);
		m_strClientInfo.hPlayWnd = hwnd;
		NativeLong lPreviewHandle = hCNetSDK.NET_DVR_RealPlay_V30(lUserID,
				m_strClientInfo, null, null, true);
		if (lPreviewHandle.intValue() == -1) {
			alaJOptionPane.showMessageDialog(frame, "该通道无法加载视频");
			jf.dispose();
			return;
		} else {
			jf.setVisible(true);
			jf.repaint();
			panel.repaint();
		}
	}

	public void playPTZ(int chn) {
		alaJOptionPane = new JOptionPane();
		HCNetSDK.NET_DVR_CLIENTINFO m_strClientInfo = new HCNetSDK.NET_DVR_CLIENTINFO();
		m_strClientInfo.lChannel = new NativeLong(chn);
		NativeLong lPreviewHandle = hCNetSDK.NET_DVR_RealPlay_V30(lUserID,
				m_strClientInfo, null, null, true);
		if (lPreviewHandle.intValue() == -1) {
			alaJOptionPane.showMessageDialog(frame, "该通道无法加载云台");

			return;
		} else {
			JFramePTZControl ptz = new JFramePTZControl(lPreviewHandle);
			ptz.setVisible(true);
		}

	}

	//////////////////////////////////////////////////////////////////////////////-------------

	/*
	 * 菜单事件
	 * (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	private CaptureVedio video;

	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getActionCommand().equals("截图")) {
			CatchImage((Panel) ((PopupMenu) e.getSource()).getParent());
		} else if (e.getActionCommand().equals("录像")) {
			Panel p = (Panel) ((PopupMenu) e.getSource()).getParent();
			String str = ((Label) p.getComponent(0)).getText();
			int index = str.indexOf("ra");
			str = str.substring(index + 2, str.length());
			video = new CaptureVedio(this, Integer.parseInt(str) + 32);
			video.setVisible(true);
		} else if (e.getActionCommand().equals("云台控制")) {

			Panel p = (Panel) ((PopupMenu) e.getSource()).getParent();
			String str = ((Label) p.getComponent(0)).getText();
			int index = str.indexOf("ra");
			str = str.substring(index + 2, str.length());
			playPTZ(Integer.parseInt(str) + 32);

		} else if (e.getActionCommand().equals("全屏")) {
			//	FullScreen((Panel) ((PopupMenu) e.getSource()).getParent());
			Panel p = (Panel) ((PopupMenu) e.getSource()).getParent();
			String str = ((Label) p.getComponent(0)).getText();
			int index = str.indexOf("ra");
			str = str.substring(index + 2, str.length());
			playFull(Integer.parseInt(str) + 32);
		}

	}

	/**
	 * @param args the command line arguments
	 */
	public static void main(String args[]) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				new Thread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						PreviewEntry t=new PreviewEntry();
												t.setVisible(true);
												t.doAlarm(33);

						try {
							Thread.sleep(6000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
				}).start();

			}
		});
	}

	/*
	 * 函数名：FullScreen
	 * @param:void
	 * 功能：全屏显示
	 * 返回值:void
	 * 
	 */

	private void FullScreen(Panel container) {

		//新建JWindow 全屏预览
		final JWindow wnd = new JWindow();
		//获取屏幕尺寸
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		wnd.setSize(screenSize);
		wnd.setVisible(true);

		final HWND hwnd = new HWND(Native.getComponentPointer(wnd));

		CommonInfor infor = null;
		int i = 0;
		for (i = 0; i < commonfors.size(); i++) {
			infor = commonfors.get(i);
			if (infor.getPanel().equals(container)) {
				System.out.println(infor.getPanel().toString());
				break;
			}
		}
		if (i == commonfors.size())
			return;

		HCNetSDK.NET_DVR_CLIENTINFO m_strClientInfo = infor
				.getM_strClientInfo();

		m_strClientInfo.hPlayWnd = hwnd;
		final NativeLong lRealHandle = hCNetSDK.NET_DVR_RealPlay_V30(lUserID,
				m_strClientInfo, null, null, true);

		//JWindow增加双击响应函数,双击时停止预览,退出全屏
		wnd.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mousePressed(java.awt.event.MouseEvent evt) {
				if (evt.getClickCount() == 2) {
					//停止预览
					hCNetSDK.NET_DVR_StopRealPlay(lRealHandle);
					wnd.dispose();
				}
			}
		});

	}

	public void setLabel(int chn) {
		if (currentScreen == 4) {
			p4.getLabel1().setText("Camera" + (chn - 33));
			p4.getLabel2().setText("Camera" + (chn - 32));
			p4.getLabel3().setText("Camera" + (chn - 31));
			p4.getLabel4().setText("Camera" + (chn - 30));
		}

		if (currentScreen == 9) {
			p9.getLabel1().setText("Camera" + (chn - 33));
			p9.getLabel2().setText("Camera" + (chn - 32));
			p9.getLabel3().setText("Camera" + (chn - 31));
			p9.getLabel4().setText("Camera" + (chn - 30));
			p9.getLabel5().setText("Camera" + (chn - 29));
			p9.getLabel6().setText("Camera" + (chn - 28));
			p9.getLabel7().setText("Camera" + (chn - 27));
			p9.getLabel8().setText("Camera" + (chn - 26));
			p9.getLabel9().setText("Camera" + (chn - 25));
		}

	}

	
	//轮询事件
	class PlayThread extends Thread {
		public void run() {
			// TODO Auto-generated method stub
			int i = 32;
			while (true) {
				if (i >= 48)
					i = 32;
				if (currentScreen == 4)
					setLabel(i + 2);
				if (currentScreen == 9)
					setLabel(i + 2);
				for (int j = 1; j <= currentScreen; j++) {

					play(i + j);

				}
				try {

					Thread.sleep(loopTime * 1000);
					
					//Thread.sleep(5 * 1000);

				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.out.println(e.getMessage());
				}
				for (int j = 0; j < currentScreen; j++) {
					hCNetSDK.NET_DVR_StopRealPlay(lPreviewHandles.get(j));
					if (j <= 3)
						p4.panels.get(j).repaint();
					if (j <= 8)
						p9.panels.get(j).repaint();
				}
				i = i + currentScreen;
				p4.repaint();
				p9.repaint();
				p16.repaint();
				jPanel3.repaint();

			}
		}
	}
}