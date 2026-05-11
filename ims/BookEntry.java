package ims;

import ims.*;
import ims.StringEntry.StringEntryType;
import ims.DoubleEntry.DoubleEntryType;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BookEntry {
	public enum BookType {
		SALES_JOURNAL,
		PURCHASE_JOURNAL,
		CASH_RECEIPTS,
		CASH_DISBURSEMENT,
		GENERAL_JOURNAL,
		LEDGER,
		INVENTORY_LIST
	}
	
	static final String[][] HEADERS = {
		// Sales Journal
		{"Date", "Invoice No.", "Customer Name", "Reference No.", "Accounts Receivable", "Sales", "Valid"},
		// Purchase Journal
		{"Date", "Invoice No.", "Vendor Name", "Reference No.", "Purchases", "Accounts Payable", "Valid"},
		// Cash Receipts Journal
		{"Date", "Description", "Reference No.", "Cash", "Sales Discount", "Accounts Receivable", "Sales", "Other Accounts", "Valid"},
		// Cash Disbursement Journal
		{"Date", "Check No.", "Payee Name", "Reference No.", "Accounts Payable", "Other Accounts", "Purchase Discount", "Cash", "Valid"},
		// General Journal
		{"Date", "Particulars", "Reference No.", "Debit", "Credit", "Valid"},
		// General Ledger
		{"Date", "Description", "Reference No.", "Debit", "Credit", "Balance", "Valid"},
		// Inventory List
		{"Product / Inventory Code", "Item Description", "Address", "Code", "Remarks", "Inventory Valuation Method", "Unit Price", "Quantity in Stocks", "Unit of Measurement", "Total Weight/Volume", "Total Cost"}
	};
	
	private Entry[] cells;
	private BookType bookType;
	private boolean initialized = false;

	private BookEntry() {
		initialized = true;
	}

	private BookEntry(BookType bookType) {
		this();
		this.bookType = bookType;
	}
	
	static BookEntry CreateBookEntry(BookType type) {
		BookEntry newEntry = new BookEntry(type);
		Entry[] entry;
		String[] labels = GetHeaders(type);
		// Determine length of entry
		int bookEntryLength = 1 + switch(type) {
			case SALES_JOURNAL, PURCHASE_JOURNAL, LEDGER -> 6;
			case CASH_RECEIPTS, CASH_DISBURSEMENT -> 8;
			case GENERAL_JOURNAL -> 5;
			case INVENTORY_LIST -> 11;
			default -> throw new IllegalStateException("Unknown BookType.");
		};
		if (type == BookType.INVENTORY_LIST) {
			--bookEntryLength;
		}
		entry = new Entry[bookEntryLength];
		entry[0] = type == BookType.INVENTORY_LIST ? StringEntry.CreateStringEntry(StringEntryType.REFERENCE, labels[0]) : LongEntry.CreateLongEntry(); // Date
		entry[1] = switch(type) {
			// Either Description/Particulars or Invoice/Check No.
			case SALES_JOURNAL, PURCHASE_JOURNAL -> StringEntry.CreateStringEntry(StringEntryType.REFERENCE, labels[1]);
			default -> StringEntry.CreateStringEntry(StringEntryType.LABEL, labels[1]);
		};
		entry[2] = switch(type) {
			// Either Customer/Payee/Vendor or Ref #
			case CASH_DISBURSEMENT, SALES_JOURNAL, PURCHASE_JOURNAL, INVENTORY_LIST ->
				StringEntry.CreateStringEntry(StringEntryType.LABEL, labels[2]);
			default -> StringEntry.CreateStringEntry(StringEntryType.REFERENCE, labels[2]);
		};
		
		switch(type) {
			case SALES_JOURNAL:
			case PURCHASE_JOURNAL:
				entry[3] = StringEntry.CreateStringEntry(StringEntryType.REFERENCE, labels[3]);
				entry[4] = new DoubleEntry(DoubleEntryType.DEBIT, 0.0);
				entry[5] = new DoubleEntry(DoubleEntryType.CREDIT, 0.0);
				break;
			
			case CASH_RECEIPTS:
				entry[3] = new DoubleEntry(DoubleEntryType.DEBIT, 0.0);
				entry[4] = new DoubleEntry(DoubleEntryType.DEBIT, 0.0);
				entry[5] = new DoubleEntry(DoubleEntryType.CREDIT, 0.0);
				entry[6] = new DoubleEntry(DoubleEntryType.CREDIT, 0.0);
				entry[7] = new DoubleEntry(DoubleEntryType.CREDIT, 0.0);
				break;
			case CASH_DISBURSEMENT:
				entry[3] = StringEntry.CreateStringEntry(StringEntryType.REFERENCE, labels[3]);
				entry[4] = new DoubleEntry(DoubleEntryType.DEBIT, 0.0);
				entry[5] = new DoubleEntry(DoubleEntryType.DEBIT, 0.0);
				entry[6] = new DoubleEntry(DoubleEntryType.CREDIT, 0.0);
				entry[7] = new DoubleEntry(DoubleEntryType.CREDIT, 0.0);
				break;
			case LEDGER:	// falls through general journal as they share the same debit-credit structure, with the ledger having a balance
				entry[5] = new DoubleEntry(DoubleEntryType.BALANCE, 0.0);
			case GENERAL_JOURNAL:
				entry[3] = new DoubleEntry(DoubleEntryType.DEBIT, 0.0);
				entry[4] = new DoubleEntry(DoubleEntryType.CREDIT, 0.0);
				break;
			case INVENTORY_LIST:
				entry[3] = StringEntry.CreateStringEntry(StringEntryType.REFERENCE, labels[3]);
				entry[4] = StringEntry.CreateStringEntry(StringEntryType.LABEL, labels[4]);
				entry[5] = StringEntry.CreateStringEntry(StringEntryType.LABEL, labels[5]);
				entry[6] = new DoubleEntry(DoubleEntryType.CREDIT, 0.0);
				entry[7] = new DoubleEntry(DoubleEntryType.CREDIT, 0.0);
				entry[8] = StringEntry.CreateStringEntry(StringEntryType.LABEL, labels[8]);
				entry[9] = new DoubleEntry(DoubleEntryType.CREDIT, 0.0);
				entry[10] = new DoubleEntry(DoubleEntryType.BALANCE, 0.0);
		}
		if (type != BookType.INVENTORY_LIST) {
			entry[bookEntryLength - 1] = BooleanEntry.CreateBooleanEntry();
		}
		newEntry.cells = entry;
		return newEntry;
	}
	
	String[] GetHeaders() {
		return HEADERS[this.bookType.ordinal()];
	}
	
	static String[] GetHeaders(BookType type) {
		return HEADERS[type.ordinal()];
	}
	
	private String ProcessReturnText() {
		return Stream.of(this.cells)
			.map(Object::toString)
			.collect(Collectors.joining(","));
	}
	
	private Entry[] GetEntries() {
		if (initialized) {
			return cells;
		}
		return null;
	}
	
	// Modifies the nth column, left-to-right
	void ModifyEntry(int n) {
		this.cells[n].ModifyEntry(GetHeaders()[n]);
	}
	
	@Override
	public String toString() {
		if (initialized) {
			return ProcessReturnText();
		}
		return null;
	}
}