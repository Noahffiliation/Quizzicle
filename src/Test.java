import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.FileInputStream;

public class Test {
    public static void main(String[] args) throws Exception {
        FileInputStream inputstream = new FileInputStream("input/csTerms.txt");
        ANTLRInputStream input = new ANTLRInputStream(inputstream);
        QuizzicleLexer lexer = new QuizzicleLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        QuizzicleParser parser = new QuizzicleParser(tokens);
        ParseTree tree = parser.file();

        QuizzicleVizz visitor = new QuizzicleVizz();
        visitor.visit(tree);
    }
}
