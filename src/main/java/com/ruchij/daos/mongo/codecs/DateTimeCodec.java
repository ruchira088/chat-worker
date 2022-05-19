package com.ruchij.daos.mongo.codecs;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.joda.time.DateTime;

public class DateTimeCodec implements Codec<DateTime> {
    @Override
    public DateTime decode(BsonReader reader, DecoderContext decoderContext) {
        return new DateTime(reader.readDateTime());
    }

    @Override
    public void encode(BsonWriter writer, DateTime dateTime, EncoderContext encoderContext) {
        if (dateTime != null) {
            writer.writeDateTime(dateTime.getMillis());
        }
    }

    @Override
    public Class<DateTime> getEncoderClass() {
        return DateTime.class;
    }
}
