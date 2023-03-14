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
     * Own method that implements error recovery and gives
     * - what the next erroneous token is
     * - what the parser is recognising at the moment
     * - what is the line with the actual problem
     * - file where problem occurs
     * @param actualToken token that is getting parsed at the moment
     * @param nxtToken next erroneous token
     * @return String format line
     * To be thrown inside CompilationException
     */
    private String errorMethod(String actualToken, Token nxtToken) {
        return "Error on line " + nxtToken.lineNumber + " in " 
        + this.file + " - Token expected: " + actualToken + " || Token accepted: " 
        + Token.getName(nxtToken.symbol) + " !\n";
    }

    /**
     * Begin processing token
     * 
     * @throws IOException
     * @throws CompilationException
     */
    @Override
    public void _statementPart_() throws IOException, CompilationException {
        myGenerate.commenceNonterminal("<statementPart>");
        acceptTerminal(Token.beginSymbol);
        try {
            statementList();
        } catch (CompilationException cause) {
            throw new CompilationException(errorMethod("statementList()", nextToken), cause);
        }
        acceptTerminal(Token.endSymbol);
        myGenerate.finishNonterminal("<statementPart>");
    }

    /**
     * Function that accepts the token and gives it to the lexical analyser for processing
     * 
     * @param symbol the specified symbol that it is going to represent
     * @throws IOException
     * @throws CompilationException
     */
    @Override
    public void acceptTerminal(int symbol) throws IOException, CompilationException {
        Token token = nextToken;
        if(symbol == token.symbol) {
            myGenerate.insertTerminal(nextToken);
            nextToken = lex.getNextToken();
            return;
        }
        myGenerate.reportError(nextToken, "Token that was accepted is "+ Token.getName(nextToken.symbol));
    }

    private void statementList() throws IOException, CompilationException{
        myGenerate.commenceNonterminal("<statementList>");
        try {
            statement();   
        } catch (CompilationException cause) {
            throw new CompilationException(errorMethod("statement()", nextToken), cause);
        }
        
        while(nextToken.symbol == Token.semicolonSymbol){
            acceptTerminal(Token.semicolonSymbol);
            try {
                statementList();
            } catch (CompilationException cause) {
                throw new CompilationException(errorMethod("statementList()", nextToken), cause);
            }
        }
        myGenerate.finishNonterminal("<statementList>");
    }

    private void statement() throws IOException, CompilationException{
        myGenerate.commenceNonterminal("<statement>");
        try {
            switch(nextToken.symbol) {
                case Token.identifier:
                    assignment();
                    break;
                case Token.ifSymbol:
                    ifStatement();
                    break;
                case Token.whileSymbol:
                    whileStatement();
                    break;
                case Token.callSymbol: 
                    procedure();
                    break;
                case Token.doSymbol: 
                    untilStatement();
                    break;
                case Token.forSymbol:
                    forStatement();
                    break;
                default:
                    myGenerate.reportError(nextToken, "Error! Expected <assignment statement>, <if statement>,  <while statement>, <procedure statement>, <until statement> or <for statement>, but got a token that is " + Token.getName(nextToken.symbol));
                    break;
                }
            } catch (CompilationException cause) {
                throw new CompilationException(errorMethod("assignment(), ifStatement(),  whileStatement(), procedure(), untilStatement() or forStatement()", nextToken), cause);
            }
        myGenerate.finishNonterminal("<statement>");
    }

    private void assignment() throws IOException, CompilationException{
        myGenerate.commenceNonterminal("<assignment>");
        acceptTerminal(Token.identifier);
        acceptTerminal(Token.becomesSymbol);
        if(nextToken.symbol == Token.stringConstant) {
            acceptTerminal(Token.stringConstant);
        } else {
            try {
                expression();
            } catch (CompilationException cause) {
                throw new CompilationException(errorMethod("expression()", nextToken), cause);
            }
        }
        myGenerate.finishNonterminal("<assignment>");
    }

    private void ifStatement() throws IOException, CompilationException{
        myGenerate.commenceNonterminal("<ifStatement>");
        acceptTerminal(Token.ifSymbol);
        try {
            condition();
        } catch (CompilationException cause) {
            throw new CompilationException(errorMethod("condition()", nextToken), cause);
        }
        acceptTerminal(Token.thenSymbol);

        try {
            statementList();

            if(nextToken.symbol == Token.elseSymbol){
                acceptTerminal(Token.elseSymbol);
                statementList();
            }
        } catch (CompilationException cause) {
            throw new CompilationException(errorMethod("statementList()", nextToken), cause);
        }

        acceptTerminal(Token.endSymbol);
        acceptTerminal(Token.ifSymbol);
        myGenerate.finishNonterminal("<ifStatement>");
    }

    private void whileStatement() throws IOException, CompilationException{
        myGenerate.commenceNonterminal("<whileStatement>");
        acceptTerminal(Token.whileSymbol);
        try {
            condition();  
        } catch (CompilationException cause) {
            throw new CompilationException(errorMethod("condition()", nextToken), cause);
        }
        acceptTerminal(Token.loopSymbol);
        try {
            statementList();
        } catch (CompilationException cause) {
            throw new CompilationException(errorMethod("statementList()", nextToken), cause);
        }
        acceptTerminal(Token.endSymbol);
        acceptTerminal(Token.loopSymbol);
        myGenerate.finishNonterminal("<whileStatement>");
    }

    private void procedure() throws IOException, CompilationException{
        myGenerate.commenceNonterminal("<procedure>");
        acceptTerminal(Token.callSymbol);
        acceptTerminal(Token.identifier);
        acceptTerminal(Token.leftParenthesis);
        try {
            argumentList();   
        } catch (CompilationException cause) {
            throw new CompilationException(errorMethod("argumentList()", nextToken), cause);
        }
        acceptTerminal(Token.rightParenthesis);
        myGenerate.finishNonterminal("<procedure>");
    }

    private void untilStatement() throws IOException, CompilationException{
        myGenerate.commenceNonterminal("<untilStatement>");
        acceptTerminal(Token.doSymbol);
        try {
            statementList();   
        } catch (CompilationException cause) {
            throw new CompilationException(errorMethod("statementList()", nextToken), cause);
        }
        acceptTerminal(Token.untilSymbol);
        try {
            condition();   
        } catch (CompilationException cause) {
            throw new CompilationException(errorMethod("condition()", nextToken), cause);
        }
        myGenerate.finishNonterminal("<untilStatement>");
    }

    private void forStatement() throws IOException, CompilationException{
        myGenerate.commenceNonterminal("<forStatement>");
        acceptTerminal(Token.forSymbol);
        acceptTerminal(Token.leftParenthesis);
        try {
            assignment();   
        } catch (CompilationException cause) {
            throw new CompilationException(errorMethod("assignment()", nextToken), cause);
        }
        acceptTerminal(Token.semicolonSymbol);
        try {
            condition();   
        } catch (CompilationException cause) {
            throw new CompilationException(errorMethod("condition()", nextToken), cause);
        }
        acceptTerminal(Token.semicolonSymbol);
        try {
            assignment();   
        } catch (CompilationException cause) {
            throw new CompilationException(errorMethod("assignment()", nextToken), cause);
        }
        acceptTerminal(Token.rightParenthesis);
        acceptTerminal(Token.doSymbol);
        try {
            statementList();  
        } catch (CompilationException cause) {
            throw new CompilationException(errorMethod("statementList()", nextToken), cause);
        }
        acceptTerminal(Token.endSymbol);
        acceptTerminal(Token.loopSymbol);
        myGenerate.finishNonterminal("<forStatement>");
    }

    private void argumentList() throws IOException, CompilationException{
        myGenerate.commenceNonterminal("<argumentList>");
        acceptTerminal(Token.identifier);

        if(nextToken.symbol == Token.commaSymbol){
            acceptTerminal(Token.commaSymbol);
            try {
                argumentList();  
            } catch (CompilationException cause) {
                throw new CompilationException(errorMethod("argumentList()", nextToken), cause);
            }
        }
        myGenerate.finishNonterminal("<argumentList>");
    }

    private void condition() throws IOException, CompilationException{
        myGenerate.commenceNonterminal("<condition>");
        acceptTerminal(Token.identifier);

        try {
            conditionOp();
        } catch (CompilationException cause) {
            throw new CompilationException(errorMethod("conditionOp()", nextToken), cause);
        }

        switch (nextToken.symbol) {
            case Token.identifier:
                acceptTerminal(Token.identifier);
                break;
            case Token.numberConstant:
                acceptTerminal(Token.numberConstant);
                break;
            case Token.stringConstant:
                acceptTerminal(Token.stringConstant);
                break;
            default:
                break;
        }
        myGenerate.finishNonterminal("<condition>");
    }

    private void conditionOp() throws IOException, CompilationException{
        myGenerate.commenceNonterminal("<conditionOp>");
        switch(nextToken.symbol){
            case Token.greaterThanSymbol:
            acceptTerminal(Token.greaterThanSymbol);
            break;
            case Token.greaterEqualSymbol:
            acceptTerminal(Token.greaterEqualSymbol);
            break;
            case Token.equalSymbol:
            acceptTerminal(Token.equalSymbol);
            break;
            case Token.notEqualSymbol:
            acceptTerminal(Token.notEqualSymbol);
            break;
            case Token.lessThanSymbol:
            acceptTerminal(Token.lessThanSymbol);
            break;
            case Token.lessEqualSymbol:
            acceptTerminal(Token.lessEqualSymbol);
            break;
        }
        myGenerate.finishNonterminal("<conditionOp>");
    }

    private void expression() throws IOException, CompilationException{
        myGenerate.commenceNonterminal("<expression>");
        try {
            term();

            while(nextToken.symbol == Token.plusSymbol 
            || nextToken.symbol == Token.minusSymbol){
                acceptTerminal(nextToken.symbol);
                expression();
            }
        } catch (CompilationException cause) {
            throw new CompilationException(errorMethod("term()", nextToken), cause);
        }
        myGenerate.finishNonterminal("<expression>");
    }

    private void term() throws IOException, CompilationException{
        myGenerate.commenceNonterminal("<term>");
        try {
            factor();

            while(nextToken.symbol == Token.timesSymbol 
            || nextToken.symbol == Token.divideSymbol){
                acceptTerminal(nextToken.symbol);
                term();
            }
        } catch (CompilationException cause) {
            throw new CompilationException(errorMethod("factor()", nextToken), cause);
        }
        myGenerate.finishNonterminal("<term>");
    }

    private void factor() throws IOException, CompilationException{
        myGenerate.commenceNonterminal("<factor>");
        try {
            switch(nextToken.symbol) {
                case Token.identifier:
                    acceptTerminal(Token.identifier);
                    break;
                case Token.numberConstant:
                    acceptTerminal(Token.numberConstant);
                    break;
                case Token.leftParenthesis:
                    acceptTerminal(Token.leftParenthesis);
                    expression();
                    acceptTerminal(Token.rightParenthesis);
                default:
                    myGenerate.reportError(nextToken, "Error! Expected IDENTIFIER, NUMBER or (expression), while token is " + Token.getName(nextToken.symbol));
            }
        } catch (CompilationException cause) {
            throw new CompilationException(errorMethod("expression()", nextToken), cause);
        }
        myGenerate.finishNonterminal("<factor>");
    }
}
