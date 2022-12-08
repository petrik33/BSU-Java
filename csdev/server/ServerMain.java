package csdev.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.Vector;

import csdev.*;

/*
Вариант 0) Обмен сообщениями. Клиент посылает через сервер сообщение другому
клиенту, выбранному из списка клиентов, подключенных в данный момент.
*/

/**
 * <p>
 * Main class of server application
 * <p>
 * Realized in console
 * 
 * @author Sergey Gutnikov
 * @version 1.0
 */

public class ServerMain {

	private static int MAX_USERS = 100;

	public static void main(String[] args) {

		try (ServerSocket serv = new ServerSocket(Protocol.PORT)) {
			System.err.println("initialized");
			ServerStopThread tester = new ServerStopThread();
			tester.start();
			while (true) {
				Socket sock = accept(serv);
				if (sock != null) {
					if (ServerMain.getNumUsers() < ServerMain.MAX_USERS) {
						System.err.println(sock.getInetAddress().getHostName() + " connected");
						ServerThread server = new ServerThread(sock);
						server.start();
					} else {
						System.err.println(sock.getInetAddress().getHostName() + " connection rejected");
						sock.close();
					}
				}
				if (ServerMain.getStopFlag()) {
					break;
				}
			}
		} catch (IOException e) {
			System.err.println(e);
		} finally {
			stopAllUsers();
			System.err.println("stopped");
		}
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}
	}

	public static Socket accept(ServerSocket serv) {
		assert (serv != null);
		try {
			serv.setSoTimeout(1000);
			Socket sock = serv.accept();
			return sock;
		} catch (SocketException e) {
		} catch (IOException e) {
		}
		return null;
	}

	private static void stopAllUsers() {
		String[] nic = getUsers();
		for (String user : nic) {
			ServerThread ut = getUser(user);
			if (ut != null) {
				ut.disconnect();
			}
		}
	}

	private static Object syncFlags = new Object();
	private static boolean stopFlag = false;

	public static boolean getStopFlag() {
		synchronized (ServerMain.syncFlags) {
			return stopFlag;
		}
	}

	public static void setStopFlag(boolean value) {
		synchronized (ServerMain.syncFlags) {
			stopFlag = value;
		}
	}

	private static Object syncUsers = new Object();
	private static TreeMap<String, ServerThread> users = new TreeMap<String, ServerThread>();

	private static Object syncLetters = new Object();
	private static Vector<String> letters = new Vector<>();

	public static ServerThread getUser(String userNickName) {
		synchronized (ServerMain.syncUsers) {
			return ServerMain.users.get(userNickName);
		}
	}

	public static ServerThread registerUser(String userNickName, ServerThread user) {
		synchronized (ServerMain.syncUsers) {
			ServerThread old = ServerMain.users.get(userNickName);
			if (old == null) {
				ServerMain.users.put(userNickName, user);
			}
			return old;
		}
	}

	public static ServerThread setUser(String userNickName, ServerThread user) {
		synchronized (ServerMain.syncUsers) {
			ServerThread res = ServerMain.users.put(userNickName, user);
			if (user == null) {
				ServerMain.users.remove(userNickName);
			}
			return res;
		}
	}

	public static String[] getUsers() {
		synchronized (ServerMain.syncUsers) {
			return ServerMain.users.keySet().toArray(new String[0]);
		}
	}

	public static String[] getChat() {
		synchronized (ServerMain.syncLetters) {
			return ServerMain.letters.toArray(new String[0]);
		}
	}

	public static void addLetter(String userNickName, String letter) {
		synchronized (ServerMain.syncLetters) {
			String userLetter = userNickName + ": " + letter;
			ServerMain.letters.add(userLetter);
			return;
		}
	}

	public static int getNumUsers() {
		synchronized (ServerMain.syncUsers) {
			return ServerMain.users.keySet().size();
		}
	}
}

class ServerStopThread extends CommandThread {

	static final String cmd = "q";
	static final String cmdL = "quit";

	Scanner fin;

	public ServerStopThread() {
		fin = new Scanner(System.in);
		ServerMain.setStopFlag(false);
		putHandler(cmd, cmdL, new CmdHandler() {
			@Override
			public boolean onCommand(int[] errorCode) {
				return onCmdQuit();
			}
		});
		this.setDaemon(true);
		System.err.println("Enter \'" + cmd + "\' or \'" + cmdL + "\' to stop server\n");
	}

