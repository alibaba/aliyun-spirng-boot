/*
 * Copyright 2013-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.alibaba.cloud.spring.boot.oss.autoconfigure;

import com.alibaba.cloud.context.AliCloudAuthorizationMode;
import com.alibaba.cloud.spring.boot.context.autoconfigure.AliCloudContextAutoConfiguration;
import com.alibaba.cloud.spring.boot.context.env.AliCloudProperties;
import com.alibaba.cloud.spring.boot.oss.env.OssProperties;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import static com.alibaba.cloud.spring.boot.oss.OssConstants.PREFIX;

/**
 * OSS Auto {@link Configuration}.
 *
 * @author <a href="mailto:fangjian0423@gmail.com">Jim</a>
 * @author xiaolongzuo
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(name = "com.alibaba.cloud.spring.boot.oss.autoconfigure.OssAutoConfiguration")
@ConditionalOnProperty(prefix = PREFIX, name = "enabled", matchIfMissing = true)
@EnableConfigurationProperties(OssProperties.class)
@ImportAutoConfiguration(AliCloudContextAutoConfiguration.class)
public class OssContextAutoConfiguration {

    @ConditionalOnMissingBean
    @Bean
    public OSS ossClient(AliCloudProperties aliCloudProperties, OssProperties ossProperties) {
        if (ossProperties.getAuthorizationMode() == AliCloudAuthorizationMode.AK_SK) {
            Assert.isTrue(!StringUtils.isEmpty(ossProperties.getEndpoint()),
                    "Oss endpoint can't be empty.");
            Assert.isTrue(!StringUtils.isEmpty(aliCloudProperties.getOssAccessKey()),
                    "${alibaba.cloud.oss-access-key} or ${alibaba.cloud.access-key} can't be empty.");
            Assert.isTrue(!StringUtils.isEmpty(aliCloudProperties.getOssSecretKey()),
                    "${alibaba.cloud.oss-secret-key} or ${alibaba.cloud.secret-key} can't be empty.");
            return new OSSClientBuilder().build(ossProperties.getEndpoint(),
                    aliCloudProperties.getOssAccessKey(), aliCloudProperties.getOssSecretKey(),
                    ossProperties.getConfig());
        } else if (ossProperties.getAuthorizationMode() == AliCloudAuthorizationMode.STS) {
            Assert.isTrue(!StringUtils.isEmpty(ossProperties.getEndpoint()),
                    "Oss endpoint can't be empty.");
            Assert.isTrue(!StringUtils.isEmpty(ossProperties.getSts().getAccessKey()),
                    "Access key can't be empty.");
            Assert.isTrue(!StringUtils.isEmpty(ossProperties.getSts().getSecretKey()),
                    "Secret key can't be empty.");
            Assert.isTrue(!StringUtils.isEmpty(ossProperties.getSts().getSecurityToken()),
                    "Security Token can't be empty.");
            return new OSSClientBuilder().build(ossProperties.getEndpoint(),
                    ossProperties.getSts().getAccessKey(),
                    ossProperties.getSts().getSecretKey(),
                    ossProperties.getSts().getSecurityToken(), ossProperties.getConfig());
        } else {
            throw new IllegalArgumentException("Unknown auth mode.");
        }
    }

}
