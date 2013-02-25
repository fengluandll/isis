/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.apache.isis.viewer.restfulobjects.tck.domainservice;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import javax.ws.rs.core.MediaType;

import org.apache.isis.viewer.restfulobjects.applib.RepresentationType;
import org.apache.isis.viewer.restfulobjects.applib.RestfulHttpMethod;
import org.apache.isis.viewer.restfulobjects.applib.client.RestfulClient;
import org.apache.isis.viewer.restfulobjects.applib.client.RestfulRequest;
import org.apache.isis.viewer.restfulobjects.applib.client.RestfulResponse;
import org.apache.isis.viewer.restfulobjects.applib.client.RestfulRequest.RequestParameter;
import org.apache.isis.viewer.restfulobjects.applib.client.RestfulResponse.HttpStatusCode;
import org.apache.isis.viewer.restfulobjects.applib.domainobjects.DomainObjectRepresentation;
import org.apache.isis.viewer.restfulobjects.tck.IsisWebServerRule;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

public class DomainServiceResourceTest_serviceId_request_queryarg_xrodomainmodel {

    @Rule
    public IsisWebServerRule webServerRule = new IsisWebServerRule();

    private RestfulClient client;
    private RestfulRequest request;


    @Before
    public void setUp() throws Exception {
        client = webServerRule.getClient();
        String href = Util.givenLinkToService(client, "JdkValuedEntities");
        request = client.createRequest(RestfulHttpMethod.GET, href);
    }

    @Test
    public void simple_rejected() throws Exception {

        request.withArg(RequestParameter.DOMAIN_MODEL, "simple");
        final RestfulResponse<DomainObjectRepresentation> restfulResponse = request.executeT();

        assertThat(restfulResponse.getStatus(), is(HttpStatusCode.BAD_REQUEST));
        assertThat(restfulResponse.getHeader(RestfulResponse.Header.WARNING), is("x-ro-domain-model of 'simple' is not supported"));
    }

    @Test
    public void formal_accepted() throws Exception {
        
        request.withArg(RequestParameter.DOMAIN_MODEL, "formal");
        final RestfulResponse<DomainObjectRepresentation> restfulResponse = request.executeT();

        assertThat(restfulResponse.getStatus(), is(HttpStatusCode.OK));
    }

}
