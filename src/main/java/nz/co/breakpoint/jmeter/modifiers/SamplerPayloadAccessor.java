package nz.co.breakpoint.jmeter.modifiers;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import org.apache.jmeter.protocol.http.sampler.HTTPSamplerBase;
import org.apache.jmeter.samplers.Sampler;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;

/* The JMeter API lacks an interface for samplers that have a content or payload,
 * so we have to use class specific methods.
 * To make this work for third-party samplers, a String getter/setter pair has to be provided.
 * The Sampler class name and Bean property name can then be provided via the JMeter property 
 * "jmeter.wssecurity.samplerPayloadAccessors" in the format
 * <className>.<propertyName>
 * Several of those may be comma separated.
 */
public class SamplerPayloadAccessor {

	private static final Logger log = LoggingManager.getLoggerForClass();
	
	static final String ACCESSORS_PROPERTY_NAME = "jmeter.wssecurity.samplerPayloadAccessors";
	
	private static final String DELIMITER = ",";
	
	/* Collection of Class/Accessor pairs (rather than a lookup map) that stores all known sampler classes 
	 * that have a payload as well as their accessor (bean property).
	 * This will be iterated until a the first class is found that the sampler is or is a subclass of.
	 */
	protected static final Map<Class<?>, PropertyDescriptor> samplerPayloadAccessors = new HashMap<Class<?>, PropertyDescriptor>();
	static {
		String accessors = ("org.apache.jmeter.protocol.jms.sampler.JMSSampler.content" + DELIMITER +
			"org.apache.jmeter.protocol.jms.sampler.PublisherSampler.textMessage" + DELIMITER +
			JMeterUtils.getPropDefault(ACCESSORS_PROPERTY_NAME, ""))
			.trim().replaceFirst(DELIMITER+"$", ""); // remove whitespaces and trailing delimiter
		
		for (String classAndAccessor : accessors.split(DELIMITER)) {
			int lastDot = classAndAccessor.lastIndexOf('.');
			
			String className = classAndAccessor.substring(0, lastDot);
			String propertyName = classAndAccessor.substring(lastDot+1);
			
			try {
				Class<?> clazz = Class.forName(className, false, SamplerPayloadAccessor.class.getClassLoader()); // load class without initialisation
				samplerPayloadAccessors.put(clazz, new PropertyDescriptor(propertyName, clazz));
			}
			catch (ClassNotFoundException e) {
				log.error("Sampler class not found ("+className+")");
			}
			catch (IntrospectionException e) {
				log.error("Sampler payload accessor not found ("+propertyName+")");
			}
		}
	}
	
	protected static PropertyDescriptor findSamplerPayloadAccessor(Sampler sampler) {
		for (Map.Entry<Class<?>, PropertyDescriptor> classAndProperty : samplerPayloadAccessors.entrySet()) {
			if (classAndProperty.getKey().isAssignableFrom(sampler.getClass())) {
				return classAndProperty.getValue();
			}
		}
		log.warn("Cannot find sampler payload accessor for "+sampler.getName());
		return null;
	}

	public static String getPayload(Sampler sampler) {
		if (sampler instanceof HTTPSamplerBase) {
			HTTPSamplerBase httpSampler = ((HTTPSamplerBase)sampler);
			
			if (!httpSampler.getPostBodyRaw()) {
				log.error("Raw post body required.");
				return null; 
			}
			return httpSampler.getArguments().getArgument(0).getValue(); 
		}		
		PropertyDescriptor accessor = findSamplerPayloadAccessor(sampler);
		if (accessor != null) {
			try {
				return (String)accessor.getReadMethod().invoke(sampler, new Object[]{});
			}
			catch (InvocationTargetException e) {
				log.error("Sampler payload getter exception: "+e.getCause());
			}
			catch (IllegalAccessException e) {
				log.error("Sampler payload getter not accessible");
			}
		}
		return null;
	}

	public static void setPayload(Sampler sampler, String payload) {
		if (sampler instanceof HTTPSamplerBase) {
			((HTTPSamplerBase)sampler).getArguments().getArgument(0).setValue(payload);
			return;
		}
		PropertyDescriptor accessor = findSamplerPayloadAccessor(sampler);
		if (accessor != null) {
			try {
				accessor.getWriteMethod().invoke(sampler, new Object[]{payload});
			}
			catch (InvocationTargetException e) {
				log.error("Sampler payload setter exception: "+e.getCause());
			}
			catch (IllegalAccessException e) {
				log.error("Sampler payload setter not accessible");
			}
		}
	}
}
