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

import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class SignedIntegerToLongPrefixTest {
    private TypeConverter typeConverter;
    private TypeConverterConfig config;

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Before
    public void runBeforeEveryTest() {
        this.config = new TypeConverterConfig();
        this.config.setCharset(StandardCharsets.UTF_8);
        this.config.setPaddingChar('0');
        config.setSigningType(CopyBookFieldSigningType.PREFIX);
        typeConverter = new SignedIntegerToLong();
        typeConverter.initialize(config);
    }

    @Test
    public void testValidateSuccess() throws Exception {
        typeConverter.validate(Long.TYPE, 2, -1);
    }

    @Test(expected = TypeConverterException.class)
    public void testValidateFail() throws Exception {
        typeConverter.validate(Integer.TYPE, 2, -1);

    }

    @Test
    public void testTo() throws Exception {
        assertEquals(-12147483648L, (long)typeConverter.to("0-12147483648".getBytes(StandardCharsets.UTF_8), 0, 13, -1, true));
        assertEquals(12147483648L, (long)typeConverter.to("0+12147483648".getBytes(StandardCharsets.UTF_8), 0, 13, -1, true));
        assertEquals(12, (long)typeConverter.to("000000+12".getBytes(StandardCharsets.UTF_8), 0, 9, -1, true));
    }

    @Test
    public void testToNullDefaultValue() throws Exception {
        config.setNullFillerChar((char)0);
        config.setDefaultValue("42");
        typeConverter.initialize(config);
        assertEquals(42, (long)typeConverter.to(new byte[4], 0, 2, 2, true));
    }

    @Test
    public void testToNullValue() throws Exception {
        expectedEx.expect(TypeConverterException.class);
        expectedEx.expectMessage("Missing sign char for value");
        config.setNullFillerChar((char)0);
        typeConverter.initialize(config);
        assertEquals(null, typeConverter.to(new byte[4], 0, 2, 2, true));
    }

    @Test
    public void testToZeroValue() throws Exception {
        assertEquals(0, (long)typeConverter.to("+00000000".getBytes(StandardCharsets.UTF_8), 0, 9, -1, true));
    }

    @Test
    public void testFrom() throws Exception {
        assertArrayEquals("0-12147483648".getBytes(StandardCharsets.UTF_8), typeConverter.from(-12147483648L, 13, -1, true));
        assertArrayEquals("0+12147483648".getBytes(StandardCharsets.UTF_8), typeConverter.from(12147483648L, 13, -1, true));
    }

    @Test
    public void testToFailMissing() throws Exception {
        expectedEx.expect(TypeConverterException.class);
        expectedEx.expectMessage("Missing sign char for value");
        typeConverter.to("012147483648".getBytes(StandardCharsets.UTF_8), 0, 12, -1, true);
    }

    @Test
    public void testToFailWrongEnd() throws Exception {
        expectedEx.expect(TypeConverterException.class);
        expectedEx.expectMessage("Missing sign char for value");
        typeConverter.to("12147483648-".getBytes(StandardCharsets.UTF_8), 0, 12, -1, true);
    }

    @Test
    public void testFromOverflow() throws Exception {
        expectedEx.expect(TypeConverterException.class);
        expectedEx.expectMessage("Field to small for value");
        byte[] bytes = typeConverter.from(12147483648L, 4, -1, true);
    }

}