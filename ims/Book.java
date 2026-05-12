package ims;

import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

import ims.*;
import ims.BookEntry.BookType;

// Class to display Book
public class Book extends AbstractTableModel {
	private Map<EntryId, BookEntry> entries = new LinkedHashMap<>();
	private List<EntryId> displayOrder = new ArrayList<>();
	private BookType acceptedType;
	
	Book(BookType acceptedType) {
		this.acceptedType = acceptedType;
	}
	
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
		EntryId id = displayOrder.get(row);
		BookEntry entry = entries.get(id);
		Entry cell = entry != null ? entry.GetCell(col) : null;
		return cell != null ? cell.toString() : "";
	}
	
	public void AddEntry(EntryId id, BookEntry entry) {
		entries.put(id, entry);
		displayOrder.add(id);
		fireTableRowsInserted(displayOrder.size() - 1, displayOrder.size() - 1);
	}
}