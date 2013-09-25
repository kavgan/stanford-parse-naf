
package stanford.parse;

import ixa.kaflib.KAFDocument;
import ixa.kaflib.WF;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import org.jdom2.JDOMException;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.Sentence;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.trees.HeadFinder;
import edu.stanford.nlp.trees.PennTreebankLanguagePack;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreePrint;
import edu.stanford.nlp.trees.TreebankLanguagePack;

/**
 * This class provides a takes KAF <word forms> as input and outputs 
 * POS tags and lemmas into the <terms> elements. For POS tagging and lemmatization 
 * the Stanford CoreNLP is used (http://www-nlp.stanford.edu/software/)
 * 
 * @author ragerri
 *
 */
public class Annotate {

	  private LexicalizedParser parser;
	  private TreePrint treePrinter;
	  private TreebankLanguagePack tlp;

	  
	  public Annotate(String outputFormat) {
	    parser = LexicalizedParser.loadModel("englishPCFG.ser.gz");
	    treePrinter = new TreePrint(outputFormat);
	  }
	  
	  public Annotate(String outputFormat, String markHeadNodes, HeadFinder headFinder) { 
		  parser = LexicalizedParser.loadModel("englishPCFG.ser.gz");
		  tlp = new PennTreebankLanguagePack();
		  treePrinter = new TreePrint(outputFormat,markHeadNodes,tlp, headFinder, headFinder);
	  }
	  
	 	  

	  /**
	   * This method uses Stanford Parser to provide Constituent Parsing
	   * 
	   * It gets a Map<SentenceId, tokens> from the input KAF document and iterates
	   * over the tokens of each sentence.
	   * 
	   * @param KAFDocument a KAF Document containing <text> and <terms> elements
	   * @return String parsed document
	   * @throws JDOMException
	   */

	  public void getParse(KAFDocument kaf) throws IOException {

	    StringBuilder parsedDoc = new StringBuilder();
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
	    System.out.print(parsedDoc.toString());
	  }
	  
	  /**
	   * This method uses Stanford Parser to provide Constituent Parsing
	   * 
	   * It gets a Map<SentenceId, tokens> from the input KAF document and iterates
	   * over the tokens of each sentence.
	   * 
	   * @param KAFDocument a KAF Document containing <text> and <terms> elements
	   * @return String parsed document
	 * @throws Exception 
	   * @throws JDOMException
	   */

	  public void getParseToKAF(KAFDocument kaf) throws Exception {

	    StringBuilder parsedDoc = new StringBuilder();
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
	      //treePrinter.printTree(parse);
	      treePrinter.printTree(parse,pw);
	      parsedDoc.append(sw.toString());
	      sw.getBuffer().setLength(0);
	      
	    }
	    kaf.addConstituencyFromParentheses(parsedDoc.toString());
	    System.out.print(kaf.toString());
	  }
	  
	}
