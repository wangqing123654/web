package jdo.bil;

//import java.io.Serializable;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BILSPCINVDtos  implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7723597214400416398L;
	//private static final long serialVersionUID = 1L;
	private List<BILSPCINVDto>  bilLists=new ArrayList<BILSPCINVDto>();
	public List<BILSPCINVDto> getBilLists() {
		return bilLists;
	}
	public void setBilLists(List<BILSPCINVDto> bilLists) {
		this.bilLists = bilLists;
	}

	
	public void addBillists(BILSPCINVDto bilObj){
		this.bilLists.add(bilObj);
	}
	
	
	
}
