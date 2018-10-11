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
//     Contributor(s):
//              Denim Group, Ltd.
//              Secure Decisions, a division of Applied Visions, Inc
//
////////////////////////////////////////////////////////////////////////
package com.denimgroup.threadfix.framework.util;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.util.Collection;
import java.util.List;

import static com.denimgroup.threadfix.CollectionUtils.list;

public class FilePathUtils {

    private FilePathUtils(){}

    @Nonnull
    public static String normalizePath(@Nonnull String filePath) {
    	return filePath.replace('\\', '/');
    }

    @Nullable
    public static String getRelativePath(@Nullable File projectFile, @Nullable File rootFile) {
        String returnPath = null;

        if (projectFile != null && rootFile != null) {
            returnPath = getRelativePath(projectFile.getAbsolutePath(), rootFile.getAbsolutePath());
        }

        return returnPath;
    }

    @Nullable
    public static String getRelativePath(@Nullable String projectFileString, @Nullable File projectRootFile) {
        String returnPath = null;

        if (projectFileString != null && projectRootFile != null) {
            returnPath = getRelativePath(projectFileString, normalizePath(projectRootFile.getAbsolutePath()));
        }

        return returnPath;
    }

    @Nullable
    public static String getRelativePath(@Nullable File projectFile, @Nullable String projectRoot) {
        String returnPath = null;

        if (projectFile != null && projectRoot != null) {
            returnPath = getRelativePath(normalizePath(projectFile.getAbsolutePath()), projectRoot);
        }

        return returnPath;
    }

    @Nullable
    public static String getRelativePath(@Nullable String string, @Nullable String projectRoot) {
        String returnPath = null;

        if (string != null && projectRoot != null &&
                string.startsWith(projectRoot)) {
            returnPath = string.substring(projectRoot.length());
            returnPath = normalizePath(returnPath);
        }

        return returnPath;
    }

    @Nonnull
    public static String getFolder(@Nonnull File file) {
        return file.getAbsoluteFile().getParentFile().getAbsolutePath();
    }

    //  Filters all folders that are contained within another
    public static List<File> findRootFolders(@Nonnull Collection<File> folders) {
        //  Remove project locations that are sub-folders of another
        List<File> filteredResults = list();
        for (File current : folders) {
            String currentPath = current.getAbsolutePath();
            boolean include = true;
            for (File check : folders) {
                if (check.equals(current)) {
                    continue;
                }
                String checkPath = check.getAbsolutePath();
                if (currentPath.startsWith(checkPath)) {
                    include = false;
                    break;
                }
            }
            if (include) {
                filteredResults.add(current);
            }
        }
        return filteredResults;
    }

}
