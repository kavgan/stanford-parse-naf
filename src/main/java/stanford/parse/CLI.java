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

    Namespace parsedArguments = null;

    // create Argument Parser
    ArgumentParser parser = ArgumentParsers.newArgumentParser(
        "stanford-parse-3.2.0.jar").description(
        "stanford-parse-3.2.0 is a KAF wrapper for the English Stanford Parser"
            + ".\n");

    MutuallyExclusiveGroup excGroup = parser.addMutuallyExclusiveGroup();

    excGroup.addArgument("-k", "--kaf").action(Arguments.storeTrue())
        .help("Choose KAF format");
    excGroup.addArgument("-o", "--outputFormat").choices("penn", "oneline")
        .setDefault("oneline").required(false)
        .help("Choose between Penn style or oneline LISP style tree output");

    parser.addArgument("-g", "--heads").choices("collins", "sem")
        .required(false)
        .help("Choose between Collins-based or Stanford Semantic HeadFinder");

    // specify language
    parser.addArgument("-l", "--lang").choices("en", "de").required(false)
        .help("Choose a language to perform annotation with ixa-pipe-parse");

    /*
     * Parse the command line arguments
     */

    // catch errors and print help
    try {
      parsedArguments = parser.parseArgs(args);
    } catch (ArgumentParserException e) {
      parser.handleError(e);
      System.out
          .println("Run java -jar target/stanford-parse-3.2.0.jar -help for details");
      System.exit(1);
    }

    /*
     * Load language and headFinder parameters
     */

    String outputFormat = parsedArguments.getString("outputFormat");
    String headFinderOption;
    if (parsedArguments.get("heads") == null) {
      headFinderOption = "";
    } else {
      headFinderOption = parsedArguments.getString("heads");
    }

    BufferedReader breader = null;

    try {
      // construct kaf Reader and read from standard input
      breader = new BufferedReader(new InputStreamReader(System.in, "UTF-8"));
      KAFDocument kaf = KAFDocument.createFromStream(breader);
      // language parameter
      String lang;
      if (parsedArguments.get("lang") == null) {
        lang = kaf.getLang();
      } else {
        lang = parsedArguments.getString("lang");
      }

      kaf.addLinguisticProcessor("constituents", "stanford-parse-" + lang,
          "3.2.0");

      // choosing HeadFinder: (Collins rules; sem Semantic headFinder
      // re-implemented from
      // Stanford CoreNLP. Default: sem (semantic head finder).

      HeadFinder headFinder = null;

      if (!headFinderOption.isEmpty()) {

        if (parsedArguments.get("lang").equals("en")) {
          if (headFinderOption.equalsIgnoreCase("collins")) {
            headFinder = new CollinsHeadFinder();
          } else {
            headFinder = new SemanticHeadFinder();
          }
          if (parsedArguments.get("lang").equals("de")) {
            if (headFinderOption.equalsIgnoreCase("collins")) {
              headFinder = new NegraHeadFinder();
            } else {
              headFinder = new NegraHeadFinder();
            }
          }
          Annotate annotator = new Annotate(lang, outputFormat,
              "markHeadNodes", headFinder);
          // check if kaf is chosen
          if (parsedArguments.getBoolean("kaf") == true) {
            annotator.parseToKAF(kaf);
          } else {
            annotator.parse(kaf);
          }

        }
      }

      // parse without heads
      else {
        Annotate annotator = new Annotate(lang, outputFormat);
        if (parsedArguments.getBoolean("kaf") == true) {
          annotator.parseToKAF(kaf);
        } else {
          annotator.parse(kaf);
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    breader.close();

  }
}
