package nz.co.breakpoint.jmeter.modifiers;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;
import org.apache.wss4j.common.ext.WSSecurityException;
import org.apache.wss4j.dom.message.WSSecBase;
import org.apache.wss4j.dom.message.WSSecHeader;
import org.apache.wss4j.dom.message.WSSecTimestamp;
import org.w3c.dom.Document;

public class WSSTimestampPreProcessor extends AbstractWSSecurityPreProcessor {
	private static final Logger log = LoggingManager.getLoggerForClass();
	transient private WSSecTimestamp secBuilder;

	private int timeToLive;

	public WSSTimestampPreProcessor() throws ParserConfigurationException {
		super();
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -4390837640018590558L;

	@Override
	protected WSSecBase getSecBuilder() {
		return secBuilder;
	}

	@Override
	protected void initSecBuilder() {
		secBuilder = new WSSecTimestamp();
	}

	@Override
	protected Document build(Document document, WSSecHeader secHeader) throws WSSecurityException {
		secBuilder.setTimeToLive(timeToLive);
		log.info("Time to live: " + timeToLive);
		return secBuilder.build(document, secHeader);
	}

	public int getTimeToLive() {
		return timeToLive;
	}

	public void setTimeToLive(int timeToLive) {
		this.timeToLive = timeToLive;
	}

}
