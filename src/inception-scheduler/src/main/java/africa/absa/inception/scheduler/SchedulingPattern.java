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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.StringTokenizer;
import java.util.TimeZone;

/**
 * The <b>SchedulingPattern</b> class supports a UNIX crontab-like pattern is a string split in five
 * space separated parts.
 *
 * <p>Each part is intented as:
 *
 * <ol>
 *   <li><strong>Minutes sub-pattern</strong>. During which minutes of the hour should the job been
 *       launched? The values range is from 0 to 59.
 *   <li><strong>Hours sub-pattern</strong>. During which hours of the day should the job been
 *       launched? The values range is from 0 to 23.
 *   <li><strong>Days of month sub-pattern</strong>. During which days of the month should the job
 *       been launched? The values range is from 1 to 31. The special value L can be used to
 *       recognize the last day of month.
 *   <li><strong>Months sub-pattern</strong>. During which months of the year should the job been
 *       launched? The values range is from 1 (January) to 12 (December), otherwise this sub-pattern
 *       allows the aliases &quot;jan&quot;, &quot;feb&quot;, &quot;mar&quot;, &quot;apr&quot;,
 *       &quot;may&quot;, &quot;jun&quot;, &quot;jul&quot;, &quot;aug&quot;, &quot;sep&quot;,
 *       &quot;oct&quot;, &quot;nov&quot; and &quot;dec&quot;.
 *   <li><strong>Days of week sub-pattern</strong>. During which days of the week should the job
 *       been launched? The values range is from 0 (Sunday) to 6 (Saturday), otherwise this
 *       sub-pattern allows the aliases &quot;sun&quot;, &quot;mon&quot;, &quot;tue&quot;,
 *       &quot;wed&quot;, &quot;thu&quot;, &quot;fri&quot; and &quot;sat&quot;.
 * </ol>
 *
 * <p>The star wildcard character is also admitted, indicating &quot;every minute of the hour&quot;,
 * &quot;every hour of the day&quot;, &quot;every day of the month&quot;, &quot;every month of the
 * year&quot; and &quot;every day of the week&quot;, according to the sub-pattern in which it is
 * used.
 *
 * <p>Once the scheduler is started, a job will be launched when the five parts in its scheduling
 * pattern will be true at the same time.
 *
 * <p>Some examples:
 *
 * <p><strong>5 * * * *</strong><br>
 * This pattern causes a job to be launched once every hour, at the begin of the fifth minute
 * (00:05, 01:05, 02:05 etc.).
 *
 * <p><strong>* * * * *</strong><br>
 * This pattern causes a job to be launched every minute.
 *
 * <p><strong>* 12 * * Mon</strong><br>
 * This pattern causes a job to be launched every minute during the 12th hour of Monday.
 *
 * <p><strong>* 12 16 * Mon</strong><br>
 * This pattern causes a job to be launched every minute during the 12th hour of Monday, 16th, but
 * only if the day is the 16th of the month.
 *
 * <p>Every sub-pattern can contain two or more comma separated values.
 *
 * <p><strong>59 11 * * 1,2,3,4,5</strong><br>
 * This pattern causes a job to be launched at 11:59AM on Monday, Tuesday, Wednesday, Thursday and
 * Friday.
 *
 * <p>Values intervals are admitted and defined using the minus character.
 *
 * <p><strong>59 11 * * 1-5</strong><br>
 * This pattern is equivalent to the previous one.
 *
 * <p>The slash character can be used to identify step values within a range. It can be used both in
 * the form <em>*&#47;c</em> and <em>a-b/c</em>. The subpattern is matched every <em>c</em> values
 * of the range <em>0,maxvalue</em> or <em>a-b</em>.
 *
 * <p><strong>*&#47;5 * * * *</strong><br>
 * This pattern causes a job to be launched every 5 minutes (0:00, 0:05, 0:10, 0:15 and so on).
 *
 * <p><strong>3-18&#47;5 * * * *</strong><br>
 * This pattern causes a job to be launched every 5 minutes starting from the third minute of the
 * hour, up to the 18th (0:03, 0:08, 0:13, 0:18, 1:03, 1:08 and so on).
 *
 * <p><strong>*&#47;15 9-17 * * *</strong><br>
 * This pattern causes a job to be launched every 15 minutes between the 9th and 17th hour of the
 * day (9:00, 9:15, 9:30, 9:45 and so on... note that the last execution will be at 17:45).
 *
 * <p>All the fresh described syntax rules can be used together.
 *
 * <p><strong>* 12 10-16&#47;2 * *</strong><br>
 * This pattern causes a job to be launched every minute during the 12th hour of the day, but only
 * if the day is the 10th, the 12th, the 14th or the 16th of the month.
 *
 * <p><strong>* 12 1-15,17,20-25 * *</strong><br>
 * This pattern causes a job to be launched every minute during the 12th hour of the day, but the
 * day of the month must be between the 1st and the 15th, the 20th and the 25, or at least it must
 * be the 17th.
 *
 * <p>Finally cron4j lets you combine more scheduling patterns into one, with the pipe character:
 *
 * <p><strong>0 5 * * *|8 10 * * *|22 17 * * *</strong><br>
 * This pattern causes a job to be launched every day at 05:00, 10:08 and 17:22.
 *
 * @author Carlo Pelliccia
 * @author Marcus Portmann
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class SchedulingPattern {

  /** The parser for the day of month values. */
  private static final ValueParser DAY_OF_MONTH_VALUE_PARSER = new DayOfMonthValueParser();

  /** The parser for the day of week values. */
  private static final ValueParser DAY_OF_WEEK_VALUE_PARSER = new DayOfWeekValueParser();

  /** The parser for the hour values. */
  private static final ValueParser HOUR_VALUE_PARSER = new HourValueParser();

  /** The parser for the minute values. */
  private static final ValueParser MINUTE_VALUE_PARSER = new MinuteValueParser();

  /** The parser for the month values. */
  private static final ValueParser MONTH_VALUE_PARSER = new MonthValueParser();

  /** The pattern as a string. */
  private final String asString;

  /** The ValueMatcher list for the "day of month" field. */
  protected List<ValueMatcher> dayOfMonthMatchers = new ArrayList<>();

  /** The ValueMatcher list for the "day of week" field. */
  protected List<ValueMatcher> dayOfWeekMatchers = new ArrayList<>();

  /** The ValueMatcher list for the "hour" field. */
  protected List<ValueMatcher> hourMatchers = new ArrayList<>();

  /** How many matcher groups in this pattern? */
  protected int matcherSize = 0;

  /** The ValueMatcher list for the "minute" field. */
  protected List<ValueMatcher> minuteMatchers = new ArrayList<>();

  /** The ValueMatcher list for the "month" field. */
  protected List<ValueMatcher> monthMatchers = new ArrayList<>();

  /**
   * Builds a SchedulingPattern by parsing it from a string.
   *
   * @param pattern the pattern as a crontab-like string
   * @throws InvalidSchedulingPatternException if the scheduling pattern is invalid
   */
  public SchedulingPattern(String pattern) throws InvalidSchedulingPatternException {
    this.asString = pattern;

    StringTokenizer st1 = new StringTokenizer(pattern, "|");

    if (st1.countTokens() < 1) {
      throw new InvalidSchedulingPatternException(
          String.format("Invalid pattern: \"%s\"", pattern));
    }

    while (st1.hasMoreTokens()) {
      String localPattern = st1.nextToken();
      StringTokenizer st2 = new StringTokenizer(localPattern, " \t");

      if (st2.countTokens() != 5) {
        throw new InvalidSchedulingPatternException(
            String.format("Invalid pattern: \"%s\"", localPattern));
      }

      try {
        minuteMatchers.add(buildValueMatcher(st2.nextToken(), MINUTE_VALUE_PARSER));
      } catch (Exception e) {
        throw new InvalidSchedulingPatternException(
            String.format("Invalid pattern \"%s\". Error parsing minutes field", localPattern), e);
      }

      try {
        hourMatchers.add(buildValueMatcher(st2.nextToken(), HOUR_VALUE_PARSER));
      } catch (Exception e) {
        throw new InvalidSchedulingPatternException(
            String.format("Invalid pattern \"%s\". Error parsing hours field", localPattern), e);
      }

      try {
        dayOfMonthMatchers.add(buildValueMatcher(st2.nextToken(), DAY_OF_MONTH_VALUE_PARSER));
      } catch (Exception e) {
        throw new InvalidSchedulingPatternException(
            String.format(
                "Invalid pattern \"%s\". Error parsing days of month field", localPattern),
            e);
      }

      try {
        monthMatchers.add(buildValueMatcher(st2.nextToken(), MONTH_VALUE_PARSER));
      } catch (Exception e) {
        throw new InvalidSchedulingPatternException(
            String.format("Invalid pattern \"%s\". Error parsing months field", localPattern), e);
      }

      try {
        dayOfWeekMatchers.add(buildValueMatcher(st2.nextToken(), DAY_OF_WEEK_VALUE_PARSER));
      } catch (Exception e) {
        throw new InvalidSchedulingPatternException(
            String.format("Invalid pattern \"%s\". Error parsing days of week field", localPattern),
            e);
      }

      matcherSize++;
    }
  }

  /**
   * Validates a string as a scheduling pattern.
   *
   * @param schedulingPattern the pattern to validate
   * @return <b>true</b> if the given string represents a valid scheduling pattern or <b> false</b>
   *     otherwise
   */
  @SuppressWarnings("unused")
  public static boolean validate(String schedulingPattern) {
    try {
      new SchedulingPattern(schedulingPattern);
    } catch (InvalidSchedulingPatternException e) {
      return false;
    }

    return true;
  }

  /**
   * This utility method changes an alias to an integer value.
   *
   * @param value the value
   * @param aliases the aliases list
   * @param offset the offset applied to the aliases list indices
   * @return the parsed value
   */
  private static int parseAlias(String value, String[] aliases, int offset) throws Exception {
    for (int i = 0; i < aliases.length; i++) {
      if (aliases[i].equalsIgnoreCase(value)) {
        return offset + i;
      }
    }

    throw new Exception(String.format("Invalid alias \"%s\"", value));
  }

  /**
   * Returns <b>true</b> if the EPOCH timestamp in milliseconds matches the pattern, according to
   * the given time zone.
   *
   * @param timezone the time zone
   * @param timestamp the EPOCH timestamp in milliseconds
   * @return <b>true</b> if the given timestamp matches the pattern or <b>false</b> otherwise
   */
  public boolean match(TimeZone timezone, long timestamp) {
    GregorianCalendar gc = new GregorianCalendar();

    gc.setTimeInMillis(timestamp);
    gc.setTimeZone(timezone);

    int minute = gc.get(Calendar.MINUTE);
    int hour = gc.get(Calendar.HOUR_OF_DAY);
    int dayOfMonth = gc.get(Calendar.DAY_OF_MONTH);
    int month = gc.get(Calendar.MONTH) + 1;
    int dayOfWeek = gc.get(Calendar.DAY_OF_WEEK) - 1;
    int year = gc.get(Calendar.YEAR);

    for (int i = 0; i < matcherSize; i++) {
      ValueMatcher minuteMatcher = minuteMatchers.get(i);
      ValueMatcher hourMatcher = hourMatchers.get(i);
      ValueMatcher dayOfMonthMatcher = dayOfMonthMatchers.get(i);
      ValueMatcher monthMatcher = monthMatchers.get(i);
      ValueMatcher dayOfWeekMatcher = dayOfWeekMatchers.get(i);
      boolean eval =
          minuteMatcher.match(minute)
              && hourMatcher.match(hour)
              && ((dayOfMonthMatcher instanceof DayOfMonthValueMatcher)
                  ? ((DayOfMonthValueMatcher) dayOfMonthMatcher)
                      .match(dayOfMonth, month, gc.isLeapYear(year))
                  : dayOfMonthMatcher.match(dayOfMonth))
              && monthMatcher.match(month)
              && dayOfWeekMatcher.match(dayOfWeek);

      if (eval) {
        return true;
      }
    }

    return false;
  }

  /**
   * Returns <b>true</b> if the given EPOCH timestamp in milliseconds matches the pattern, according
   * to the system default time zone.
   *
   * @param timestamp the EPOCH timestamp in milliseconds
   * @return <b>true</b> if the given timestamp matches the pattern or <b>false</b> otherwise
   */
  public boolean match(long timestamp) {
    return match(TimeZone.getDefault(), timestamp);
  }

  /**
   * Returns the pattern as a string.
   *
   * @return The pattern as a string.
   */
  public String toString() {
    return asString;
  }

  /**
   * A <b>ValueMatcher</b> utility builder.
   *
   * @param str the pattern part for the <b>ValueMatcher</b> creation
   * @param parser the parser used to parse the values
   * @return the requested <b>ValueMatcher</b>
   */
  private ValueMatcher buildValueMatcher(String str, ValueParser parser) throws Exception {
    if ((str.length() == 1) && str.equals("*")) {
      return new AlwaysTrueValueMatcher();
    }

    List<Integer> values = new ArrayList<>();
    StringTokenizer st = new StringTokenizer(str, ",");

    while (st.hasMoreTokens()) {
      String element = st.nextToken();
      List<Integer> local;

      try {
        local = parseListElement(element, parser);
      } catch (Exception e) {
        throw new Exception(
            String.format(
                "Invalid field \"%s\", invalid element \"%s\": %s", str, element, e.getMessage()));
      }

      for (Integer value : local) {
        if (!values.contains(value)) {
          values.add(value);
        }
      }
    }

    if (values.size() == 0) {
      throw new Exception(String.format("Invalid field \"%s\"", str));
    }

    if (parser == DAY_OF_MONTH_VALUE_PARSER) {
      return new DayOfMonthValueMatcher(values);
    } else {
      return new IntArrayValueMatcher(values);
    }
  }

  /**
   * Parses an element to retrieve the values for the pattern.
   *
   * @param str the element string
   * @param parser the parser used to parse the values
   * @return the integers representing the allowed values
   */
  private List<Integer> parseListElement(String str, ValueParser parser) throws Exception {
    StringTokenizer st = new StringTokenizer(str, "/");
    int size = st.countTokens();

    if ((size < 1) || (size > 2)) {
      throw new Exception("Syntax error");
    }

    List<Integer> values;

    try {
      values = parseRange(st.nextToken(), parser);
    } catch (Exception e) {
      throw new Exception(String.format("Invalid range: %s", e.getMessage()));
    }

    if (size == 2) {
      String dStr = st.nextToken();
      int div;

      try {
        div = Integer.parseInt(dStr);
      } catch (NumberFormatException e) {
        throw new Exception("Invalid divisor \"" + dStr + "\"");
      }

      if (div < 1) {
        throw new Exception("Non positive divisor \"" + div + "\"");
      }

      List<Integer> values2 = new ArrayList<>();

      for (int i = 0; i < values.size(); i += div) {
        values2.add(values.get(i));
      }

      return values2;
    } else {
      return values;
    }
  }

  /**
   * Parses a range of values.
   *
   * @param str the range string.
   * @param parser the parser used to parse the values
   * @return the integers representing the allowed values
   */
  private List<Integer> parseRange(String str, ValueParser parser) throws Exception {
    if (str.equals("*")) {
      int min = parser.getMinValue();
      int max = parser.getMaxValue();
      List<Integer> values = new ArrayList<>();

      for (int i = min; i <= max; i++) {
        values.add(i);
      }

      return values;
    }

    StringTokenizer st = new StringTokenizer(str, "-");
    int size = st.countTokens();

    if ((size < 1) || (size > 2)) {
      throw new Exception("Syntax error");
    }

    String v1Str = st.nextToken();
    int v1;

    try {
      v1 = parser.parse(v1Str);
    } catch (Exception e) {
      throw new Exception(String.format("Invalid value \"%s\": %s", v1Str, e.getMessage()));
    }

    if (size == 1) {
      List<Integer> values = new ArrayList<>();

      values.add(v1);

      return values;
    } else {
      String v2Str = st.nextToken();
      int v2;

      try {
        v2 = parser.parse(v2Str);
      } catch (Exception e) {
        throw new Exception(String.format("Invalid value \"%s\": %s", v2Str, e.getMessage()));
      }

      List<Integer> values = new ArrayList<>();

      if (v1 < v2) {
        for (int i = v1; i <= v2; i++) {
          values.add(i);
        }
      } else if (v1 > v2) {
        int min = parser.getMinValue();
        int max = parser.getMaxValue();

        for (int i = v1; i <= max; i++) {
          values.add(i);
        }

        for (int i = min; i <= v2; i++) {
          values.add(i);
        }
      } else {
        // v1 == v2
        values.add(v1);
      }

      return values;
    }
  }

  /** Definition for a value parser. */
  private interface ValueParser {

    /**
     * Returns the maximum value accepted by the parser.
     *
     * @return the maximum value accepted by the parser
     */
    int getMaxValue();

    /**
     * Returns the minimum value accepted by the parser.
     *
     * @return the minimum value accepted by the parser
     */
    int getMinValue();

    /**
     * Attempts to parse a value.
     *
     * @param value the value
     * @return the parsed value
     * @throws Exception If the value can't be parsed.
     */
    int parse(String value) throws Exception;
  }

  /** The days of month value parser. */
  private static class DayOfMonthValueParser extends SimpleValueParser {

    /** Builds the value parser. */
    public DayOfMonthValueParser() {
      super(1, 31);
    }

    /**
     * Added to support last-day-of-month.
     *
     * @param value the value to be parsed
     * @return the integer day of the month or 32 for last day of the month
     */
    public int parse(String value) throws Exception {
      if (value.equalsIgnoreCase("L")) {
        return 32;
      } else {
        return super.parse(value);
      }
    }
  }

  /** The value parser for the months field. */
  private static class DayOfWeekValueParser extends SimpleValueParser {

    /** Days of week aliases. */
    private static final String[] ALIASES = {"sun", "mon", "tue", "wed", "thu", "fri", "sat"};

    /** Builds the months value parser. */
    public DayOfWeekValueParser() {
      super(0, 7);
    }

    /**
     * Parse the value.
     *
     * @param value the value to parse
     * @return the parsed value
     */
    public int parse(String value) throws Exception {
      try {
        // try as a simple value
        return super.parse(value) % 7;
      } catch (Exception e) {
        // try as an alias
        return parseAlias(value, ALIASES, 0);
      }
    }
  }

  /** The hours value parser. */
  private static class HourValueParser extends SimpleValueParser {

    /** Builds the value parser. */
    public HourValueParser() {
      super(0, 23);
    }
  }

  /** The minutes value parser. */
  private static class MinuteValueParser extends SimpleValueParser {

    /** Builds the value parser. */
    public MinuteValueParser() {
      super(0, 59);
    }
  }

  /** The value parser for the months field. */
  private static class MonthValueParser extends SimpleValueParser {

    /** Months aliases. */
    private static final String[] ALIASES = {
      "jan", "feb", "mar", "apr", "may", "jun", "jul", "aug", "sep", "oct", "nov", "dec"
    };

    /** Builds the months value parser. */
    public MonthValueParser() {
      super(1, 12);
    }

    /**
     * Parse the value.
     *
     * @param value the value to parse
     * @return the parsed value
     */
    public int parse(String value) throws Exception {
      try {
        // try as a simple value
        return super.parse(value);
      } catch (Exception e) {
        // try as an alias
        return parseAlias(value, ALIASES, 1);
      }
    }
  }

  /** A simple value parser. */
  private static class SimpleValueParser implements ValueParser {

    /** The maximum allowed value. */
    protected int maxValue;

    /** The minimum allowed value. */
    protected int minValue;

    /**
     * Builds the value parser.
     *
     * @param minValue the minimum allowed value
     * @param maxValue the maximum allowed value
     */
    public SimpleValueParser(int minValue, int maxValue) {
      this.minValue = minValue;
      this.maxValue = maxValue;
    }

    /**
     * Returns the maximum value.
     *
     * @return the maximum value
     */
    public int getMaxValue() {
      return maxValue;
    }

    /**
     * Returns the minimum value.
     *
     * @return the minimum value
     */
    public int getMinValue() {
      return minValue;
    }

    /**
     * Parse the value
     *
     * @param value the value to parse
     * @return the parsed value
     */
    public int parse(String value) throws Exception {
      int i;

      try {
        i = Integer.parseInt(value);
      } catch (NumberFormatException e) {
        throw new Exception("invalid integer value");
      }

      if ((i < minValue) || (i > maxValue)) {
        throw new Exception("value out of range");
      }

      return i;
    }
  }
}
