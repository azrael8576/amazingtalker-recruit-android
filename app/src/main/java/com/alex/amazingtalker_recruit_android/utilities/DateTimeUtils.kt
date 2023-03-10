package com.alex.amazingtalker_recruit_android.utilities

import com.alex.amazingtalker_recruit_android.data.AmazingtalkerTeacherSchedule
import com.alex.amazingtalker_recruit_android.data.AmazingtalkerTeacherScheduleUnit
import com.alex.amazingtalker_recruit_android.data.DuringDayType
import com.alex.amazingtalker_recruit_android.data.ScheduleUnitState
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

object DateTimeUtils {

    /**
     * 獲取固定間隔時刻集合
     * @param List<AmazingtalkerTeacherSchedule>
     * @param interval 時間間隔(單位：分鐘)
     * @return MutableList<AmazingtalkerTeacherScheduleUnit>
     */
    fun getIntervalTimeByScheduleList(amazingtalkerTeacherScheduleList: List<AmazingtalkerTeacherSchedule>, interval: Long, scheduleUnitState: ScheduleUnitState)
            : MutableList<AmazingtalkerTeacherScheduleUnit> {

        val scheduleUnitList = mutableListOf<AmazingtalkerTeacherScheduleUnit>()

        for (amazingtalkerTeacherSchedule in amazingtalkerTeacherScheduleList) {
            var startDate = Instant.from(DateTimeFormatter.ISO_INSTANT.parse(amazingtalkerTeacherSchedule.start))
                .atOffset(ZoneOffset.UTC)
                .getLocalOffsetDateTime()
            val endDate = Instant.from(DateTimeFormatter.ISO_INSTANT.parse(amazingtalkerTeacherSchedule.end))
                .atOffset(ZoneOffset.UTC)
                .getLocalOffsetDateTime()

            while (startDate < endDate) {
                var startDateLocalTime = AmazingtalkerTeacherScheduleUnit(startDate,
                    startDate.plusMinutes(interval),
                    scheduleUnitState,
                    startDate.getDuringDayType())

                scheduleUnitList.add(startDateLocalTime)

                startDate = startDate.plusMinutes(interval)
                if (startDate > endDate) {
                    if (startDate != endDate) {
                        var endDateLocalTime = AmazingtalkerTeacherScheduleUnit(endDate,
                            endDate.plusMinutes(interval),
                            scheduleUnitState,
                            endDate.getDuringDayType())

                        scheduleUnitList.add(endDateLocalTime)
                    }
                }
            }
        }
        return scheduleUnitList
    }

    /**
     * 判斷時間為上午/下午/晚上
     * @param offsetDateTime OffsetDateTime
     * @return DuringDayType
     */
    fun OffsetDateTime.getDuringDayType()
            : DuringDayType {

        return when(this.hour) {
            in 0..11 -> DuringDayType.Morning
            in 12..17 -> DuringDayType.Afternoon
            in 18..23 -> DuringDayType.Evening
            else -> {DuringDayType.Morning}
        }
    }

    fun OffsetDateTime.getLocalOffsetDateTime()
            : OffsetDateTime {

        return this.atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime()
    }

    fun OffsetDateTime.getUTCOffsetDateTime()
            : OffsetDateTime {

        return this.atZoneSameInstant(ZoneOffset.UTC).toOffsetDateTime()
    }
}