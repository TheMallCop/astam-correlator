////////////////////////////////////////////////////////////////////////
//
//     Copyright (c) 2009-2014 Denim Group, Ltd.
//
//     The contents of this file are subject to the Mozilla Public License
//     Version 2.0 (the "License"); you may not use this file except in
//     compliance with the License. You may obtain a copy of the License at
//     http://www.mozilla.org/MPL/
//
//     Software distributed under the License is distributed on an "AS IS"
//     basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
//     License for the specific language governing rights and limitations
//     under the License.
//
//     The Original Code is ThreadFix.
//
//     The Initial Developer of the Original Code is Denim Group, Ltd.
//     Portions created by Denim Group, Ltd. are Copyright (C)
//     Denim Group, Ltd. All Rights Reserved.
//
//     Contributor(s): Denim Group, Ltd.
//
////////////////////////////////////////////////////////////////////////
package com.denimgroup.threadfix.framework.impl.struts;

import com.denimgroup.threadfix.data.enums.FrameworkType;
import com.denimgroup.threadfix.data.interfaces.Endpoint;
import com.denimgroup.threadfix.framework.TestConstants;
import com.denimgroup.threadfix.framework.engine.cleaner.DefaultPathCleaner;
import com.denimgroup.threadfix.framework.engine.full.EndpointDatabase;
import com.denimgroup.threadfix.framework.engine.full.EndpointDatabaseFactory;
import com.denimgroup.threadfix.framework.engine.full.EndpointQuery;
import com.denimgroup.threadfix.framework.engine.full.EndpointQueryBuilder;
import org.junit.Test;

import java.io.File;


public class StrutsEndpointMappingsTests {

