package ims;

import javax.swing.*;
import java.awt.Component;

public class DoubleEntry extends Entry {
	// It helps identify if I use the entry to subtract or not.
	public enum DoubleEntryType {
		CREDIT,
		DEBIT,
		BALANCE
	}
	
	private DoubleEntryType doubleType;
	private double amount;
	
	public DoubleEntry(DoubleEntryType doubleType, double amount) {
		super(Entry.EntryType.NUMBER);
		this.doubleType = doubleType;
		this.amount = amount;
	}
	
	private static double RequestDouble(DoubleEntryType type, boolean isModifying, String desc) {
		String label = switch(type) {
			case CREDIT -> "Credit";
			case DEBIT -> "Debit";
			default -> throw new IllegalStateException("Unexpected value: " + String.valueOf(type));
		};
		
		String prefix = isModifying ? "Change" : "Enter";
		String value = null;
		double currentAmount = -1;
		
		boolean canceled = false;
		do {
			value = JOptionPane.showInputDialog(
				(Component)null,
				new JLabel("%s %s: ".formatted(prefix, desc)),
				"%s %sed Amount".formatted(prefix, label),
				isModifying ? JOptionPane.QUESTION_MESSAGE : JOptionPane.INFORMATION_MESSAGE
			);
			canceled = value == null && !isModifying;
			
			if (canceled) {
				JOptionPane.showMessageDialog(
					(Component)null,
					new JLabel("Please input any amount for the uninitialized field."),
					"Invalid Input",
					JOptionPane.ERROR_MESSAGE
				);
				continue;
			}
			
			// Handle number formatting
			try {
				if (value != null) {
					currentAmount = Double.parseDouble(value);
					currentAmount = Math.abs(currentAmount);	// Avoid negative values
				}
			} catch (NumberFormatException e) {
				canceled = true; // naming makes no sense but this prevents an invalid input to be skipped...
				currentAmount = -1;
				JOptionPane.showMessageDialog(
					(Component)null,
					new JLabel("Must be a valid number. Please try again."),
					"Invalid Input",
					JOptionPane.ERROR_MESSAGE
				);
			}
		} while (canceled);
		
		return currentAmount;
	}
	
	public static DoubleEntry CreateDoubleEntry(DoubleEntryType doubleType, String desc) {
		return new DoubleEntry(doubleType, RequestDouble(doubleType, false, desc));
	}
	
	public void ModifyDoubleEntry(String desc) {
		if (this.doubleType != DoubleEntryType.BALANCE) {
			double value = RequestDouble(this.doubleType, true, desc);
			if (Double.compare(value, -1.0) > 0) {
				SetValue(value);
			}
		}
	}
	
	public DoubleEntryType GetDoubleEntryType() {
		return this.doubleType;
	}
	
	public double GetValue() {
		return this.amount * (this.doubleType == DoubleEntryType.CREDIT || this.doubleType == DoubleEntryType.BALANCE ? 1 : -1);
	}
	
	public void SetValue(double amount) {
		this.amount = amount;
	}
	
	@Override
	public void ModifyEntry(String desc) {
		ModifyDoubleEntry(desc);
	}
	
	@Override
	public String toString() {
		return String.format("%.2f", GetValue());
	}
}