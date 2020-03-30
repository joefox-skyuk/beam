/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.beam.sdk.util;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/** Unit tests for {@link UnownedOutputStream}. */
@RunWith(JUnit4.class)
public class UnownedOutputStreamTest {
  @Rule public ExpectedException expectedException = ExpectedException.none();
  private ByteArrayOutputStream baos;
  private UnownedOutputStream os;

  @Before
  public void setup() {
    baos = new ByteArrayOutputStream();
    os = new UnownedOutputStream(baos);
  }

  @Test
  public void testHashCodeEqualsAndToString() throws Exception {
    assertEquals(baos.hashCode(), os.hashCode());
    assertEquals("UnownedOutputStream{out=" + baos + "}", os.toString());
    assertEquals(new UnownedOutputStream(baos), os);
  }

  @Test
  public void testClosingThrows() throws Exception {
    expectedException.expect(UnsupportedOperationException.class);
    expectedException.expectMessage("Caller does not own the underlying");
    os.close();
  }

  @Test
  public void testWrite() throws IOException {
    ByteArrayOutputStream expected = new ByteArrayOutputStream();
    ByteArrayOutputStream actual = new ByteArrayOutputStream();
    UnownedOutputStream osActual = new UnownedOutputStream(actual);

    writeToBoth(expected, osActual, "Hello World!".getBytes(StandardCharsets.UTF_8));
    writeToBoth(expected, osActual, "Welcome!".getBytes(StandardCharsets.UTF_8));

    assertArrayEquals(expected.toByteArray(), actual.toByteArray());
  }

  private static void writeToBoth(OutputStream a, OutputStream b, byte[] data) throws IOException {
    a.write(data);
    b.write(data);
  }
}
