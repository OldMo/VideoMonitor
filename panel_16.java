package com.splitPanel;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.MenuItem;
import java.awt.Panel;
import java.awt.PopupMenu;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class panel_16 extends JPanel {
	
	private MenuItem mCaptureImage = new MenuItem("截图");
	private MenuItem mCaptureVedio = new MenuItem("录像");
	private MenuItem PlayControl = new MenuItem("云台控制");
	private MenuItem Full = new MenuItem("全屏");
	
	public PopupMenu popupMenu1 = new PopupMenu();
	
	public Panel panel1;
	public Panel panel2;
	public Panel panel3;
	public Panel panel4;
	public Panel panel5;
	public Panel panel6;
	public Panel panel7;
	public Panel panel8;
	public Panel panel9;
	public Panel panel10;
	public Panel panel11;
	public Panel panel12;
	public Panel panel13;
	public Panel panel14;
	public Panel panel15;
	public Panel panel16;
	
	public Panel getPanel1() {
		return panel1;
	}
	public void setPanel1(Panel panel1) {
		this.panel1 = panel1;
	}
	public Panel getPanel2() {
		return panel2;
	}
	public void setPanel2(Panel panel2) {
		this.panel2 = panel2;
	}
	public Panel getPanel3() {
		return panel3;
	}
	public void setPanel3(Panel panel3) {
		this.panel3 = panel3;
	}
	public Panel getPanel4() {
		return panel4;
	}
	public void setPanel4(Panel panel4) {

		
		this.panel4 = panel4;
	}
	
	
	public Panel getPanel5() {
		return panel5;
	}
	public void setPanel5(Panel panel5) {
		this.panel5 = panel5;
	}
	public Panel getPanel6() {
		return panel6;
	}
	public void setPanel6(Panel panel6) {
		this.panel6 = panel6;
	}
	public Panel getPanel7() {
		return panel7;
	}
	public void setPanel7(Panel panel7) {
		this.panel7 = panel7;
	}
	public Panel getPanel8() {
		return panel8;
	}
	public void setPanel8(Panel panel8) {
		this.panel8 = panel8;
	}
	public Panel getPanel9() {
		return panel9;
	}
	public void setPanel9(Panel panel9) {
		this.panel9 = panel9;
	}
	
	
	public Panel getPanel10() {
		return panel10;
	}
	public void setPanel10(Panel panel10) {
		this.panel10 = panel10;
	}
	public Panel getPanel12() {
		return panel12;
	}
	public void setPanel12(Panel panel12) {
		this.panel12 = panel12;
	}
	public Panel getPanel13() {
		return panel13;
	}
	public void setPanel13(Panel panel13) {
		this.panel13 = panel13;
	}
	public Panel getPanel14() {
		return panel14;
	}
	public void setPanel14(Panel panel14) {
		this.panel14 = panel14;
	}
	public Panel getPanel15() {
		return panel15;
	}
	public void setPanel15(Panel panel15) {
		this.panel15 = panel15;
	}
	public Panel getPanel16() {
		return panel16;
	}
	public void setPanel16(Panel panel16) {
		this.panel16 = panel16;
	}
	public List<Panel> getPanels() {
		return panels;
	}
	public void setPanels(List<Panel> panels) {
		this.panels = panels;
	}
	
	public GridLayout layout;
	
	public List<Panel> panels = new ArrayList<Panel>();
	
	
	public panel_16()
	{
		
		InitPanel();
		SetPanel();
		Add();
		
		this.setVisible(true);
	}
	
	
	
	private Label label1=new Label("Camera1");
	private Label label2=new Label("Camera2");
	private Label label3=new Label("Camera3");
	private Label label4=new Label("Camera4");
	private Label label5=new Label("Camera5");
	private Label label6=new Label("Camera6");
	private Label label7=new Label("Camera7");
	private Label label8=new Label("Camera8");
	private Label label9=new Label("Camera9");
	private Label label10=new Label("Camera10");
	public Label getLabel1() {
		return label1;
	}
	public Label getLabel2() {
		return label2;
	}
	public Label getLabel3() {
		return label3;
	}
	public Label getLabel4() {
		return label4;
	}
	public Label getLabel5() {
		return label5;
	}
	public Label getLabel6() {
		return label6;
	}
	public Label getLabel7() {
		return label7;
	}
	public Label getLabel8() {
		return label8;
	}
	public Label getLabel9() {
		return label9;
	}
	public Label getLabel10() {
		return label10;
	}
	public Label getLabel11() {
		return label11;
	}
	public Label getLabel12() {
		return label12;
	}
	public Label getLabel13() {
		return label13;
	}
	public Label getLabel14() {
		return label14;
	}
	public Label getLabel15() {
		return label15;
	}
	public Label getLabel16() {
		return label16;
	}
	public Label getLabel17() {
		return label17;
	}
	public Label getLabel18() {
		return label18;
	}
	public Label getLabel19() {
		return label19;
	}

	private Label label11=new Label("Camera11");
	private Label label12=new Label("Camera12");	
	private Label label13=new Label("Camera13");
	private Label label14=new Label("Camera14");
	private Label label15=new Label("Camera15");
	private Label label16=new Label("Camera16");
	private Label label17=new Label("Camera17");
	private Label label18=new Label("Camera18");
	private Label label19=new Label("Camera19");
	
	public void InitPanel()
	{
		label1.setSize(60,20);
		label2.setSize(60,20);
		label3.setSize(60,20);
		label4.setSize(60,20);
		label5.setSize(60,20);
		label6.setSize(60,20);
		label7.setSize(60,20);
		label8.setSize(60,20);
		label9.setSize(60,20);
		label10.setSize(60,20);
		label11.setSize(60,20);
		label12.setSize(60,20);
		label13.setSize(60,20);
		label14.setSize(60,20);
		label15.setSize(60,20);
		label16.setSize(60,20);
		label1.setBackground(Color.white);
		label2.setBackground(Color.white);
		label3.setBackground(Color.white);
		label4.setBackground(Color.white);
		label5.setBackground(Color.white);
		label6.setBackground(Color.white);
		label7.setBackground(Color.white);
		label8.setBackground(Color.white);
		label9.setBackground(Color.white);
		label10.setBackground(Color.white);
		label11.setBackground(Color.white);
		label12.setBackground(Color.white);
		label13.setBackground(Color.white);
		label14.setBackground(Color.white);
		label15.setBackground(Color.white);
		label16.setBackground(Color.white);
		panel1 = new Panel();
		panel2 = new Panel();
		panel3 = new Panel();
		panel4 = new Panel();
		panel5 = new Panel();
		panel6 = new Panel();
		panel7 = new Panel();
		panel8 = new Panel();
		panel9 = new Panel();
		panel10 = new Panel();
		panel11 = new Panel();
		panel12 = new Panel();
		panel13 = new Panel();
		panel14 = new Panel();
		panel15 = new Panel();
		panel16 = new Panel();
		
		panel1.add(label1);
		panel2.add(label2);
		panel3.add(label3);
		panel4.add(label4);
		panel5.add(label5);
		panel6.add(label6);
		panel7.add(label7);
		panel8.add(label8);
		panel9.add(label9);
		panel10.add(label10);
		panel11.add(label11);
		panel12.add(label12);
		panel13.add(label13);
		panel14.add(label14);
		panel15.add(label15);
		panel16.add(label16);
		
		
		layout = new GridLayout();
	}
	public void SetPanel()
	{
		panel1.setBackground(Color.black);
		panel2.setBackground(Color.black);
		panel3.setBackground(Color.black);
		panel4.setBackground(Color.black);
		panel5.setBackground(Color.black);
		panel6.setBackground(Color.black);
		panel7.setBackground(Color.black);
		panel8.setBackground(Color.black);
		panel9.setBackground(Color.black);
		panel10.setBackground(Color.black);
		panel11.setBackground(Color.black);
		panel12.setBackground(Color.black);
		panel13.setBackground(Color.black);
		panel14.setBackground(Color.black);
		panel15.setBackground(Color.black);
		panel16.setBackground(Color.black);
		
		
		layout.setColumns(4);
		layout.setRows(4);
		layout.setHgap(10);
		layout.setVgap(10);	
		setLayout(layout);
	}
	public void Add()
	{
		add(panel1);
		add(panel2);
		add(panel3);
		add(panel4);
		add(panel5);
		add(panel6);
		add(panel7);
		add(panel8);
		add(panel9);
		add(panel10);
		add(panel11);
		add(panel12);
		add(panel13);
		add(panel14);
		add(panel15);
		add(panel16);
		
		
		addListener(panel1);
		addListener(panel2);
		addListener(panel3);
		addListener(panel4);
		addListener(panel5);
		addListener(panel6);
		addListener(panel7);
		addListener(panel8);
		addListener(panel9);
		addListener(panel10);
		addListener(panel11);
		addListener(panel12);
		addListener(panel13);
		addListener(panel14);
		addListener(panel15);
		addListener(panel16);


		panels.add(panel1);
		panels.add(panel2);
		panels.add(panel3);
		panels.add(panel4);
		panels.add(panel5);
		panels.add(panel6);
		panels.add(panel7);
		panels.add(panel8);
		panels.add(panel9);
		panels.add(panel10);
		panels.add(panel11);
		panels.add(panel12);
		panels.add(panel13);
		panels.add(panel14);
		panels.add(panel15);
		panels.add(panel16);
		
	}
	
	private void addListener(Panel p){
		popupMenu1.add(mCaptureImage);
		popupMenu1.add(mCaptureVedio);
		popupMenu1.add(PlayControl);

		popupMenu1.add(Full);
		
		p.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent e) {
				pMouseClicked(e);
			}
		});
	}
	
	public void pMouseClicked(MouseEvent e) {
		if (e.isMetaDown()) {
			((Panel) e.getSource()).add(popupMenu1);
			popupMenu1.show((Component) e.getSource(), e.getX(), e.getY());
		}

	}
	public static void main(String[] args) {
		JFrame jf = new JFrame();
		JPanel jp = new JPanel();
		jp.setLayout(new BorderLayout());
		jp.add(new panel_4());
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.add(jp);
		jf.setVisible(true);
	}
}
