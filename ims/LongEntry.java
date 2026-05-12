package ims;

import javax.swing.*;
import java.util.stream.IntStream;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.Instant;
import java.awt.Component;

import ims.Entry;

/**
*	This class stores dates as long.
*/
public class LongEntry extends Entry {
	private long amount;
	
	// ISO 8601 compliant function. Uses YYYY-MM-DD format for international use.
	// I consulted Stephen for this.
	// He said, "Non-resident Digital Service Providers also report VAT transactions between Filipinos."
	// Looked up ISO 8601 for international recognition.
	// Uses a spinner wheel for easier date setting.
	private static long ShowDateDialog(boolean isModified, String desc) {
		LocalDate date = LocalDate.now();	
	
		SpinnerNumberModel dayModel = new SpinnerNumberModel(date.getDayOfMonth(), 1, 31, 1);
		SpinnerNumberModel monthModel = new SpinnerNumberModel(date.getMonthValue(), 1, 12, 1);
		SpinnerNumberModel yearModel = new SpinnerNumberModel(date.getYear(), 1970, 2037, 1);
		
		JSpinner yearSpin = new JSpinner(yearModel);
		JSpinner monthSpin = new JSpinner(monthModel);
		JSpinner daySpin = new JSpinner(dayModel);
		
		JSpinner.NumberEditor monthPadding = new JSpinner.NumberEditor(monthSpin, "00");
		JSpinner.NumberEditor dayPadding = new JSpinner.NumberEditor(daySpin, "00");
		JSpinner.NumberEditor yearPadding = new JSpinner.NumberEditor(yearSpin, "#");
		daySpin.setEditor(dayPadding);
		monthSpin.setEditor(monthPadding);
		yearSpin.setEditor(yearPadding);
		
		JPanel panel = new JPanel();
		String label = isModified ? "Change" : "Enter";
		panel.add(new JLabel("%s Date: ".formatted(label)));
		panel.add(yearSpin);
		panel.add(monthSpin);
		panel.add(daySpin);
		
		yearSpin.addChangeListener(e -> UpdateDay(monthSpin, yearSpin, daySpin, dayModel));
		monthSpin.addChangeListener(e -> UpdateDay(monthSpin, yearSpin, daySpin, dayModel));
		
		int result = 0;
		boolean requirementIgnored = true;
		do {
			result = JOptionPane.showOptionDialog(
				(Component)null,
				panel, // Add the input panel
				"%s Transaction Date".formatted(label), // Title for popup
				JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.INFORMATION_MESSAGE,
				null,
				new String[]{"Ok"},
				"Ok"
			);
			
			requirementIgnored = result == JOptionPane.CANCEL_OPTION && !isModified;
			
			if (requirementIgnored) {
				JOptionPane.showMessageDialog(
					(Component)null,
					new JLabel("Please input a date for the uninitialized field."),
					"Invalid Input",
					JOptionPane.ERROR_MESSAGE
				);
			}
		} while (requirementIgnored);
		
		if (result == JOptionPane.OK_OPTION) {
			date = LocalDate.of(
				(int)yearSpin.getValue(),
				(int)monthSpin.getValue(), // Cast Object -> String, then parseInt to get Integer for local date
				(int)daySpin.getValue()
			);
			return date.atStartOfDay(ZoneId.of("Asia/Manila")).toInstant().toEpochMilli(); // Use current date in Philippine Standard Time (GMT +08:00)
		}
		return -1;	// Return a negative to retain previous state
	}
	
	public LongEntry(long amount) {
		super(Entry.EntryType.LONG);
		this.amount = amount;
	}
	
	public static LongEntry CreateLongEntry(String desc) {
		return new LongEntry(ShowDateDialog(false, desc));
	}
	
	public static LongEntry CreateLongEntry() {
		return CreateLongEntry("");
	}
	
	public void ModifyLongEntry(String desc) {
		long value = ShowDateDialog(true, desc);
		if (value != -1) {
			SetValue(value);
		}
	}
	
	// Return stored long value as YYYY-MM-DD
	public String GetValue() {
		return Instant.ofEpochMilli(this.amount).atZone(ZoneId.of("Asia/Manila")).toLocalDate().toString();
	}
	
	public void SetValue(long amount) {
		this.amount = amount;
	}
	
	private static void UpdateDay(JSpinner monthSpin, JSpinner yearSpin, JSpinner daySpin, SpinnerNumberModel dayModel) {
		int month = (int) monthSpin.getValue();
		int year = (int) yearSpin.getValue();
		int maxDay = LocalDate.of(year, month, 1).lengthOfMonth();
		
		int day = (int) daySpin.getValue();
		dayModel.setMaximum(maxDay);
		if (day > maxDay) {
			daySpin.setValue(maxDay);
		}
	}
	
	@Override
	public void ModifyEntry(String desc) {
		ModifyLongEntry(desc);
	}
	
	@Override
	public String toString() {
		return GetValue();
	}
}