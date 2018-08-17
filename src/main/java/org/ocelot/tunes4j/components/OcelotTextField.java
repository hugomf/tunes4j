package org.ocelot.tunes4j.components;

import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

@SuppressWarnings("serial")
public class OcelotTextField extends JTextField {
	int cols = 0;
	int maxlength = 0;
	
	
	public OcelotTextField(int cols, int maxlength) {
		super(cols);
		this.cols = cols;
		this.maxlength = maxlength;
		setDocument(new JTextFieldLimit(maxlength));
	}
	
	public OcelotTextField() {
		this(0,0);
	}
	
	class JTextFieldLimit extends PlainDocument {
		int maxlength = 0;

		JTextFieldLimit(int maxlength) {
			super();
			this.maxlength = maxlength;
		}
		
		public void setMaxLength(int maxlength) {
			this.maxlength =  maxlength;
		}

		public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException {
			if (str.isEmpty())
				return;
			if ((getLength() + str.length()) <= this.maxlength) {
				super.insertString(offset, str, attr);
			}
		}
	}
	
}
