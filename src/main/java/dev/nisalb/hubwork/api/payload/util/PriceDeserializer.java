package dev.nisalb.hubwork.api.payload.util;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;

public class PriceDeserializer extends StdDeserializer<BigDecimal> {

    public PriceDeserializer() {
        this(null);
    }

    public PriceDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public BigDecimal deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
        String price = p.getText();

        try {
            var decimalFormatter = new DecimalFormat("#,###.00");
            decimalFormatter.setParseBigDecimal(true);
             return (BigDecimal) decimalFormatter.parse(price);
         } catch (ParseException ex) {
             throw new RuntimeException(ex);
         }
    }
}
