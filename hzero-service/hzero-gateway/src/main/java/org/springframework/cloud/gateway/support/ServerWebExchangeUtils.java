/*
 * Copyright 2013-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.springframework.cloud.gateway.support;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.cloud.gateway.handler.AsyncPredicate;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * @author Spencer Gibb
 */
public class ServerWebExchangeUtils {

	private static final Log logger = LogFactory.getLog(ServerWebExchangeUtils.class);

	public static final String PRESERVE_HOST_HEADER_ATTRIBUTE = qualify("preserveHostHeader");
	public static final String URI_TEMPLATE_VARIABLES_ATTRIBUTE = qualify("uriTemplateVariables");

	public static final String CLIENT_RESPONSE_ATTR = qualify("webHandlerClientResponse");
	public static final String GATEWAY_ROUTE_ATTR = qualify("gatewayRoute");
	public static final String GATEWAY_REQUEST_URL_ATTR = qualify("gatewayRequestUrl");
	public static final String GATEWAY_ORIGINAL_REQUEST_URL_ATTR = qualify("gatewayOriginalRequestUrl");
	public static final String GATEWAY_HANDLER_MAPPER_ATTR = qualify("gatewayHandlerMapper");
	public static final String GATEWAY_SCHEME_PREFIX_ATTR = qualify("gatewaySchemePrefix");
	public static final String GATEWAY_PREDICATE_ROUTE_ATTR = qualify("gatewayPredicateRouteAttr");
	public static final String WEIGHT_ATTR = qualify("routeWeight");
	public static final String ORIGINAL_RESPONSE_CONTENT_TYPE_ATTR = "original_response_content_type";

	/**
	 * Used when a routing filter has been successfully call. Allows users to write custom
	 * routing filters that disable built in routing filters.
	 */
	public static final String GATEWAY_ALREADY_ROUTED_ATTR = qualify("gatewayAlreadyRouted");

	public static final String GATEWAY_ALREADY_PREFIXED_ATTR = qualify("gatewayAlreadyPrefixed");

	private static String qualify(String attr) {
		return ServerWebExchangeUtils.class.getName() + "." + attr;
	}

	public static void setAlreadyRouted(ServerWebExchange exchange) {
		exchange.getAttributes().put(GATEWAY_ALREADY_ROUTED_ATTR, true);
	}

	public static boolean isAlreadyRouted(ServerWebExchange exchange) {
		return exchange.getAttributeOrDefault(GATEWAY_ALREADY_ROUTED_ATTR, false);
	}

	public static boolean setResponseStatus(ServerWebExchange exchange, HttpStatus httpStatus) {
		boolean response = exchange.getResponse().setStatusCode(httpStatus);
		if (!response && logger.isWarnEnabled()) {
			logger.warn("Unable to set status code to "+ httpStatus + ". Response already committed.");
		}
		return response;
	}

	/**
	 * fix encode bug
	 * @param uri
	 * @return
	 */
	public static boolean containsEncodedParts(URI uri) {
		boolean encoded = (uri.getRawQuery() != null && uri.getRawQuery().contains("%"))
				|| (uri.getRawPath() != null && uri.getRawPath().contains("%"));

		// Verify if it is really fully encoded. Treat partial encoded as unencoded.
		if (encoded) {
			try {
				UriComponentsBuilder.fromUri(uri).build(true);
				return true;
			}
			catch (IllegalArgumentException ignore) {
				if (logger.isTraceEnabled()) {
					logger.trace("Error in containsEncodedParts", ignore);
				}
			}

			return false;
		}

		return encoded;
	}

	public static HttpStatus parse(String statusString) {
		HttpStatus httpStatus;

		try {
			int status = Integer.parseInt(statusString);
			httpStatus = HttpStatus.resolve(status);
		} catch (NumberFormatException e) {
			// try the enum string
			httpStatus = HttpStatus.valueOf(statusString.toUpperCase());
		}
		return httpStatus;
	}

	public static void addOriginalRequestUrl(ServerWebExchange exchange, URI url) {
		exchange.getAttributes().computeIfAbsent(GATEWAY_ORIGINAL_REQUEST_URL_ATTR, s -> new LinkedHashSet<>());
		LinkedHashSet<URI> uris = exchange.getRequiredAttribute(GATEWAY_ORIGINAL_REQUEST_URL_ATTR);
		uris.add(url);
	}

	public static AsyncPredicate<ServerWebExchange> toAsyncPredicate(Predicate<? super ServerWebExchange> predicate) {
		Objects.requireNonNull(predicate, "predicate must not be null");
		return t -> Mono.just(predicate.test(t));
	}
}
