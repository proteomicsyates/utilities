package edu.scripps.yates.utilities.swing;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.MissingOptionException;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * This abstract class defines the implementor as a class that can be used by
 * {@link CommandLineProgramGuiEnclosable} to generate a GUI from a command line
 * program
 * 
 * @author salvador
 *
 */
public abstract class CommandLineProgramGuiEnclosable {
	protected static final String GUI = "gui";
	protected static final String GUI_LONG = "graphical_interface";

	private final Options options;

	/**
	 * 
	 * @param mainArgs the arguments passed to the {@code main(String[] args) }
	 *                 method
	 * @throws ParseException
	 * @throws DoNotInvokeRunMethod         catch this exception so that you dont
	 *                                      invoke {@code run()} method
	 * @throws SomeErrorInParametersOcurred
	 */
	public CommandLineProgramGuiEnclosable(String[] mainArgs)
			throws ParseException, DoNotInvokeRunMethod, SomeErrorInParametersOcurred {
		boolean guiMode = false;
		try {
			final List<Option> optionList = defineCommandLineOptions();
			options = new Options();
			// add new option gui
			options.addOption(GUI, GUI_LONG, false,
					"Launch this tool with a graphical interface. All other command line parameters will be ignored.");
			final CommandLineParser parser = new DefaultParser();
			CommandLine cmd = parser.parse(options, mainArgs);

			guiMode = cmd.hasOption(GUI);

			if (guiMode) {
				final Options guiOptions = new Options();
				for (final Option option : optionList) {

					options.addOption(option);
					final Option optionalOption = (Option) option.clone();
					optionalOption.setRequired(false);
					guiOptions.addOption(optionalOption);
				}
				mainArgs = removeGUIOptionFromArguments(mainArgs);
				cmd = parser.parse(guiOptions, mainArgs);
				startGUI();
				// throw this exception so that in the main, we dont run the run method
				throw new DoNotInvokeRunMethod();
			} else {

				for (final Option option : optionList) {
					options.addOption(option);
				}
				mainArgs = removeGUIOptionFromArguments(mainArgs);
				cmd = parser.parse(options, mainArgs);
				initToolFromCommandLineOptions(cmd);
			}
		} catch (final MissingOptionException e) {
			if (!guiMode) {
				throw new SomeErrorInParametersOcurred(e.getMessage() + ".  ***You can also run this tool with '-" + GUI
						+ "' option to launch the graphical interface.***");
			} else {
				throw e;
			}
		}
	}

	private String[] removeGUIOptionFromArguments(String[] mainArgs) {
		final List<String> ret = new ArrayList<String>();
		for (final String arg : mainArgs) {
			if (arg.startsWith("-")) {
				if (!arg.equals("-" + GUI)) {
					ret.add(arg);
				}
			} else if (arg.startsWith("--")) {
				if (!arg.equals("--" + GUI_LONG)) {
					ret.add(arg);
				}
			} else {
				ret.add(arg);
			}
		}
		return ret.toArray(new String[0]);
	}

	private void startGUI() {
		final AutomaticGUICreator gui = new AutomaticGUICreator(this);
		gui.setVisible(true);
	}

	protected abstract List<Option> defineCommandLineOptions();

	protected Options getCommandLineOptions() {
		return options;
	};

	/**
	 * Method invoked by the graphical interface when the user press the RUN button
	 * 
	 * @throws Exception
	 */
	public abstract void run() throws Exception;

	/**
	 * Title of the frame window
	 * 
	 * @return
	 */
	public abstract String getTitleForFrame();

	/**
	 * Init the tool with the options set in the command line.<br>
	 * Use this method to further check the validity of the argument captured from
	 * the GUI (such as checking whether a file exists or not), and it is not valid,
	 * call to
	 * 
	 * <pre>
	 * {@code errorInParameters(error_message);}
	 * </pre>
	 * 
	 * For example:
	 * 
	 * <pre>
	 * {@code 
	* if (cmd.hasOption("in")) { 
	*    final String inputFilePath = cmd.getOptionValue("in"); 
	* } else { 
	* 	 super.errorInParameters("Input file path is is missing"); 
	* }
	 * 
	 * </pre>
	 * 
	 * @param cmd command line object
	 * 
	 * @throws SomeErrorInParametersOcurred
	 */
	protected abstract void initToolFromCommandLineOptions(CommandLine cmd) throws SomeErrorInParametersOcurred;

	/**
	 * This method should print the usage of the command line such as:<br>
	 * programName -i [input file] -an [value] ..."
	 * 
	 * @return
	 */
	public abstract String printCommandLineSintax();

	/**
	 * Override this method if you want something different than:<br>
	 * "Contact Salvador Martinez-Bartolome at salvador@scripps.edu for more help"
	 * 
	 * @return
	 */
	public String printFooter() {
		return "Contact Salvador Martinez-Bartolome at salvador@scripps.edu for more help";
	}

	/**
	 * Prints an error regarding the parameter and subsequently throws
	 * {@link SomeErrorInParametersOcurred}
	 * 
	 * @param header
	 * @throws SomeErrorInParametersOcurred
	 */
	protected void errorInParameters(String header) throws SomeErrorInParametersOcurred {
		// automatically generate the help statement
		final HelpFormatter formatter = new HelpFormatter();
		String printCommandLineSintax = printCommandLineSintax();
		final boolean printAutomaticUsage = printCommandLineSintax == null;
		if (printCommandLineSintax == null) {
			printCommandLineSintax = "java -jar *.jar";
		}
//		if (header == null) {
//			formatter.printHelp(250,printCommandLineSintax, options);
//		} else {
//		if (header != null) {
//			header = "\n\n************\n" + header + "\n************\n\n";
//		}
		formatter.printHelp(250, printCommandLineSintax, header, options, printFooter(), printAutomaticUsage);
//		}
		throw new SomeErrorInParametersOcurred(header);
	}
}
