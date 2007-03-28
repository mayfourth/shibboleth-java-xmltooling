/*
 * Copyright [2007] [University Corporation for Advanced Internet Development, Inc.]
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

package org.opensaml.xml.security.x509;

import org.opensaml.xml.security.keyinfo.KeyInfoCredentialContext;
import org.opensaml.xml.signature.KeyInfo;
import org.opensaml.xml.signature.Signature;

/**
 * A credential context for credentials resolved from a {@link KeyInfo} that was found in 
 * in XML Signature {@link Signature} element.
 */
public class XMLSignatureCredentialContext extends KeyInfoCredentialContext {

    /** The Signature element context. */ 
    private Signature signature;
    
    /** {@inheritDoc} */
    public void setKeyInfo(KeyInfo info) {
        super.setKeyInfo(info);
        // KeyInfo -> Signature
        if (info != null) {
            signature = (Signature) info.getParent();
        }
    }

    /**
     * Gets the Signature element context.
     * 
     * @return signature context
     */
    public Signature getSignature() {
        return signature;
    }
    
    /**
     * Sets the Signature element context.
     * 
     * @param newSignature context
     */
    public void setSignature(Signature newSignature) {
        signature = newSignature;
    }
    
}