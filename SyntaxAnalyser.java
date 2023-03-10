import java.io.IOException;

public class SyntaxAnalyser extends AbstractSyntaxAnalyser{

    private String file;

    /**
     * Constructor for Syntax Analyser, getting called in the Compile.java
     * Needs to invoke and initialise lexical analyser
     * 
     * @param String program file to start analysing
     */
    public SyntaxAnalyser(String file) {
        this.file = file;
        try {
            lex = new LexicalAnalyser(file);
        } catch (Exception e) {
            System.err.println("Failed to load!");
        }
    }

    /**
     * Begin processing token
     * 
     * @throws IOException
     * @throws CompilationException
     */
    @Override
    public void _statementPart_() throws IOException, CompilationException {
        throw new UnsupportedOperationException("Unimplemented method '_statementPart_'");
    }

    @Override
    public void acceptTerminal(int symbol) throws IOException, CompilationException {
        throw new UnsupportedOperationException("Unimplemented method 'acceptTerminal'");
    }

    private void statementList() throws IOException, CompilationException{}

    private void statement() throws IOException, CompilationException{}

    private void assignment() throws IOException, CompilationException{}

    private void ifStatement() throws IOException, CompilationException{}

    private void whileStatement() throws IOException, CompilationException{}

    private void procedure() throws IOException, CompilationException{}

    private void untilStatement() throws IOException, CompilationException{}

    private void forStatement() throws IOException, CompilationException{}

    private void argumentList() throws IOException, CompilationException{}

    private void condition() throws IOException, CompilationException{}

    private void conditionOp() throws IOException, CompilationException{}

    private void expression() throws IOException, CompilationException{}

    private void term() throws IOException, CompilationException{}

    private void factor() throws IOException, CompilationException{}
}
