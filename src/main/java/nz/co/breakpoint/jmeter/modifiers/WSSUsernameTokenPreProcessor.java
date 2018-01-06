package nz.co.breakpoint.jmeter.modifiers;

import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;
import org.apache.wss4j.common.ext.WSSecurityException;
import org.apache.wss4j.dom.message.WSSecBase;
import org.apache.wss4j.dom.message.WSSecHeader;
import org.apache.wss4j.dom.message.WSSecUsernameToken;
import org.apache.wss4j.dom.WSConstants;
import org.w3c.dom.Document;

public class WSSUsernameTokenPreProcessor extends AbstractWSSecurityPreProcessor { 

    private static final long serialVersionUID = 1L;

    private static final Logger log = LoggingManager.getLoggerForClass();

    transient private WSSecUsernameToken secBuilder;
    
    private String passwordType;
    private boolean addNonce;
    private boolean addCreated;
    private boolean precisionInMilliSeconds;

    static final Map<String, String> passwordTypeMap = new HashMap<String, String>();
    static {
        passwordTypeMap.put("Password Digest", WSConstants.PASSWORD_DIGEST);
        passwordTypeMap.put("Password Text",   WSConstants.PASSWORD_TEXT);
        passwordTypeMap.put("No Password",     null); // wss4j does not seem to use WSConstants.PW_NONE
    }

    /* Currently supported attributes are listed below.
     * The first value for each will be displayed in the GUI as default.
     */
    static final String[] passwordTypes = new String[]{
        getPasswordTypeLabelForType(WSConstants.PASSWORD_DIGEST),
        getPasswordTypeLabelForType(WSConstants.PASSWORD_TEXT),
        getPasswordTypeLabelForType(null)
    };

    public WSSUsernameTokenPreProcessor() throws ParserConfigurationException {
        super();
    }

    /* Reverse lookup for above passwordTypeMap. Mainly used for populating the GUI dropdown.
     */
    protected static String getPasswordTypeLabelForType(String passwordType) {
        for (Map.Entry<String, String> id : passwordTypeMap.entrySet()) {
            if (passwordType != null && passwordType.equals(id.getValue())
                || passwordType == null && passwordType == id.getValue())
                return id.getKey();
        }
        return null;
    }

    @Override
    protected void initSecBuilder() {
        secBuilder = new WSSecUsernameToken();
    }

    @Override
    protected WSSecBase getSecBuilder() {
        return secBuilder;
    }

    @Override
    protected Document build(Document document, WSSecHeader secHeader) throws WSSecurityException {
        if (addNonce) secBuilder.addNonce();
        if (addCreated) secBuilder.addCreated();
        secBuilder.setPrecisionInMilliSeconds(precisionInMilliSeconds);
        if (passwordType == null) {
            /* An empty password GUI field will result in an empty password string "", regardless of password type.
             * This would cause a NPE in org.apache.wss4j.dom.message.token.UsernameToken.setPassword("") 
             * when trying to retrieve the password element that is not there (as per password type).
             * Therefore it needs to be set to null explicitly.
             */
            setPassword(null);
        }
        return secBuilder.build(document, secHeader);
    }

    // Accessors
    public String getPasswordType() {
        return getPasswordTypeLabelForType(passwordType);
    }

    public void setPasswordType(String passwordType) {
        secBuilder.setPasswordType(this.passwordType = passwordTypeMap.get(passwordType));
    }

    public boolean getAddNonce() {
        return addNonce;
    }

    public void setAddNonce(boolean addNonce) {
        this.addNonce = addNonce;
    }

    public boolean getAddCreated() {
        return addCreated;
    }

    public void setAddCreated(boolean addCreated) {
        this.addCreated = addCreated;
    }

    public boolean isPrecisionInMilliSeconds() {
		return precisionInMilliSeconds;
	}

	public void setPrecisionInMilliSeconds(boolean precisionInMilliSeconds) {
		this.precisionInMilliSeconds = precisionInMilliSeconds;
	}

	// Make accessors public for bean introspector to build the GUI.
    public String getUsername() {
        return super.getUsername();
    }

    public void setUsername(String username) {
        super.setUsername(username);
    }

    public String getPassword() {
        return super.getPassword();
    }

    public void setPassword(String password) {
        super.setPassword(password);
    }
}
