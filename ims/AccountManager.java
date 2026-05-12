package ims;

import javax.swing.*;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.Arrays;
import java.util.List;

public class AccountManager {
	private enum AuthState {
		SUCCESS,
		CANCELED,
		FAILED
	}

	// Not too secure, yet here we are for a project...
	private static final Path CREDENTIAL_FILE = Path.of("credentials.csv");
	private static Map<String, String> passwords = new HashMap<>();
	private static String currentUser = null;
	
	// Creates one if credentials.csv doesn't exist.
	private static void CreateCredentialFile() {
		try {
			Files.createFile(CREDENTIAL_FILE);
			Files.writeString(CREDENTIAL_FILE, "test,4567"); // dummy account
		} catch (Exception e) {
			CredentialsFileError();
		} finally {
			GetCredentials();
		}
	}
	
	private static void CredentialsFileError() {
		JOptionPane.showMessageDialog(
			null,
			new JLabel("Failed to modify \"credentials.csv\"!"),
			"Critical Error",
			JOptionPane.ERROR_MESSAGE
		);
		System.exit(0);
	}
	
	static void GetCredentials() {
		if (Files.exists(CREDENTIAL_FILE)) {
			// Automatically stops when reader hits an error
			try {
				Stream<String> lines = Files.lines(CREDENTIAL_FILE);
				passwords = lines
					.filter(x -> !x.isEmpty())
					.map(x -> x.split(",", 2)) 		// Split strings, max two columns
					.collect(Collectors.toMap(		// Store as a Map entry using the first two comma-separated pairs...
						x -> x[0], 					// Doesn't this count as storing in arrays, albeit temporarily?
						x -> x[1]
					)
				);
			} catch (Exception e) {
				CredentialsFileError();
			}
		} else {
			CreateCredentialFile();
		}
	}
	public static boolean IsPasswordValid(String password) {
		return password.chars().noneMatch(x -> x == '\n' || x == ',' || x == '\r');
	}
	
	private static boolean CheckCredentials(String username, String password) {
		return password.equals(passwords.get(username));
	}
	
	private static AuthState CheckAuthorization() {
		JTextField usernameField = new JTextField();
		JPasswordField passwordField = new JPasswordField();
		
		Object[] authPrompt = {
			"Username: ", usernameField,
			"Password: ", passwordField
		};
		
		// Prompt username and password
		int option = JOptionPane.showConfirmDialog(
			null,
			authPrompt,
			"Login",
			JOptionPane.OK_CANCEL_OPTION
		);
		
		if (option == JOptionPane.OK_OPTION) {
			String username = usernameField.getText();
			String password = new String(passwordField.getPassword()); // getPassword() returns char[]. Have to convert that to String obj
			
			// Auth logic
			boolean inputCorrect = CheckCredentials(username, password);
			if (inputCorrect) {
				currentUser = username;
			}
			return inputCorrect ? AuthState.SUCCESS : AuthState.FAILED;
		} else {
			return AuthState.CANCELED;
		}
	}
	
	private static char[] PromptNewPassword(String username) {
		if (username == null) {
			return null;		// Return nothing when inputted nothing as sanity check
		}
		JPasswordField password = new JPasswordField();
		JPasswordField passwordConfirm = new JPasswordField();
		
		Object[] changePrompt = {
			"New password: ", password,
			"Confirm new password: ", passwordConfirm
		};
		
		int option = JOptionPane.OK_OPTION;
		boolean passwordsMatch = false;
		
		do {
			option = JOptionPane.showConfirmDialog(
				null,
				changePrompt,
				"Change your Account Password",
				JOptionPane.OK_CANCEL_OPTION
			);
			if (option == JOptionPane.CANCEL_OPTION) {
				return null;
			}
			
			passwordsMatch = Arrays.equals(password.getPassword(), passwordConfirm.getPassword());
			if (!passwordsMatch) {
				JOptionPane.showMessageDialog(
					null,
					new JLabel("Passwords do not match. Please try again."),
					"Authorization Error",
					JOptionPane.ERROR_MESSAGE
				);
			}
			
			if (!IsPasswordValid(new String(password.getPassword()))) {
				JOptionPane.showMessageDialog(
					null,
					new JLabel("Passwords cannot have line breaks or commas. Please try again."),
					"Authorization Error",
					JOptionPane.ERROR_MESSAGE
				);
				passwordsMatch = false;
			}
			
		} while (option == JOptionPane.OK_OPTION && !passwordsMatch);
		return password.getPassword();
	}
	
	private static void ReplacePassword(String username, String password) {
		try {
			List<String> lines = Files.readAllLines(CREDENTIAL_FILE);
			
			lines.replaceAll(x -> {
				String[] entry = x.split(",", 2);
				if (entry[0].equals(username)) {
					return username + "," + password;
				}
				return x;
			});
			
			Files.writeString(CREDENTIAL_FILE, String.join(System.lineSeparator(), lines));
		} catch (Exception e) {
			CredentialsFileError();
		}
	}
	
	private static void ShowUserPassMismatch() {
		JOptionPane.showMessageDialog(
			null,
			new JLabel("Incorrect username or password. Please try again."),
			"Authorization Error",
			JOptionPane.ERROR_MESSAGE
		);
	}
	
	static void SetCredentials() {
		AuthState changeAuth = AuthState.FAILED; // Default
		do {
			changeAuth = CheckAuthorization();
			if (changeAuth == AuthState.FAILED) {
				ShowUserPassMismatch();
			}
		} while (changeAuth == AuthState.FAILED);
		if (changeAuth == AuthState.SUCCESS) {
			char[] changedPass = PromptNewPassword(currentUser);
			if (changedPass != null && currentUser != null) {
				passwords.put(currentUser, new String(changedPass));
				ReplacePassword(currentUser, new String(changedPass));
				JOptionPane.showMessageDialog(
					null,
					new JLabel("Password for %s had been changed.".formatted(currentUser)),
					"Password Change Success",
					JOptionPane.INFORMATION_MESSAGE
				);
			}
		}
	}
	
	
	// This is a blocking function.
	// When called in Main.java, this acts like a gatekeeper for the entire program.
	static boolean PromptLogin() {
		AuthState loginAuth = AuthState.FAILED;
		
		do {
			loginAuth = CheckAuthorization();
			switch(loginAuth) {
				case SUCCESS:
					return true;		// Stops the block
				case FAILED:
					ShowUserPassMismatch();
					break;
				default:
					return false;
			}
		} while (loginAuth == AuthState.FAILED);
		return false;
	}

}