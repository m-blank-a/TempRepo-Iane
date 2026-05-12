package ims;

import javax.swing.*;

import ims.*;
import ims.BookEntry.BookType;
import java.awt.event.ItemEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;
import java.util.concurrent.CountDownLatch;

public class Menu {
	public enum ProgramState {
		LOGIN,
		RUNNING,
		TERMINATED
	}

	private static final String[] COMBOBOX_OPTIONS = {
		"Sales Journal",
		"Purchase Journal",
		"Cash Receipts Journal",
		"Cash Disbursement Journal",
		"General Journal",
		"General Ledger",
		"Inventory List (Annex A)"
	};

	private int currentContext = 0;
	private BookDisplay[] displays = new BookDisplay[7];
	
	Menu() {
		BookType[] values = BookType.values();
		for (int i = 0; i < values.length; ++i) {
			displays[i] = new BookDisplay(values[i]);
		}
	}
	
	public void showBook(int index) {
		for (int i = 0; i < displays.length; ++i) {
			displays[i].setVisible(i == index);
		}
	}
	
	public ProgramState showMenu() {
		// JDialog menu instead of a showConfirmDialog or showMessageDialog (My table can't be selected or moved or closed)
		ProgramState[] finalState = {ProgramState.LOGIN};	// Don't you hate life when "local variables referenced from a lambda 
															// expression must be final or effectively final"?
		
		CountDownLatch latch = new CountDownLatch(1);		// basically a timer to prevent the function from returning after JDialog.setVisible()
															// since I set setModal(false) at line 55.
		
		JDialog menu = new JDialog();
		menu.setTitle("Inventory Management System");
		menu.setModal(false);								// This makes the window unselectable, so I can click my JFrame
		menu.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		
		// Panel for JDialog
		JPanel panel = new JPanel();
		
		// Drop-down list for user selection
		JComboBox<String> comboBox = new JComboBox<>(COMBOBOX_OPTIONS);
		currentContext = COMBOBOX_OPTIONS.length - 1;
		comboBox.setSelectedIndex(currentContext); // Always last item on list
		
		// Buttons below the drop-down list as follows
		JButton viewBook = new JButton("View Book");
		JButton addTransaction = new JButton("Add Transaction");
		JButton exportCsv = new JButton("Export Tax Filings (.csv)");
		JButton logout = new JButton("Log Out");
		JButton quit = new JButton("Quit");
		
		comboBox.addItemListener(e -> {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				currentContext = comboBox.getSelectedIndex();
			}
		});
		
		viewBook.addActionListener(e -> {
			showBook(currentContext);
			addTransaction.setEnabled(currentContext != 5); // General Ledger shown
		});
		addTransaction.addActionListener(e -> {
			Entry.entryCount++;
			EntryId id = new EntryId(Entry.entryCount, (short)0, Entry.entryCount);
			BookEntry entry = BookEntry.CreateBookEntry(currentContext);
			displays[currentContext].book.AddEntry(id, entry);
		});
		
		// Handle states
		logout.addActionListener(e -> {
			finalState[0] = ProgramState.LOGIN;
			menu.dispose();						// Closes the JDialog
			latch.countDown();					// interrupts latch.await() to stop the program.
		});
		quit.addActionListener(e -> {
			finalState[0] = ProgramState.TERMINATED;
			menu.dispose();
			latch.countDown();
		});
		menu.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				finalState[0] = ProgramState.TERMINATED;
			}
		});
		
		panel.add(comboBox);
		panel.add(viewBook);
		panel.add(addTransaction);
		panel.add(exportCsv);
		panel.add(logout);
		panel.add(quit);
		
		menu.add(panel);
		menu.pack();
		menu.setVisible(true);
		
		// try-catch block to return finalState[0] 
		try {
			latch.await();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();		// this makes it proceed to line 124 so I don't need a finally block
		}
		
		return finalState[0]; 
	}
}