	public void run() {

		while (true) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				break;
			}
			if (fin.hasNextLine() == false)
				continue;
			String str = fin.nextLine();
			if (command(str)) {
				break;
			}
		}
	}

	public boolean onCmdQuit() {
		System.err.print("stop server...");
		fin.close();
		ServerMain.setStopFlag(true);
		return true;
	}
}

class ServerThread extends Thread {

	private Socket sock;
	private ObjectOutputStream os;
	private ObjectInputStream is;
	private InetAddress addr;

	private String userNickName = null;
	private String userFullName;

	// private Object syncLetters = new Object();
	// private Vector<String> letters = null;

	// public void addLetter(String letter) {
	// synchronized (syncLetters) {
	// if (letters == null) {
	// letters = new Vector<String>();
	// }
	// letters.add(letter);
	// }
	// }

	// public String[] getLetters() {
	// synchronized (syncLetters) {
	// String[] lts = new String[0];
	// synchronized (syncLetters) {
	// if (letters != null) {
	// lts = letters.toArray(lts);
	// letters = null;
	// }
	// }
	// return lts;
	// }
	// }

	public ServerThread(Socket s) throws IOException {
		sock = s;
		s.setSoTimeout(1000);
		os = new ObjectOutputStream(s.getOutputStream());
		is = new ObjectInputStream(s.getInputStream());
		addr = s.getInetAddress();
		this.setDaemon(true);
	}

	public void run() {
		try {
			while (true) {
				Message msg = null;
				try {
					msg = (Message) is.readObject();
				} catch (IOException e) {
				} catch (ClassNotFoundException e) {
				}
				if (msg != null)
					switch (msg.getID()) {

						case Protocol.CMD_CONNECT:
							if (!connect((MessageConnect) msg))
								return;
							break;

						case Protocol.CMD_DISCONNECT:
							return;

						case Protocol.CMD_USER:
							user((MessageUser) msg);
							break;

						case Protocol.CMD_CHECK_CHAT:
							checkChat((MessageCheckChat) msg);
							break;

						case Protocol.CMD_SEND:
							letter((MessageSend) msg);
							break;
					}
			}
		} catch (IOException e) {
			System.err.print("Disconnect...");
		} finally {
			disconnect();
		}
	}

	boolean connect(MessageConnect msg) throws IOException {

		ServerThread old = register(msg.userNickName, msg.userFullName);
		if (old == null) {
			os.writeObject(new MessageConnectResult());
			return true;
		} else {
			os.writeObject(new MessageConnectResult(
					"User " + old.userFullName + " already connected as " + userNickName));
			return false;
		}
	}

	void letter(MessageSend msg) throws IOException {
		ServerMain.addLetter(userNickName, msg.txt);
		os.writeObject(new MessageSendResult());
	}

	void user(MessageUser msg) throws IOException {

		String[] nics = ServerMain.getUsers();
		if (nics != null)
			os.writeObject(new MessageUserResult(nics));
		else
			os.writeObject(new MessageUserResult("Unable to get users list"));
	}

	void checkChat(MessageCheckChat msg) throws IOException {

		String[] lts = ServerMain.getChat();
		if (lts != null)
			os.writeObject(new MessageCheckChatResult(lts));
		else
			os.writeObject(new MessageCheckChatResult("Unable to get chat"));
	}

	private boolean disconnected = false;

	public void disconnect() {
		if (!disconnected)
			try {
				System.err.println(addr.getHostName() + " disconnected");
				unregister();
				os.close();
				is.close();
				sock.close();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				this.interrupt();
				disconnected = true;
			}
	}

	private void unregister() {
		if (userNickName != null) {
			ServerMain.setUser(userNickName, null);
			userNickName = null;
		}
	}

	private ServerThread register(String nic, String name) {
		ServerThread old = ServerMain.registerUser(nic, this);
		if (old == null) {
			if (userNickName == null) {
				userNickName = nic;
				userFullName = name;
				System.err.println("User \'" + name + "\' registered as \'" + nic + "\'");
			}
		}
		return old;
	}
}
