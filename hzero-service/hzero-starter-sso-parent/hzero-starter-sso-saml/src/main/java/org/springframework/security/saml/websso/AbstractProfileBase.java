/*
 * Copyright 2009 Vladimir Schaefer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.security.saml.websso;

import java.security.SecureRandom;
import java.util.Random;

import org.joda.time.DateTime;
import org.opensaml.Configuration;
import org.opensaml.common.SAMLException;
import org.opensaml.common.SAMLObjectBuilder;
import org.opensaml.common.SAMLVersion;
import org.opensaml.common.binding.artifact.SAMLArtifactMap;
import org.opensaml.common.binding.decoding.BasicURLComparator;
import org.opensaml.common.binding.decoding.URIComparator;
import org.opensaml.common.xml.SAMLConstants;
import org.opensaml.saml2.core.*;
import org.opensaml.saml2.metadata.Endpoint;
import org.opensaml.saml2.metadata.IDPSSODescriptor;
import org.opensaml.saml2.metadata.provider.MetadataProviderException;
import org.opensaml.security.MetadataCriteria;
import org.opensaml.security.SAMLSignatureProfileValidator;
import org.opensaml.ws.message.encoder.MessageEncodingException;
import org.opensaml.xml.XMLObjectBuilderFactory;
import org.opensaml.xml.security.CriteriaSet;
import org.opensaml.xml.security.credential.UsageType;
import org.opensaml.xml.security.criteria.EntityIDCriteria;
import org.opensaml.xml.security.criteria.UsageCriteria;
import org.opensaml.xml.signature.Signature;
import org.opensaml.xml.signature.SignatureTrustEngine;
import org.opensaml.xml.validation.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.saml.context.SAMLMessageContext;
import org.springframework.security.saml.metadata.MetadataManager;
import org.springframework.security.saml.processor.SAMLProcessor;
import org.springframework.security.saml.util.SAMLUtil;
import org.springframework.util.Assert;

import org.hzero.core.base.BaseConstants;

/**
 * Base superclass for classes implementing processing of SAML messages.
 *
 * @author Vladimir Schaefer
 */
public abstract class AbstractProfileBase implements InitializingBean {

    /**
     * Maximum time from response creation when the message is deemed valid.
     */
    private int responseSkew = 60;

    /**
     * Maximum time between assertion creation and current time when the assertion is usable
     */
    private int maxAssertionTime = 3000;

    /**
     * Class logger.
     */
    protected final Logger log = LoggerFactory.getLogger(getClass());

    protected MetadataManager metadata;
    protected SAMLProcessor processor;
    protected SAMLArtifactMap artifactMap;
    protected XMLObjectBuilderFactory builderFactory;
    protected URIComparator uriComparator;

    public AbstractProfileBase() {
        this.builderFactory = Configuration.getBuilderFactory();
        this.uriComparator = new BasicURLComparator();
    }

    public AbstractProfileBase(SAMLProcessor processor, MetadataManager manager) {
        this();
        this.processor = processor;
        this.metadata = manager;
    }

    public URIComparator getUriComparator() {
        return uriComparator;
    }

    /**
     * Implementation are expected to provide an unique identifier for the profile this class implements.
     *
     * @return profile name
     */
    public abstract String getProfileIdentifier();

    /**
     * Sets maximum difference between local time and time of the assertion creation which still allows
     * message to be processed. Basically determines maximum difference between clocks of the IDP and SP machines.
     * Defaults to 60.
     *
     * @param responseSkew response skew time (in seconds)
     */
    public void setResponseSkew(int responseSkew) {
        this.responseSkew = responseSkew;
    }

    /**
     * @return response skew time (in seconds)
     */
    public int getResponseSkew() {
        return responseSkew;
    }

    /**
     * Maximum time between assertion creation and current time when the assertion is usable in seconds.
     *
     * @return max assertion time
     */
    public int getMaxAssertionTime() {
        return maxAssertionTime;
    }

    /**
     * Customizes max assertion time between assertion creation and it's usability. Default to 3000 seconds.
     *
     * @param maxAssertionTime time in seconds
     */
    public void setMaxAssertionTime(int maxAssertionTime) {
        this.maxAssertionTime = maxAssertionTime;
    }

