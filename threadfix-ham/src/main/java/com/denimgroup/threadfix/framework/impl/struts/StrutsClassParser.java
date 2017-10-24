package com.denimgroup.threadfix.framework.impl.struts;

import com.denimgroup.threadfix.framework.impl.struts.annotationParsers.*;
import com.denimgroup.threadfix.framework.impl.struts.conventions.Convention;
import com.denimgroup.threadfix.framework.impl.struts.model.StrutsClass;
import com.denimgroup.threadfix.framework.impl.struts.model.StrutsMethod;
import com.denimgroup.threadfix.framework.impl.struts.model.annotations.ActionAnnotation;
import com.denimgroup.threadfix.framework.impl.struts.model.annotations.Annotation;
import com.denimgroup.threadfix.framework.util.EventBasedTokenizerRunner;
import com.denimgroup.threadfix.logging.SanitizedLogger;

import java.io.File;
import java.util.*;

import static com.denimgroup.threadfix.CollectionUtils.list;

public class StrutsClassParser {

    static SanitizedLogger LOG = new SanitizedLogger(StrutsClassParser.class.getName());

    ActionAnnotationParser actionParser = new ActionAnnotationParser();
    NamespaceAnnotationParser namespaceParser = new NamespaceAnnotationParser();
    ParentPackageAnnotationParser parentPackageParser = new ParentPackageAnnotationParser();
    ResultAnnotationParser resultParser = new ResultAnnotationParser();
    ResultPathAnnotationParser resultPathParser = new ResultPathAnnotationParser();
    StrutsClassMethodParser methodParser = new StrutsClassMethodParser();


    StrutsClass resultClass;


    public StrutsClassParser(File file) {

        EventBasedTokenizerRunner.run(file, true, actionParser, namespaceParser, parentPackageParser, resultParser, resultPathParser, methodParser);
        //EventBasedTokenizerRunner.run(file, true, actionParser);

        String className = methodParser.getParsedClassName();
        resultClass = new StrutsClass(className, file.getAbsolutePath());
        resultClass.addAllMethods(methodParser.getParsedMethods());



        List<Annotation> allAnnotations = new ArrayList<Annotation>();
        allAnnotations.addAll(actionParser.getAnnotations());
        allAnnotations.addAll(namespaceParser.getAnnotations());
        allAnnotations.addAll(parentPackageParser.getAnnotations());
        allAnnotations.addAll(resultParser.getAnnotations());
        allAnnotations.addAll(resultPathParser.getAnnotations());

        if (allAnnotations.size() == 0) {
            return;
        }

        registerClassAnnotations(allAnnotations);


        //  Some annotations have sub-annotations as parameters, these will have been parsed
        //      and attached to either the parent method or class, AND will have been
        //      parsed as children of the parent annotations. Collect the annotations
        //      that are members of another annotation, and remove them from the global
        //      annotation collection.

        List<Annotation> childAnnotations = list();
        for (Annotation genericAction : actionParser.getAnnotations()) {
            ActionAnnotation action = (ActionAnnotation)genericAction;
            childAnnotations.addAll(action.getResults());
        }

        allAnnotations.removeAll(childAnnotations);

        //  Assign annotations to their attached methods

        //  Generate method map
        Map<String, StrutsMethod> methodNameMap = new HashMap<String, StrutsMethod>();
        for (StrutsMethod method : methodParser.getParsedMethods()) {
            String methodName = method.getName();
            if (methodNameMap.containsKey(methodName)) {
                LOG.debug("Multiple methods named " + methodName + " were found in \"" + file.getAbsolutePath() + "\", only the first will be used.");
            } else {
                methodNameMap.put(methodName, method);
            }
        }

        for (Annotation annotation : allAnnotations) {

            if (annotation.getTargetType() != Annotation.TargetType.METHOD) {
                continue;
            }

            String targetName = annotation.getTargetName();
            if (!methodNameMap.containsKey(targetName)) {
                LOG.debug("A " + annotation.getClass().getName() + " annotation was attached to "
                        + className + "." + targetName + ", but no parsed method could be found with that name.");
            } else {
                StrutsMethod attachedMethod = methodNameMap.get(targetName);
                attachedMethod.addAnnotation(annotation);
            }
        }
    }

    public StrutsClass getResultClass() {
        return resultClass;
    }




    void registerClassAnnotations(Collection<Annotation> annotations) {
        for (Annotation annotation : annotations) {
            if (annotation.getTargetType() == Annotation.TargetType.CLASS) {
                resultClass.addAnnotation(annotation);
            }
        }
    }
}
