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

package org.apache.isis.viewer.wicket.ui.components.scalars;

import java.io.Serializable;

import org.apache.wicket.Component;
import org.apache.wicket.extensions.yui.calendar.DatePicker;
import org.apache.wicket.markup.html.form.AbstractTextComponent;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;

import org.apache.isis.core.metamodel.adapter.ObjectAdapter;
import org.apache.isis.viewer.wicket.model.models.ScalarModel;

/**
 * Panel for rendering scalars representing dates, along with a date picker.
 */
public abstract class ScalarPanelTextFieldDatePickerAbstract<T extends Serializable> extends ScalarPanelTextFieldAbstract<T> {

    private static final long serialVersionUID = 1L;

    private final DateConverter<T> converter;

    public ScalarPanelTextFieldDatePickerAbstract(final String id, final ScalarModel scalarModel, final Class<T> cls, DateConverter<T> converter) {
        super(id, scalarModel, cls);
        this.converter = converter;
    }
    
    protected TextField<T> createTextField(final String id) {
        return new TextFieldWithDateConverter<T>(id, new TextFieldValueModel<T>(this), cls, converter);
    }

    @Override
    protected void addSemantics() {
        super.addSemantics();

        final DatePicker datePicker = new DatePicker(){
            private static final long serialVersionUID = 1L;

            @Override
            protected String getAdditionalJavaScript()
            {
                return "${calendar}.cfg.setProperty(\"navigator\",true,false); ${calendar}.render();";
            }
            @Override
            protected String getDatePattern() {
                return converter.getDatePattern(getLocale());
            }
        };
        datePicker.setShowOnFieldClick(true);
        datePicker.setAutoHide(true);
        getTextField().add(datePicker);

        addObjectAdapterValidator();
    }

    protected Component addComponentForCompact() {
        final AbstractTextComponent<T> textField = createTextField(ID_SCALAR_IF_COMPACT);
        final IModel<T> model = textField.getModel();
        final T object = (T) model.getObject();
        model.setObject(object);
        
        textField.setEnabled(false);
        setTextFieldSize(textField, converter.getDateTimePattern(getLocale()).length());
        
        addOrReplace(textField);
        return textField;
    }

    private void addObjectAdapterValidator() {
        final AbstractTextComponent<T> textField = getTextField();

        textField.add(new IValidator<T>() {
            private static final long serialVersionUID = 1L;

            @Override
            public void validate(final IValidatable<T> validatable) {
                final T proposed = validatable.getValue();
                final ObjectAdapter proposedAdapter = adapterFor(proposed);
                String reasonIfAny = scalarModel.validate(proposedAdapter);
                if (reasonIfAny != null) {
                    final ValidationError error = new ValidationError();
                    error.setMessage(reasonIfAny);
                    validatable.error(error);
                }
            }
        });
    }

    private ObjectAdapter adapterFor(final Object pojo) {
        return getAdapterManager().adapterFor(pojo);
    }
}
