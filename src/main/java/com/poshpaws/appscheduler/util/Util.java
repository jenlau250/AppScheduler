/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.poshpaws.appscheduler.util;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.Temporal;
import java.util.Date;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

/**
 *
 * @author Jen
 */
public class Util {

    private static final DateTimeFormatter TIMEFORMATTER = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT);
    private static final DateTimeFormatter DATEFORMATTER = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT);
    private static final DateTimeFormatter LABELFORMATTER = DateTimeFormatter.ofPattern("E MMM d, yyyy");
    private static final DateTimeFormatter DBFORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd h:mm a");
    private static final DateTimeFormatter SQLFORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM-dd-yyyy");
    private static final DateTimeFormatter DATEFORMATTOLOCAL = DateTimeFormatter.ofPattern("MM-dd-yyyy");

    private static final ZoneId zid = ZoneId.systemDefault();

    public static LocalTime parseStringToTimeFormat(String timeString) {
        try {
            return LocalTime.parse(timeString, TIMEFORMATTER);
        } catch (Exception e) {
            System.out.println("error parsing DateTimeUtil");
            return null;
        }
    }

    public static String parseTimeToStringFormat(LocalTime time) {
        try {
            return time.format(TIMEFORMATTER);
        } catch (Exception e) {
            System.out.println("error parsing DateTimeUtil");
            return null;
        }
    }

    public static LocalDateTime parseStringToLocalDateTime(String timeString) {
        try {
            return LocalDateTime.parse(timeString, DBFORMAT);
        } catch (Exception e) {
            System.out.println("error parsing DateTimeUtil");
            return null;
        }
    }

    public static ZonedDateTime parseLocalToZoneDateTime(LocalDateTime datetimeString) {
        ZoneId zid = ZoneId.systemDefault();
        try {
            return datetimeString.atZone(zid).withZoneSameInstant(ZoneId.of("UTC"));
        } catch (Exception e) {
            System.out.println("error parsing DateTimeUtil");
            return null;
        }
    }

    public static Timestamp parseZonedToTimestamp(ZonedDateTime time) {
        try {
            return Timestamp.valueOf(time.toLocalDateTime());
        } catch (Exception e) {
            System.out.println("error parsing DateTimeUtil");
            return null;
        }
    }

    public static Timestamp parseStringDateTimetoDbFormat(String dateTimeString) {
        // use function to get DB time format from selected combo dropdown Date and Time
        try {
            LocalDateTime ltime = LocalDateTime.parse(dateTimeString, DBFORMAT);

            ZonedDateTime ztime = ltime.atZone(zid).withZoneSameInstant(ZoneId.of("UTC"));
            return Timestamp.valueOf(ztime.toLocalDateTime());

        } catch (Exception e) {
            System.out.println("error parsing DateTimeUtil");
            return null;
        }
    }

    //Used to convert appointment date and time columns in ListController
    public static <ROW, T extends Temporal> Callback<TableColumn<ROW, T>, TableCell<ROW, T>> getDateCell(DateTimeFormatter format) {
        return column -> {
            return new TableCell<ROW, T>() {
                @Override
                protected void updateItem(T item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setText(null);
                    } else {
                        setText(format.format(item));
                    }
                }
            };
        };
    }

    public static String getDateString(Date date) {
        return DATE_FORMAT.format(date);
    }

//    public static LocalDate parseStringtoLocalDate(String dateString) {
//        try {
//            return LocalDate.parse(dateString, DATEFORMATTOLOCAL);
//
//        } catch (Exception e) {
//            System.out.println("error parsing DateTimeUtil");
//            return null;
//        }
//    }
}
