package csdev;

/**
 * <p>
 * CMD interface: Client message IDs
 * 
 * @author Sergey Gutnikov
 * @version 1.0
 */
interface CMD {
	static final byte CMD_CONNECT = 1;
	static final byte CMD_DISCONNECT = 2;
	static final byte CMD_USER = 3;
	static final byte CMD_CHECK_CHAT = 4;
	static final byte CMD_SEND = 5;
}

/**
 * <p>
 * RESULT interface: Result codes
 * 
 * @author Sergey Gutnikov
 * @version 1.0
 */
interface RESULT {
	static final int RESULT_CODE_OK = 0;
	static final int RESULT_CODE_ERROR = -1;
}

/**
 * <p>
 * PORT interface: Port #
 * 
 * @author Sergey Gutnikov
 * @version 1.0
 */
interface PORT {
	static final int PORT = 8088;
}

/**
 * <p>
 * Protocol class: Protocol constants
 * 
 * @author Sergey Gutnikov
 * @version 1.0
 */
public class Protocol implements CMD, RESULT, PORT {
	private static final byte CMD_MIN = CMD_CONNECT;
	private static final byte CMD_MAX = CMD_SEND;

	public static boolean validID(byte id) {
		return id >= CMD_MIN && id <= CMD_MAX;
	}
}
