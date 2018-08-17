package org.ocelot.tunes4j.gui;

import java.awt.BorderLayout;
import java.awt.Container;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class SampleToolBar {
	  private ToolBarButton fileButton
	    = new ToolBarButton("File", UIManager.getIcon("FileView.fileIcon"));
	  private ToolBarButton folderButton
	    = new ToolBarButton("Folder", UIManager.getIcon("FileView.directoryIcon"));

	  public SampleToolBar() {
	    JToolBar tb = new JToolBar();
	    tb.setFloatable(false);
	    tb.add(fileButton);
	    tb.add(folderButton);

	    JFrame fr = new JFrame("Sample Tool Bar");
	    fr.getRootPane().putClientProperty("Aqua.windowStyle", "unifiedToolBar");
	    Container cp = fr.getContentPane();
	    cp.setLayout(new BorderLayout());
	    cp.add(tb, BorderLayout.NORTH);
	    cp.add(new JPanel());
	    fr.setSize(300, 200);
	    fr.setVisible(true);
	  }

	  private class ToolBarButton extends JButton {
	    public ToolBarButton(String text, Icon icon) {
	      setIcon(icon);
	      setText(text);
	      setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
	      setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
	      //putClientProperty("JComponent.sizeVariant", "small");
	    }
	  }
	  
	  public static void main(String[] args) {
		  SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					try {
						UIManager.setLookAndFeel("org.violetlib.aqua.AquaLookAndFeel");
						new SampleToolBar();
					} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
							| UnsupportedLookAndFeelException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
	}
	  
	}

