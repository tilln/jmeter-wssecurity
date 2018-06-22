# jmeter-wssecurity [![travis][travis-image]][travis-url]

[travis-image]: https://travis-ci.org/tilln/jmeter-wssecurity.svg?branch=master
[travis-url]: https://travis-ci.org/tilln/jmeter-wssecurity

Overview
------------

Apache JMeter plugin for signing, encrypting and decrypting SOAP messages (WS-Security).

The plugin provides 
* [Pre-Processors](http://jmeter.apache.org/usermanual/component_reference.html#preprocessors) 
for adding digital signature or encryption to a sampler's payload (based on a certificate from a given keystore),
* a Pre-Processor for adding a Username Token to a sampler's payload,
* a Pre-Processor for adding a Timestamp to a sampler's payload,
* a Pre-Processor for adding a Saml Token to a sampler's payload,
* a [Post-Processor](http://jmeter.apache.org/usermanual/component_reference.html#postprocessors)
for decrypting a sampler's response.

Supported are HTTP Request, JMS Publisher and JMS Point-to-Point samplers, as well as third party samplers 
that expose the payload via a pair of getter/setter methods.

Installation
------------

### Via [PluginsManager](https://jmeter-plugins.org/wiki/PluginsManager/)

Under tab "Available Plugins", select "WS Security for SOAP", then click "Apply Changes and Restart JMeter".

### Via Package from [JMeter-Plugins.org](https://jmeter-plugins.org/)

Extract the [zip package](https://jmeter-plugins.org/files/packages/tilln-wssecurity-1.5.zip) into JMeter's lib directory, then restart JMeter.

### Via Manual Download

1. Copy the [jmeter-wssecurity jar file](https://github.com/tilln/jmeter-wssecurity/releases/download/1.5/jmeter-wssecurity-1.5.jar) into JMeter's lib/ext directory.
2. Copy the following dependencies into JMeter's lib directory:
	* [org.apache.wss4j / wss4j-ws-security-dom](https://search.maven.org/remotecontent?filepath=org/apache/wss4j/wss4j-ws-security-dom/2.1.8/wss4j-ws-security-dom-2.1.8.jar)
	* [org.apache.wss4j / wss4j-ws-security-common](https://search.maven.org/remotecontent?filepath=org/apache/wss4j/wss4j-ws-security-common/2.1.8/wss4j-ws-security-common-2.1.8.jar)
	* [org.apache.santuario / xmlsec](https://search.maven.org/remotecontent?filepath=org/apache/santuario/xmlsec/2.0.8/xmlsec-2.0.8.jar)
	* [org.opensaml / opensaml-core](https://search.maven.org/remotecontent?filepath=org/opensaml/opensaml-core/3.3.0/opensaml-core-3.3.0.jar)
	* [org.opensaml / opensaml-messaging-api](https://search.maven.org/remotecontent?filepath=org/opensaml/opensaml-messaging-api/3.3.0/opensaml-messaging-api-3.3.0.jar)
	* [org.opensaml / opensaml-profile-api](https://search.maven.org/remotecontent?filepath=org/opensaml/opensaml-profile-api/3.3.0/opensaml-profile-api-3.3.0.jar)
	* [org.opensaml / opensaml-saml-api](https://search.maven.org/remotecontent?filepath=org/opensaml/opensaml-saml-api/3.3.0/opensaml-saml-api-3.3.0.jar)
	* [org.opensaml / opensaml-saml-impl](https://search.maven.org/remotecontent?filepath=org/opensaml/opensaml-saml-impl/3.3.0/opensaml-saml-impl-3.3.0.jar)
	* [org.opensaml / opensaml-security-api](https://search.maven.org/remotecontent?filepath=org/opensaml/opensaml-security-api/3.3.0/opensaml-security-api-3.3.0.jar)
	* [org.opensaml / opensaml-security-impl](https://search.maven.org/remotecontent?filepath=org/opensaml/opensaml-security-impl/3.3.0/opensaml-security-impl-3.3.0.jar)
	* [org.opensaml / opensaml-soap-api](https://search.maven.org/remotecontent?filepath=org/opensaml/opensaml-soap-api/3.3.0/opensaml-soap-api-3.3.0.jar)
	* [org.opensaml / opensaml-storage-api](https://search.maven.org/remotecontent?filepath=org/opensaml/opensaml-storage-api/3.3.0/opensaml-storage-api-3.3.0.jar)
	* [org.opensaml / opensaml-xacml-api](https://search.maven.org/remotecontent?filepath=org/opensaml/opensaml-xacml-api/3.3.0/opensaml-xacml-api-3.3.0.jar)
	* [org.opensaml / opensaml-xacml-impl](https://search.maven.org/remotecontent?filepath=org/opensaml/opensaml-xacml-impl/3.3.0/opensaml-xacml-impl-3.3.0.jar)
	* [org.opensaml / opensaml-xacml-saml-api](https://search.maven.org/remotecontent?filepath=org/opensaml/opensaml-xacml-saml-api/3.3.0/opensaml-xacml-saml-api-3.3.0.jar)
	* [org.opensaml / opensaml-xacml-saml-impl](https://search.maven.org/remotecontent?filepath=org/opensaml/opensaml-xacml-saml-impl/3.3.0/opensaml-xacml-saml-impl-3.3.0.jar)
	* [org.opensaml / opensaml-xmlsec-api](https://search.maven.org/remotecontent?filepath=org/opensaml/opensaml-xmlsec-api/3.3.0/opensaml-xmlsec-api-3.3.0.jar)
	* [org.opensaml / opensaml-xmlsec-impl](https://search.maven.org/remotecontent?filepath=org/opensaml/opensaml-xmlsec-impl/3.3.0/opensaml-xmlsec-impl-3.3.0.jar)
	* [com.google.guava / guava](https://search.maven.org/remotecontent?filepath=com/google/guava/guava/19.0/guava-19.0.jar)
	* [net.shibboleth.utilities / java-support](https://search.maven.org/remotecontent?filepath=net/shibboleth/utilities/java-support/7.3.0/java-support-7.3.0.jar)
        * [joda-time / joda-time](https://search.maven.org/remotecontent?filepath=joda-time/joda-time/2.1/joda-time-2.1.jar)

3. Restart JMeter.

Usage
------------

From the context menu, select "Add" / "Pre Processors" / "SOAP Message Signer", "SOAP Message Encrypter" , "SOAP Message Saml" or "SOAP Message UsernameToken".

The message to be signed or encrypted must be a valid SOAP message and must be in one of the following locations:
* For [HTTP request](http://jmeter.apache.org/usermanual/component_reference.html#HTTP_Request): Tab "Body Data" (not "Parameters")
* For [JMS Point-to-Point](http://jmeter.apache.org/usermanual/component_reference.html#JMS_Point-to-Point): Text area "Content"
* For [JMS Publisher](http://jmeter.apache.org/usermanual/component_reference.html#JMS_Publisher): Text area "Text Message..." with "Message source": Textarea (from files is not supported)

*Note that the plugin does not assist with composing the message nor does it do any XML schema validation.
Only the WS-Security header element will be inserted or modified.*
*It is recommended to exclude the WS-Security header from the SOAP request.*

Users familiar with SoapUI will find similarities to the [outgoing WS-Security configuration](https://www.soapui.org/soapui-projects/ws-security.html#3-Outgoing-WS-Security-configurations).

### SOAP Message Signer

![SOAP Message Signer](https://raw.githubusercontent.com/tilln/jmeter-wssecurity/master/docs/signature.png)

### SOAP Message Encrypter

![SOAP Message Encrypter](https://raw.githubusercontent.com/tilln/jmeter-wssecurity/master/docs/encryption.png)

### SOAP Message Saml

![SOAP Message Saml](https://raw.githubusercontent.com/fpirson/jmeter-wssecurity/master/docs/saml.png)

### SOAP Message Username Token

![SOAP Message Username Token](https://raw.githubusercontent.com/tilln/jmeter-wssecurity/master/docs/usernametoken.png)

### SOAP Message Decrypter

![SOAP Message Decrypter](https://raw.githubusercontent.com/tilln/jmeter-wssecurity/master/docs/decryption.png)

Configuration
-------------

### Pre-Processors

The dropdown fields are initialized with WSS default values, and allow the customization of most signature and encryption settings, 
depending on what the endpoint's WSDL defines.

The "Parts to Sign"/"Parts to Encrypt" are empty by default, however, that results in the SOAP Body content to be signed or encrypted.

Suppose the Timestamp element was to be included in the signature or encryption in addition to the Body element, both would have to be listed as follows: 

|ID|Name|Namespace|Encode|
|--|----|---------|------|
|  |Body|http://schemas.xmlsoap.org/soap/envelope/ | |
|  |Timestamp|http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd | |

If there are multiple XML elements with the same name and namespace, the element's ID attribute can be used to determine which element is to be signed/encrypted.
If the ID is specified, the Name and Namespace are not necessary and will not be used.  

Example:

```xml
<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
    <soap:Body>
        <element ID="e1">this should be encrypted</element>
        <element ID="e2">this is not to be encrypted</element>
        <element>another one</element>
	</soap:Body>
</soap:Envelope>
```

|ID|Name|Namespace|Encode|
|--|----|---------|------|
|e1|    |         |      |

Encode is only relevant for encryption and can be one of the following:
* "Element" (default): The entire XML element is encrypted.
* "Content": Only child nodes of the XML element are encrypted (i.e. the element name and its attributes will remain in clear text).
* "Header": Encloses the XML element in an EncryptedHeader element ("http://docs.oasis-open.org/wss/oasis-wss-wssecurity-secext-1.1.xsd"), 
but only if it is an immediate child node of the SOAP Header.

### Post-Processor

Any WS-Security related exception encountered by the SOAP Message Decrypter 
while trying to decrypt a response message will cause the sampler to fail and will create an 
[assertion](http://jmeter.apache.org/usermanual/component_reference.html#assertions) result, 
effectively behaving like an implicit assertion.

If this behaviour is not desired, it may be turned off via setting the JMeter property `jmeter.wssecurity.failSamplerOnWSSException=false`.

### Support for 3rd party samplers

Samplers that are not JMeter core functionality, such as [JMeter-Plugins](http://jmeter-plugins.org), can also be used
if they provide a getter/setter pair to access a String property that contains the sampler's payload that is to be signed or encrypted.

In that case, the JMeter property `jmeter.wssecurity.samplerPayloadAccessors` can be used to specify the class and member name (without the get/set prefix) 
which the Pre-Processor will access at run time via Reflection.

Suppose a sampler like the following:
```java
package some.package;
public class SomeSampler extends AbstractSampler {
	public String getPayload() 
	// ...
	public void setPayload(String content)
	// ...
}
```

Then the JMeter property should be set like so: `jmeter.wssecurity.samplerPayloadAccessors=some.package.SomeSampler.payload`

More than one of these can be comma separated (if really required).

Troubleshooting
---------------

The signed or encrypted message payload can be inspected via "View Results Tree".

To avoid common problems, make sure that:
- the Keystore contains an entry for the specified certificate alias,
- the certificate and signature/encryption algorithms match,
- the SOAP message is correctly formed and can be parsed,
- [Unlimited Strength JCE](http://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html) is installed to support all key lengths,
etc.

It may be useful to increase the logging level in order to investigate any keystore or encryption related issues, 
for example by adding `--loglevel=org.apache.wss4j=DEBUG` to the JMeter command line. 

It may also be helpful to inspect server side logs, especially for HTTP 500 type responses, unspecific SOAP Fault messages etc.
