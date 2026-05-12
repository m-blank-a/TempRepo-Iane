package ims;

import ims.*;
import ims.BookEntry.BookType;
import ims.Menu.ProgramState;

public class Main {

	public static void main(String[] args) {
		AccountManager.GetCredentials();
		ProgramState state = ProgramState.LOGIN;
		do {
			boolean isActive = AccountManager.PromptLogin();
			
			if (!isActive) {
				state = ProgramState.TERMINATED;
				break;
			}
			
			Menu menu = new Menu();
			state = menu.showMenu();
		} while (state == ProgramState.LOGIN);
		
		if (state == ProgramState.TERMINATED) {
			System.exit(0); // Kill all threads and subprocesses of this program, just in case.
		}
	}
}