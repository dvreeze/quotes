/*
 * Copyright 2024-2024 Chris de Vreeze
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.cdevreeze.quotes.appconfig.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.*;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.servlet.*;
import org.springframework.web.servlet.mvc.annotation.ModelAndViewResolver;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Objects;

/**
 * ApplicationContext event listener that shows (groups of) beans created by the Spring container.
 *
 * @author Chris de Vreeze
 */
@Component
public class IntrospectingContextEventListener {

    private final Logger logger = LoggerFactory.getLogger(IntrospectingContextEventListener.class);

    @EventListener
    public void handleContextEvent(ApplicationContextEvent contextEvent) {
        switch (contextEvent) {
            case ContextRefreshedEvent ev -> handleContextRefreshedEvent(ev);
            case ContextStartedEvent ev -> handleContextStartedEvent(ev);
            case ContextStoppedEvent ev -> handleContextStoppedEvent(ev);
            case ContextClosedEvent ev -> handleContextClosedEvent(ev);
            default -> throw new IllegalStateException("Unexpected value: " + contextEvent);
        }
    }

    private void handleContextRefreshedEvent(ContextRefreshedEvent contextRefreshedEvent) {
        logger.info("ApplicationContext refreshed. Showing beans created in the Spring container.");
        showBeans(contextRefreshedEvent.getApplicationContext());
    }

    private void handleContextStartedEvent(ContextStartedEvent contextStartedEvent) {
        logger.info("ApplicationContext started.");
    }

    private void handleContextStoppedEvent(ContextStoppedEvent contextStoppedEvent) {
        logger.info("ApplicationContext stopped.");
    }

    private void handleContextClosedEvent(ContextClosedEvent contextClosedEvent) {
        logger.info("ApplicationContext closed.");
    }

    private void showBeans(ApplicationContext appContext) {
        showComponents(appContext);
        showDispatcherServlets(appContext);
        showBeanFactoryPostProcessors(appContext);
        showBeanPostProcessors(appContext);
        showApplicationContextAware(appContext);
    }

    private void showComponents(ApplicationContext appContext) {
        logger.info("------ Showing Configuration(-meta)-annotated beans ------");
        showBeansAnnotatedWith(Configuration.class, appContext);
        logger.info("------ Showing Controller(-meta)-annotated beans ------");
        showBeansAnnotatedWith(Controller.class, appContext);
        logger.info("------ Showing Service(-meta)-annotated beans ------");
        showBeansAnnotatedWith(Service.class, appContext);
        logger.info("------ Showing Repository(-meta)-annotated beans ------");
        showBeansAnnotatedWith(Repository.class, appContext);
        logger.info("------ Showing all Component(-meta)-annotated beans ------");
        showBeansAnnotatedWith(Component.class, appContext);
    }

    private void showDispatcherServlets(ApplicationContext appContext) {
        logger.info("------ Showing DispatcherServlet beans with their dependencies ------");
        var dispatcherServlets = getSingletonBeansOfType(DispatcherServlet.class, appContext);

        for (DispatcherServlet servlet : dispatcherServlets.values()) {
            showDispatcherServlet(servlet);
        }
    }

    private void showDispatcherServlet(DispatcherServlet servlet) {
        WebApplicationContext appContext = Objects.requireNonNull(servlet.getWebApplicationContext());

        logger.info("------ Showing one DispatcherServlet bean with its dependencies ------");
        logger.info(String.format("DispatcherServlet name: %s", servlet.getServletName()));

        logger.info(String.format("DispatcherServlet bean(s): %s", getSingletonBeansOfType(DispatcherServlet.class, appContext)));

        logger.info(String.format("DispatcherServlet's ApplicationContext display name: %s", appContext.getDisplayName()));

        if (appContext.getParent() == null) {
            logger.info("No parent ApplicationContext (so this is the root context)");
        } else {
            logger.info(String.format("Parent ApplicationContext display name: %s", appContext.getParent().getDisplayName()));
        }

        showSingletonBeansOfType(HandlerMapping.class, appContext);
        showSingletonBeansOfType(HandlerAdapter.class, appContext);
        showSingletonBeansOfType(HandlerExceptionResolver.class, appContext);
        showSingletonBeansOfType(ViewResolver.class, appContext);
        showSingletonBeansOfType(RequestToViewNameTranslator.class, appContext);
        showSingletonBeansOfType(MultipartResolver.class, appContext);
        showSingletonBeansOfType(LocaleResolver.class, appContext);
        logger.info("---- Showing HttpMessageConverter beans (potentially used by RequestMappingHandlerAdapter) ----");
        showSingletonBeansOfType(HttpMessageConverter.class, appContext);
        logger.info("---- Showing ModelAndViewResolver beans (potentially used by RequestMappingHandlerAdapter) ----");
        showSingletonBeansOfType(ModelAndViewResolver.class, appContext);
    }

    private void showBeanFactoryPostProcessors(ApplicationContext appContext) {
        logger.info("------ Showing BeanFactoryPostProcessor beans ------");

        showSingletonBeansOfType(BeanFactoryPostProcessor.class, appContext);
    }

    private void showBeanPostProcessors(ApplicationContext appContext) {
        logger.info("------ Showing BeanPostProcessor beans ------");

        showSingletonBeansOfType(BeanPostProcessor.class, appContext);
    }

    private void showApplicationContextAware(ApplicationContext appContext) {
        logger.info("------ Showing ApplicationContextAware beans ------");

        showSingletonBeansOfType(ApplicationContextAware.class, appContext);
    }

    private <T extends Annotation> void showBeansAnnotatedWith(Class<T> annotationType, ApplicationContext appContext) {
        var beans = appContext.getBeansWithAnnotation(annotationType);

        for (Map.Entry<String, Object> kv : beans.entrySet()) {
            logger.info(String.format(
                    "%s bean '%s' of bean type %s",
                    annotationType.getSimpleName(),
                    kv.getKey(),
                    kv.getValue()));
        }
    }

    private <T> void showSingletonBeansOfType(Class<T> tpe, ApplicationContext appContext) {
        var beans = getSingletonBeansOfType(tpe, appContext);

        for (Map.Entry<String, T> kv : beans.entrySet()) {
            logger.info(String.format("%s bean '%s' of type %s", tpe.getSimpleName(), kv.getKey(), kv.getValue()));
        }
    }

    private <T> Map<String, T> getSingletonBeansOfType(Class<T> tpe, ApplicationContext appContext) {
        // Returns only top-level beans, not nested ones
        return appContext.getBeansOfType(tpe, false, false);
    }
}
