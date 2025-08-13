/*
 * Copyright 2012-2017 the original author or authors.
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
 */

package org.hzero.core.redis.config;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.data.redis.JedisClientConfigurationBuilderCustomizer;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration.JedisClientConfigurationBuilder;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.util.StringUtils;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Redis connection configuration using Jedis.
 *
 * @author Mark Paluch
 * @author Stephane Nicoll
 */
class JedisConnectionConfigure extends RedisConnectionConfiguration {

	private final RedisProperties properties;

	private final List<JedisClientConfigurationBuilderCustomizer> builderCustomizers;

	JedisConnectionConfigure(RedisProperties properties,
							 ObjectProvider<RedisSentinelConfiguration> sentinelConfiguration,
							 ObjectProvider<RedisClusterConfiguration> clusterConfiguration,
							 ObjectProvider<List<JedisClientConfigurationBuilderCustomizer>> builderCustomizers,
							 int database) {
		super(properties, sentinelConfiguration, clusterConfiguration, database);
		this.properties = properties;
		this.builderCustomizers = builderCustomizers.getIfAvailable(Collections::emptyList);
	}

	public JedisConnectionFactory redisConnectionFactory() {
		return createJedisConnectionFactory();
	}

	private JedisConnectionFactory createJedisConnectionFactory() {
		JedisClientConfiguration clientConfiguration = getJedisClientConfiguration();
		JedisConnectionFactory jedisConnectionFactory;
		if (getSentinelConfig() != null) {
			jedisConnectionFactory = new JedisConnectionFactory(getSentinelConfig(), clientConfiguration);
		} else if (getClusterConfiguration() != null) {
			jedisConnectionFactory = new JedisConnectionFactory(getClusterConfiguration(), clientConfiguration);
		} else {
			jedisConnectionFactory = new JedisConnectionFactory(getStandaloneConfig(), clientConfiguration);
		}
		jedisConnectionFactory.afterPropertiesSet();
		return jedisConnectionFactory;
	}

	private JedisClientConfiguration getJedisClientConfiguration() {
		JedisClientConfigurationBuilder builder = applyProperties(
				JedisClientConfiguration.builder());
		RedisProperties.Pool pool = this.properties.getJedis().getPool();
		if (pool != null) {
			applyPooling(pool, builder);
		}
		if (StringUtils.hasText(this.properties.getUrl())) {
			customizeConfigurationFromUrl(builder);
		}
		customize(builder);
		return builder.build();
	}

	private JedisClientConfigurationBuilder applyProperties(
			JedisClientConfigurationBuilder builder) {
		if (this.properties.isSsl()) {
			builder.useSsl();
		}
		if (this.properties.getTimeout() != null) {
			Duration timeout = this.properties.getTimeout();
			builder.readTimeout(timeout).connectTimeout(timeout);
		}
		return builder;
	}

	private void applyPooling(RedisProperties.Pool pool,
			JedisClientConfigurationBuilder builder) {
		builder.usePooling().poolConfig(jedisPoolConfig(pool));
	}

	private JedisPoolConfig jedisPoolConfig(RedisProperties.Pool pool) {
		JedisPoolConfig config = new JedisPoolConfig();
		config.setMaxTotal(pool.getMaxActive());
		config.setMaxIdle(pool.getMaxIdle());
		config.setMinIdle(pool.getMinIdle());
		if (pool.getMaxWait() != null) {
			config.setMaxWaitMillis(pool.getMaxWait().toMillis());
		}
		return config;
	}

	private void customizeConfigurationFromUrl(
			JedisClientConfigurationBuilder builder) {
		ConnectionInfo connectionInfo = parseUrl(this.properties.getUrl());
		if (connectionInfo.isUseSsl()) {
			builder.useSsl();
		}
	}

	private void customize(
			JedisClientConfigurationBuilder builder) {
		for (JedisClientConfigurationBuilderCustomizer customizer : this.builderCustomizers) {
			customizer.customize(builder);
		}
	}

}
