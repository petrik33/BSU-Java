package csdev;

/**
 * <p>
 * Command handler interface
 * 
 * @author Sergey Gutnikov
 * @version 1.0
 */

public interface CmdHandler {
	boolean onCommand(int[] errorCode);
}
