package ims;

public abstract class Entry {
	public enum EntryType {
		NUMBER,
		LONG,
		STRING,
		BOOLEAN
	}
	
	public static long entryCount = 0;
	
	private EntryType type;
	
	protected Entry(EntryType type) {
		this.type = type;
	}
	
	public EntryType GetType() {
		return this.type;
	}
	
	public abstract void ModifyEntry(String desc);
}