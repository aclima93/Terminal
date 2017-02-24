/***
 Copyright (c) 2008-2011 CommonsWare, LLC
 Licensed under the Apache License, Version 2.0 (the "License"); you may not
 use this file except in compliance with the License. You may obtain a copy
 of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
 by applicable law or agreed to in writing, software distributed under the
 License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 OF ANY KIND, either express or implied. See the License for the specific
 language governing permissions and limitations under the License.

 From _Tuning Android Applications_
 http://commonsware.com/AndTuning
 */

package pt.uc.student.aclima.device_agent.Collectors.PeriodicDataCollector.Utilities.traffic;

import android.content.Context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class TrafficMonitor{

    private TrafficSnapshot latest=null;
    private TrafficSnapshot previous=null;

    public List<String> takeSnapshot(Context context) {

        previous = latest;

        latest=new TrafficSnapshot(context);

        List<String> rows= new ArrayList<>();
        HashSet<Integer> intersection= new HashSet<>(latest.apps.keySet());

        if (previous!=null) {
            intersection.retainAll(previous.apps.keySet());
        }

        for (Integer uid : intersection) {
            TrafficRecord latest_rec = latest.apps.get(uid);
            TrafficRecord previous_rec = (previous==null ? null : previous.apps.get(uid));

            emitLog(latest_rec, previous_rec, rows);
        }

        Collections.sort(rows);

        return rows;
    }

    private void emitLog(TrafficRecord latest_rec, TrafficRecord previous_rec, List<String> rows) {

        if (latest_rec.rx>-1 || latest_rec.tx>-1) {
            StringBuilder buf = new StringBuilder(latest_rec.tag);

            buf.append("=");
            buf.append(String.valueOf(latest_rec.rx));
            buf.append(" bytes received");

            if (previous_rec!=null) {
                buf.append(" (delta=");
                buf.append(String.valueOf(latest_rec.rx - previous_rec.rx));
                buf.append(")");
            }

            buf.append(", ");
            buf.append(String.valueOf(latest_rec.tx));
            buf.append(" bytes sent");

            if (previous_rec!=null) {
                buf.append(" (delta=");
                buf.append(String.valueOf(latest_rec.tx - previous_rec.tx));
                buf.append(")");
            }

            rows.add(buf.toString());
        }
    }
}