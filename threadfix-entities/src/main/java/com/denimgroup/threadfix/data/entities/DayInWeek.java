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

package com.denimgroup.threadfix.data.entities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by zabdisubhan on 8/14/14.
 */

public enum DayInWeek {
    MON("Monday"),
    TUE("Tuesday"),
    WED("Wednesday"),
    THU("Thursday"),
    FRI("Friday"),
    SAT("Saturday"),
    SUN("Sunday");

    private String day;

    public String getDay() {
        return this.day;
    }

    private DayInWeek(String day) {
        this.day = day;
    }

    public static DayInWeek getDay(String keyword) {
        for (DayInWeek t: values()) {
            if (keyword != null && keyword.equalsIgnoreCase(t.getDay())) {
                return t;
            }
        }
        return null;
    }


    public static List<String> getDayInWeekDescriptions(){
        ArrayList<String> descriptions = new ArrayList<String>();
        for(DayInWeek dayInWeek : DayInWeek.values()){
            descriptions.add(dayInWeek.getDay());
        }
        return descriptions;
    }

}