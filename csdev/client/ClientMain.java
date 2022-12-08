package csdev.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;
import java.util.TreeMap;

import csdev.*;

/*
Вариант 0) Обмен сообщениями. Клиент посылает через сервер сообщение другому
клиенту, выбранному из списка клиентов, подключенных в данный момент.
*/

/**
 * <p>
 * Main class of client application
 * <p>
 * Realized in console
 * <br>
 * Use arguments: userNickName userFullName [host]
 * 
 * @author Sergey Gutnikov
 * @version 1.0
 */

public class ClientMain {
	// arguments: userNickName userFullName [host]
	public static void main(String[] args) {
		if (args.length < 2 || args.length > 3) {
			System.err.println("Invalid number of arguments\n" + "Use: nick name [host]");
			waitKeyToStop();
			return;
		}
		try (Socket sock = (args.length == 2 ? new Socket(InetAddress.getLocalHost(), Protocol.PORT)
				: new Socket(args[2], Protocol.PORT))) {
			System.err.println("initialized");
			startSession(sock, args[0], args[1]);
		} catch (Exception e) {
			System.err.println(e);
		} finally {
			System.err.println("bye...");
		}
	}

	static void waitKeyToStop() {
		System.err.println("Press a key to stop...");
		try {
			System.in.read();
		} catch (IOException e) {
		}
	}

	static class Session {
		boolean connected = false;
		String userNickName = null;
		String userName = null;

		Session(String nic, String name) {
			userNickName = nic;
			userName = name;
		}
	}

	static void startSession(Socket s, String nic, String name) {
		try (Scanner in = new Scanner(System.in);
				ObjectInputStream is = new ObjectInputStream(s.getInputStream());
				ObjectOutputStream os = new ObjectOutputStream(s.getOutputStream())) {
			Session ses = new Session(nic, name);
			if (openSession(ses, is, os, in)) {
				try {
					while (true) {
						Message msg = getCommand(ses, in);
						if (!processCommand(ses, msg, is, os)) {
							break;
						}
					}
				} finally {
					closeSession(ses, os);
				}
			}
		} catch (Exception e) {
			System.err.println(e);
		}
	}

	static boolean openSession(Session ses, ObjectInputStream is, ObjectOutputStream os, Scanner in)
			throws IOException, ClassNotFoundException {
		os.writeObject(new MessageConnect(ses.userNickName, ses.userName));
		MessageConnectResult msg = (MessageConnectResult) is.readObject();
		if (msg.Error() == false) {
			System.err.println("connected");
			ses.connected = true;
			return true;
		}
		System.err.println("Unable to connect: " + msg.getErrorMessage());
		System.err.println("Press <Enter> to continue...");
		if (in.hasNextLine())
			in.nextLine();
		return false;
	}

	static void closeSession(Session ses, ObjectOutputStream os) throws IOException {
		if (ses.connected) {
			ses.connected = false;
			os.writeObject(new MessageDisconnect());
		}
	}

	static Message getCommand(Session ses, Scanner in) {
		while (true) {
			printPrompt();
			if (in.hasNextLine() == false)
				break;
			String str = in.nextLine();
			byte cmd = translateCmd(str);
			switch (cmd) {
				case -1:
					return null;
				case Protocol.CMD_CHECK_CHAT:
					return new MessageCheckChat();
				case Protocol.CMD_USER:
					return new MessageUser();
				case Protocol.CMD_SEND:
					return inputLetter(in);
				case 0:
					continue;
				default:
					System.err.println("Unknow command!");
					continue;
			}
		}
		return null;
	}

	static MessageSend inputLetter(Scanner in) {
		String letter;
		System.out.print("Enter message: ");
		letter = in.nextLine();
		return new MessageSend(letter);
	}

	static TreeMap<String, Byte> commands = new TreeMap<String, Byte>();
	static {
		commands.put("q", new Byte((byte) -1));
		commands.put("quit", new Byte((byte) -1));
		commands.put("c", new Byte(Protocol.CMD_CHECK_CHAT));
		commands.put("chat", new Byte(Protocol.CMD_CHECK_CHAT));
		commands.put("u", new Byte(Protocol.CMD_USER));
		commands.put("users", new Byte(Protocol.CMD_USER));
		commands.put("m", new Byte(Protocol.CMD_SEND));
		commands.put("message", new Byte(Protocol.CMD_SEND));
	}

	static byte translateCmd(String str) {
		// returns -1-quit, 0-invalid cmd, Protocol.CMD_XXX
		str = str.trim();
		Byte r = commands.get(str);
		return (r == null ? 0 : r.byteValue());
	}

	static void printPrompt() {
		System.out.println();
		System.out.print("(q)uit/(c)hat/(u)sers/(m)essage >");
		System.out.flush();
	}

	static boolean processCommand(Session ses, Message msg,
			ObjectInputStream is, ObjectOutputStream os)
			throws IOException, ClassNotFoundException {
		if (msg != null) {
			os.writeObject(msg);
			MessageResult res = (MessageResult) is.readObject();
			if (res.Error()) {
				System.err.println(res.getErrorMessage());
			} else {
				switch (res.getID()) {
					case Protocol.CMD_CHECK_CHAT:
						printChat((MessageCheckChatResult) res);
						break;
					case Protocol.CMD_USER:
						printUsers((MessageUserResult) res);
						break;
					case Protocol.CMD_SEND:
						System.out.println("OK...");
						break;
					default:
						assert (false);
						break;
				}
			}
			return true;
		}
		return false;
	}

	static void printChat(MessageCheckChatResult m) {
		if (m.letters != null && m.letters.length > 0) {
			for (String str : m.letters) {
				System.out.println(str);
			}
		} else {
			System.out.println("Chat is empty...");
		}
	}

	static void printUsers(MessageUserResult m) {
		if (m.userNickNames != null) {
			System.out.println("Users {");
			for (String str : m.userNickNames) {
				System.out.println("\t" + str);
			}
			System.out.println("}");
		}
	}
}