    /**
     * Method calls the processor and sends the message contained in the context. Subclasses can provide additional
     * processing before the message delivery. Message is sent using binding defined in the peer entity of the context.
     *
     * @param context context
     * @param sign    whether the message should be signed
     * @throws MetadataProviderException metadata error
     * @throws SAMLException             SAML encoding error
     * @throws org.opensaml.ws.message.encoder.MessageEncodingException
     *                                   message encoding error
     */
    protected void sendMessage(SAMLMessageContext context, boolean sign) throws MetadataProviderException, SAMLException, MessageEncodingException {
        processor.sendMessage(context, sign);
    }

    /**
     * Method calls the processor and sends the message contained in the context. Subclasses can provide additional
     * processing before the message delivery. Message is sent using the specified binding.
     *
     * @param context context
     * @param sign    whether the message should be signed
     * @param binding binding to use to send the message
     * @throws MetadataProviderException metadata error
     * @throws SAMLException             SAML encoding error
     * @throws org.opensaml.ws.message.encoder.MessageEncodingException
     *                                   message encoding error
     */
    protected void sendMessage(SAMLMessageContext context, boolean sign, String binding) throws MetadataProviderException, SAMLException, MessageEncodingException {
        processor.sendMessage(context, sign, binding);
    }

    protected Status getStatus(String code, String statusMessage) {
        SAMLObjectBuilder<StatusCode> codeBuilder = (SAMLObjectBuilder<StatusCode>) builderFactory.getBuilder(StatusCode.DEFAULT_ELEMENT_NAME);
        StatusCode statusCode = codeBuilder.buildObject();
        statusCode.setValue(code);

        SAMLObjectBuilder<Status> statusBuilder = (SAMLObjectBuilder<Status>) builderFactory.getBuilder(Status.DEFAULT_ELEMENT_NAME);
        Status status = statusBuilder.buildObject();
        status.setStatusCode(statusCode);

        if (statusMessage != null) {
            SAMLObjectBuilder<StatusMessage> messageBuilder = (SAMLObjectBuilder<StatusMessage>) builderFactory.getBuilder(StatusMessage.DEFAULT_ELEMENT_NAME);
            StatusMessage statusMessageObject = messageBuilder.buildObject();
            statusMessageObject.setMessage(statusMessage);
            status.setStatusMessage(statusMessageObject);
        }

        return status;
    }

    /**
     * Fills the request with version, issue instants and destination data.
     *
     * @param localEntityId entityId of the local party acting as message issuer
     * @param request       request to be filled
     * @param service       service to use as destination for the request
     */
    protected void buildCommonAttributes(String localEntityId, RequestAbstractType request, Endpoint service) {

        request.setID(generateID());
        request.setIssuer(getIssuer(localEntityId));
        request.setVersion(SAMLVersion.VERSION_20);
        request.setIssueInstant(new DateTime());

        if (service != null) {
            // Service is now known when we do not know which IDP will be used
            request.setDestination(service.getLocation());
        }

    }

    protected Issuer getIssuer(String localEntityId) {
        SAMLObjectBuilder<Issuer> issuerBuilder = (SAMLObjectBuilder<Issuer>) builderFactory.getBuilder(Issuer.DEFAULT_ELEMENT_NAME);
        Issuer issuer = issuerBuilder.buildObject();
        issuer.setValue(localEntityId);
        return issuer;
    }

    /**
     * Generates random ID to be used as Request/Response ID.
     *
     * @return random ID
     */
    protected String generateID() {
        Random r = new SecureRandom();
        return 'a' + Long.toString(abs(r.nextLong()), 20) + Long.toString(abs(r.nextLong()), 20);
    }

    private long abs (long value){
        if (value == Long.MIN_VALUE) {
            return Long.MIN_VALUE;
        }
        return Math.abs(value);
    }

