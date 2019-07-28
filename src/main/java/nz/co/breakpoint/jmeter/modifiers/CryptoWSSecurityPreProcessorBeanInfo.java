package nz.co.breakpoint.jmeter.modifiers;

import java.beans.PropertyDescriptor;
import org.apache.jmeter.testbeans.gui.TableEditor;

public class CryptoWSSecurityPreProcessorBeanInfo extends AbstractWSSecurityPreProcessorBeanInfo {

    public CryptoWSSecurityPreProcessorBeanInfo(Class<? extends CryptoWSSecurityPreProcessor> clazz) {
        super(clazz);

        createCertificateProperties();
    }

    // This may be added to a property group by subclasses (if desired)
    protected PropertyDescriptor createPartsToSecureProperty() {
        PropertyDescriptor p = property("partsToSecure");
        p.setPropertyEditorClass(TableEditor.class);
        p.setValue(TableEditor.CLASSNAME, SecurityPart.class.getName());
        p.setValue(TableEditor.HEADERS, getTableHeadersWithDefaults("partsToSecure.tableHeaders", new String[]{"ID", "Name", "Namespace", "Encode"}));
        p.setValue(TableEditor.OBJECT_PROPERTIES, new String[]{"id", "name", "namespace", "modifier"});
        return p;
    }
}
