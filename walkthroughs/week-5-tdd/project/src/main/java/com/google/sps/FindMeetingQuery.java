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

    ArrayList<TimeRange> timeSlots = new ArrayList<TimeRange>();
    Collection<TimeRange> timeSlotsWithOptionalAttendees = new ArrayList<TimeRange>();
    // Find event that are attended by at least one of our people
    Collection<String> attendees = request.getAttendees();
    Collection<String> optionalAttendees = request.getOptionalAttendees();
    
    Collection<Event> relevantEventsForMandatoryAttendees = new HashSet<>();
    Collection<Event> relevantEventsForOptionalAttendees = new HashSet<>();

    for (Event e : events) {
      if (!Collections.disjoint(e.getAttendees(), attendees)) {
          relevantEventsForMandatoryAttendees.add(e);  
      }
      else if (!Collections.disjoint(e.getAttendees(), optionalAttendees)) {
          relevantEventsForOptionalAttendees.add(e);
      }
    }


    if (relevantEventsForMandatoryAttendees.size() == 0 && relevantEventsForOptionalAttendees.size() == 0){
        return Arrays.asList(TimeRange.WHOLE_DAY);
    }

    //ArrayList<TimeRange> relevantEventsTimeRange = new ArrayList<TimeRange>();
    ArrayList<TimeRange> relevantEventsForMandatoryAttendeesTimeRange = new ArrayList<TimeRange>();
    ArrayList<TimeRange> relevantEventsForOptionalAttendeesTimeRange = new ArrayList<TimeRange>();
    for (Event e: relevantEventsForMandatoryAttendees){
        relevantEventsForMandatoryAttendeesTimeRange.add(e.getWhen());
    }

    for (Event e: relevantEventsForOptionalAttendees){
        relevantEventsForOptionalAttendeesTimeRange.add(e.getWhen());
    }

    //return queryOptional(relevantEventsForMandatoryAttendeesTimeRange, relevantEventsForOptionalAttendeesTimeRange, request.getDuration());

   timeSlots = queryHelper(relevantEventsForMandatoryAttendeesTimeRange, request.getDuration());
   if (optionalAttendees.size() != 0){
    timeSlotsWithOptionalAttendees = queryOptional(relevantEventsForOptionalAttendeesTimeRange, timeSlots, request.getDuration());
    if (timeSlotsWithOptionalAttendees.size() != 0) return timeSlotsWithOptionalAttendees;
   }
    
    return timeSlots;
  }

  // return free time ranges for all the mandatory attendees
  private ArrayList<TimeRange> queryHelper(ArrayList<TimeRange> relevantEventsTimeRange, long meetingDuration) {

    ArrayList<TimeRange> results = new ArrayList<TimeRange>();


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

 // return free time ranges for mandatory and optional attendees if any
 private Collection<TimeRange> queryOptional(ArrayList<TimeRange> relevantEventsForOptionalAttendeesTimeRange, ArrayList<TimeRange> timeSlots, long meetingDuration) {

    Collection<TimeRange> timeSlotsWithOptionalAttendees = new ArrayList<TimeRange>();

    if (timeSlots.size() == 0) return timeSlotsWithOptionalAttendees;
    
    Collections.sort(relevantEventsForOptionalAttendeesTimeRange, TimeRange.ORDER_BY_START);

    int i = 0;
    int j = 0;
    int start = 0;
    int end = relevantEventsForOptionalAttendeesTimeRange.get(0).start();


    while (true){
      if (j == timeSlots.size()) break;
    
      TimeRange tempTR = TimeRange.fromStartEnd(start, end, false);
      if (tempTR.contains(timeSlots.get(j))) timeSlotsWithOptionalAttendees.add(timeSlots.get(j));
      else if (timeSlots.get(j).contains(tempTR) && tempTR.duration() >= meetingDuration) timeSlotsWithOptionalAttendees.add(tempTR);
      else {
          TimeRange intersectionTR = TimeRange.fromStartEnd(Math.max(start, timeSlots.get(j).start()), Math.min(end, timeSlots.get(j).end()), false);
          if (intersectionTR.duration() >= meetingDuration) timeSlotsWithOptionalAttendees.add(intersectionTR);
      }

      if (i == relevantEventsForOptionalAttendeesTimeRange.size()) break;


      if (timeSlots.get(j).end() <= relevantEventsForOptionalAttendeesTimeRange.get(i).end() || end == TimeRange.END_OF_DAY + 1){
        j++;
      }
      else if (timeSlots.get(j).end() >= relevantEventsForOptionalAttendeesTimeRange.get(i).end()){
        start = relevantEventsForOptionalAttendeesTimeRange.get(i).end();
        if (i+1 == relevantEventsForOptionalAttendeesTimeRange.size()) end = TimeRange.END_OF_DAY + 1;
        else {
            end = relevantEventsForOptionalAttendeesTimeRange.get(i+1).start();
            i++;
        }
      }

    }
    return timeSlotsWithOptionalAttendees;
  }
}
