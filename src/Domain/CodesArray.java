package Domain;

import java.util.ArrayList;
import java.util.List;

public class CodesArray extends Thread{
	private List<Code> codes;
	private int faults;
	public CodesArray() {
		codes = new ArrayList<Code>();
		faults = 0;
	}
	public void addCode(Code code) {
		codes.add(code);
	}
	public int getSize() {
		return codes.size();
	}
	public List<Code> getCodes(){
		return codes;
	}
	public int getFaults() {
		return this.faults;
	}
	@Override
	public void run() {
		for(Code code:codes) {
			System.out.println("Checking Code: "+code.getPath());
			code.Analyse();
			faults += code.getTfault();
		}
		this.interrupt();
	}

}
