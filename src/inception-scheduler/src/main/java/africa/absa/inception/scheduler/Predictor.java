/*
 * Copyright 2021 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package africa.absa.inception.scheduler;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * The <b>Predictor</b> class provides the capability to predict when a scheduling pattern will be
 * matched.
 *
 * <p>Suppose you want to know when the scheduler will execute a job scheduled with the pattern
 * <em>0 3 * jan-jun,sep-dec mon-fri</em>. You can predict the next <em>n</em> execution of the job
 * using a Predictor instance:
 *
 * <p>
 *
 * <p>
 *
 * <pre>
 * String pattern = &quot;0 3 * jan-jun,sep-dec mon-fri&quot;;
 * Predictor p = new Predictor(pattern);
 * for (int i = 0; i &lt; n; i++) {
 *  System.out.println(p.nextMatchingDate());
 * }
 * </pre>
 *
 * @author Carlo Pelliccia
 * @author Marcus Portmann
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class Predictor {

  /** The scheduling pattern on which the predictor works. */
  private final SchedulingPattern schedulingPattern;

  /** The start time for the next prediction. */
  private long time;

  /** The time zone for the prediction. */
  private TimeZone timeZone = TimeZone.getDefault();

  /**
   * Constructs a new <b>SchedulingPattern</b>.
   *
   * @param schedulingPattern the scheduling pattern on which the prediction will be based
   */
  public Predictor(SchedulingPattern schedulingPattern) {
    this(schedulingPattern, System.currentTimeMillis());
  }

  /**
   * Constructs a new <b>SchedulingPattern</b>.
   *
   * @param schedulingPattern the scheduling pattern on which the prediction will be based
   */
  public Predictor(String schedulingPattern) throws InvalidSchedulingPatternException {
    this(schedulingPattern, System.currentTimeMillis());
  }

  /**
   * Constructs a new <b>SchedulingPattern</b>.
   *
   * @param schedulingPattern the scheduling pattern on which the prediction will be based
   * @param start the start time of the prediction
   */
  public Predictor(SchedulingPattern schedulingPattern, Date start) {
    this(schedulingPattern, start.getTime());
  }

  /**
   * It builds a predictor with the given scheduling pattern and start time.
   *
   * @param schedulingPattern the scheduling pattern on which the prediction will be based
   * @param start the start time of the prediction as a milliseconds value
   */
  public Predictor(SchedulingPattern schedulingPattern, long start) {
    this.schedulingPattern = schedulingPattern;
    this.time = (start / (1000 * 60)) * 1000 * 60;
  }

  /**
   * It builds a predictor with the given scheduling pattern and start time.
   *
   * @param schedulingPattern the pattern on which the prediction will be based
   * @param start the start time of the prediction
   */
  public Predictor(String schedulingPattern, Date start) throws InvalidSchedulingPatternException {
    this(schedulingPattern, start.getTime());
  }

  /**
   * It builds a predictor with the given scheduling pattern and start time.
   *
   * @param schedulingPattern the pattern on which the prediction will be based
   * @param start the start time of the prediction
   */
  public Predictor(String schedulingPattern, long start) throws InvalidSchedulingPatternException {
    this.schedulingPattern = new SchedulingPattern(schedulingPattern);
    this.time = (start / (1000 * 60)) * 1000 * 60;
  }

  /**
   * Returns the next matching moment as a {@link Date} object.
   *
   * @return the next matching moment as a {@link Date} object
   */
  public synchronized Date nextMatchingDate() {
    return new Date(nextMatchingTime());
  }

  /**
   * Returns the next matching moment as a {@link LocalDateTime} object.
   *
   * @return the next matching moment as a {@link LocalDateTime} object
   */
  public synchronized LocalDateTime nextMatchingLocalDateTime() {
    return LocalDateTime.ofInstant(
        Instant.ofEpochMilli(nextMatchingTime()), ZoneId.systemDefault());
  }

  /**
   * Returns the next matching moment as a milliseconds value.
   *
   * @return the next matching moment as a milliseconds value
   */
  public synchronized long nextMatchingTime() {
    // Go a minute ahead.
    time += 60000;

    // Is it matching?
    if (schedulingPattern.match(time)) {
      return time;
    }

    // Go through the matcher groups.
    int size = schedulingPattern.matcherSize;
    long[] times = new long[size];

    for (int k = 0; k < size; k++) {
      // Ok, split the time!
      GregorianCalendar c = new GregorianCalendar();

      c.setTimeInMillis(time);
      c.setTimeZone(timeZone);

      int minute = c.get(Calendar.MINUTE);
      int hour = c.get(Calendar.HOUR_OF_DAY);
      int dayOfMonth = c.get(Calendar.DAY_OF_MONTH);
      int month = c.get(Calendar.MONTH);
      int year = c.get(Calendar.YEAR);

      // Gets the matchers.
      ValueMatcher minuteMatcher = schedulingPattern.minuteMatchers.get(k);
      ValueMatcher hourMatcher = schedulingPattern.hourMatchers.get(k);
      ValueMatcher dayOfMonthMatcher = schedulingPattern.dayOfMonthMatchers.get(k);
      ValueMatcher dayOfWeekMatcher = schedulingPattern.dayOfWeekMatchers.get(k);
      ValueMatcher monthMatcher = schedulingPattern.monthMatchers.get(k);

      for (; ; ) { // day of week
        for (; ; ) { // month
          for (; ; ) { // day of month
            for (; ; ) { // hour
              for (; ; ) { // minutes
                if (minuteMatcher.match(minute)) {
                  break;
                } else {
                  minute++;

                  if (minute > 59) {
                    minute = 0;
                    hour++;
                  }
                }
              }

              if (hour > 23) {
                hour = 0;
                dayOfMonth++;
              }

              if (hourMatcher.match(hour)) {
                break;
              } else {
                hour++;
                minute = 0;
              }
            }

            if (dayOfMonth > 31) {
              dayOfMonth = 1;
              month++;
            }

            if (month > Calendar.DECEMBER) {
              month = Calendar.JANUARY;
              year++;
            }

            if (dayOfMonthMatcher instanceof DayOfMonthValueMatcher) {
              DayOfMonthValueMatcher aux = (DayOfMonthValueMatcher) dayOfMonthMatcher;

              if (aux.match(dayOfMonth, month + 1, c.isLeapYear(year))) {
                break;
              } else {
                dayOfMonth++;
                hour = 0;
                minute = 0;
              }
            } else if (dayOfMonthMatcher.match(dayOfMonth)) {
              break;
            } else {
              dayOfMonth++;
              hour = 0;
              minute = 0;
            }
          }

          if (monthMatcher.match(month + 1)) {
            break;
          } else {
            month++;
            dayOfMonth = 1;
            hour = 0;
            minute = 0;
          }
        }

        // Is this ok?
        c = new GregorianCalendar();
        c.setTimeZone(timeZone);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.YEAR, year);

        // Day-of-month/month/year compatibility check.
        int oldDayOfMonth = dayOfMonth;
        int oldMonth = month;
        int oldYear = year;

        dayOfMonth = c.get(Calendar.DAY_OF_MONTH);
        month = c.get(Calendar.MONTH);
        year = c.get(Calendar.YEAR);

        if ((month != oldMonth) || (dayOfMonth != oldDayOfMonth) || (year != oldYear)) {
          // Take another spin!
          continue;
        }

        // Day of week.
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);

        if (dayOfWeekMatcher.match(dayOfWeek - 1)) {
          break;
        } else {
          dayOfMonth++;
          hour = 0;
          minute = 0;

          if (dayOfMonth > 31) {
            dayOfMonth = 1;
            month++;

            if (month > Calendar.DECEMBER) {
              month = Calendar.JANUARY;
              year++;
            }
          }
        }
      }

      // Seems it matches!
      times[k] = (c.getTimeInMillis() / (1000 * 60)) * 1000 * 60;
    }

    // Which one?
    long min = Long.MAX_VALUE;

    for (int k = 0; k < size; k++) {
      if (times[k] < min) {
        min = times[k];
      }
    }

    // Updates the object current time value.
    time = min;

    // Here it is.
    return time;
  }

  /**
   * Set the time zone for predictions.
   *
   * @param timeZone the time zone to user for predictions
   */
  public void setTimeZone(TimeZone timeZone) {
    this.timeZone = timeZone;
  }
}
