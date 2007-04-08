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

import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.opensaml.xml.security.SecurityException;
import org.opensaml.xml.security.credential.AbstractCredentialResolver;
import org.opensaml.xml.security.credential.EntityCredentialCriteria;
import org.opensaml.xml.security.credential.Credential;
import org.opensaml.xml.security.credential.CredentialCriteriaSet;
import org.opensaml.xml.security.credential.CredentialResolver;
import org.opensaml.xml.security.credential.UsageType;

/**
 * A {@link CredentialResolver} that extracts {@link Credential}'s from a key store.
 * 
 * If no key usage type is presented at construction time this resolver will return the key, if available, regardless of
 * the usage type provided to its resolve method.
 */
public class KeyStoreCredentialResolver extends AbstractCredentialResolver  implements CredentialResolver {

    /** Class logger. */
    private static Logger log = Logger.getLogger(KeyStoreCredentialResolver.class);

    /** Key store credentials are retrieved from. */
    private KeyStore keyStore;

    /** Passwords for keys. The key must be the entity ID, the value the password. */
    private Map<String, String> keyPasswords;

    /** Usage type of all keys in the store. */
    private UsageType keyUsage;

    /**
     * Constructor.
     * 
     * @param store key store credentials are retrieved from
     * @param passwords for key entries, map key is the entity id, map value is the password
     * 
     * @throws IllegalArgumentException thrown if the given keystore is null
     */
    public KeyStoreCredentialResolver(KeyStore store, Map<String, String> passwords) throws IllegalArgumentException {
        this(store, passwords, null);
    }

    /**
     * Constructor.
     * 
     * @param store key store credentials are retrieved from
     * @param passwords for key entries, map key is the entity id, map value is the password
     * @param usage usage type of all keys in the store
     * 
     * @throws IllegalArgumentException thrown if the given keystore is null
     */
    public KeyStoreCredentialResolver(KeyStore store, Map<String, String> passwords, UsageType usage)
            throws IllegalArgumentException {
        if (store == null) {
            throw new IllegalArgumentException("Provided key store may not be null.");
        }

        try {
            store.size();
        } catch (KeyStoreException e) {
            throw new IllegalArgumentException("Keystore has not been initialized.");
        }

        keyStore = store;

        keyUsage = usage;
    }

    /** {@inheritDoc} */
    public Iterable<Credential> resolveCredentials(CredentialCriteriaSet criteriaSet) throws SecurityException {
        Set<Credential> credentials = new HashSet<Credential>();
        
        EntityCredentialCriteria entityCriteria = criteriaSet.getCriteria(EntityCredentialCriteria.class);
        if (entityCriteria == null) {
            log.error("Basic criteria not specified, resolution can not be attempted");
            throw new SecurityException("No basic criteria was specified in criteria set");
        }
        UsageType usage = entityCriteria.getUsage();
        String entity = entityCriteria.getOwnerID();
        if (keyUsage != null && keyUsage != usage) {
            return credentials;
        }

        KeyStore.PasswordProtection keyPassword = null;

        if (keyPasswords.containsKey(entity)) {
            keyPassword = new KeyStore.PasswordProtection(keyPasswords.get(entity).toCharArray());
        }

        BasicX509Credential credential = new BasicX509Credential();
        credential.setEntityId(entity);
        try {
            KeyStore.Entry keyEntry = keyStore.getEntry(entity, keyPassword);
            // alias doesn't exist
            if (keyEntry == null) {
                return credentials;
            }

            if (keyEntry instanceof KeyStore.PrivateKeyEntry) {
                processPrivateKeyEntry(credential, (KeyStore.PrivateKeyEntry) keyEntry);
            } else if (keyEntry instanceof KeyStore.TrustedCertificateEntry) {
                processTrustedCertificateEntry(credential, (KeyStore.TrustedCertificateEntry) keyEntry);
            } else {
                throw new SecurityException("KeyStore entry was of an unsupported type: " 
                        + keyEntry.getClass().getName());
            }
        } catch (GeneralSecurityException e) {
            log.error("Unable to retrieve key for entity " + entity, e);
            return credentials;
        }
        
        credentials.add(credential);
        return credentials;
    }

    /** Extract X509Credential info from a keystore trusted certificate entry.
     * 
     * @param credential the credential being built
     * @param trustedCertEntry the entry being processed
     */
    protected void processTrustedCertificateEntry(BasicX509Credential credential, 
            KeyStore.TrustedCertificateEntry trustedCertEntry) {
        X509Certificate cert = (X509Certificate) trustedCertEntry.getTrustedCertificate();

        credential.setPublicKey(cert.getPublicKey());

        credential.setEntityCertificate(cert);

        ArrayList<X509Certificate> certChain = new ArrayList<X509Certificate>();
        certChain.add(cert);
        credential.setEntityCertificateChain(certChain);
    }

    /**
     * Extract X509Credential info from a keystore private key entry.
     * 
     * @param credential the credential being built
     * @param privateKeyEntry the entry being processed
     */
    protected void processPrivateKeyEntry(BasicX509Credential credential, KeyStore.PrivateKeyEntry privateKeyEntry) {
        credential.setPrivateKey(privateKeyEntry.getPrivateKey());

        credential.setPublicKey( privateKeyEntry.getCertificate().getPublicKey() );

        credential.setEntityCertificate((X509Certificate) privateKeyEntry.getCertificate());
        credential.setEntityCertificateChain(Arrays.asList((X509Certificate[]) privateKeyEntry
                .getCertificateChain()));
    }
}