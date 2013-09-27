
package stanford.parse;

import ixa.kaflib.KAFDocument;
import ixa.kaflib.WF;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.Sentence;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.trees.HeadFinder;
import edu.stanford.nlp.trees.PennTreebankLanguagePack;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreePrint;
import edu.stanford.nlp.trees.TreebankLanguagePack;

/**
 * This class provides a takes KAF <text> and <terms> as input and outputs 
 * parsing. The parser used is from Stanford CoreNLP (http://www-nlp.stanford.edu/software/)
 * 
 * @author ragerri
 *
 */
public class Annotate {

	  private LexicalizedParser parser;
	  private TreePrint treePrinter;
	  private TreebankLanguagePack tlp;
	  private StringBuilder parsedDoc;

	  
	  /**
	   * This constructor takes into account the outputFormat option to print the parse tree. 
	   * The options are "oneline" (default) and "penn". 
	   * 
	   * @param outputFormat
	   */
	  public Annotate(String outputFormat) {
	    parser = LexicalizedParser.loadModel("englishPCFG.ser.gz");
	    treePrinter = new TreePrint(outputFormat);
	    parsedDoc = new StringBuilder();
	  }
	  
	  /**
	   * Constructor that takes into account the options of outputFormat (oneline, penn), instructs
	   * TreePrint to mark the head words in the parse tree and it specifies which HeadFinder to use. 
	   * 
	   * The headFinder choices are SemanticHeadFinder or CollinsHeadFinder. For more info check those
	   * classes in the Stanford Parser javadoc. 
	   * 
	   * @param outputFormat either oneline or penn format
	   * @param markHeadNodes mark head words in the parse tree
	   * @param headFinder either Collins or Semantic HeadFinder
	   */
	  public Annotate(String outputFormat, String markHeadNodes, HeadFinder headFinder) {
	      parsedDoc = new StringBuilder();
		  parser = LexicalizedParser.loadModel("englishPCFG.ser.gz");
		  tlp = new PennTreebankLanguagePack();
		  treePrinter = new TreePrint(outputFormat,markHeadNodes,tlp, headFinder, headFinder);
	  }
	  

	  /**
       * This method uses Stanford Parser to provide Constituent Parsing
       * 
       * @param KAFDocument a KAF Document containing <text> and <terms> elements
       * @return StringBuilder containing the parsed document
       * @throws IOException
       */
	  private StringBuilder getParse(KAFDocument kaf) throws IOException {

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
          
        List<List<WF>> sentences = kaf.getSentences();
        for (List<WF> sentence : sentences) {
          // get array of token forms from a list of WF objects
          String[] tokens = new String[sentence.size()];
          for (int i = 0; i < sentence.size(); i++) {
            tokens[i] = sentence.get(i).getForm();
          }

          // Constituent Parsing
          List<CoreLabel> rawWords = Sentence.toCoreLabelList(tokens);
          Tree parse = parser.apply(rawWords);
          //output Tree in various formats
          treePrinter.printTree(parse,pw);
          parsedDoc.append(sw.toString());
          sw.getBuffer().setLength(0);
          
        }
        return parsedDoc;
      }
	  

	  

      /**
       * Prints parse tree in Penn TreeBank format (either oneline or penn) into 
       * standard output. 
       * 
       * @param kaf
       * @throws IOException
       */
      public void parse(KAFDocument kaf) throws IOException {

        parsedDoc = this.getParse(kaf);
        System.out.print(parsedDoc.toString());
      }
	  
	  
	  /**
	   * Prints parse tree in KAF <constituents> elements to standard output 
	   * 
	   * @param kaf
	   * @throws Exception
	   */
	  public void parseToKAF(KAFDocument kaf) throws Exception {

	    parsedDoc = this.getParse(kaf);
	    kaf.addConstituencyFromParentheses(parsedDoc.toString());
	    System.out.print(kaf.toString());
	  }
	  
	}
