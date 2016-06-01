package de.roamingthings.cditemplate.control;

import de.roamingthings.cditemplate.GStringTemplate;
import groovy.text.GStringTemplateEngine;
import groovy.text.Template;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.Annotated;
import javax.enterprise.inject.spi.InjectionPoint;
import java.io.IOException;
import java.lang.reflect.Member;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * @author alxs
 * @version 2016/05/18
 */
@Stateless
public class GStringTemplateProducer {
    private static Logger log = Logger.getLogger(GStringTemplateProducer.class.getName());

    private GStringTemplateEngine templateEngine;

    @PostConstruct
    public void init() {
        templateEngine = new GStringTemplateEngine();
    }

    @Produces
    @GStringTemplate
    public Template generateTemplate(InjectionPoint injectionPoint) {
        Template template = null;

        final Annotated annotated = injectionPoint.getAnnotated();
        final GStringTemplate annotation = annotated.getAnnotation(GStringTemplate.class);

        if (annotation != null) {
            String resourcePath = annotation.resourcePath();
            String templateValue = annotation.value();

            if (!resourcePath.isEmpty() && !templateValue.isEmpty()) {
                throw new IllegalStateException("Ambiguous configuration. Please specify either `resourcePath` or `value`.");
            }

            if (!resourcePath.isEmpty()) {
                try {
                    final Member member = injectionPoint.getMember();
                    final Class<?> declaringClass = member.getDeclaringClass();
                    final URL templateResource = declaringClass.getResource(resourcePath);
                    if (templateResource != null) {
                        template = templateEngine.createTemplate(templateResource);
                    }
                } catch (ClassNotFoundException | IOException e) {
                    log.log(Level.WARNING, String.format("Error when creating template for resource <%s>: ", resourcePath), e);
                }
            } else if (!templateValue.isEmpty()) {
                try {
                    template = templateEngine.createTemplate(templateValue);
                } catch (ClassNotFoundException | IOException e) {
                    log.log(Level.WARNING, String.format("Error when creating template <%s>: ", templateValue), e);
                }
            }
        }

        // Return an empty string template if there is no template or an error has occurred
        if (template == null) {
            try {
                template = templateEngine.createTemplate("");
            } catch (Exception e) {
                throw new IllegalStateException("Could not create empty template: ", e);
            }
        }

        return template;
    }
}
