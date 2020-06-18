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
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public final class FindMeetingQuery {
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    //Return Collection
    Collection<TimeRange> answer = new ArrayList<TimeRange>();
    
    //Get requested meeting attendees 
    Collection<String> requestAttendees = request.getAttendees();
    long requestDuration = request.getDuration();
    
    if (requestDuration > 24 * 60) {
      return answer;
    }
    
    //Filter events to only consider those with relevant attendees
    Iterator<Event> eventsIterator = events.iterator(); 
    List<TimeRange> relevantEventTimes = new ArrayList<TimeRange>();
    while (eventsIterator.hasNext()) {
      Event event = eventsIterator.next(); 
      Set<String> eventAttendees = new HashSet<String>(event.getAttendees());
      eventAttendees.retainAll(requestAttendees);
      if(eventAttendees.size() > 0) {
        relevantEventTimes.add(event.getWhen());
      }
    }
    
    if(relevantEventTimes.size() == 0) {
      answer.add(TimeRange.WHOLE_DAY);
      return answer;
    }

    //Sort list of relevant event time ranges by start time
    Collections.sort(relevantEventTimes, TimeRange.ORDER_BY_START);

    //Initialize variables used during linear scan 
    int combinedRangeStartTime = relevantEventTimes.get(0).start();
    int combinedRangeEndTime = relevantEventTimes.get(0).end();
    
    //Process the first available timeRange from start of day to start of first event
    if (validTimeRange(TimeRange.START_OF_DAY, combinedRangeStartTime, requestDuration)) {
        answer.add(TimeRange.fromStartEnd(TimeRange.START_OF_DAY, combinedRangeStartTime, false));
    }
    
    //Linear scan of relevant time ranges starting from the end
    for (TimeRange currentTimeRange : relevantEventTimes) {
      int currentStartTime = currentTimeRange.start();
      int currentEndTime = currentTimeRange.end();
      if (currentStartTime <= combinedRangeEndTime) {
        if (currentEndTime > combinedRangeEndTime) {
          //|----combinedRange---|
          //             |----currentRange----|
          //extend combinedRange end time:
          //|---------combinedRange-----------|
          combinedRangeEndTime = currentEndTime;
        }
      } else {
        if(validTimeRange(combinedRangeEndTime, currentStartTime, requestDuration)) {
            answer.add(TimeRange.fromStartEnd(combinedRangeEndTime, currentStartTime, false));
        }
        combinedRangeStartTime = currentStartTime;
        combinedRangeEndTime = currentEndTime;
      }
    }
    
    //Process the last available timeRange from end of last event to end of day.

    if (validTimeRange(combinedRangeEndTime, TimeRange.END_OF_DAY, requestDuration)) {
        answer.add(TimeRange.fromStartEnd(combinedRangeEndTime, TimeRange.END_OF_DAY, true));
    }
    
    return answer;
  }
  
  /**
   * Helper function to check if a time range is valid, given meeting request
   * duration
   * 
   */
  
  private boolean validTimeRange(int startTime, int endTime, long requestDuration) {
      return (endTime - startTime >= requestDuration);
  }
}
