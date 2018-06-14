package nz.co.breakpoint.jmeter.modifiers;

import org.apache.wss4j.common.ext.WSSecurityException;
import org.apache.wss4j.common.saml.SamlAssertionWrapper;
import org.apache.wss4j.dom.message.WSSecBase;
import org.apache.wss4j.dom.message.WSSecHeader;
import org.apache.wss4j.dom.message.WSSecSAMLToken;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;

public class WSSSamlPreProcessor extends  AbstractWSSecurityPreProcessor{

    private String saml;
    private WSSecSAMLToken wsSecSAMLToken;

    public WSSSamlPreProcessor() throws ParserConfigurationException {
        super();
    }

    protected WSSecBase getSecBuilder() {
        return null;
    }

    protected void initSecBuilder() {
        wsSecSAMLToken = new WSSecSAMLToken();
    }

    protected Document build(Document document, WSSecHeader secHeader) throws WSSecurityException {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            docFactory.setNamespaceAware(true);
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document samlAsserionDoc = docBuilder.parse(new ByteArrayInputStream(saml.getBytes()));
            SamlAssertionWrapper samlAssertionWrapper = new SamlAssertionWrapper(samlAsserionDoc.getDocumentElement());
            return wsSecSAMLToken.build(document, samlAssertionWrapper, secHeader);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public String getSaml() {
        return saml;
    }

    public void setSaml(String samlAssertion) {
        this.saml = samlAssertion;
    }
}
