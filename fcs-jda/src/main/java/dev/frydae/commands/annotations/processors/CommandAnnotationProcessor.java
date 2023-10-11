package dev.frydae.commands.annotations.processors;

import com.google.auto.service.AutoService;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import dev.frydae.commands.annotations.SkipAnnotationProcessor;
import dev.frydae.commands.annotations.CommandAlias;
import dev.frydae.commands.annotations.Description;
import dev.frydae.commands.annotations.Name;
import dev.frydae.commands.annotations.Subcommand;
import dev.frydae.commands.CommandUtils;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.tools.Diagnostic;
import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@SupportedAnnotationTypes({
    "dev.frydae.commands.annotations.CommandAlias",
    "dev.frydae.commands.annotations.Subcommand"
})
@SupportedSourceVersion(SourceVersion.RELEASE_17)
@AutoService(Processor.class)
public class CommandAnnotationProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment env) {
        Map<Class<? extends Annotation>, Element> invalidElements = Maps.newHashMap();

        List<ElementKind> typeKinds = Collections.singletonList(ElementKind.CLASS);
        // let's gather all types we are interested in
        List<Element> commandElements = env
                .getElementsAnnotatedWithAny(Sets.newHashSet(CommandAlias.class))
                .stream()
                .filter(e -> typeKinds.contains(e.getKind()))
                .filter(e -> e.getAnnotation(SkipAnnotationProcessor.class) == null)
                .collect(Collectors.toList());

        commandElements.forEach(c -> checkElementForAnnotation(invalidElements, c, Description.class));

        processCommandMethods(env, invalidElements);

        for (Map.Entry<Class<? extends Annotation>, Element> entry : invalidElements.entrySet()) {
            String message = String.format("\n%s %s must be annotated with @%s", CommandUtils.ucfirst(entry.getValue().getKind().name()), entry.getValue().getSimpleName(), entry.getKey().getSimpleName());

            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, message, entry.getValue());
        }

        return false;
    }

    private void processCommandMethods(RoundEnvironment env, Map<Class<? extends Annotation>, Element> invalidElements) {
        List<ElementKind> typeKinds = Collections.singletonList(ElementKind.METHOD);
        // let's gather all types we are interested in
        List<ExecutableElement> commandElements = env
                .getElementsAnnotatedWithAny(Sets.newHashSet(CommandAlias.class, Subcommand.class))
                .stream()
                .filter(e -> typeKinds.contains(e.getKind()))
                .filter(e -> e.getAnnotation(SkipAnnotationProcessor.class) == null)
                .filter(ExecutableElement.class::isInstance)
                .map(e -> (ExecutableElement) e)
                .collect(Collectors.toList());

        for (ExecutableElement commandElement : commandElements) {
            processMethod(invalidElements, commandElement);
            processParameters(invalidElements, commandElement);
        }
    }

    private void processMethod(Map<Class<? extends Annotation>, Element> invalidElements, ExecutableElement commandElement) {
        checkElementForAnnotation(invalidElements, commandElement, Description.class);
    }

    private void processParameters(Map<Class<? extends Annotation>, Element> invalidElements, ExecutableElement commandElement) {
        for (VariableElement parameter : commandElement.getParameters()) {
            checkElementForAnnotation(invalidElements, parameter, Name.class);
            checkElementForAnnotation(invalidElements, parameter, Description.class);
        }
    }

    private void checkElementForAnnotation(Map<Class<? extends Annotation>, Element> invalidElements, Element element, Class<? extends Annotation> annoClass) {
        if (element.getAnnotation(annoClass) == null) {
            invalidElements.put(annoClass, element);
        }
    }
}
