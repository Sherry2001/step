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
    
    Collection<TimeRange> answer = new ArrayList<>();

    Collection<String> requestAttendees = new ArrayList<String>(request.getAttendees());
    requestAttendees.addAll(request.getOptionalAttendees());
    long requestDuration = request.getDuration();
    
    //Get events that have mandatory attendees also in the Meeting Requested
    List<TimeRange> relevantEventTimes = filterEvents(events, requestAttendees);

    System.out.println(relevantEventTimes);
    findPossibleTimes(answer, relevantEventTimes, requestDuration);
    
    //If accommodating optional attendees isn't possible, only consider mandatory attendees
    if (answer.size() == 0 && request.getAttendees().size() > 0) {
      requestAttendees = request.getAttendees();  
      relevantEventTimes = filterEvents(events, requestAttendees);
      findPossibleTimes(answer, relevantEventTimes, requestDuration);
    }
    
    return answer;
  }
 
  /**
   * Helper function that updates a list of available times for the meeting.
   * 
   * @param answer- list containing available times
   * @param relevantEventTimes- events during the day to plan around
   * @param requestDuration- how long the request meeting is
   */
  private void findPossibleTimes(Collection<TimeRange> answer, List<TimeRange> relevantEventTimes, long requestDuration) {      
    Collections.sort(relevantEventTimes, TimeRange.ORDER_BY_START);
    
    //Variables used to resolve time range overlaps during linear scan 
    int combinedRangeEndTime = TimeRange.START_OF_DAY;    
  
    //Linear scan to assess all possible time slots between and before events
    for (TimeRange currentTimeRange : relevantEventTimes) {
      int currentStartTime = currentTimeRange.start();
      int currentEndTime = currentTimeRange.end();
      
      if (currentStartTime <= combinedRangeEndTime) {
        combinedRangeEndTime = Math.max(currentEndTime, combinedRangeEndTime);
      } else {
        addPossibleTimeRange(answer, combinedRangeEndTime, currentStartTime, requestDuration, false);
        combinedRangeEndTime = currentEndTime;
      }
      
      //Process the timeRange from end of last event to end of day, or the entire day if there are no events
      addPossibleTimeRange(answer, combinedRangeEndTime, TimeRange.END_OF_DAY, requestDuration, true);
  }
  
  /**
   * Helper function to filter through the events given and only return the time ranges 
   * for events that have attendees in the MeetingRequest's attendees list.
   * 
   * @param events
   * @param requestAttendees
   * @return List of TimeRange for events that have attendees who are also in the requested meeting
   */
  private List<TimeRange> filterEvents(Collection<Event> events, Collection<String> requestAttendees) {
    List<TimeRange> relevantEventTimes = new ArrayList<TimeRange>();
    for (Event event: events) {
      Set<String> eventAttendees = new HashSet<String>(event.getAttendees());
      eventAttendees.retainAll(requestAttendees);
      if(!eventAttendees.isEmpty()) {
        relevantEventTimes.add(event.getWhen());
      }
    }
    return relevantEventTimes;
  }
  
  /**
   * Helper function to check if a time range is long enough for the requested duration
   * @param startTime
   * @param endTime
   * @param requestDuration
   * @return true or false, whether or not this time works 
   */
  private boolean validTimeRange(int startTime, int endTime, long requestDuration) {
<<<<<<< HEAD
    return (endTime - startTime >= requestDuration);
=======
    return endTime - startTime >= requestDuration;
>>>>>>> 6e6c225a863901834a80e80b5c168aeb004dcf42
  }
  
  /**
   * Helper to update the list of possible time ranges by creating a new TimeRange
   * if a given time range is longer than the requested duration
   *  
   * @param answer 
   * @param startTime
   * @param endTime
   * @param requestDuration
   * @param inclusive
   */
  private void addPossibleTimeRange(Collection<TimeRange> answer, int startTime, int endTime, 
          long requestDuration, boolean inclusive) {
    if (validTimeRange(startTime, endTime, requestDuration)) {
      answer.add(TimeRange.fromStartEnd(startTime, endTime, inclusive));
    }
  }
}
