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
package org.apache.isis.viewer.wicket.ui.components.widgets.select2.providers;

import org.apache.isis.core.commons.collections.Can;
import org.apache.isis.core.commons.internal.base._NullSafe;
import org.apache.isis.core.metamodel.spec.ManagedObject;
import org.apache.isis.core.metamodel.specloader.specimpl.PendingParameterModel;
import org.apache.isis.core.webapp.context.memento.ObjectMemento;
import org.apache.isis.viewer.common.model.feature.ParameterUiModel;
import org.apache.isis.viewer.wicket.model.models.ScalarModel;

import lombok.val;

public class ObjectAdapterMementoProviderForReferenceParamOrPropertyAutoComplete
extends ObjectAdapterMementoProviderAbstract {

    private static final long serialVersionUID = 1L;
    
    private final Can<ObjectMemento> pendingArgMementos;

    public ObjectAdapterMementoProviderForReferenceParamOrPropertyAutoComplete(ScalarModel scalarModel) {
        super(scalarModel);
        val commonContext = scalarModel.getCommonContext();
        val pendingArgs = scalarModel.isParameter() 
                ? ((ParameterUiModel)scalarModel).getPendingParameterModel().getParamValues()
                : Can.<ManagedObject>empty();
        val pendingArgMementos = pendingArgs
                .map(commonContext::mementoForParameter);
        
        this.pendingArgMementos = pendingArgMementos;
    }

    @Override
    protected Can<ObjectMemento> obtainMementos(String term) {
        
        val scalarModel = getScalarModel();
        
        if (scalarModel.hasAutoComplete()) {
        
            if(scalarModel.isParameter()) {
                // recover any pendingArgs
                val paramModel = (ParameterUiModel)scalarModel;
                val pendingArgs = reconstructPendingArgs(paramModel, pendingArgMementos);
                paramModel.setPendingParameterModel(pendingArgs);
            }
            
            val commonContext = super.getCommonContext();
            return scalarModel
                    .getAutoComplete(term)
                    .map(commonContext::mementoFor);
        }
        
        return Can.empty();
        
    }
    
    private PendingParameterModel reconstructPendingArgs(
            final ParameterUiModel parameterModel, 
            final Can<ObjectMemento> pendingArgMementos) {
        
        val commonContext = super.getCommonContext();
        val pendingArgsList = _NullSafe.stream(pendingArgMementos)
            .map(commonContext::reconstructObject)
            .map(ManagedObject.class::cast)
            .collect(Can.toCan());
        
       return parameterModel.getPendingParamHead()
            .model(pendingArgsList);
    }

}
