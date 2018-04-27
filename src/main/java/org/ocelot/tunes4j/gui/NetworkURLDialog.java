package org.ocelot.tunes4j.gui;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.ocelot.tunes4j.components.OcelotTextField;
import org.ocelot.tunes4j.dto.RadioStation;
import org.ocelot.tunes4j.utils.GUIUtils;

import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@SuppressWarnings("serial")
public class NetworkURLDialog extends JDialog {

	public NetworkURLDialog(ApplicationWindow parentFrame) {

		JLabel label = new JLabel("URL:");
		JTextField text = new OcelotTextField(40,150);
		JButton okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String url = text.getText();
				RadioStation radioStation = getRadioStationMetadata(url);
				if(radioStation!=null) {
					parentFrame.getRadioTable().getModel().addRow(radioStation);
					parentFrame.getRadioTable().getRadioStationService().save(radioStation);
				}
				setVisible(false);
				dispose();
			}
		});
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		setUndecorated(true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLayout(new FlowLayout());
		add(label);
		add(text);
		add(okButton);
		add(cancelButton);
		pack();
		setLocationRelativeTo(parentFrame);
		GUIUtils.centerWindow(this);
		setVisible(true);
	}
	
	private  static RadioStation getRadioStationMetadata(String sUrl)  {
		
		try {
			OkHttpClient client = new OkHttpClient();
			Request request = new Request.Builder().url(sUrl).build();
			Response response = client.newCall(request).execute();
			Headers headers = response.headers();
			headers.names().stream().forEach(
				item-> System.out.println(
				String.format("%s: %s", item, headers.get(item))
			));
			RadioStation radioStation = new RadioStation();
			radioStation.setUrl(sUrl);
			radioStation.setName(		headers.get("icy-name"));
			radioStation.setDescription(	headers.get("icy-description"));
			radioStation.setGenre(		headers.get("icy-genre"));
			radioStation.setContentType(	headers.get("Content-Type"));
			radioStation.setBitRate(		headers.get("icy-br"));
			return radioStation;
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
		 
//		conn.setRequestProperty ("Icy-Metadata", "1");
//		Map<String, List<String>> headerFields = conn.getHeaderFields();
//		headerFields.entrySet().forEach(item->System.out.println(
//				String.format("%s: %s",  
//					item.getKey(), 
//					item.getValue().stream().collect(Collectors.joining(", "))	
//				)
//			));

//		radioStation.setName(		conn.getHeaderField("icy-name"));
//		radioStation.setDescription(	conn.getHeaderField("icy-description"));
//		radioStation.setGenre(		conn.getHeaderField("icy-genre"));
//		radioStation.setContentType(	conn.getHeaderField("Content-Type"));
//		radioStation.setBitRate(		conn.getHeaderField("icy-br"));
		
		
//		conn.disconnect();
		
	}

//	private static HttpURLConnection getURLConnection(String sUrl) {
//		try {
//			URL url = new URL(sUrl);
//			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//			return conn;
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return null;
//	}
	
	

}
