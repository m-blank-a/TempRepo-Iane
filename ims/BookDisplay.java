package ims;

import javax.swing.*;

import ims.*;
import ims.BookEntry.BookType;

public class BookDisplay extends JFrame {
	public static int transactionCount = 0;
	Book book;
	
	public BookDisplay(BookType type) {
		super(type.name().replace("_", " "));
		this.book = new Book(type);
		
		JTable table = new JTable(book);
		add(new JScrollPane(table));
		setSize(1280, 720);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLocationByPlatform(true);
	}
}