    private String[][] TEST_DATA = {

        /*
         * NOTE - Testing here is incomplete; there are various methods in Roller actions ie `Setup.java`
         * that have methods not being mapped to actions, ie `save()`. These are likely meant to be mapped,
         * but there's nothing obvious in any interceptors or XML mappings that would indicate this.
         */

        {"/app/src/main/java/org/apache/roller/weblogger/ui/struts2/core/Setup.java",
                    "/roller-ui/setup.rol", "POST",
                    "frontpageBlog", "aggregated", "userCount", "blogCount"},

            {"/app/src/main/java/org/apache/roller/weblogger/ui/struts2/core/Login.java",
                    "/roller-ui/login.rol", "POST",
                    "error", "authMethod"},

            {"/app/src/main/java/org/apache/roller/weblogger/ui/struts2/core/Register.java",
                    "/roller-ui/register!save.rol", "POST",
                    "bean.openIdUrl", "servletRequest", "bean.passwordConfirm", "bean.id",
                    "bean.timeZone", "bean.locale", "bean.fullName", "bean.password",
                    "bean.passwordText", "bean.userName", "activationStatus",
                    "bean.screenName", "activationCode", "bean.emailAddress", "authMethod"},

            {"/app/src/main/java/org/apache/roller/weblogger/ui/struts2/core/Profile.java",
                    "/roller-ui/profile!save.rol", "POST",
                    "bean.openIdUrl", "bean.userName", "bean.passwordConfirm", "bean.id",
                    "bean.screenName", "bean.locale", "bean.timeZone", "bean.emailAddress",
                    "bean.fullName", "authMethod", "bean.passwordText", "bean.password"},

            {"/app/src/main/java/org/apache/roller/weblogger/ui/struts2/core/CreateWeblog.java",
                    "/roller-ui/createWeblog!save.rol", "POST",
                    "bean.name", "bean.description", "themes", "bean.timeZone",
                    "bean.locale", "bean.handle", "bean.theme", "bean.emailAddress"},

            {"/app/src/main/java/org/apache/roller/weblogger/ui/struts2/core/MainMenu.java",
                    "/roller-ui/menu!accept.rol", "POST",
                    "websiteId", "inviteId"},

            {"/app/src/main/java/org/apache/roller/weblogger/ui/struts2/core/Install.java",
                    "/roller-ui/install/install.rol", "POST",
                    "databaseName", "rootCauseStackTrace", "prop", "databaseProductName", "rootCauseException"},

            {"/app/src/main/java/org/apache/roller/weblogger/ui/struts2/admin/GlobalConfig.java",
                    "/roller-ui/admin/globalConfig!save.rol", "POST",
                    "globalConfigDef.name"},

            {"/app/src/main/java/org/apache/roller/weblogger/ui/struts2/admin/UserAdmin.java",
                    "/roller-ui/admin/userAdmin.rol", "POST",
                    "authMethod"},

            {"/app/src/main/java/org/apache/roller/weblogger/ui/struts2/admin/UserEdit.java",
                    "/roller-ui/admin/createUser!save.rol", "POST",
                    "bean.openIdUrl", "bean.userName", "bean.id", "bean.screenName",
                    "bean.activationCode", "bean.timeZone", "bean.locale", "bean.enabled",
                    "bean.fullName", "bean.emailAddress", "authMethod", "bean.password"},

            {"/app/src/main/java/org/apache/roller/weblogger/ui/struts2/admin/UserEdit.java",
                    "/roller-ui/admin/modifyUser!save.rol", "POST",
                    "bean.openIdUrl", "bean.userName", "bean.id", "bean.screenName",
                    "bean.activationCode", "bean.timeZone", "bean.locale", "bean.enabled",
                    "bean.fullName", "bean.emailAddress", "authMethod", "bean.password"},

            {"/app/src/main/java/org/apache/roller/weblogger/ui/struts2/admin/PingTargets.java",
                    "/roller-ui/admin/commonPingTargets!enable.rol", "POST",
                    "pingTarget.lastSuccess", "pingTargetId", "pingTarget.id", "pingTarget.name",
                    "pingTarget.pingUrl", "pingTarget.conditionCode"},

            {"/app/src/main/java/org/apache/roller/weblogger/ui/struts2/admin/PingTargetEdit.java",
                    "/roller-ui/admin/commonPingTargetAdd!save.rol", "POST",
                    "bean.name", "bean.id", "bean.pingUrl"},

            {"/app/src/main/java/org/apache/roller/weblogger/ui/struts2/admin/PingTargetEdit.java",
                    "/roller-ui/admin/commonPingTargetEdit!save.rol", "POST",
                    "bean.name", "bean.id", "bean.pingUrl"},

            {"/app/src/main/java/org/apache/roller/weblogger/ui/struts2/admin/CacheInfo.java",
                    "/roller-ui/admin/cacheInfo!clear.rol", "POST",
                    "cache", "stats"},

            {"/app/src/main/java/org/apache/roller/weblogger/planet/ui/PlanetConfig.java",
                    "/roller-ui/admin/planetConfig!save.rol", "POST",
                    "globalConfigDef.name", "parameters"},

            {"/app/src/main/java/org/apache/roller/weblogger/planet/ui/PlanetSubscriptions.java",
                    "/roller-ui/admin/planetSubscriptions!save.rol", "POST",
                    "group.planet.handle", "group.id", "group.handle", "group.planet.id",
                    "group.title", "group.categoryRestriction", "subUrl", "group.description",
                    "group.planet.description", "groupHandle", "group.planet.title",
                    "group.maxFeedEntries", "group.maxPageEntries"},

            {"/app/src/main/java/org/apache/roller/weblogger/planet/ui/PlanetGroups.java",
                    "/roller-ui/admin/planetGroups!save.rol", "POST",
                    "bean.title", "group.title", "bean.id", "group.description",
                    "group.planet.description", "bean.handle", "group.planet.title",
                    "group.maxFeedEntries", "group.planet.handle", "group.id", "group.handle",
                    "group.planet.id", "group.categoryRestriction", "group.maxPageEntries"},

            {"/app/src/main/java/org/apache/roller/weblogger/ui/struts2/editor/CategoryEdit.java",
                    "/roller-ui/authoring/categoryAdd!save.rol", "POST",
                    "bean.image", "bean.name", "bean.description", "bean.id"},

            {"/app/src/main/java/org/apache/roller/weblogger/ui/struts2/editor/CategoryEdit.java",
                    "/roller-ui/authoring/categoryEdit!save.rol", "POST",
                    "bean.image", "bean.name", "bean.description", "bean.id"},

            {"/app/src/main/java/org/apache/roller/weblogger/ui/struts2/editor/FolderEdit.java",
                    "/roller-ui/authoring/folderAdd!save.rol", "POST",
                    "bean.name", "bean.id", "folderId"},

            {"/app/src/main/java/org/apache/roller/weblogger/ui/struts2/editor/FolderEdit.java",
                    "/roller-ui/authoring/folderEdit!save.rol", "POST",
                    "bean.name", "bean.id", "folderId"},

            {"/app/src/main/java/org/apache/roller/weblogger/ui/struts2/editor/WeblogConfig.java",
                    "/roller-ui/authoring/weblogConfig!save.rol", "POST",
                    "editorsList", "bean.icon", "weblogCategories", "bean.handle", "bean.locale",
                    "bean.timeZone", "bean.tagline", "bean.editorPage", "bean.entryDisplayCount",
                    "bean.name", "bean.defaultAllowComments", "bean.analyticsCode", "bean.emailAddress",
                    "bean.bloggerCategoryId", "bean.blacklist", "bean.allowComments", "pluginsList",
                    "bean.emailComments", "bean.commentModerationRequired", "bean.enableBloggerApi",
                    "bean.defaultCommentDays", "bean.about", "bean.moderateComments", "bean.active",
                    "bean.applyCommentDefaults"},

            {"/app/src/main/java/org/apache/roller/weblogger/ui/struts2/editor/ThemeEdit.java",
                    "/roller-ui/authoring/themeEdit!save.rol", "POST",
                    "themeId", "themeType", "selectedThemeId"},

            {"/app/src/main/java/org/apache/roller/weblogger/ui/struts2/editor/Templates.java",
                    "/roller-ui/authoring/templates!add.rol", "POST",
                    "newTmplName", "newTmplAction"},

            {"/app/src/main/java/org/apache/roller/weblogger/ui/struts2/editor/TemplatesRemove.java",
                    "/roller-ui/authoring/templatesRemove!remove.rol", "POST",
                    "ids"},

            {"/app/src/main/java/org/apache/roller/weblogger/ui/struts2/editor/Members.java",
                    "/roller-ui/authoring/members!save.rol", "POST",
                    "parameter", "parameters"},

            {"/app/src/main/java/org/apache/roller/weblogger/ui/struts2/editor/MembersInvite.java",
                    "/roller-ui/authoring/invite!save.rol", "POST",
                    "permissionString", "userName"},

            {"/app/src/main/java/org/apache/roller/weblogger/ui/struts2/editor/Pings.java",
                    "/roller-ui/authoring/pings!enable.rol", "POST",
                    "pingStatus", "pingTarget.lastSuccess", "pingTargetId", "pingTarget.id",
                    "pingTarget.name", "pingTarget.pingUrl", "pingTarget.conditionCode"},

            {"/app/src/main/java/org/apache/roller/weblogger/ui/struts2/editor/MediaFileAdd.java",
                    "/roller-ui/authoring/overlay/mediaFileAdd!save.rol", "POST",
                    "bean.contentType", "bean.thumbnailURL", "bean.width","bean.permalink", "bean.id",
                    "bean.height", "bean.description", "bean.tagsAsString", "bean.name", "bean.length",
                    "bean.copyrightText", "bean.directoryId", "bean.originalPath", "directoryName"}
    };

