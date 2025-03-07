/**
 * Copyright (c) 2010-2023 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.core.config.core.internal.normalization;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

import java.math.BigDecimal;
import java.util.List;
import java.util.TreeSet;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.junit.jupiter.api.Test;
import org.openhab.core.config.core.ConfigDescriptionParameter;
import org.openhab.core.config.core.ConfigDescriptionParameterBuilder;

/**
 * @author Simon Kaufmann - Initial contribution
 * @author Wouter Born - Migrate tests from Groovy to Java
 */
@NonNullByDefault
public class NormalizerTest {

    @Test
    public void testBooleanNormalizer() {
        Normalizer normalizer = NormalizerFactory.getNormalizer(
                ConfigDescriptionParameterBuilder.create("test", ConfigDescriptionParameter.Type.BOOLEAN).build());

        assertThat(normalizer.normalize(null), is(nullValue()));
        assertThat(normalizer.normalize(true), is(equalTo(true)));
        assertThat(normalizer.normalize(1), is(equalTo(true)));
        assertThat(normalizer.normalize(false), is(equalTo(false)));
        assertThat(normalizer.normalize(0), is(equalTo(false)));
        assertThat(normalizer.normalize(Boolean.TRUE), is(equalTo(true)));
        assertThat(normalizer.normalize(Boolean.FALSE), is(equalTo(false)));
        assertThat(normalizer.normalize("true"), is(equalTo(true)));
        assertThat(normalizer.normalize("false"), is(equalTo(false)));
        assertThat(normalizer.normalize("yes"), is(equalTo(true)));
        assertThat(normalizer.normalize("no"), is(equalTo(false)));
        assertThat(normalizer.normalize("on"), is(equalTo(true)));
        assertThat(normalizer.normalize("off"), is(equalTo(false)));
        assertThat(normalizer.normalize("1"), is(equalTo(true)));
        assertThat(normalizer.normalize("0"), is(equalTo(false)));
        assertThat(normalizer.normalize("True"), is(equalTo(true)));
        assertThat(normalizer.normalize("TRUE"), is(equalTo(true)));
        assertThat(normalizer.normalize(new Object() {
            @Override
            public String toString() {
                return "true";
            }
        }), is(equalTo(true)));

        // no chance -> leaving it untouched
        assertThat(normalizer.normalize(""), is(equalTo("")));
        assertThat(normalizer.normalize("gaga"), is(equalTo("gaga")));
        assertThat(normalizer.normalize(2L), is(equalTo(2L)));
    }

    @Test
    public void testIntNormalizer() {
        Normalizer normalizer = NormalizerFactory.getNormalizer(
                ConfigDescriptionParameterBuilder.create("test", ConfigDescriptionParameter.Type.INTEGER).build());

        assertThat(normalizer.normalize(null), is(nullValue()));
        assertThat(normalizer.normalize(42), is(equalTo(new BigDecimal(42))));
        assertThat(normalizer.normalize(42L), is(equalTo(new BigDecimal(42))));
        assertThat(normalizer.normalize((byte) 42), is(equalTo(new BigDecimal(42))));
        assertThat(normalizer.normalize(42.0), is(equalTo(new BigDecimal(42))));
        assertThat(normalizer.normalize(42.0f), is(equalTo(new BigDecimal(42))));
        assertThat(normalizer.normalize(42.0d), is(equalTo(new BigDecimal(42))));
        assertThat(normalizer.normalize("42"), is(equalTo(new BigDecimal(42))));
        assertThat(normalizer.normalize("42.0"), is(equalTo(new BigDecimal(42))));

        // no chance -> leaving it untouched
        Object local = new Object();
        assertThat(normalizer.normalize(""), is(equalTo("")));
        assertThat(normalizer.normalize(local), is(equalTo(local)));
        assertThat(normalizer.normalize(42.1), is(equalTo(42.1)));
        assertThat(normalizer.normalize(42.1f), is(equalTo(42.1f)));
        assertThat(normalizer.normalize(42.1d), is(equalTo(42.1d)));
        assertThat(normalizer.normalize("42.1"), is(equalTo("42.1")));
        assertThat(normalizer.normalize("true"), is(equalTo("true")));
        assertThat(normalizer.normalize("gaga"), is(equalTo("gaga")));
    }

