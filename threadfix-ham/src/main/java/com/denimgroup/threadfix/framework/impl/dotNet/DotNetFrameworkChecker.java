////////////////////////////////////////////////////////////////////////
//
//     Copyright (c) 2009-2015 Denim Group, Ltd.
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
package com.denimgroup.threadfix.framework.impl.dotNet;

import com.denimgroup.threadfix.data.enums.FrameworkType;
import com.denimgroup.threadfix.framework.engine.ProjectDirectory;
import com.denimgroup.threadfix.framework.engine.framework.FrameworkChecker;
import com.denimgroup.threadfix.framework.filefilter.FileExtensionFileFilter;
import com.denimgroup.threadfix.framework.util.EventBasedTokenizer;
import com.denimgroup.threadfix.framework.util.EventBasedTokenizerRunner;
import com.denimgroup.threadfix.logging.SanitizedLogger;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;

import javax.annotation.Nonnull;
import java.io.File;
import java.util.Collection;

/**
 * Created by mac on 6/17/14.
 */
public class DotNetFrameworkChecker extends FrameworkChecker {

    private static final SanitizedLogger LOG = new SanitizedLogger(DotNetFrameworkChecker.class);

    @Nonnull
    @Override
    public FrameworkType check(@Nonnull ProjectDirectory directory) {

        Collection<File> configFiles = FileUtils.listFiles(directory.getDirectory(), new String[] { "config", "csproj" }, true);

        MvcNamespaceParser parser = new MvcNamespaceParser();
        for (File configFile : configFiles) {
            EventBasedTokenizerRunner.run(configFile, parser);
            if (parser.isMvc) {
                break;
            }
        }

        return parser.isMvc ? FrameworkType.DOT_NET_MVC : FrameworkType.NONE;
    }

    static class MvcNamespaceParser implements EventBasedTokenizer {

        private static String MVC_NAMESPACE = "System.Web.Mvc";
        private static String MVC_CORE_NAMESPACE = "Microsoft.AspNetCore.Mvc";

        boolean isMvc = false;

        @Override
        public boolean shouldContinue() {
            return !isMvc;
        }

        @Override
        public void processToken(int type, int lineNumber, String stringValue) {
            if (stringValue != null && (stringValue.contains(MVC_NAMESPACE) || stringValue.contains(MVC_CORE_NAMESPACE))) {
                isMvc = true;
            }
        }
    }

}
