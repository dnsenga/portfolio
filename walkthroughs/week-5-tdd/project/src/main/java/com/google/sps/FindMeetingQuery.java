// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
// 
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps;
 
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.HashSet;

public final class FindMeetingQuery {
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    // handle outside cases
    if (request.getDuration() > TimeRange.WHOLE_DAY.duration()) {
          return Arrays.asList();
    }

    if (request.getAttendees().size() == 0){
        return Arrays.asList(TimeRange.WHOLE_DAY);
    }

    Collection<TimeRange> results = new ArrayList<TimeRange>();
    // Find event that are attended by at least one of our people
    Collection<String> attendees = request.getAttendees();
    Collection<String> optionalAttendees = request.getOptionalAttendees();
    
    Collection<Event> relevantEvents = new HashSet<>();
    Collection<Event> relevantEventsForMandatoryAttendees = new HashSet<>();

    for (Event e : events) {
      if (!Collections.disjoint(e.getAttendees(), attendees)) {
          relevantEvents.add(e);
          relevantEventsForMandatoryAttendees.add(e);  
      }
      else if (!Collections.disjoint(e.getAttendees(), optionalAttendees)) {
          relevantEvents.add(e);
      }
    }

    if (relevantEvents.size() == 0){
        return Arrays.asList(TimeRange.WHOLE_DAY);
    }

    ArrayList<TimeRange> relevantEventsTimeRange = new ArrayList<TimeRange>();
    ArrayList<TimeRange> relevantEventsForMandatoryAttendeesTimeRange = new ArrayList<TimeRange>();
    for (Event e: relevantEvents){
        relevantEventsTimeRange.add(e.getWhen());
    }

    for (Event e: relevantEventsForMandatoryAttendees){
        relevantEventsForMandatoryAttendeesTimeRange.add(e.getWhen());
    }

    results = queryHelper(relevantEventsTimeRange, request.getDuration());

    if (results.size() != 0) return results;

    return queryHelper(relevantEventsForMandatoryAttendeesTimeRange, request.getDuration());


    
  }

  private Collection<TimeRange> queryHelper(ArrayList<TimeRange> relevantEventsTimeRange, long meetingDuration) {

    Collection<TimeRange> results = new ArrayList<TimeRange>();


    Collections.sort(relevantEventsTimeRange, TimeRange.ORDER_BY_START);
    TimeRange firstWindow = TimeRange.fromStartEnd(TimeRange.START_OF_DAY, relevantEventsTimeRange.get(0).start(), false);

    if (firstWindow.duration() >= meetingDuration){
        results.add(firstWindow);
    }

    int currentEnd = relevantEventsTimeRange.get(0).end();
    for (int i = 1; i < relevantEventsTimeRange.size(); i++){
        if (currentEnd < relevantEventsTimeRange.get(i).start() ) {
            TimeRange tempTimeRange = TimeRange.fromStartEnd(currentEnd, relevantEventsTimeRange.get(i).start(), false);

            if (tempTimeRange.duration() >= meetingDuration){
                results.add(tempTimeRange);
            }
        }
        if (relevantEventsTimeRange.get(i).end() > currentEnd) currentEnd = relevantEventsTimeRange.get(i).end();
    }

    Collections.sort(relevantEventsTimeRange, TimeRange.ORDER_BY_END);
    TimeRange lastWindow = TimeRange.fromStartEnd(relevantEventsTimeRange.get(relevantEventsTimeRange.size()-1).end(), TimeRange.END_OF_DAY, true);

    if (lastWindow.duration() >= meetingDuration){
        results.add(lastWindow);
    }

    return results;
  }
}
