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

package eu.cdevreeze.quotes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Quotes application main program.
 * <p>
 * The web application can be run with:
 * <pre>java -jar application/target/quotes-0.0.1-SNAPSHOT.jar</pre>
 * This only works after the build has performed a "mvnw spring-boot:repackage" command.
 * <p>
 * The application wiring has been done in a somewhat disciplined way. First note that the Spring Boot
 * SpringBootApplication annotation is itself annotated with the Spring ComponentScan annotation.
 * This makes it easy to end up with Spring wiring that is hard to understand. In this small application,
 * however, despite the (implicit) use of component scanning, the wiring is easy to understand,
 * because of the following. First of all, the package structure follows "application layering",
 * thus preventing the occurrence of circular package dependencies (inspired by Spring code itself).
 * Secondly, component scanning is done (in a compile-time safe way) for only a few packages: the web
 * package/layer, service layer and repository layer.
 * <p>
 * In the web layer there are mostly controllers and other beans annotated or meta-annotated with the
 * Component annotation, but hardly any of them are Configuration-annotated ones. Component scanning
 * (scanning the web package) will find them, and most annotations in controllers will be interpreted by
 * RequestMappingHandlerMapping and RequestMappingHandlerAdapter beans used by a/the DispatcherServlet.
 * So the wiring for the web layer is a bit implicit, but relatively clear when reasoning from
 * the DispatcherServlet (and its plugged in handler mappings/adapters etc.), given the fact that
 * the web package is included for component scanning.
 * <p>
 * The wiring for the service (and below that repository) layer is simpler, in that there are fewer
 * beans, and they are found by component scanning as well.
 * <p>
 * Note that the repositories and services are annotated with Repository and Service annotations,
 * respectively. Hence, they are meta-annotated with the Component annotation, and their packages are included for
 * component scanning, so component scanning finds them.
 * <p>
 * The SpringBootApplication annotation is itself annotated not only with the ComponentScan annotation,
 * but also with the SpringBootConfiguration annotation (and therefore indirectly with the Configuration
 * and Component annotations). Finally, besides ComponentScan and SpringBootConfiguration, the
 * SpringBootApplication annotation is itself also annotated with the Spring Boot EnableAutoConfiguration
 * annotation. That latter annotation seems a rather "mystical" one, guessing from the classpath which
 * beans to instantiate and configure, but backing away where own configurations are defined.
 * It is less mystical than it seems. The EnableAutoConfiguration annotation enables beans annotated
 * with the AutoConfiguration annotation (and therefore indirectly the Configuration annotation). Consider
 * for example the DataSourceAutoConfiguration bean for autoconfiguration of a JDBC DataSource. This bean
 * is annotated with the AutoConfiguration annotation, but also with annotations such as ConditionalOnClass.
 * Looking at many of those AutoConfiguration-annotated beans this autoconfiguration system makes a lot
 * of sense for providing defaults for much of the wiring (for non-application classes), but it does
 * depend a lot on the classpath, so discipline is needed to keep the classpath (and therefore POM file)
 * clean.
 * <p>
 * Spring Boot AutoConfiguration is explained very well in
 * <a href="https://www.marcobehler.com/guides/spring-boot-autoconfiguration">Spring Boot AutoConfiguration</a>.
 *
 * @author Chris de Vreeze
 */
@SpringBootApplication(scanBasePackageClasses = {
        eu.cdevreeze.quotes.web.ScanMe.class,
        eu.cdevreeze.quotes.service.ScanMe.class,
        eu.cdevreeze.quotes.repository.ScanMe.class,
        eu.cdevreeze.quotes.springeventlistener.ScanMe.class
})
public class QuotesApplication {

    public static void main(String[] args) {
        SpringApplication.run(QuotesApplication.class, args);
    }

}
