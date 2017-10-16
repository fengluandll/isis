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
package org.apache.isis.viewer.restfulobjects.rendering.service.conmap;

import java.util.List;

import javax.ws.rs.core.MediaType;

import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.viewer.restfulobjects.applib.RepresentationType;

/**
 * @deprecated - replaced by simplified version, {@link org.apache.isis.applib.services.conmap.ContentMappingService}, in the applib.
 */
@Deprecated
public interface ContentMappingService {

    /**
     * @deprecated - replaced by simplified version, {@link org.apache.isis.applib.services.conmap.ContentMappingService#map(Object, List)}, in the applib.
     */
    @Deprecated
    @Programmatic
    Object map(Object object, final List<MediaType> acceptableMediaTypes, final RepresentationType representationType);

}
