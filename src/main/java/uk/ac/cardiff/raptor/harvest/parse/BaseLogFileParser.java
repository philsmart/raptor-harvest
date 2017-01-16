package uk.ac.cardiff.raptor.harvest.parse;

public abstract class BaseLogFileParser implements LogParser {

	protected String logfile;

	public String getLogfile() {
		return logfile;
	}

	public void setLogfile(final String logfile) {
		this.logfile = logfile;
	}

}
