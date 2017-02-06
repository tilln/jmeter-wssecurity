package nz.co.breakpoint.jmeter.modifiers;

import java.beans.PropertyDescriptor;
import org.apache.jmeter.testbeans.BeanInfoSupport;
import org.apache.jmeter.testbeans.gui.FileEditor;
import org.apache.jmeter.testbeans.gui.TableEditor;
import org.apache.jmeter.testbeans.gui.PasswordEditor;

public class AbstractWSSecurityPreProcessorBeanInfo extends BeanInfoSupport {

	public AbstractWSSecurityPreProcessorBeanInfo(Class<? extends AbstractWSSecurityPreProcessor> clazz) {
        super(clazz);
		
		createPropertyGroup("Certificate", new String[]{ 
			"keystoreFile", "keystorePassword", "certAlias", "certPassword"
		});
		PropertyDescriptor p;

		p = property("keystoreFile"); 
		p.setPropertyEditorClass(FileEditor.class);
		p.setValue(NOT_UNDEFINED, Boolean.TRUE);
		p.setValue(DEFAULT, "");

		p = property("keystorePassword");
		p.setPropertyEditorClass(PasswordEditor.class);
		p.setValue(NOT_UNDEFINED, Boolean.TRUE);
		p.setValue(DEFAULT, "");

		p = property("certAlias");
		p.setValue(NOT_UNDEFINED, Boolean.TRUE);
		p.setValue(DEFAULT, "");
		
		p = property("certPassword");
		p.setPropertyEditorClass(PasswordEditor.class);
		p.setValue(NOT_UNDEFINED, Boolean.TRUE);
		p.setValue(DEFAULT, "");
		
		p = property(getPartsToSecurePropertyName());
		p.setPropertyEditorClass(TableEditor.class);
		p.setValue(TableEditor.CLASSNAME, SecurityPart.class.getName());
		p.setValue(TableEditor.HEADERS, new String[]{"Name", "Namespace", "Encode"});
		p.setValue(TableEditor.OBJECT_PROPERTIES, new String[]{"name", "namespace", "modifier"});
	}
	
	// Parts should go at the bottom, but then subclasses will have to add the element.
	protected String getPartsToSecurePropertyName() {
		return "partsToSecure";
	}
}
