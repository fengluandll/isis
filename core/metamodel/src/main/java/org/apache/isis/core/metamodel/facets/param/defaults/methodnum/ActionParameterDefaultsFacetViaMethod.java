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

package org.apache.isis.core.metamodel.facets.param.defaults.methodnum;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.isis.core.metamodel.facetapi.FacetHolder;
import org.apache.isis.core.metamodel.facets.ImperativeFacet;
import org.apache.isis.core.metamodel.facets.param.defaults.ActionParameterDefaultsFacetAbstract;
import org.apache.isis.core.metamodel.spec.ManagedObjects;
import org.apache.isis.core.metamodel.specloader.specimpl.PendingParameterModel;

import lombok.NonNull;

public class ActionParameterDefaultsFacetViaMethod 
extends ActionParameterDefaultsFacetAbstract 
implements ImperativeFacet {

    private final Method method;
    private final int paramNum;
    private final Optional<Constructor<?>> ppmFactory;

    /**
     *
     * @param method
     * @param paramNum - which parameter this facet relates to.
     * @param holder
     * @param adapterManager
     */
    public ActionParameterDefaultsFacetViaMethod(
            final Method method,
            final int paramNum,
            final Optional<Constructor<?>> ppmFactory,
            final FacetHolder holder) {

        super(holder);
        this.method = method;
        this.paramNum = paramNum;
        this.ppmFactory = ppmFactory;
    }

    /**
     * Returns a singleton list of the {@link Method} provided in the
     * constructor.
     */
    @Override
    public List<Method> getMethods() {
        return Collections.singletonList(method);
    }

    @Override
    public Intent getIntent(final Method method) {
        return Intent.DEFAULTS;
    }

    @Override
    public Object getDefault(@NonNull PendingParameterModel pendingArgs) {
            
        // call with args: defaultNAct(X x, Y y, ...) 
        
        if(ppmFactory.isPresent()) {
            // PPM programming model
            return ManagedObjects.InvokeUtil
                    .invokeWithPPM(ppmFactory.get(), method, 
                            pendingArgs.getActionTarget(), pendingArgs.getParamValues());    
        }
        
        // else support legacy programming model, call any-arg defaultNAct(...)
        return ManagedObjects.InvokeUtil
                .invokeAutofit(method, 
                    pendingArgs.getActionTarget(), pendingArgs.getParamValues());
    }

    @Override
    protected String toStringValues() {
        return "method=" + method;
    }

    @Override public void appendAttributesTo(final Map<String, Object> attributeMap) {
        super.appendAttributesTo(attributeMap);
        ImperativeFacet.Util.appendAttributesTo(this, attributeMap);
    }

}
