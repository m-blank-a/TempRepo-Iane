package ims;

import javax.swing.*;
import java.util.stream.IntStream;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.awt.Component;

import ims.Entry;

public class StringEntry extends Entry {
	public enum StringEntryType {
		LABEL, 		// default pero kami gani walay label
		REFERENCE 	// reference no.
	}
	
	private StringEntryType strType;
	private String str;
	
	public StringEntry(StringEntryType strType, String str) {
		super(Entry.EntryType.STRING);
		this.str = str;
		this.strType = strType;
	}
	
	private static String RequestStringEntry(StringEntryType strType, boolean isModifying, String desc) {
		String value = null;
		String mod = isModifying ? "Change" : "Enter";
		
		switch(strType) {
			case LABEL:
				value = JOptionPane.showInputDialog(
					(Component)null,
					new JLabel("%s %s:".formatted(mod, desc)),
					"%s Label".formatted(mod),
					JOptionPane.QUESTION_MESSAGE
				);
				
				// Put a blank label if window is closed or "Cancel"ed
				if (!isModifying && value == null) {
					value = "";
				}
				break;
			case REFERENCE:
				boolean isInvalidFormat = true;
				do {
					value = JOptionPane.showInputDialog(
						(Component)null,
						new JLabel("%s %s: ".formatted(mod, desc)),
						"%s Reference Number".formatted(mod),
						isModifying ? JOptionPane.QUESTION_MESSAGE : JOptionPane.INFORMATION_MESSAGE // Make adding entry mandatory...
					);
					
					// Check if value is null (when closed), empty (returned ok without putting anything), or is invalid (ABC-123-67 is allowed)
					// if it's used in changing a value instead of entering a new one, null is ignored.
					isInvalidFormat = (value == null && !isModifying) || value.isEmpty()
						|| !value.chars().allMatch(c -> Character.isLetterOrDigit(c) || c == '-'); // ISO/IEC 19845:2015 compliant reference number format
					
					if (isInvalidFormat) {
						JOptionPane.showMessageDialog(
							(Component)null,
							new JLabel("Reference number must be valid (ISO/IEC 19845:2015 format).%s".formatted(isModifying ? "" : " Cannot be blank.")),
							"Invalid Input",
							JOptionPane.ERROR_MESSAGE
						);
					}
				} while (isInvalidFormat);
				break;
			default:
				throw new IllegalStateException("Unexpected value: " + String.valueOf(value));
		}
		return value;
	}
	
	public static StringEntry CreateStringEntry(StringEntryType strType, String desc) {
		return new StringEntry(strType, RequestStringEntry(strType, false, desc));
	}
	
	public void ModifyStringEntry(String desc) {
		SetValue(RequestStringEntry(this.strType, true, desc));
	}
		
	public StringEntryType GetStringType() {
		return this.strType;
	}
	
	public String GetValue() {
		return this.str;
	}
	
	public void SetValue(String str) {
		this.str = str;
	}
	
	@Override
	public void ModifyEntry(String desc) {
		ModifyStringEntry(desc);
	}
	
	@Override
	public String toString() {
		return GetValue();
	}
}