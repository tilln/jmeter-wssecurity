package nz.co.breakpoint.jmeter.modifiers;

import java.beans.PropertyDescriptor;
import org.apache.wss4j.dom.WSConstants;

public class WSSEncryptionPreProcessorBeanInfo extends AbstractWSSecurityPreProcessorBeanInfo {

	public WSSEncryptionPreProcessorBeanInfo() {
		super(WSSEncryptionPreProcessor.class);
		
		createPropertyGroup("Encryption", new String[]{ 
			"keyIdentifier", "symmetricEncryptionAlgorithm", "keyEncryptionAlgorithm", "createEncryptedKey",
			getPartsToSecurePropertyName()
		});
		PropertyDescriptor p;

		p = property("keyIdentifier");
		p.setValue(NOT_UNDEFINED, Boolean.TRUE);
		p.setValue(DEFAULT, WSSEncryptionPreProcessor.getKeyIdentifierLabelForType(WSConstants.BST_DIRECT_REFERENCE));
		p.setValue(NOT_OTHER, Boolean.TRUE);
		p.setValue(TAGS, WSSEncryptionPreProcessor.keyIdentifiers.keySet().toArray(new String[]{}));

		p = property("symmetricEncryptionAlgorithm");
		p.setValue(NOT_UNDEFINED, Boolean.TRUE);
		p.setValue(DEFAULT, WSSEncryptionPreProcessor.symmetricEncryptionAlgorithms[0]);
		p.setValue(NOT_OTHER, Boolean.TRUE);
		p.setValue(TAGS, WSSEncryptionPreProcessor.symmetricEncryptionAlgorithms);

		p = property("keyEncryptionAlgorithm");
		p.setValue(NOT_UNDEFINED, Boolean.TRUE);
		p.setValue(DEFAULT, WSSEncryptionPreProcessor.keyEncryptionAlgorithms[0]);
		p.setValue(NOT_OTHER, Boolean.TRUE);
		p.setValue(TAGS, WSSEncryptionPreProcessor.keyEncryptionAlgorithms);

		p = property("createEncryptedKey");
		p.setValue(NOT_UNDEFINED, Boolean.TRUE);
		p.setValue(DEFAULT, Boolean.TRUE);
	}
}
