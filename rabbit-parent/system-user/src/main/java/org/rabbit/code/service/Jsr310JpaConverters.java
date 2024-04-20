package org.rabbit.code.service;

import org.springframework.data.convert.Jsr310Converters.*;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import javax.annotation.Nullable;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.time.*;
import java.util.Date;

/**
 * JPA 2.1 converters to turn JSR-310 types into legacy {@link Date}s. To activate these converters
 * make sure your persistence provider detects them by including this class in the list of mapped
 * classes. In Spring environments, you can simply register the package of this class (i.e. {@code
 * org.springframework.data.jpa.convert.threeten}) as package to be scanned on e.g. the {@link
 * LocalContainerEntityManagerFactoryBean}.
 *
 * @author Oliver Gierke
 * @author Kevin Peters
 */
public class Jsr310JpaConverters {

    @Converter(autoApply = true)
    public static class LocalDateConverter implements AttributeConverter<LocalDate, Date> {

        @Nullable
        @Override
        public Date convertToDatabaseColumn(LocalDate date) {
            return date == null ? null : LocalDateToDateConverter.INSTANCE.convert(date);
        }

        @Nullable
        @Override
        public LocalDate convertToEntityAttribute(Date date) {
            return date == null ? null : DateToLocalDateConverter.INSTANCE.convert(date);
        }
    }

    @Converter(autoApply = true)
    public static class LocalTimeConverter implements AttributeConverter<LocalTime, Date> {

        @Nullable
        @Override
        public Date convertToDatabaseColumn(LocalTime time) {
            return time == null ? null : LocalTimeToDateConverter.INSTANCE.convert(time);
        }

        @Nullable
        @Override
        public LocalTime convertToEntityAttribute(Date date) {
            return date == null ? null : DateToLocalTimeConverter.INSTANCE.convert(date);
        }
    }

    @Converter(autoApply = true)
    public static class LocalDateTimeConverter implements AttributeConverter<LocalDateTime, Date> {

        @Nullable
        @Override
        public Date convertToDatabaseColumn(LocalDateTime date) {
            return date == null ? null : LocalDateTimeToDateConverter.INSTANCE.convert(date);
        }

        @Nullable
        @Override
        public LocalDateTime convertToEntityAttribute(Date date) {
            return date == null ? null : DateToLocalDateTimeConverter.INSTANCE.convert(date);
        }
    }

    @Converter(autoApply = true)
    public static class InstantConverter implements AttributeConverter<Instant, Date> {

        @Nullable
        @Override
        public Date convertToDatabaseColumn(Instant instant) {
            return instant == null ? null : InstantToDateConverter.INSTANCE.convert(instant);
        }

        @Nullable
        @Override
        public Instant convertToEntityAttribute(Date date) {
            return date == null ? null : DateToInstantConverter.INSTANCE.convert(date);
        }
    }

    @Converter(autoApply = true)
    public static class ZoneIdConverter implements AttributeConverter<ZoneId, String> {

        @Nullable
        @Override
        public String convertToDatabaseColumn(ZoneId zoneId) {
            return zoneId == null ? null : ZoneIdToStringConverter.INSTANCE.convert(zoneId);
        }

        @Nullable
        @Override
        public ZoneId convertToEntityAttribute(String zoneId) {
            return zoneId == null ? null : StringToZoneIdConverter.INSTANCE.convert(zoneId);
        }
    }
}