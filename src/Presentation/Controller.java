package Presentation;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingWorker;

import Domain.Code;
import Domain.CodesArray;

public class Controller extends SwingWorker<List<String>,Void>{
	private List<Code> codes;
	private List<CodesArray> codesArray;
	int empty,over,todo,processed,total;
	private static final int MaxSize = 50;
	private static final int MaxThreads = 10;
	public Controller() {
		codes= new ArrayList<Code>();
		codesArray = new ArrayList<CodesArray>();
		empty = 0;
		over = 0;
		todo = 0;
		processed = 0;
		total = 0;
	}
	public void Explore(File file) {
		if(file.isDirectory()) {
			for(File child:file.listFiles()) {
				this.Explore(child);
			}
		}else {
			if(file.getPath().endsWith(".java")) {
				codes.add(new Code(file));
			}
		}
		if(codes.size() != total) {
			total = codes.size();
		}
	}
	public int numberFiles() {
		return total;
	}
	public int getProcessed() {
		return this.processed;
	}
	public void clearList() {
		codes.clear();
		empty = 0;
		over = 0;
		todo = 0;
		processed = 0;
		total = 0;
	}
	public void checkCode() throws FileNotFoundException {
		int iant = 0;
		int size = codes.size();
		int threadSize = codes.size();
		while(threadSize > MaxSize) {
			threadSize /=10;
		}
		for(int i = 0;i < size;i+=threadSize) {
			if((i  + threadSize) > size) {
				threadSize = size - i;
			}
			assignment(i,threadSize);
			if((codesArray.size() - iant) >= MaxThreads) {// threads to start
				StartThreads(iant);
				iant = codesArray.size();
			}
			this.setProgress((processed/total)*100);
		}
		if(iant < codesArray.size()) {// is missing one execution
			StartThreads(iant);
		}
		
	}
	public List<String> getResults(){
		List<String> returned = new ArrayList<String>();
		for(int i = 0; i < codesArray.size();i++) {
			if(codesArray.get(i).getFaults() > 0) {
				CodesArray codesarray = codesArray.get(i);
				for(int j = 0;j < codesarray.getSize();j++) {
					Code code = codesarray.getCodes().get(j);
					if(code.getTfault() > 0) {
						returned.add("Source File:" + code.getName()+ "\n"+code.getResult());
						CountCounters(code.getEmpty(),code.getOver(), code.getTodo());
					}
				}
			}
		}
		returned.add(Report());
		return returned;
	}
	private void CountCounters(int empty,int over,int todo) {
		this.empty += empty;
		this.over += over;
		this.todo = todo;
	}
	private String Report() {
		String returned = "";
		returned += "Empty Cacth blocks:    " + String.valueOf(empty)+"\n";
		returned += "Over-catch exceptions: " + String.valueOf(over)+"\n";
		returned += "TODO FIXME exceptions: " + String.valueOf(todo)+"\n";
		return returned;
	}
	private void assignment(int init, int size) throws FileNotFoundException {
		CodesArray codesarray = new CodesArray();
		for(int i = init; i < (init + size);i++) {
			codes.get(i).ParseCode();
			processed ++;
			codesarray.addCode(codes.get(i));
		}
		codesArray.add(codesarray);
	}
	public boolean CheckStatus(int init,int size) {
		boolean returned = false;
		for(int i = init; i < (init + size);i++) {
			if(codesArray.get(i).isAlive()) {
				returned = true;
			}
		}
		return returned;
	}
	private void StartThreads(int iant) {
		for(int j = 0;j < (codesArray.size()-iant);j++)
		{
			CodesArray codesarray = codesArray.get(j+iant);
			System.out.println("Thread size: "+ codesarray.getSize());
			new Thread(codesarray).start();
		}
		while(CheckStatus(iant, codesArray.size()-iant)) {
		}
	}
	@Override
	protected List<String> doInBackground() throws FileNotFoundException {
		this.checkCode();
		return this.getResults();
	}
	
}
