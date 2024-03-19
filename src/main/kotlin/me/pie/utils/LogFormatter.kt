package me.pie.utils

import java.util.logging.Formatter
import java.util.logging.LogRecord


class LogFormatter : Formatter() {
    override fun format(record: LogRecord): String {
        val sb = StringBuilder()
        sb.append(String.format("%1\$tY-%1\$tm-%1\$td %1\$tH:%1\$tM:%1\$tS.%1\$tL [", record.millis))
            .append(record.sourceMethodName)
            .append("] ")
            .append(record.level.name)
            .append(" ")
            .append(record.loggerName)
            .append(" - ")
            .append(record.message)
            .append(System.lineSeparator())
        return sb.toString()
    }
}