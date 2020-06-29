package edu.scripps.yates.utilities.swing;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

/**
 * This abstract class defines the implementor as a class that can be used by
 * {@link CommandLineProgramGuiEnclosable} to generate a GUI from a command line
 * program
 * 
 * @author salvador
 *
 */
public abstract class CommandLineProgramGuiEnclosable {

	private final Options options;

	public CommandLineProgramGuiEnclosable(Options options) {
		this.options = options;
	}

	public Options getCommandLineOptions() {
		return options;
	};

	public abstract void run() throws Exception;

	public abstract String getTitleForFrame();

	protected abstract void setCommandLine(CommandLine cmd) throws DoNotInvokeRunMethod;

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

	protected void errorInParameters(String header) {
		// automatically generate the help statement
		final HelpFormatter formatter = new HelpFormatter();
		if (header == null) {
			formatter.printHelp(printCommandLineSintax(), options);
		} else {
			formatter.printHelp(printCommandLineSintax(), "\n\n************\n" + header + "\n************\n\n", options,
					printFooter());
		}
		throw new IllegalArgumentException();
	}
}
