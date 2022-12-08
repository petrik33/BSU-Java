package csdev;

import java.io.Serializable;

/**
 * <p>MessageDisconnect class
 * @author Sergey Gutnikov
 * @version 1.0
 */
public class MessageDisconnect extends Message implements Serializable {
		
	private static final long serialVersionUID = 1L;
	
	public MessageDisconnect() {
		super( Protocol.CMD_DISCONNECT );
	}
	
}