    protected void verifyIssuer(Issuer issuer, SAMLMessageContext context) throws SAMLException {
        // Validate format of issuer
        if (issuer.getFormat() != null && !issuer.getFormat().equals(NameIDType.ENTITY)) {
            throw new SAMLException("Issuer invalidated by issuer type " + issuer.getFormat());
        }
        // Validate that issuer is expected peer entity
        if (!context.getPeerEntityMetadata().getEntityID().equals(issuer.getValue())) {
            throw new SAMLException("Issuer invalidated by issuer value " + issuer.getValue());
        }
    }

    /**
     * Verifies that the destination URL intended in the message matches with the endpoint address. The URL message
     * was ultimately received doesn't need to necessarily match the one defined in the metadata (in case of e.g. reverse-proxying
     * of messages).
     *
     * @param endpoint endpoint the message was received at
     * @param destination URL of the endpoint the message was intended to be sent to by the peer or null when not included
     * @throws SAMLException in case endpoint doesn't match
     */
    protected void verifyEndpoint(Endpoint endpoint, String destination) throws SAMLException {
        // Verify that destination in the response matches one of the available endpoints
        if (destination != null) {
            String[] uris = destination.split("/");
            Assert.isTrue(uris.length > 0, BaseConstants.ErrorCode.ERROR);
            if (endpoint.getLocation().contains(uris[uris.length-1])) {
                // Expected
                log.debug("The endpoint location contains uri");
            } else if (endpoint.getResponseLocation().contains(uris[uris.length-1])) {
                // Expected
                log.debug("The endpoint response location contains uri");
            } else {
                throw new SAMLException("Intended destination " + destination + " doesn't match any of the endpoint URLs on endpoint " + endpoint.getLocation() + " for profile " + getProfileIdentifier());
            }
        }
    }

    protected void verifySignature(Signature signature, String IDPEntityID, SignatureTrustEngine trustEngine) throws org.opensaml.xml.security.SecurityException, ValidationException {

        if (trustEngine == null) {
            throw new SecurityException("Trust engine is not set, signature can't be verified");
        }

        SAMLSignatureProfileValidator validator = new SAMLSignatureProfileValidator();
        validator.validate(signature);
        CriteriaSet criteriaSet = new CriteriaSet();
        criteriaSet.add(new EntityIDCriteria(IDPEntityID));
        criteriaSet.add(new MetadataCriteria(IDPSSODescriptor.DEFAULT_ELEMENT_NAME, SAMLConstants.SAML20P_NS));
        criteriaSet.add(new UsageCriteria(UsageType.SIGNING));
        log.debug("Verifying signature", signature);

        if (!trustEngine.validate(signature, criteriaSet)) {
            throw new ValidationException("Signature is not trusted or invalid");
        }

    }

    /**
     * Method is expected to return binding used to transfer messages to this endpoint. For some profiles the
     * binding attribute in the metadata contains the profile name, method correctly parses the real binding
     * in these situations.
     *
     * @param endpoint endpoint
     * @return binding
     */
    protected String getEndpointBinding(Endpoint endpoint) {
        return SAMLUtil.getBindingForEndpoint(endpoint);
    }

    /**
     * Determines whether given endpoint can be used together with the specified binding.
     * <p>
     * By default value of the binding in the endpoint is compared for equality with the user provided binding.
     * <p>
     * Method is automatically called for verification of user supplied binding value in the WebSSOProfileOptions.
     *
     * @param endpoint endpoint to check
     * @param binding  binding the endpoint must support for the method to return true
     * @return true if given endpoint can be used with the binding
     */
    protected boolean isEndpointMatching(Endpoint endpoint, String binding) {
        return binding.equals(getEndpointBinding(endpoint));
    }

    @Autowired
    public void setMetadata(MetadataManager metadata) {
        this.metadata = metadata;
    }

    @Autowired(required = false)
    public void setProcessor(SAMLProcessor processor) {
        this.processor = processor;
    }

    // TODO autowire when ready
    public void setArtifactMap(SAMLArtifactMap artifactMap) {
        this.artifactMap = artifactMap;
    }

    public SAMLArtifactMap getArtifactMap() {
        return artifactMap;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // TODO verify artifact map when ready
        Assert.notNull(metadata, "Metadata must be set");
        Assert.notNull(processor, "SAML Processor must be set");
    }

}