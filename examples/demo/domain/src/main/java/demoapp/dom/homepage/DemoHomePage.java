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
package demoapp.dom.homepage;

import java.time.LocalDateTime;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.HomePage;
import org.apache.isis.applib.annotation.Nature;
import org.apache.isis.applib.services.user.UserService;
import org.apache.isis.applib.util.JaxbAdapters.MarkupAdapter;
import org.apache.isis.applib.value.Markup;
import org.apache.isis.valuetypes.asciidoc.applib.value.AsciiDoc;

import lombok.Getter;
import lombok.Setter;

import demoapp.dom._infra.AsciiDocReaderService;
import demoapp.dom._infra.HasAsciiDocDescription;

@DomainObject(
        nature=Nature.VIEW_MODEL
        , objectType = "demoapp.Homepage"
)
@HomePage
public class DemoHomePage implements HasAsciiDocDescription {

    public String title() {
        return "Hello, " + userService.getUser().getName();
    }

    public AsciiDoc getWelcome() {
        return asciiDocReaderService.readFor(this, "welcome");
    }

    @Inject
    UserService userService;

    @Inject
    AsciiDocReaderService asciiDocReaderService;
}
