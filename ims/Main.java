package ims;

import javax.swing.*;
import ims.*;
import ims.BookEntry.BookType;

public class Main {
	public static void main(String[] args) {
		AccountManager.GetCredentials();
		do {
			AccountManager.PromptLogin();
			
			System.exit(0);
		} while (true);
	}
}