    @Test
    public void testRoller() {
        /*
         * This test (and `testRollerFrameworkType`) fail since endpoints no longer list all parameters
         * discovered in an action - they are culled based on which parameters are actually referenced
         * in the action. For this case, the 'frontpageBlog' parameter is not found. It could be found
         * if Struts parsing included support for Tiles, which would map the `Setup` action to the
         * appropriate `webapp/WEB-INF/jsps/core/Setup.jsp` page. This page has references to 'frontpageBlog'
         * and includes a form that POSTs to the correct 'save' method on the action (where the 'save'
         * method also references 'frontpageBlog'.
         *
         * This test cannot pass with the current requirements without adding support for Struts Tiles
         * (at least for display files) or removing reference-based parameter culling.
         */
        File rootFile = new File(TestConstants.ROLLER_SOURCE_LOCATION);
        StrutsEndpointMappings mappings = new StrutsEndpointMappings(rootFile);

        EndpointDatabase database = EndpointDatabaseFactory.getDatabase(
                mappings,
                FrameworkType.STRUTS,
                new DefaultPathCleaner("", ""));

        test(database);
    }

    @Test
    public void testRollerFrameworkType() {
        File rootFile = new File(TestConstants.ROLLER_SOURCE_LOCATION);

        // test with EndpointDatabaseFactory finding the FrameworkType
        EndpointDatabase database = EndpointDatabaseFactory.getDatabase(rootFile);

        test(database);
    }

