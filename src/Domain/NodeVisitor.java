package Domain;

import java.util.List;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.CatchClause;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class NodeVisitor extends VoidVisitorAdapter<Void> {
	private String catchStm;
	private int EmptyStat,ToDoFix,OverCatch;
	public NodeVisitor() {
		this.catchStm = "";
		this.EmptyStat = 0;
		this.ToDoFix = 0;
		this.OverCatch = 0;
	}
	@Override
	public void visit(CatchClause clause, Void arg) {
		super.visit(clause, arg);
		boolean empty = false,fixme = false, overCatch = false;
		overCatch = overCatchExit(clause);
		if(!overCatch) {
			
			fixme =todoFixmeComment(clause.getBody());
			if(!fixme){
				empty = EmptyStatement(clause.getBody());
			}
		}
		if(empty || fixme || overCatch) {
			if(overCatch) {
				catchStm += "Warning OverCatched Exception";
			}
			if(fixme) {
				catchStm +="Warning TODO FIXME Comment";
			}
			if(empty) {
				catchStm += "Warning Empty Catch";
			}
			catchStm +="[Line : "+ clause.getBegin().get().line +"] "+ clause.toString()+"\n";
		}
	}
	public String getStatement() {
		return catchStm;
	}
	private boolean EmptyStatement(BlockStmt statements) {
		boolean returned = false;
		NodeList<Statement> blockStm = statements.getStatements();
		if(blockStm.isEmpty()) {
			returned = true;
			this.EmptyStat ++;
		}
		return returned;
	}
	private boolean todoFixmeComment(BlockStmt statements) {
		boolean returned = false;
		List<Comment> comments = statements.getAllContainedComments();
		if(!comments.isEmpty()) {
			while(comments.iterator().hasNext() && !returned) {
				String content = comments.iterator().next().getContent();
				if( content.contains("TODO") || content.contains("FIXME") ) {
					returned = true;
					this.ToDoFix ++;
				}
			}
		}
		return returned;
	}
	private boolean overCatchExit(CatchClause clause) {
		boolean returned = false;
		String parameter = clause.getParameter().getType().asString();
		if(parameter.equals("Exception") || parameter.equals("Throwable")) {
			returned = true;
			this.OverCatch ++;
		}
		return returned;
	}
	public int getOverCatchCounter() {
		return this.OverCatch;
	}
	public int getToDoFixCounter() {
		return this.ToDoFix;
	}
	public int getEmptyCounter() {
		return this.EmptyStat;
	}
}
