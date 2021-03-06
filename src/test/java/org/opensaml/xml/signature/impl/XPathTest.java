/*
 * Licensed to the University Corporation for Advanced Internet Development, 
 * Inc. (UCAID) under one or more contributor license agreements.  See the 
 * NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The UCAID licenses this file to You under the Apache 
 * License, Version 2.0 (the "License"); you may not use this file except in 
 * compliance with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensaml.xml.signature.impl;


import org.opensaml.xml.XMLObjectProviderBaseTestCase;
import org.opensaml.xml.signature.XPath;

/**
 *
 */
public class XPathTest extends XMLObjectProviderBaseTestCase {
    
    private String expectedStringContent;

    /**
     * Constructor
     *
     */
    public XPathTest() {
        singleElementFile = "/data/org/opensaml/xml/signature/impl/XPath.xml";
        
    }

    /** {@inheritDoc} */
    protected void setUp() throws Exception {
        super.setUp();
        
        expectedStringContent = "someXPathValue";
    }

    /** {@inheritDoc} */
    public void testSingleElementUnmarshall() {
        XPath xp = (XPath) unmarshallElement(singleElementFile);
        
        assertNotNull("XPath", xp);
        assertEquals("XPath value", xp.getValue(), expectedStringContent);
    }

    /** {@inheritDoc} */
    public void testSingleElementMarshall() {
        XPath xp = (XPath) buildXMLObject(XPath.DEFAULT_ELEMENT_NAME);
        xp.setValue(expectedStringContent);
        
        assertEquals(expectedDOM, xp);
    }

}
