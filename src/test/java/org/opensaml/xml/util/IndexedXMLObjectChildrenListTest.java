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

package org.opensaml.xml.util;

import java.util.List;

import javax.xml.namespace.QName;

import junit.framework.TestCase;

import org.opensaml.xml.XMLObject;
import org.opensaml.xml.mock.SimpleXMLObject;
import org.opensaml.xml.mock.SimpleXMLObjectBuilder;

/**
 * Test case for {@link org.opensaml.xml.util.IndexedXMLObjectChildrenList}. Note that this test only tests those
 * methods that modify the list because everything else is delegated to the
 * {@link org.opensaml.xml.util.XMLObjectChildrenList} which has it's own test cases that works all the other methods.
 * 
 */
public class IndexedXMLObjectChildrenListTest extends TestCase {

    private QName type1 = new QName("example.org/ns/type1", "Type1");

    private QName type2 = new QName("example.org/ns/type2", "Type2");

    private SimpleXMLObjectBuilder sxoBuilder = new SimpleXMLObjectBuilder();

    /**
     * Test the add method to make sure it creates the index correctly.
     */
    public void testAdd() {
        SimpleXMLObject parentObject = sxoBuilder.buildObject();
        IndexedXMLObjectChildrenList<SimpleXMLObject> indexedList = new IndexedXMLObjectChildrenList<SimpleXMLObject>(
                parentObject);

        SimpleXMLObject child1 = sxoBuilder.buildObject(SimpleXMLObject.ELEMENT_NAME, type1);
        indexedList.add(child1);
        assertEquals("List gotten by element QName index should have had 1 element", 1, indexedList.get(
                child1.getElementQName()).size());
        assertEquals("List gotten by type QName index should have had 1 element", 1, indexedList.get(
                child1.getSchemaType()).size());

        SimpleXMLObject child2 = sxoBuilder.buildObject();
        indexedList.add(child2);
        assertEquals("List gotten by element QName index should have had 1 element", 2, indexedList.get(
                child1.getElementQName()).size());
        assertEquals("List gotten by type QName index should have had 1 element", 1, indexedList.get(
                child1.getSchemaType()).size());
    }

    /**
     * Test the set method to make sure it removes items that have been replaced from the index.
     */
    public void testSet() {
        SimpleXMLObjectBuilder sxoBuilder = new SimpleXMLObjectBuilder();
        SimpleXMLObject parentObject = sxoBuilder.buildObject();
        IndexedXMLObjectChildrenList<SimpleXMLObject> indexedList = new IndexedXMLObjectChildrenList<SimpleXMLObject>(
                parentObject);

        SimpleXMLObject child1 = sxoBuilder.buildObject(SimpleXMLObject.ELEMENT_NAME, type1);
        indexedList.add(child1);

        SimpleXMLObject child2 = sxoBuilder.buildObject();
        indexedList.set(0, child2);

        assertEquals("List gotten by element QName index should have had 1 element", 1, indexedList.get(
                child1.getElementQName()).size());
        assertTrue("List gotten by type QName index should have been empty", indexedList.get(child1.getSchemaType()).isEmpty());
    }

    /**
     * Test to ensure removed items are removed from the index.
     */
    public void testRemove() {
        SimpleXMLObject parentObject = sxoBuilder.buildObject();
        IndexedXMLObjectChildrenList<SimpleXMLObject> indexedList = new IndexedXMLObjectChildrenList<SimpleXMLObject>(
                parentObject);

        SimpleXMLObject child1 = sxoBuilder.buildObject(SimpleXMLObject.ELEMENT_NAME, type1);
        indexedList.add(child1);

        SimpleXMLObject child2 = sxoBuilder.buildObject();
        indexedList.add(child2);

        indexedList.remove(child1);
        assertEquals("List gotten by element QName index should have had 1 element", 1, indexedList.get(
                child1.getElementQName()).size());
        assertTrue("List gotten by type QName index should have been empty", indexedList.get(child1.getSchemaType()).isEmpty());
    }

