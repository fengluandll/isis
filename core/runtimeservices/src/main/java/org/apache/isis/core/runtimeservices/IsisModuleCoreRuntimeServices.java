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
package org.apache.isis.core.runtimeservices;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import org.apache.isis.core.codegen.bytebuddy.IsisModuleCoreCodegenByteBuddy;
import org.apache.isis.core.runtime.IsisModuleCoreRuntime;
import org.apache.isis.core.runtimeservices.auth.AuthenticationSessionProviderDefault;
import org.apache.isis.core.runtimeservices.bookmarks.BookmarkServiceInternalDefault;
import org.apache.isis.core.runtimeservices.command.CommandDtoServiceInternalDefault;
import org.apache.isis.core.runtimeservices.command.CommandExecutorServiceDefault;
import org.apache.isis.core.runtimeservices.command.CommandServiceDefault;
import org.apache.isis.core.runtimeservices.confmenu.ConfigurationViewServiceDefault;
import org.apache.isis.core.runtimeservices.email.EmailServiceDefault;
import org.apache.isis.core.runtimeservices.eventbus.EventBusServiceSpring;
import org.apache.isis.core.runtimeservices.exceprecog.ExceptionRecognizerServiceDefault;
import org.apache.isis.core.runtimeservices.factory.FactoryServiceDefault;
import org.apache.isis.core.runtimeservices.homepage.HomePageResolverServiceDefault;
import org.apache.isis.core.runtimeservices.i18n.po.TranslationServicePo;
import org.apache.isis.core.runtimeservices.i18n.po.TranslationServicePoMenu;
import org.apache.isis.core.runtimeservices.ixn.InteractionDtoServiceInternalDefault;
import org.apache.isis.core.runtimeservices.menubars.MenuBarsLoaderServiceDefault;
import org.apache.isis.core.runtimeservices.menubars.bootstrap3.MenuBarsServiceBS3;
import org.apache.isis.core.runtimeservices.message.MessageServiceDefault;
import org.apache.isis.core.runtimeservices.publish.PublisherDispatchServiceDefault;
import org.apache.isis.core.runtimeservices.repository.RepositoryServiceDefault;
import org.apache.isis.core.runtimeservices.routing.RoutingServiceDefault;
import org.apache.isis.core.runtimeservices.sessmgmt.SessionManagementServiceDefault;
import org.apache.isis.core.runtimeservices.sudo.SudoServiceDefault;
import org.apache.isis.core.runtimeservices.userprof.UserProfileServiceDefault;
import org.apache.isis.core.runtimeservices.userreg.EmailNotificationServiceDefault;
import org.apache.isis.core.runtimeservices.wrapper.WrapperFactoryDefault;
import org.apache.isis.core.runtimeservices.xactn.TransactionServiceSpring;
import org.apache.isis.core.runtimeservices.xmlsnapshot.XmlSnapshotServiceDefault;

@Configuration
@Import({
        // modules
        IsisModuleCoreRuntime.class,
        IsisModuleCoreCodegenByteBuddy.class,

        // @Service's
        AuthenticationSessionProviderDefault.class,
        BookmarkServiceInternalDefault.class,
        CommandDtoServiceInternalDefault.class,
        CommandExecutorServiceDefault.class,
        CommandServiceDefault.class,
        ConfigurationViewServiceDefault.class,
        EmailNotificationServiceDefault.class,
        EmailServiceDefault.class,
        ExceptionRecognizerServiceDefault.class,
        EventBusServiceSpring.class,
        FactoryServiceDefault.class,
        HomePageResolverServiceDefault.class,
        InteractionDtoServiceInternalDefault.class,
        TranslationServicePo.class,
        MenuBarsLoaderServiceDefault.class,
        MenuBarsServiceBS3.class,
        MessageServiceDefault.class,
        PublisherDispatchServiceDefault.class,
        SessionManagementServiceDefault.class,
        SudoServiceDefault.class,
        TransactionServiceSpring.class,
        UserProfileServiceDefault.class,
        WrapperFactoryDefault.class,
        XmlSnapshotServiceDefault.class,

        // @Controller
        RoutingServiceDefault.class,

        // @Repository's
        RepositoryServiceDefault.class,

        // @DomainService's
        TranslationServicePoMenu.class,
})
public class IsisModuleCoreRuntimeServices {

}