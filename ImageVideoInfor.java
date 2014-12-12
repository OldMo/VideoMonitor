/*
 * ImageVedioInfor.java
 *
 * Created on __DATE__, __TIME__
 */

package com.splitPanel;

import java.io.File;
import java.util.Date;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import com.common.CommonMethods;
import com.common.SysInfo;
import com.database.SystemSettingDao;

public class ImageVideoInfor extends javax.swing.JFrame {

	private int iChannelNum = 0;
	private  String CaptureImage = "CaptureImage/";
	private  String CaptureVideo = "CaptureVideo/";

	public ImageVideoInfor(int iChannelNum) {
		initComponents();
		
		SysInfo info =  new SystemSettingDao().getSystemInfo();
		CaptureImage = info.getPicStorePath()+"/";
		CaptureVideo = info.getVideoStorePath()+"/";
		
		this.iChannelNum = iChannelNum;
		File imageDir = new File(CaptureImage);
		String[] files = imageDir.list();
		for (int i = 0; i < files.length; i++) {

			String fileName = files[i];
			if (Integer.valueOf(fileName.substring(0, 2)).intValue() == iChannelNum) {

				File file = new File(CaptureImage + fileName);
				String filepath = file.getAbsolutePath();
				long time = file.lastModified();

				DefaultTableModel tableModel = (DefaultTableModel) jTable1
						.getModel();
				tableModel.addRow(new Object[] { fileName, "jpg", filepath,
						time });
			}
		}
		
		File videoDir = new File(CaptureVideo);

		String[] videoFiles = videoDir.list();
		for (int i = 0; i < videoFiles.length; i++) {

			String fileName = videoFiles[i];
			if (Integer.valueOf(fileName.substring(0, 2)).intValue() == iChannelNum) {

				File file = new File(CaptureVideo+ fileName);
				String filepath = file.getAbsolutePath();
				long time = file.lastModified();

				DefaultTableModel tableModel = (DefaultTableModel) jTable1
						.getModel();
				tableModel.addRow(new Object[] { fileName, "mp4", filepath,
						time });
			}
		}
		
		
		CommonMethods.CenterWindow(this);

	}

	//GEN-BEGIN:initComponents
	// <editor-fold defaultstate="collapsed" desc="Generated Code">
	private void initComponents() {

		jScrollPane1 = new javax.swing.JScrollPane();
		jTable1 = new javax.swing.JTable();

		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		setTitle("\u56fe\u50cf\u4e0e\u5f55\u50cf\u4fe1\u606f");

		jTable1.setModel(new javax.swing.table.DefaultTableModel(
				new Object[][] {

				}, new String[] { "文件名", "文件类型", "文件路径", "创建时间" }));
		jTable1.setCellSelectionEnabled(true);
		jScrollPane1.setViewportView(jTable1);

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(
				getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addComponent(
				jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 533,
				Short.MAX_VALUE));
		layout.setVerticalGroup(layout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				layout.createSequentialGroup()
						.addComponent(jScrollPane1,
								javax.swing.GroupLayout.DEFAULT_SIZE, 390,
								Short.MAX_VALUE).addContainerGap()));

		pack();
	}// </editor-fold>
		//GEN-END:initComponents

	public static void main(String args[]) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				new ImageVideoInfor(37).setVisible(true);
			}
		});
	}

	private javax.swing.JScrollPane jScrollPane1;
	private javax.swing.JTable jTable1;

}