    /**
     * Tests the sublist functionality.
     */
    public void testSublist() {
        SimpleXMLObject parentObject = sxoBuilder.buildObject();
        IndexedXMLObjectChildrenList<XMLObject> indexedList = new IndexedXMLObjectChildrenList<XMLObject>(parentObject);

        SimpleXMLObject child1 = sxoBuilder.buildObject(SimpleXMLObject.ELEMENT_NAME, type1);
        indexedList.add(child1);

        SimpleXMLObject child2 = sxoBuilder.buildObject(SimpleXMLObject.ELEMENT_NAME, type2);
        indexedList.add(child2);

        SimpleXMLObject child3 = sxoBuilder.buildObject();
        indexedList.add(child3);

        SimpleXMLObject child4 = sxoBuilder.buildObject(SimpleXMLObject.ELEMENT_NAME, type2);
        indexedList.add(child4);

        SimpleXMLObject child5 = sxoBuilder.buildObject(SimpleXMLObject.ELEMENT_NAME, type1);
        indexedList.add(child5);

        SimpleXMLObject child6 = sxoBuilder.buildObject(SimpleXMLObject.ELEMENT_NAME, type1);
        indexedList.add(child6);

        List<SimpleXMLObject> elementNameSublist = (List<SimpleXMLObject>) indexedList
                .subList(child1.getElementQName());
        List<SimpleXMLObject> type1SchemaSublist = (List<SimpleXMLObject>) indexedList.subList(type1);
        List<SimpleXMLObject> type2SchemaSublist = (List<SimpleXMLObject>) indexedList.subList(type2);

        assertEquals("Element name index sublist did not have expected number of elements", 6, elementNameSublist
                .size());
        assertEquals("Schema Type1 index sublist did not have expected number of elements", 3, type1SchemaSublist
                .size());
        assertEquals("Schema Type2 index sublist did not have expected number of elements", 2, type2SchemaSublist
                .size());

        SimpleXMLObject child7 = sxoBuilder.buildObject(SimpleXMLObject.ELEMENT_NAME, type1);
        type1SchemaSublist.add(child7);
        assertTrue(type1SchemaSublist.contains(child7));
        assertTrue(indexedList.contains(child7));

        type1SchemaSublist.remove(child7);
        assertFalse(type1SchemaSublist.contains(child7));
        assertFalse(indexedList.contains(child7));

        try {
            type1SchemaSublist.set(0, child7);
            fail("Unsupported set operation did not throw proper exception");
        } catch (UnsupportedOperationException e) {

        }

        try {
            type1SchemaSublist.remove(0);
            fail("Unsupported remove operation did not throw proper exception");
        } catch (UnsupportedOperationException e) {

        }
        
    }
    
    /**
     *  Test sublist  indexOf method.
     */
    public void testSublistIndexOf() {
        SimpleXMLObject parentObject = sxoBuilder.buildObject();
        IndexedXMLObjectChildrenList<XMLObject> indexedList = new IndexedXMLObjectChildrenList<XMLObject>(parentObject);

        SimpleXMLObject child1 = sxoBuilder.buildObject(SimpleXMLObject.ELEMENT_NAME, type1);
        indexedList.add(child1);

        SimpleXMLObject child2 = sxoBuilder.buildObject(SimpleXMLObject.ELEMENT_NAME, type2);
        indexedList.add(child2);
        
        List<SimpleXMLObject> sublist = (List<SimpleXMLObject>) indexedList.subList(type2);
        assertTrue(child2 == sublist.get(sublist.indexOf(child2)));
    }
    
    /**
     *  Test sublist  lastIndexOf method.
     */
    public void testSublistLastIndexOf() {
        SimpleXMLObject parentObject = sxoBuilder.buildObject();
        IndexedXMLObjectChildrenList<XMLObject> indexedList = new IndexedXMLObjectChildrenList<XMLObject>(parentObject);

        SimpleXMLObject child1 = sxoBuilder.buildObject(SimpleXMLObject.ELEMENT_NAME, type1);
        indexedList.add(child1);

        SimpleXMLObject child2 = sxoBuilder.buildObject(SimpleXMLObject.ELEMENT_NAME, type2);
        indexedList.add(child2);
        
        SimpleXMLObject child3 = sxoBuilder.buildObject(SimpleXMLObject.ELEMENT_NAME, type1);
        indexedList.add(child3);
        
        List<SimpleXMLObject> sublist = (List<SimpleXMLObject>) indexedList.subList(type1);
        assertTrue(child3 == sublist.get(sublist.lastIndexOf(child3)));
    }
    
    public void testSublistClear() {
        SimpleXMLObject parentObject = sxoBuilder.buildObject();
        IndexedXMLObjectChildrenList<XMLObject> indexedList = new IndexedXMLObjectChildrenList<XMLObject>(parentObject);

        SimpleXMLObject child1 = sxoBuilder.buildObject(SimpleXMLObject.ELEMENT_NAME, type1);
        indexedList.add(child1);

        SimpleXMLObject child2 = sxoBuilder.buildObject(SimpleXMLObject.ELEMENT_NAME, type2);
        indexedList.add(child2);
        
        SimpleXMLObject child3 = sxoBuilder.buildObject(SimpleXMLObject.ELEMENT_NAME, type1);
        indexedList.add(child3);
        
        SimpleXMLObject child4 = sxoBuilder.buildObject(SimpleXMLObject.ELEMENT_NAME, type1);
        indexedList.add(child4);
        
        assertEquals(4, indexedList.size());
        
        List<SimpleXMLObject> sublist = (List<SimpleXMLObject>) indexedList.subList(type1);
        assertEquals(3, sublist.size());
        
        sublist.clear();
        assertEquals(0, sublist.size());
        assertEquals(1, indexedList.size());
        assertTrue(indexedList.contains(child2));
    }

    
}