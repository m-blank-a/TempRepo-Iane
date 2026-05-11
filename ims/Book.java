package ims;

import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;

import ims.*;
import ims.BookEntry.BookType;

// Class to display Book
public class Book {
	private Map<EntryId, BookEntry> entries = new LinkedHashMap<>();
	private List<EntryId> displayOrder = new ArrayList<>();
	private BookType acceptedType;
	
	@Override
	public int getRowCount() {
		return entries.size();
	}
	
	@Override
	public int getColumnCount() {
		return BookEntry.GetHeaders(this.acceptedType).length;
	}
	
	@Override
	public String getColumnName(int col) {
		return BookEntry.GetHeaders(this.acceptedType)[col];
	}
	
	@Override
	public Object getValueAt(int row, int col) {
		
	}
	
	public void AddEntry(EntryId id, BookEntry entry) {
		entries.put(id, entry);
	}
}