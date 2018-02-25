package Domain;
import java.io.File;
import java.io.FileNotFoundException;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.*;
import Domain.NodeVisitor;

public class Code{
	File origin;
	CompilationUnit unit;
	NodeVisitor visitor;
	int empty,over,todo,tfault;
	String Result;
	public Code(File origin) {
		this.origin = origin;
		Result = new String();
		visitor = new NodeVisitor();
		empty = 0;
		over = 0;
		todo = 0;
		tfault = 0;
	}
	public void ParseCode() throws FileNotFoundException{
		unit = JavaParser.parse(origin);
	}
	public String checkClause() {
		visitor.visit(unit,null);
		return visitor.getStatement();
	}
	public String getPath() {
		return origin.getAbsolutePath();
	}
	public String getName() {
		return origin.getName();
	}
	public void fillCounters() {
		empty = visitor.getEmptyCounter();
		over = visitor.getOverCatchCounter();
		todo = visitor.getToDoFixCounter();
		tfault = empty + over + todo;
	}
	public int getEmpty() {
		return empty;
	}
	public int getOver() {
		return over;
	}
	public int getTodo() {
		return todo;
	}
	public int getTfault() {
		return tfault;
	}
	public String getResult() {
		return Result;
	}
	public void Analyse() {
		Result = checkClause();
		if(Result.equals(null)) {
			Result = "";
		}
		fillCounters();
	}
}
