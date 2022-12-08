package csdev;

import java.io.Serializable;

/**
 * <p>MessageUser class: Get userNics list
 * @author Sergey Gutnikov
 * @version 1.0
 */
public class MessageUser extends Message implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	public MessageUser() {
		super( Protocol.CMD_USER );
	}
}