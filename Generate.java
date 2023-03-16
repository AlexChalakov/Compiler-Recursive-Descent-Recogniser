public class Generate extends AbstractGenerate{

    // Constructor
    public Generate() {
    }

    /**
     * Method for reporting an Error
     * 
     * @param Token token coming from the Token class
     * @param String explanatoryMessage - a specified string of what the error says
     */
    @Override
    public void reportError(Token token, String explanatoryMessage) throws CompilationException {
        throw new CompilationException(explanatoryMessage);
    }
    
}
