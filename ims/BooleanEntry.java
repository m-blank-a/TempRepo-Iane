package ims;

import javax.swing.*;

import ims.Entry;

public class BooleanEntry extends Entry {

	private boolean value;
	
	public BooleanEntry(boolean value) {
		super(Entry.EntryType.BOOLEAN);
		this.value = value;
	}
	
	public static BooleanEntry CreateBooleanEntry() {
		return new BooleanEntry(false); // false by default for checkboxes
	}
	
	public void ModifyBooleanEntry(boolean value) {
		this.value = value;
	}
	
	public boolean GetValue() {
		return this.value;
	}
	
	@Override
	public void ModifyEntry(String desc)	 {
		ModifyBooleanEntry(!GetValue());
	}
	
	@Override
	public String toString() {
		return String.valueOf(GetValue());
	}
}