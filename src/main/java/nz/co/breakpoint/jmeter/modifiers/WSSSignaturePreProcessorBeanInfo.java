package nz.co.breakpoint.jmeter.modifiers;

import java.beans.PropertyDescriptor;
import org.apache.wss4j.dom.WSConstants;

public class WSSSignaturePreProcessorBeanInfo extends AbstractWSSecurityPreProcessorBeanInfo {

	public WSSSignaturePreProcessorBeanInfo() {
		super(WSSSignaturePreProcessor.class);

		createPropertyGroup("Signature", new String[]{ 
			"keyIdentifier", "signatureAlgorithm", "signatureCanonicalization", "digestAlgorithm", "useSingleCertificate",
			getPartsToSecurePropertyName()
		});
		PropertyDescriptor p;

		p = property("keyIdentifier");
		p.setValue(NOT_UNDEFINED, Boolean.TRUE);
		p.setValue(DEFAULT, WSSSignaturePreProcessor.getKeyIdentifierLabelForType(WSConstants.BST_DIRECT_REFERENCE));
		p.setValue(NOT_OTHER, Boolean.TRUE);
		p.setValue(TAGS, WSSSignaturePreProcessor.keyIdentifiers.keySet().toArray(new String[]{}));

		p = property("signatureAlgorithm");
		p.setValue(NOT_UNDEFINED, Boolean.TRUE);
		p.setValue(DEFAULT, WSSSignaturePreProcessor.signatureAlgorithms[0]);
		p.setValue(NOT_OTHER, Boolean.TRUE);
		p.setValue(TAGS, WSSSignaturePreProcessor.signatureAlgorithms);

		p = property("signatureCanonicalization");
		p.setValue(NOT_UNDEFINED, Boolean.TRUE);
		p.setValue(DEFAULT, WSSSignaturePreProcessor.signatureCanonicalizations[0]);
		p.setValue(NOT_OTHER, Boolean.TRUE);
		p.setValue(TAGS, WSSSignaturePreProcessor.signatureCanonicalizations);

		p = property("digestAlgorithm");
		p.setValue(NOT_UNDEFINED, Boolean.TRUE);
		p.setValue(DEFAULT, WSSSignaturePreProcessor.digestAlgorithms[0]);
		p.setValue(NOT_OTHER, Boolean.TRUE);
		p.setValue(TAGS, WSSSignaturePreProcessor.digestAlgorithms);

		p = property("useSingleCertificate");
		p.setValue(NOT_UNDEFINED, Boolean.TRUE);
		p.setValue(DEFAULT, Boolean.FALSE);
	}
}
