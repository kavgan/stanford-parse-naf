package stanford.parse;

import ixa.kaflib.KAFDocument;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.MutuallyExclusiveGroup;
import net.sourceforge.argparse4j.inf.Namespace;
import edu.stanford.nlp.trees.CollinsHeadFinder;
import edu.stanford.nlp.trees.HeadFinder;
import edu.stanford.nlp.trees.SemanticHeadFinder;
import edu.stanford.nlp.trees.international.negra.NegraHeadFinder;

/**
 * This module provides parsing for English text using Stanford CoreNLP API
 * http://www-nlp.stanford.edu/software/).
 * 
 * The module takes KAF and reads the header, <text> and <terms> elements and
 * uses Annotate class to provide constituent parsing of sentences in both Penn
 * Treebank and KAF form which are provided via standard output.
 * 
 * 
 * @author ragerri
 */
public class CLI {

  /**
   * 
   * 
   * BufferedReader (from standard input) and BufferedWriter are opened.
   * 
   * @param args
   * @throws Exception
   */

  public static void main(String[] args) throws Exception {
     /*
     * Parse the command line arguments
     */
      ArgumentParser parser = getArgumentParser();
      Namespace parsedArguments = parseParameters(args, parser);

    /*
     * Parameters
     */

    String outputFormat = parsedArguments.getString("outputFormat");
    String model = parsedArguments.getString("model");
    String headFinderOption = parsedArguments.getString("heads");
    String lang = parsedArguments.getString("lang");
    Boolean outputKaf = parsedArguments.getBoolean("kaf");

    //Load KAF
    KAFDocument kaf = null;
    try (BufferedReader buffer = new BufferedReader(new InputStreamReader(System.in, "UTF-8"))){
      // construct kaf Reader and read from standard input
      kaf = KAFDocument.createFromStream(buffer);
    } catch (IOException e) {
      e.printStackTrace();
      System.exit(1);
    }

    // language parameter from KAF

    if (lang.isEmpty()) {
      lang = kaf.getLang();
        System.err.println("Language from KAF");
    }

    kaf.addLinguisticProcessor("constituents", "stanford-parse-" + lang, "3.5.2");

    // Prepare annotator and annotate
    Annotate annotator;

    if (!headFinderOption.isEmpty()) {
      // choosing HeadFinder: (Collins rules; sem Semantic headFinder
      // re-implemented from
      // Stanford CoreNLP. Default: sem (semantic head finder).
      HeadFinder headFinder = null;
      if (lang.isEmpty()){
        System.err.println("If you want Head you need to specify lang");
          System.exit(1);
      }
      if (lang.equalsIgnoreCase("en")) {
        if (headFinderOption.equalsIgnoreCase("collins")) {
          headFinder = new CollinsHeadFinder();
        } else {
          headFinder = new SemanticHeadFinder();
        }
      }
      if (lang.equalsIgnoreCase("de")) {
        headFinder = new NegraHeadFinder();
        }
      annotator = new Annotate(
        lang,model,
        outputFormat,
        "markHeadNodes", headFinder);
      }
      // annotator without heads
      else {
        annotator = new Annotate(model, outputFormat);
      }
      // print output
      String output;
      // check if kaf is chosen and parse
      if (outputKaf) {
        output = annotator.parseToKAF(kaf);
      } else {
        output = annotator.parse(kaf);
      }
      System.out.print(output);
  }

    private static Namespace parseParameters(String[] args, ArgumentParser parser) {
        Namespace parsedArguments = null;
        try {
          parsedArguments = parser.parseArgs(args);
        } catch (ArgumentParserException e) {
          parser.handleError(e);
          System.out
              .println("Run java -jar target/stanford-parse-3.2.0.jar -help for details");
          System.exit(1);
        }
        return parsedArguments;
    }

    private static ArgumentParser getArgumentParser() {
        // create Argument Parser
        ArgumentParser parser = ArgumentParsers.newArgumentParser("stanford-parse-3.5.2.jar")
                .description("stanford-parse-3.5.2 is a KAF wrapper for the English Stanford Parser.\n");

        MutuallyExclusiveGroup excGroup = parser.addMutuallyExclusiveGroup();

        excGroup.addArgument("-k", "--kaf").action(Arguments.storeTrue())
            .help("Choose KAF format");
        excGroup.addArgument("-o", "--outputFormat").choices("penn", "oneline")
            .setDefault("oneline").required(false)
            .help("Choose between Penn style or oneline LISP style tree output");
        parser.addArgument("-g", "--heads")
            .choices("collins", "sem")
            .setDefault("")
            .required(false)
            .help("Choose between Collins-based or Stanford Semantic HeadFinder");
        // specify language
        parser.addArgument("-l", "--lang").choices("en", "de").setDefault("")
            .help("Choose a language to perform annotation with stanford-parse");
        parser.addArgument("-m", "--model")
            .setDefault("edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz").required(false)
                .help("Choose a Model to perform annotation with stanford-parse");
        return parser;
    }
}