    @Test
    public void testDecimalNormalizer() {
        Normalizer normalizer = NormalizerFactory.getNormalizer(
                ConfigDescriptionParameterBuilder.create("test", ConfigDescriptionParameter.Type.DECIMAL).build());

        assertThat(normalizer.normalize(null), is(nullValue()));
        assertThat(normalizer.normalize(42), is(equalTo(new BigDecimal("42.0"))));
        assertThat(normalizer.normalize(42L), is(equalTo(new BigDecimal("42.0"))));
        assertThat(normalizer.normalize((byte) 42), is(equalTo(new BigDecimal("42.0"))));
        assertThat(normalizer.normalize(42.0), is(equalTo(new BigDecimal("42.0"))));
        assertThat(normalizer.normalize(42.0f), is(equalTo(new BigDecimal("42.0"))));
        assertThat(normalizer.normalize(42.0d), is(equalTo(new BigDecimal("42.0"))));
        assertThat(normalizer.normalize(42.1), is(equalTo(new BigDecimal("42.1"))));
        assertThat(normalizer.normalize(42.88f), is(equalTo(new BigDecimal("42.88"))));
        assertThat(normalizer.normalize(42.88d), is(equalTo(new BigDecimal("42.88"))));
        assertThat(normalizer.normalize("42"), is(equalTo(new BigDecimal("42.0"))));
        assertThat(normalizer.normalize("42.0"), is(equalTo(new BigDecimal("42.0"))));
        assertThat(normalizer.normalize("42.1"), is(equalTo(new BigDecimal("42.1"))));
        assertThat(normalizer.normalize("42.11"), is(equalTo(new BigDecimal("42.11"))));
        assertThat(normalizer.normalize("42.00"), is(equalTo(new BigDecimal("42.0"))));

        // no chance -> leaving it untouched
        Object local = new Object();
        assertThat(normalizer.normalize(""), is(equalTo("")));
        assertThat(normalizer.normalize(local), is(equalTo(local)));
        assertThat(normalizer.normalize("true"), is(equalTo("true")));
        assertThat(normalizer.normalize("gaga"), is(equalTo("gaga")));
    }

    @Test
    public void testTextNormalizer() {
        Normalizer normalizer = NormalizerFactory.getNormalizer(
                ConfigDescriptionParameterBuilder.create("test", ConfigDescriptionParameter.Type.TEXT).build());

        assertThat(normalizer.normalize(null), is(nullValue()));
        assertThat(normalizer.normalize(""), is(equalTo("")));
        assertThat(normalizer.normalize(42), is(equalTo("42")));
        assertThat(normalizer.normalize(42L), is(equalTo("42")));
        assertThat(normalizer.normalize((byte) 42), is(equalTo("42")));
        assertThat(normalizer.normalize(42.0), is(equalTo("42.0")));
        assertThat(normalizer.normalize(42.0f), is(equalTo("42.0")));
        assertThat(normalizer.normalize(42.0d), is(equalTo("42.0")));
        assertThat(normalizer.normalize(42.1), is(equalTo("42.1")));
        assertThat(normalizer.normalize(42.88f), is(equalTo("42.88")));
        assertThat(normalizer.normalize(42.88d), is(equalTo("42.88")));
        assertThat(normalizer.normalize(true), is(equalTo("true")));
        assertThat(normalizer.normalize("true"), is(equalTo("true")));
        assertThat(normalizer.normalize("null"), is(equalTo("null")));
        assertThat(normalizer.normalize("gaga"), is(equalTo("gaga")));
    }

    @Test
    public void testListNormalizer() {
        Normalizer normalizer = NormalizerFactory.getNormalizer(ConfigDescriptionParameterBuilder
                .create("test", ConfigDescriptionParameter.Type.BOOLEAN).withMultiple(true).build());

        assertThat(normalizer.normalize(null), is(nullValue()));

        List<Boolean> expectedList = List.of(true, false, true);

        assertThat(normalizer.normalize(List.of(true, false, true)), is(equalTo(expectedList)));
        assertThat(normalizer.normalize(List.of(true, false, true).toArray()), is(equalTo(expectedList)));
        assertThat(normalizer.normalize(new TreeSet<>(List.of(false, true))), is(equalTo(List.of(false, true))));
        assertThat(normalizer.normalize(List.of(true, "false", true)), is(equalTo(expectedList)));
        assertThat(normalizer.normalize(List.of(true, 0, "true")), is(equalTo(expectedList)));
    }
}