    @Test
    public void testRegisterUrl() {
        File rootFile = new File(TestConstants.ROLLER_SOURCE_LOCATION);
        EndpointDatabase database = EndpointDatabaseFactory.getDatabase(rootFile);

        EndpointQueryBuilder epqBuilder = EndpointQueryBuilder.start();

        epqBuilder.setDynamicPath("/roller-ui/register!save.rol");
        epqBuilder.setParameter("bean.userName");

        EndpointQuery endpointQuery = epqBuilder.generateQuery();

        Endpoint bestMatch = database.findBestMatch(endpointQuery);

        assert bestMatch.getFilePath().endsWith("Register.java") :
                "Endpoint didn't have file 'Register.java', had " + bestMatch.getFilePath();

        assert bestMatch.getHttpMethod().equals("POST") :
                "Endpoint didn't have HTTP method 'POST', had " + bestMatch.getHttpMethod();

        assert bestMatch.getParameters().keySet().contains("bean.userName") :
                "Endpoint didn't have parameter 'bean.userName', had " + bestMatch.getParameters();

    }

    private void test(EndpointDatabase edb) {
        for (String[] endpointTest : TEST_DATA) {
            String testFileName = endpointTest[0];
            String testUrl = endpointTest[1];
            String testMethod = endpointTest[2];
            String[] testParams = null;
            if (endpointTest.length > 3) {
                testParams = new String[endpointTest.length - 3];
                for (int i = 0; i < testParams.length; i++) {
                    testParams[i] = endpointTest[i + 3];
                }
            }
            test(edb, testFileName, testUrl, testMethod, testParams);
        }
    }

    private void test(EndpointDatabase database, String fileName, String url, String method, String[] parameters) {

        EndpointQuery endpointQuery = EndpointQueryBuilder.start().setDynamicPath(url).setHttpMethod(method).generateQuery();

        Endpoint bestMatch = database.findBestMatch(endpointQuery);

        assert bestMatch != null : "No match found for url " + url;

        assert bestMatch.getFilePath().equals(fileName) :
                "Endpoint didn't match fileName " + fileName + ", got " + bestMatch.getFilePath() + " instead.";

        assert bestMatch.getHttpMethod().equals(method) :
                "Endpoint didn't have HTTP method " + method + ", had " + bestMatch.getHttpMethod();

        if (parameters != null) {
            for (String parameter : parameters) {
                assert bestMatch.getParameters().keySet().contains(parameter) : "Parameters didn't contain " + parameter;
            }
        }
    }
}
