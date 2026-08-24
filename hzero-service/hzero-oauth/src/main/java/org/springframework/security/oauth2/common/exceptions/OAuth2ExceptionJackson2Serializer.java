/*
 * Copyright 2006-2011 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.springframework.security.oauth2.common.exceptions;

import java.io.IOException;
import java.util.Map.Entry;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.springframework.web.util.HtmlUtils;

import org.hzero.core.message.MessageAccessor;
import org.hzero.oauth.security.exception.CustomAuthenticationException;
import org.hzero.oauth.security.exception.ErrorWithTimesException;
import org.hzero.oauth.security.util.LoginUtil;

/**
 *
 * @author bojiangzhou 2019/06/02 加上自定义返回信息
 * @author Brian Clozel
 *
 */
public class OAuth2ExceptionJackson2Serializer extends StdSerializer<OAuth2Exception> {
	private static final long serialVersionUID = -2984969822506915347L;

	public OAuth2ExceptionJackson2Serializer() {
        super(OAuth2Exception.class);
    }

	@Override
	public void serialize(OAuth2Exception value, JsonGenerator jgen, SerializerProvider provider) throws IOException,
			JsonProcessingException {
        jgen.writeStartObject();
		jgen.writeStringField("error", value.getOAuth2ErrorCode());
		String errorMessage = value.getMessage();
		if (errorMessage != null) {
			errorMessage = HtmlUtils.htmlEscape(errorMessage);
		}
		jgen.writeStringField("error_description", errorMessage);
		if (value.getAdditionalInformation()!=null) {
			for (Entry<String, String> entry : value.getAdditionalInformation().entrySet()) {
				String key = entry.getKey();
				String add = entry.getValue();
				jgen.writeStringField(key, add);				
			}
		}
		jgen.writeBooleanField("success", false);
		jgen.writeStringField("code", errorMessage);
		Object[] parameters = null;
		if (value.getCause() instanceof CustomAuthenticationException) {
			CustomAuthenticationException ex = (CustomAuthenticationException) value.getCause();
			parameters = ex.getParameters();
		}
		if (value.getCause() instanceof ErrorWithTimesException) {
			ErrorWithTimesException ex = (ErrorWithTimesException) value.getCause();
			// 返回错误次数和剩余次数
			jgen.writeStringField("errorTimes", String.valueOf(ex.getErrorTimes()));
			jgen.writeStringField("surplusTimes", String.valueOf(ex.getSurplusTimes()));
		}
		jgen.writeStringField("message", MessageAccessor.getMessage(errorMessage, parameters, LoginUtil.getLanguageLocale()).desc());
        jgen.writeEndObject();
	}

}
