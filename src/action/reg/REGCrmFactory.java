package action.reg;
/**
 * 
 * @author Administrator
 *
 */
public class REGCrmFactory {
	private final static REGCRMAction INSTANCE = new REGCRMAction();
	public static REGCRMAction getInstance() {
		return INSTANCE;
	}

}
