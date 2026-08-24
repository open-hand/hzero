package org.hzero.starter.keyencrypt.json;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.core.exception.CommonException;
import org.hzero.starter.keyencrypt.core.IEncryptionService;

/**
 * Crypto Module for Jackson JSON library
 */
public class CryptoModule extends Module {

    public final static String GROUP_ID = "org.srm";
    public final static String ARTIFACT_ID = "jackson-json-crypto";
    //
    private EncryptedSerializerModifier serializerModifierModifier;
    private EncryptedDeserializerModifier deserializerModifierModifier;


    /**
     * Set the encryption service to use
     *
     * @param encryptionService Service to supply crypto functionality
     * @return Updated module
     */
    public CryptoModule addEncryptionService(final IEncryptionService encryptionService) {
        serializerModifierModifier = new EncryptedSerializerModifier(encryptionService);
        deserializerModifierModifier = new EncryptedDeserializerModifier(encryptionService);
        return this;
    }

    /**
     * Method that returns a display that can be used by Jackson
     *
     * @return Name for display
     */
    @Override
    public String getModuleName() {
        return ARTIFACT_ID;
    }

    /**
     * Method that returns version of this module.
     *
     * @return Version info
     */
    @Override
    public Version version() {
        return new Version(1, 0, 0, null, GROUP_ID, ARTIFACT_ID);
    }

    /**
     * Method called by {@link ObjectMapper} when module is registered.
     *
     * @param context Context for 'registering extended' functionality.
     */
    @Override
    public void setupModule(final SetupContext context) {
        if ((null == serializerModifierModifier) || (null == deserializerModifierModifier))
            throw new CommonException("Crypto module not initialised with an encryption service");
        context.addBeanSerializerModifier(serializerModifierModifier);
        context.addBeanDeserializerModifier(deserializerModifierModifier);
    }
}

