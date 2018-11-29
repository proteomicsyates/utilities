package edu.scripps.yates.utilities;

import java.util.Map;

public interface CommandLineParameters {

	Map<String, String> getParametersMap();

	String getParameterValue(String parameterName);

}
