/*
 * Copyright [2006] [University Corporation for Advanced Internet Development, Inc.]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * 
 */

package org.opensaml.xml.mock;

import org.opensaml.xml.XMLObject;
import org.opensaml.xml.io.AbstractDOMCachingXMLObjectUnmarshaller;
import org.opensaml.xml.io.UnmarshallingException;
import org.w3c.dom.Attr;

/**
 * Unmarshaller for {@link org.opensaml.xml.mock.SimpleXMLObject}.
 */
public class SimpleXMLObjectUnmarshaller extends AbstractDOMCachingXMLObjectUnmarshaller {

    /**
     * Constructor
     * 
     * @param xmlObjectBuilderFactory factory for retrieving XMLObjectBuilders
     * @param unmarshallerFactory factory for retrieving Unmarshallers
     */
    public SimpleXMLObjectUnmarshaller() {
        super(SimpleXMLObject.NAMESPACE, SimpleXMLObject.LOCAL_NAME);
    }

    /*
     * @see org.opensaml.xml.io.AbstractXMLObjectUnmarshaller#processChildElement(org.opensaml.xml.XMLObject,
     *      org.opensaml.xml.XMLObject)
     */
    protected void processChildElement(XMLObject parentXMLObject, XMLObject childXMLObject)
            throws UnmarshallingException {

        SimpleXMLObject simpleXMLObject = (SimpleXMLObject) parentXMLObject;

        if (childXMLObject instanceof SimpleXMLObject) {
            simpleXMLObject.getSimpleXMLObjects().add((SimpleXMLObject) childXMLObject);
        }else{
            simpleXMLObject.getUnknownXMLObjects().add(childXMLObject);
        }
    }

    /*
     * @see org.opensaml.xml.io.AbstractXMLObjectUnmarshaller#processAttribute(org.opensaml.xml.XMLObject, org.w3c.dom.Attr)
     */
    protected void processAttribute(XMLObject xmlObject, Attr attribute)
            throws UnmarshallingException {
        SimpleXMLObject simpleXMLObject = (SimpleXMLObject) xmlObject;

        if (attribute.getLocalName().equals(SimpleXMLObject.ID_ATTRIB_NAME)) {
            simpleXMLObject.setId(attribute.getValue());
        }
    }

    /*
     * @see org.opensaml.xml.io.AbstractXMLObjectUnmarshaller#processElementContent(org.opensaml.xml.XMLObject,
     *      java.lang.String)
     */
    protected void processElementContent(XMLObject xmlObject, String elementContent) {
        SimpleXMLObject simpleXMLObject = (SimpleXMLObject) xmlObject;
        
        simpleXMLObject.setValue(elementContent);
    }
}