package com.denimgroup.threadfix.framework.impl.dotNet.classParsers;

import com.denimgroup.threadfix.framework.impl.dotNet.DotNetControllerParser;
import com.denimgroup.threadfix.framework.impl.dotNet.classDefinitions.CSharpClass;
import com.denimgroup.threadfix.logging.SanitizedLogger;

import java.util.Set;

import static com.denimgroup.threadfix.CollectionUtils.set;

public class OldControllerParser {

    //final List<DotNetClass> classes;
    CSharpClass pendingClass;
    CSharpClass finishedClass;

    CSharpAttributeParser attributeParser;
    CSharpMethodParser methodParser;
    CSharpScopeTracker scopeTracker;

    private boolean disabled;

    public static final SanitizedLogger LOG = new SanitizedLogger(DotNetControllerParser.class);

    public static final Set<String> DOT_NET_BUILTIN_CONTROLLERS = set(
        "ApiController", "Controller", "HubController", "HubControllerBase", "AsyncController", "BaseController"
    );
//
//    public OldControllerParser() {
//        currentClass = new DotNetClass();
//        classes = list(currentClass);
//
//        this.methodParser = methodParser;
//        this.attributeParser = attributeParser;
//        this.scopeTracker = scopeTracker;
//    }
//
//    @Override
//    public boolean shouldContinue() {
//        return true;
//    }
//
//    enum State {
//        START, NAMESPACE, OPEN_BRACKET, AREA, CONTROLLER_BASE_ROUTE, ACTION_ROUTE, PUBLIC, CLASS, TYPE_SIGNATURE, BODY, PUBLIC_IN_BODY, ACTION_RESULT, IACTION_RESULT, IN_ACTION_SIGNATURE, AFTER_BIND_INCLUDE, DEFAULT_VALUE, IN_ACTION_BODY
//    }
//
//    enum AttributeState {
//        START, OPEN_BRACKET, OPEN_PAREN, STRING, AREA
//    }
//
//    enum ParameterState {
//        START, REQUEST, REQUEST_INDEXER, SESSION, SESSION_INDEXER, FILES, QUERY_STRING, QUERY_STRING_INDEXER, COOKIES, COOKIES_INDEXER
//    }
//
//
//
//    public void disable() {
//        disabled = true;
//    }
//
//    public void enable() {
//        disabled = false;
//    }
//
//
//
//    @Override
//    public void processToken(int type, int lineNumber, String stringValue) {
//        if (disabled) {
//            return;
//        }
//
//        processMainThread(type, lineNumber, stringValue);
//        processAttributes(type, stringValue);
//        processRequestDataReads(type, stringValue);
//    }
//
//    private void processMainThread(int type, int lineNumber, String stringValue) {
//
//        switch (type) {
//            case '{':
//                currentCurlyBrace += 1;
//                break;
//            case '}':
//                currentCurlyBrace -= 1;
//                break;
//            case '(':
//                currentParen += 1;
//                break;
//            case ')':
//                currentParen -= 1;
//                break;
//        }
//
//        switch (currentState) {
//            case START:
//                if (NAMESPACE.equals(stringValue)) {
//                    currentState = DotNetControllerParser.State.NAMESPACE;
//                }else if(PUBLIC.equals(stringValue)){
//                    currentState = DotNetControllerParser.State.PUBLIC;
//                }
//                break;
//            case NAMESPACE:
//                if(PUBLIC.equals(stringValue)){
//                    currentState = DotNetControllerParser.State.PUBLIC;
//                }else if( type == '['){
//                    currentState = DotNetControllerParser.State.OPEN_BRACKET;
//                }else {
//                    if (currentNamespace == null) {
//                        currentNamespace = "";
//                    }
//                    if (currentCurlyBrace == 0)
//                        currentNamespace += CodeParseUtil.buildTokenString(type, stringValue);
//                }
//                break;
//            case OPEN_BRACKET:
//                if(stringValue != null && AREA.equalsIgnoreCase(stringValue)) {
//                    currentState = DotNetControllerParser.State.AREA;
//                } else if (stringValue != null && ROUTE.equals(stringValue)) {
//                    if (currentCurlyBrace == 1) {
//                        currentState = DotNetControllerParser.State.CONTROLLER_BASE_ROUTE;
//                    } else {
//                        currentState = DotNetControllerParser.State.ACTION_ROUTE;
//                    }
//                } else if(type == ']'){
//                    currentState = DotNetControllerParser.State.NAMESPACE;
//                }
//                break;
//            case AREA:
//                if(PUBLIC.equals(stringValue)){
//                    currentState = DotNetControllerParser.State.PUBLIC;
//                } else if(stringValue != null && type != '(' && type != ')'){
//                    currentMapping.setAreaName(stringValue);
//                    currentState = DotNetControllerParser.State.START;
//                }
//                break;
//            case CONTROLLER_BASE_ROUTE:
//                if (type == ']') {
//                    currentState = DotNetControllerParser.State.START;
//                } else if (type == '"' && stringValue != null) {
//                    controllerBaseRoute = stringValue;
//                    currentState = DotNetControllerParser.State.START;
//                }
//                break;
//            case ACTION_ROUTE:
//                if (type == ']') {
//                    currentState = DotNetControllerParser.State.START;
//                } else if (type == '"' && stringValue != null) {
//                    explicitActionRoute = stringValue;
//                    currentState = DotNetControllerParser.State.START;
//                }
//                break;
//            case PUBLIC:
//                currentState = CLASS.equals(stringValue) ?
//                    DotNetControllerParser.State.CLASS :
//                    DotNetControllerParser.State.START;
//                break;
//            case CLASS:
//                if (stringValue != null && stringValue.endsWith("Controller") &&
//                    // Make sure we're not parsing internal ASP.NET MVC controller classes
//                    !DOT_NET_BUILTIN_CONTROLLERS.contains(stringValue)) {
//                    String controllerName = stringValue.substring(0, stringValue.indexOf("Controller"));
//                    LOG.debug("Got Controller name " + controllerName);
//                    currentMapping.setControllerName(controllerName);
//                    currentMapping.setNamespace(currentNamespace);
//                }
//
//                currentState = DotNetControllerParser.State.TYPE_SIGNATURE;
//                break;
//            case TYPE_SIGNATURE:
//                if (type == '{') {
//                    currentState = DotNetControllerParser.State.BODY;
//                    classBraceLevel = currentCurlyBrace - 1;
//                }
//                break;
//            case BODY:
//                if (classBraceLevel == currentCurlyBrace) {
//                    currentMapping = new DotNetControllerMappings(mappings.get(0).getFilePath());
//                    currentMapping.setNamespace(currentNamespace);
//
//                    mappings.add(currentMapping);
//                    currentState = DotNetControllerParser.State.NAMESPACE;
//                    currentAttributeState = DotNetControllerParser.AttributeState.START;
//                    currentParameterState = DotNetControllerParser.ParameterState.START;
//
//                    currentAttributes.clear();
//                    lastAttribute = null;
//                    methodName = null;
//                    parametersWithTypes.clear();
//                    possibleParamType = null;
//                    controllerBaseRoute = null;
//                    explicitActionRoute = null;
//                } else if (PUBLIC.equals(stringValue)) {
//                    currentState = DotNetControllerParser.State.PUBLIC_IN_BODY;
//                } else if (methodBraceLevel <= 0) {
//                    methodBraceLevel = classBraceLevel + 1;
//                }
//                break;
//            case PUBLIC_IN_BODY:
//                if (RESULT_TYPES.contains(stringValue) || hasHttpAttribute()) {
//                    currentState = DotNetControllerParser.State.ACTION_RESULT;
//                } else if (type == '(' || type == ';' || type == '{') {
//                    currentState = DotNetControllerParser.State.BODY;
//                    currentAttributes.clear();
//                }
//                break;
//            case ACTION_RESULT:
//                if (stringValue != null) {
//                    lastString = stringValue;
//                } else if (type == '(') {
//                    assert lastString != null;
//
//                    methodName = lastString;
//                    lastString = null;
//                    methodLineNumber = lineNumber;
//                    storedParen = currentParen - 1;
//                    lastString = null;
//                    currentState = DotNetControllerParser.State.IN_ACTION_SIGNATURE;
//                }
//
//                break;
//            case IN_ACTION_SIGNATURE:
//                if (stringValue == null) {
//                    if (type == ',' || type == ')' && lastString != null) {
//                        if (isValidParameterName(lastString)) {
//                            String name, dataType;
//                            if (wasDefaultValue) {
//                                name = twoStringsAgo;
//                                dataType = threeStringsAgo;
//                            } else {
//                                name = lastString;
//                                dataType = twoStringsAgo;
//                            }
//
//                            RouteParameter param = new RouteParameter(name);
//                            param.setDataType(dataType);
//                            if (actionSignatureParamAttr != null) {
//                                if (actionSignatureParamAttr.equals(FROM_BODY)) {
//                                    param.setParamType(RouteParameterType.FORM_DATA);
//                                }
//                            }
//
//                            parametersWithTypes.add(param);
//                        }
//                        if (twoStringsAgo.equals("Include")) {
//                            currentState = DotNetControllerParser.State.AFTER_BIND_INCLUDE;
//                        }
//
//                        actionSignatureParamAttr = null;
//
//                        wasDefaultValue = false;
//                    } else if (type == '=' && !"Include".equals(lastString)) {
//                        currentState = DotNetControllerParser.State.DEFAULT_VALUE;
//                    } else if (type == '[') {
//                        isActionSignatureParamAttr = true;
//                    }
//                } else if (isActionSignatureParamAttr) {
//                    isActionSignatureParamAttr = false;
//                    actionSignatureParamAttr = stringValue;
//                } else if (lastString != null && lastString.equals("Include")) {
//                    String paramNames = CodeParseUtil.trim(stringValue, "\"");
//                    String[] paramNameParts = StringUtils.split(paramNames, ',');
//
//                    for (String paramName : paramNameParts) {
//                        paramName = paramName.trim();
//                        RouteParameter param = new RouteParameter(paramName);
//                        param.setParamType(RouteParameterType.FORM_DATA);
//                        parametersWithTypes.add(param);
//                    }
//                }
//
//                if (currentParen == storedParen) {
//                    currentState = DotNetControllerParser.State.IN_ACTION_BODY;
//                    methodBraceLevel = currentCurlyBrace;
//                }
//                break;
//            case DEFAULT_VALUE:
//                wasDefaultValue = true;
//                if (stringValue != null) {
//                    currentState = DotNetControllerParser.State.IN_ACTION_SIGNATURE;
//                }
//                break;
//            case AFTER_BIND_INCLUDE:
//                if (type == ',') {
//                    currentState = DotNetControllerParser.State.IN_ACTION_SIGNATURE;
//                }
//
//                if (type == ')' && currentParen == storedParen) {
//                    currentState = DotNetControllerParser.State.IN_ACTION_BODY;
//                    methodBraceLevel = currentCurlyBrace;
//                }
//                break;
//            case IN_ACTION_BODY:
//                if (currentCurlyBrace == methodBraceLevel) {
//                    if (controllerBaseRoute != null)
//                        explicitActionRoute = PathUtil.combine(controllerBaseRoute, explicitActionRoute);
//                    currentMapping.addAction(
//                        methodName, currentAttributes, methodLineNumber,
//                        lineNumber, parametersWithTypes, explicitActionRoute);
//                    currentAttributes = set();
//                    parametersWithTypes = set();
//                    methodName = null;
//                    explicitActionRoute = null;
//                    currentState = DotNetControllerParser.State.BODY;
//                }
//                break;
//        }
//
//    }
//
//    private void processAttributes(int type, String stringValue) {
//        if (currentState == DotNetControllerParser.State.BODY && currentCurlyBrace == methodBraceLevel) {
//            switch (currentAttributeState) {
//                case START:
//                    if (type == '[') {
//                        currentAttributeState = DotNetControllerParser.AttributeState.OPEN_BRACKET;
//                    }
//                    break;
//                case OPEN_BRACKET:
//                    if (stringValue != null) {
//                        lastAttribute = stringValue;
//                        currentAttributeState = DotNetControllerParser.AttributeState.STRING;
//                    }
//                    break;
//                case OPEN_PAREN:
//                    if (isHttpAttribute(lastAttribute) && explicitActionRoute == null && type == '"') {
//                        explicitActionRoute = stringValue;
//                    } else if (type == ')' || type == ']') {
//                        currentAttributeState = DotNetControllerParser.AttributeState.START;
//                    }
//                case STRING:
//                    boolean addAttribute = false;
//                    if (type == ']') {
//                        currentAttributeState = DotNetControllerParser.AttributeState.START;
//                        addAttribute = true;
//                    }
//
//                    if (type == ',') {
//                        addAttribute = true;
//                        currentAttributeState = DotNetControllerParser.AttributeState.OPEN_BRACKET;
//                    }
//
//                    if (type == '(') {
//                        addAttribute = true;
//                        currentAttributeState = DotNetControllerParser.AttributeState.OPEN_PAREN;
//                    }
//
//                    if (addAttribute) {
//                        LOG.debug("Adding " + lastAttribute);
//                        currentAttributes.add(lastAttribute);
//                    }
//                    break;
//            }
//        }
//    }
//
//    private void processRequestDataReads(int type, String stringValue) {
//        if (currentState != DotNetControllerParser.State.IN_ACTION_BODY) {
//            return;
//        }
//
//        switch (currentParameterState) {
//            case START:
//                possibleParamType = null;
//
//                if ("Request.Files".equals(stringValue)) {
//                    RouteParameter param = new RouteParameter("[File Data]");
//                    param.setParamType(RouteParameterType.FILES);
//                    parametersWithTypes.add(param);
//                } else if ("Request.Cookies".equals(stringValue)) {
//                    currentParameterState = DotNetControllerParser.ParameterState.COOKIES;
//                } else if ("Request.QueryString".equals(stringValue)) {
//                    currentParameterState = DotNetControllerParser.ParameterState.QUERY_STRING;
//                } else if ("Request".equals(stringValue)) {
//                    currentParameterState = DotNetControllerParser.ParameterState.REQUEST;
//                } else if ("Session".equals(stringValue)) {
//                    currentParameterState = DotNetControllerParser.ParameterState.SESSION;
//                }
//
//                if (currentParameterState != DotNetControllerParser.ParameterState.START) {
//                    if (twoStringsAgo != null && ParameterDataType.getType(twoStringsAgo).getDisplayName() != null) {
//                        possibleParamType = twoStringsAgo;
//                    } else if (lastString != null && ParameterDataType.getType(lastString).getDisplayName() != null) {
//                        possibleParamType = lastString;
//                    } else {
//                        possibleParamType = null;
//                    }
//                }
//                break;
//
//            case REQUEST:
//                if (type == '[') {
//                    currentParameterState = DotNetControllerParser.ParameterState.REQUEST_INDEXER;
//                } else {
//                    currentParameterState = DotNetControllerParser.ParameterState.START;
//                }
//                break;
//
//            case REQUEST_INDEXER:
//                if (type == '"' && stringValue != null) {
//                    RouteParameter param = new RouteParameter(stringValue);
//                    param.setParamType(RouteParameterType.UNKNOWN);
//                    param.setDataType(possibleParamType);
//                    currentParameterState = DotNetControllerParser.ParameterState.START;
//                } else {
//                    currentParameterState = DotNetControllerParser.ParameterState.START;
//                }
//                break;
//
//            case QUERY_STRING:
//                if (type == '[') {
//                    currentParameterState = DotNetControllerParser.ParameterState.QUERY_STRING_INDEXER;
//                } else {
//                    currentParameterState = DotNetControllerParser.ParameterState.START;
//                }
//                break;
//
//            case QUERY_STRING_INDEXER:
//                if (stringValue != null && type == '"') {
//                    RouteParameter param = new RouteParameter(stringValue);
//                    param.setDataType(possibleParamType);
//                    param.setParamType(RouteParameterType.QUERY_STRING);
//                    parametersWithTypes.add(param);
//                }
//                currentParameterState = DotNetControllerParser.ParameterState.START;
//                break;
//
//            case COOKIES:
//                if (type == '[') {
//                    currentParameterState = DotNetControllerParser.ParameterState.COOKIES_INDEXER;
//                } else {
//                    currentParameterState = DotNetControllerParser.ParameterState.START;
//                }
//                break;
//
//            case COOKIES_INDEXER:
//                if (type == '"' && stringValue != null) {
//                    RouteParameter param = new RouteParameter(stringValue);
//                    param.setDataType(possibleParamType);
//                    param.setParamType(RouteParameterType.COOKIE);
//                    parametersWithTypes.add(param);
//                }
//
//                currentParameterState = DotNetControllerParser.ParameterState.START;
//                break;
//
//            case SESSION:
//                if (type == '[') {
//                    currentParameterState = DotNetControllerParser.ParameterState.SESSION_INDEXER;
//                } else {
//                    currentParameterState = DotNetControllerParser.ParameterState.START;
//                }
//                break;
//
//            case SESSION_INDEXER:
//                if (type == '"' && stringValue != null) {
//                    RouteParameter param = new RouteParameter(stringValue);
//                    param.setDataType(possibleParamType);
//                    param.setParamType(RouteParameterType.SESSION);
//                    parametersWithTypes.add(param);
//                }
//                currentParameterState = DotNetControllerParser.ParameterState.START;
//                break;
//        }
//    }
//
//    private boolean isHttpAttribute(String attributeName) {
//        return
//            "HttpGet".equals(attributeName) ||
//                "HttpPost".equals(attributeName) ||
//                "HttpPatch".equals(attributeName) ||
//                "HttpPut".equals(attributeName) ||
//                "HttpDelete".equals(attributeName);
//    }
//
//    private boolean hasHttpAttribute() {
//        for (String attr : currentAttributes) {
//            if (isHttpAttribute(attr)) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//    private boolean isValidParameterName(String name) {
//        for (int i = 0; i < name.length(); i++) {
//            char c = name.charAt(i);
//            if (c < 48) {
//                return false;
//            }
//            if (c > 57 && c < 65) {
//                return false;
//            }
//            if (c > 90 && c < 97 && c != '_') {
//                return false;
//            }
//            if (c > 122) {
//                return false;
//            }
//        }
//        return true;
//    }
}