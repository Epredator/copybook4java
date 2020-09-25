/*
 * Copyright (c) 2015. Troels Liebe Bentsen <tlb@nversion.dk>
 * Copyright (c) 2016. Nordea Bank AB
 * Licensed under the MIT license (LICENSE.txt)
 */

package com.nordea.oss.copybook.converters;

import com.nordea.oss.copybook.exceptions.TypeConverterException;
import com.nordea.oss.copybook.serializers.CopyBookFieldSigningType;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertEquals;

public class SignedDecimalToBigDecimalLastByteTest {
    private TypeConverter typeConverter;
    private TypeConverterConfig config;

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Before
    public void runBeforeEveryTest() {
        this.config = new TypeConverterConfig();
        this.config.setCharset(StandardCharsets.ISO_8859_1);
        this.config.setPaddingChar('0');
        config.setSigningType(CopyBookFieldSigningType.LAST_BYTE_EBCDIC_BIT5);
        typeConverter = new SignedDecimalToBigDecimal();
        typeConverter.initialize(config);
    }

    @Test
    public void testValidateSuccess() throws Exception {
        typeConverter.validate(BigDecimal.class, 10, -1);
    }

    @Test
    public void testValidateFail() throws Exception {
        expectedEx.expect(TypeConverterException.class);
        expectedEx.expectMessage("Only supports converting to and from BigDecimal");
        typeConverter.validate(Integer.TYPE, 2, -1);

    }

    @Test
    public void testTo() throws Exception {
        assertEquals(new BigDecimal("0.00"), typeConverter.to("00000000000000ä".getBytes(StandardCharsets.ISO_8859_1), 0, 15, 2, true));
        assertEquals(new BigDecimal("-200541.00"), typeConverter.to("00000002005410ä".getBytes(StandardCharsets.ISO_8859_1), 0, 15, 2, true));
        assertEquals(new BigDecimal("33258.91"), typeConverter.to("00000000332589A".getBytes(StandardCharsets.ISO_8859_1), 0, 15, 2, true));
    }
}