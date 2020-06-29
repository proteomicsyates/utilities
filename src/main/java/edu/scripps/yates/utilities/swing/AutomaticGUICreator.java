package edu.scripps.yates.utilities.swing;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.Logger;

import edu.scripps.yates.utilities.dates.DatesUtil;
import gnu.trove.map.TMap;
import gnu.trove.map.hash.THashMap;

public class AutomaticGUICreator extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4829866413822345381L;
	private static Logger log = Logger.getLogger(AutomaticGUICreator.class);
	private final CommandLineProgramGuiEnclosable program;
	private final TMap<String, JComponent> componentsByOption = new THashMap<String, JComponent>();
	private final JTextArea status;

	public AutomaticGUICreator(CommandLineProgramGuiEnclosable program) {
		super(program.getTitleForFrame());
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.program = program;

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException
				| IllegalAccessException e) {
			e.printStackTrace();
		}
		getContentPane().setLayout(new BorderLayout(10, 10));
		final JPanel headerPanel = new JPanel();
		headerPanel.add(new Label("Graphical Interface for " + program.getTitleForFrame()));
		getContentPane().add(headerPanel, BorderLayout.NORTH);

		final GridBagLayout layout = new GridBagLayout();
		layout.columnWeights = new double[] { 50.0, 250.0, 250.0 };
		final JPanel componentsPanel = new JPanel(layout);
		componentsPanel.setBorder(BorderFactory.createTitledBorder("Parameters:"));

		final Options options = program.getCommandLineOptions();
		final Collection<Option> optionList = options.getOptions();
		int y = 0;
		for (final Option option : optionList) {
			final JLabel label = getLabelForOptionName(option);
			componentsPanel.add(label, getGridBagConstraints(0, y, GridBagConstraints.EAST));
			final JComponent component = getComponentForOption(option, componentsByOption);
			componentsPanel.add(component, getGridBagConstraints(1, y, GridBagConstraints.WEST));
			final JLabel label2 = getLabelForOptionDescription(option);
			componentsPanel.add(label2, getGridBagConstraints(2, y, GridBagConstraints.WEST));
			y++;
		}
		// now the run button
		final JButton button = new JButton("RUN");
		button.setToolTipText("Click to start program");
		final GridBagConstraints c = getGridBagConstraints(0, y, 1, 3, GridBagConstraints.CENTER);
		c.fill = GridBagConstraints.NONE;
		componentsPanel.add(button, c);

		// status at the south
		status = new JTextArea(10, 80) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 5204026945517631469L;

			@Override
			public void setText(String text) {
				log.info(text);
				super.setText(text);
				final String text2 = getText();
				if (text2 != null && !"".equals(text2)) {
					setCaretPosition(text2.length() - 1);
				}
			}

			@Override
			public void append(String text) {
				log.info(text);
				super.append(text);
				final String text2 = getText();
				if (text2 != null && !"".equals(text2)) {
					setCaretPosition(text2.length() - 1);
				}
			}
		};
		status.setEditable(false);
		status.setWrapStyleWord(true);
		status.setFont(new Font("Tahoma", Font.PLAIN, 11));
		// set System.out to the textarea
		System.setOut(new PrintStream(new TextAreaOutputStream(status)));
		System.setErr(new PrintStream(new TextAreaOutputStream(status)));

		//
		final JScrollPane scroll = new JScrollPane(status);
		final JPanel panelStatus = new JPanel(new BorderLayout());
		panelStatus.setBorder(BorderFactory.createTitledBorder("Status:"));
		panelStatus.add(scroll);

		// split
		final JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, componentsPanel, panelStatus);
		getContentPane().add(splitPane, BorderLayout.CENTER);
		// what happens when pressing run
		button.addActionListener(getRunButtonAction(componentsByOption, program, status));

		// loadDefaults from defaults.properties
		loadDefaults(program.getCommandLineOptions());

		pack();
		final java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		final java.awt.Dimension dialogSize = getSize();
		setLocation((screenSize.width - dialogSize.width) / 2, (screenSize.height - dialogSize.height) / 2);
	}

	private void loadDefaults(Options options) {
		final AppDefaults defaults = new AppDefaults(componentsByOption.keySet());
		for (final String optionName : componentsByOption.keySet()) {
			final JComponent jComponent = componentsByOption.get(optionName);
			final String value = defaults.getPropertyValue(optionName);
			if (value != null) {
				if (jComponent instanceof JCheckBox) {
					((JCheckBox) jComponent).setSelected(Boolean.valueOf(value));
				} else if (jComponent instanceof JTextField) {
					((JTextField) jComponent).setText(value);
				}
			}
		}
	}

	private ActionListener getRunButtonAction(TMap<String, JComponent> componentsByOption,
			CommandLineProgramGuiEnclosable program, JTextArea status) {
		final Options options = program.getCommandLineOptions();
		// log the options
		for (final String optionOpt : componentsByOption.keySet()) {
			final Option option = options.getOption(optionOpt);
			log.info(option.getOpt() + "=" + option.getValue());
		}

		return new ActionListener() {
			private final SimpleDateFormat format = new SimpleDateFormat("yyy-MM-dd HH:mm:ss:SSS");

			@Override
			public void actionPerformed(ActionEvent e) {

				try {
					final long t1 = System.currentTimeMillis();
					// save defaults
					saveDefaults();
					clearStatus();
					final CommandLine commandLine = AutomaticGUICreator.this.getCommandLineFromGui();
					showMessage(getParametersString(commandLine));
					program.setCommandLine(commandLine);
					showMessage("Parameters are correct. Starting program...");
					final ScheduledExecutorService service = new ScheduledThreadPoolExecutor(1);
					final Runnable command = new Runnable() {

						@Override
						public void run() {
							final ComponentEnableStateKeeper keeper = new ComponentEnableStateKeeper();
							keeper.addInvariableComponent(status);
							keeper.addInvariableComponent(status.getParent());
							try {
								keeper.keepEnableStates(AutomaticGUICreator.this);
								keeper.disable(AutomaticGUICreator.this);
								program.run();
								showMessage("Everything finished OK!!");
							} catch (final Exception e) {
								e.printStackTrace();
								showError(e.getMessage());
							} finally {
								keeper.setToPreviousState(AutomaticGUICreator.this);
								final long t2 = System.currentTimeMillis();
								final long time = t2 - t1;
								status.append("Process took " + DatesUtil.getDescriptiveTimeFromMillisecs(time));
							}
						}
					};
					service.schedule(command, 10, TimeUnit.MILLISECONDS);
				} catch (final Exception e1) {
					e1.printStackTrace();
					showError(e1.getMessage());
				}
			}

			private void showError(String message) {
				showMessage("Error: " + message);
			}

			private void showMessage(String message) {
				status.append(getFormattedTime() + ": " + message + "\n");
			}

			private String getFormattedTime() {
				return format.format(new Date());

			}
		};
	}

	protected String getParametersString(CommandLine commandLine) {
		final StringBuilder sb = new StringBuilder("Program arguments:\n");
		final Option[] options = commandLine.getOptions();
		for (final Option option : options) {

			sb.append(option.getOpt());
			if (option.getValue() != null && !"".equals(option.getValue())) {
				sb.append(" = " + option.getValue());
			}
			sb.append("\n");
		}
		return sb.toString();
	}

	protected void clearStatus() {
		status.setText("");
	}

	private void saveDefaults() {
		final AppDefaults appDefaults = new AppDefaults(componentsByOption.keySet());
		for (final String optionName : componentsByOption.keySet()) {
			final JComponent jComponent = componentsByOption.get(optionName);
			if (jComponent instanceof JCheckBox) {
				final Boolean checked = ((JCheckBox) jComponent).isSelected();
				appDefaults.setProperty(optionName, checked.toString());

			} else if (jComponent instanceof JTextField) {
				final String value = ((JTextField) jComponent).getText();
				appDefaults.setProperty(optionName, value);
			}
		}

	}

	protected CommandLine getCommandLineFromGui() throws ParseException {
		final List<String> args = new ArrayList<String>();
		final StringBuilder sb = new StringBuilder();
		for (final String optionName : this.componentsByOption.keySet()) {
			if (!"".equals(sb.toString())) {
				sb.append(" ");
			}
			final JComponent jComponent = this.componentsByOption.get(optionName);
			if (jComponent instanceof JCheckBox) {
				final boolean checked = ((JCheckBox) jComponent).isSelected();
				if (checked) {
					args.add("-" + optionName);
				}
			} else if (jComponent instanceof JTextField) {
				final String value = ((JTextField) jComponent).getText();
				if (!"".equals(value)) {
					args.add("-" + optionName);
					args.add(value);
				}
			}
		}
		final CommandLineParser parser = new DefaultParser();
		final CommandLine cmd = parser.parse(program.getCommandLineOptions(), args.toArray(new String[0]));
		return cmd;
	}

	private static JComponent getComponentForOption(Option option, TMap<String, JComponent> componentsByOption) {
		JComponent ret = null;
		// if admits parameter use text box
		if (option.hasArg()) {
			final JTextField text = new JTextField();
			text.setColumns(30);
			ret = text;
		} else {
			// otherwise just a checkbox
			final JCheckBox check = new JCheckBox();
			ret = check;
		}
		componentsByOption.put(option.getOpt(), ret);
		ret.setToolTipText(option.getDescription());
		return ret;
	}

	private static String getNameFromOption(Option option) {
		String text = option.getOpt();
		if (option.hasLongOpt()) {
			text += " [" + option.getLongOpt() + "]";
		}
		return text;
	}

	private static JLabel getLabelForOptionName(Option option) {
		final String text = getNameFromOption(option);
		final JLabel label = new JLabel(text + ":");
		final Font font = label.getFont();
		label.setFont(new Font(font.getName(), Font.BOLD, font.getSize()));
		label.setToolTipText(option.getDescription());
		return label;
	}

	private static JLabel getLabelForOptionDescription(Option option) {
		final String text = option.getDescription();
		final JLabel label = new JLabel("<html>" + text + "</html>");
		final Font font = label.getFont();
		label.setFont(new Font(font.getName(), Font.BOLD, font.getSize()));
		label.setToolTipText(option.getDescription());
		return label;
	}

	private static GridBagConstraints getGridBagConstraints(int x, int y, int anchor) {
		return getGridBagConstraints(x, y, 1, 1, anchor);
	}

	private static GridBagConstraints getGridBagConstraints(int x, int y, int gridheight, int gridwidth, int anchor) {
		final GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = x;
		gridBagConstraints.gridy = y;
		gridBagConstraints.insets = new Insets(5, 5, 5, 5);
		gridBagConstraints.gridheight = gridheight;
		gridBagConstraints.gridwidth = gridwidth;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = anchor;
		return gridBagConstraints;
	}
}
