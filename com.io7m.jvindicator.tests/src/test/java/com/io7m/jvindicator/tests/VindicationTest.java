/*
 * Copyright © 2022 Mark Raynsford <code@io7m.com> https://www.io7m.com
 *
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY
 * SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR
 * IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */


package com.io7m.jvindicator.tests;

import com.io7m.jvindicator.core.Vindication;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public final class VindicationTest
{
  /**
   * Parameters cannot be specified multiple times.
   */

  @Test
  public void testRequiredParameterTwice()
  {
    final var v =
      Vindication.start();
    final var p0 =
      v.addRequiredParameter("p0", UUID::fromString);

    final var ex =
      assertThrows(IllegalArgumentException.class, () -> {
        v.addRequiredParameter("p0", UUID::fromString);
      });

    assertTrue(ex.getMessage().contains("p0"));
  }

  /**
   * Required parameters are required.
   *
   * @throws Exception On errors
   */

  @Test
  public void testRequiredParameterCheck0()
    throws Exception
  {
    final var v =
      Vindication.start();
    final var p0 =
      v.addRequiredParameter("p0", UUID::fromString);
    final var p1 =
      v.addRequiredParameter("p1", Integer::parseUnsignedInt);

    v.check(Map.ofEntries(
      Map.entry("p0", new String[]{"98da4b91-76b7-42ef-ba03-3bed60fd73db"}),
      Map.entry("p1", new String[]{"23"})
    ));

    assertEquals(
      UUID.fromString("98da4b91-76b7-42ef-ba03-3bed60fd73db"),
      p0.get()
    );
    assertEquals(
      Integer.parseUnsignedInt("23"),
      p1.get()
    );
  }

  /**
   * Required parameters are required.
   *
   * @throws Exception On errors
   */

  @Test
  public void testRequiredParameterCheckWithExceptions()
    throws Exception
  {
    final var v =
      Vindication.startWithExceptions(RuntimeException::new);
    final var p0 =
      v.addRequiredParameter("p0", UUID::fromString);
    final var p1 =
      v.addRequiredParameter("p1", Integer::parseUnsignedInt);

    v.check(Map.ofEntries(
      Map.entry("p0", new String[]{"98da4b91-76b7-42ef-ba03-3bed60fd73db"}),
      Map.entry("p1", new String[]{"23"})
    ));

    assertEquals(
      UUID.fromString("98da4b91-76b7-42ef-ba03-3bed60fd73db"),
      p0.get()
    );
    assertEquals(
      Integer.parseUnsignedInt("23"),
      p1.get()
    );
  }

  /**
   * Required parameters are required.
   *
   * @throws Exception On errors
   */

  @Test
  public void testRequiredParameterCheck1()
    throws Exception
  {
    final var v =
      Vindication.start();
    final var p0 =
      v.addRequiredParameter("p0", UUID::fromString);
    final var p1 =
      v.addRequiredParameter("p1", Integer::parseUnsignedInt);

    final var ex =
      assertThrows(Exception.class, () -> {
        v.check(Map.ofEntries());
      });

    assertTrue(ex.getMessage().contains("p0"));
  }

  /**
   * Required parameters are required to parse.
   *
   * @throws Exception On errors
   */

  @Test
  public void testRequiredParameterCheck2()
    throws Exception
  {
    final var v =
      Vindication.start();
    final var p0 =
      v.addRequiredParameter("p0", UUID::fromString);
    final var p1 =
      v.addRequiredParameter("p1", Integer::parseUnsignedInt);

    final var ex =
      assertThrows(Exception.class, () -> {
        v.check(Map.ofEntries(
          Map.entry("p0", new String[]{"23"}),
          Map.entry("p1", new String[]{"98da4b91-76b7-42ef-ba03-3bed60fd73db"})
        ));
      });

    assertTrue(ex.getMessage().contains("p0"));
    assertTrue(ex.getMessage().contains("p1"));
  }

  /**
   * Required parameters are required.
   *
   * @throws Exception On errors
   */

  @Test
  public void testRequiredParameterCheckIntegers()
    throws Exception
  {
    final var v =
      Vindication.start();
    final var p0 =
      v.addRequiredParameter("u0", Vindication.integerUnsigned());
    final var p1 =
      v.addRequiredParameter("u1", Vindication.integerUnsignedLong());
    final var p2 =
      v.addRequiredParameter("s0", Vindication.integerSigned());
    final var p3 =
      v.addRequiredParameter("s1", Vindication.integerSignedLong());
    final var p4 =
      v.addRequiredParameter("i0", Vindication.integerBig());

    v.check(Map.ofEntries(
      Map.entry("u0", new String[]{"23"}),
      Map.entry("u1", new String[]{"24"}),
      Map.entry("s0", new String[]{"-23"}),
      Map.entry("s1", new String[]{"-24"}),
      Map.entry("i0", new String[]{"-4703919738795935661825"})
    ));

    assertEquals(23, p0.get());
    assertEquals(24L, p1.get());
    assertEquals(-23, p2.get());
    assertEquals(-24L, p3.get());
    assertEquals(new BigInteger("-4703919738795935661825"), p4.get());
  }

  /**
   * Required parameters are required.
   *
   * @throws Exception On errors
   */

  @Test
  public void testRequiredParameterCheckIntegersInvalid()
    throws Exception
  {
    final var v =
      Vindication.start();
    final var p0 =
      v.addRequiredParameter("u0", Vindication.integerUnsigned());
    final var p1 =
      v.addRequiredParameter("u1", Vindication.integerUnsignedLong());
    final var p2 =
      v.addRequiredParameter("s0", Vindication.integerSigned());
    final var p3 =
      v.addRequiredParameter("s1", Vindication.integerSignedLong());
    final var p4 =
      v.addRequiredParameter("i0", Vindication.integerBig());

    final var ex =
      assertThrows(Exception.class, () -> {
        v.check(Map.ofEntries(
          Map.entry("u0", new String[]{"x"}),
          Map.entry("u1", new String[]{"y"}),
          Map.entry("s0", new String[]{"z"}),
          Map.entry("s1", new String[]{"w"}),
          Map.entry("i0", new String[]{"q"})
        ));
      });

    assertTrue(ex.getMessage().contains("u0"));
    assertTrue(ex.getMessage().contains("u1"));
    assertTrue(ex.getMessage().contains("s0"));
    assertTrue(ex.getMessage().contains("s1"));
    assertTrue(ex.getMessage().contains("i0"));
  }

  /**
   * Required parameters are required.
   *
   * @throws Exception On errors
   */

  @Test
  public void testRequiredParameterCheckBooleans()
    throws Exception
  {
    final var v =
      Vindication.start();
    final var p0 =
      v.addRequiredParameter("u0", Vindication.booleans());
    final var p1 =
      v.addRequiredParameter("u1", Vindication.booleans());

    v.check(Map.ofEntries(
      Map.entry("u0", new String[]{"true"}),
      Map.entry("u1", new String[]{"false"})
    ));

    assertEquals(true, p0.get());
    assertEquals(false, p1.get());
  }

  /**
   * Required parameters are required.
   *
   * @throws Exception On errors
   */

  @Test
  public void testRequiredParameterCheckBooleansInvalid()
    throws Exception
  {
    final var v =
      Vindication.start();
    final var p0 =
      v.addRequiredParameter("u0", Vindication.booleans());
    final var p1 =
      v.addRequiredParameter("u1", Vindication.booleans());

    final var ex =
      assertThrows(Exception.class, () -> {
        v.check(Map.ofEntries(
          Map.entry("u0", new String[]{"x"}),
          Map.entry("u1", new String[]{"y"})
        ));
      });

    assertTrue(ex.getMessage().contains("u0"));
    assertTrue(ex.getMessage().contains("u1"));
  }

  /**
   * Required parameters are required.
   *
   * @throws Exception On errors
   */

  @Test
  public void testRequiredParameterCheckStrings()
    throws Exception
  {
    final var v =
      Vindication.start();
    final var p0 =
      v.addRequiredParameter("u0", Vindication.strings());

    v.check(Map.ofEntries(
      Map.entry("u0", new String[]{"true"})
    ));

    assertEquals("true", p0.get());
  }

  /**
   * Required parameters are required.
   *
   * @throws Exception On errors
   */

  @Test
  public void testRequiredParameterCheckDoubles()
    throws Exception
  {
    final var v =
      Vindication.start();
    final var p0 =
      v.addRequiredParameter("u0", Vindication.doubles());

    v.check(Map.ofEntries(
      Map.entry("u0", new String[]{"23"})
    ));

    assertEquals(23, p0.get());
  }

  /**
   * Required parameters are required.
   *
   * @throws Exception On errors
   */

  @Test
  public void testRequiredParameterCheckDoublesInvalid()
    throws Exception
  {
    final var v =
      Vindication.start();
    final var p0 =
      v.addRequiredParameter("u0", Vindication.doubles());

    final var ex =
      assertThrows(Exception.class, () -> {
        v.check(Map.ofEntries(
          Map.entry("u0", new String[]{"x"})
        ));
      });

    assertTrue(ex.getMessage().contains("u0"));
  }


  /**
   * Required parameters are required.
   *
   * @throws Exception On errors
   */

  @Test
  public void testRequiredParameterCheckTimestamp()
    throws Exception
  {
    final var v =
      Vindication.start();
    final var p0 =
      v.addRequiredParameter("u0", Vindication.offsetDateTimes());

    v.check(Map.ofEntries(
      Map.entry("u0", new String[]{"2000-01-01T00:00:00+00:00"})
    ));

    assertEquals(OffsetDateTime.parse("2000-01-01T00:00:00+00:00"), p0.get());
  }

  /**
   * Required parameters are required.
   *
   * @throws Exception On errors
   */

  @Test
  public void testRequiredParameterCheckTimestampInvalid()
    throws Exception
  {
    final var v =
      Vindication.start();
    final var p0 =
      v.addRequiredParameter("u0", Vindication.offsetDateTimes());

    final var ex =
      assertThrows(Exception.class, () -> {
        v.check(Map.ofEntries(
          Map.entry("u0", new String[]{"x"})
        ));
      });

    assertTrue(ex.getMessage().contains("u0"));
  }

  /**
   * Checks must be performed before parameters can be read.
   *
   * @throws Exception On errors
   */

  @Test
  public void testRequiredParameterCheckMissed()
    throws Exception
  {
    final var v =
      Vindication.start();
    final var p0 =
      v.addRequiredParameter("u0", Vindication.doubles());

    final var ex =
      assertThrows(IllegalStateException.class, p0::get);

    assertTrue(
      ex.getMessage().contains("Parameters have not yet been validated!")
    );
  }

  /**
   * Required parameters are required.
   *
   * @throws Exception On errors
   */

  @Test
  public void testRequiredParameterCheckUUID()
    throws Exception
  {
    final var v =
      Vindication.start();
    final var p0 =
      v.addRequiredParameter("u0", Vindication.uuids());

    v.check(Map.ofEntries(
      Map.entry("u0", new String[]{"98da4b91-76b7-42ef-ba03-3bed60fd73db"})
    ));

    assertEquals(
      UUID.fromString("98da4b91-76b7-42ef-ba03-3bed60fd73db"),
      p0.get());
  }

  /**
   * Required parameters are required.
   *
   * @throws Exception On errors
   */

  @Test
  public void testRequiredParameterCheckUUIDInvalid()
    throws Exception
  {
    final var v =
      Vindication.start();
    final var p0 =
      v.addRequiredParameter("u0", Vindication.uuids());

    final var ex =
      assertThrows(Exception.class, () -> {
        v.check(Map.ofEntries(
          Map.entry("u0", new String[]{"x"})
        ));
      });

    assertTrue(ex.getMessage().contains("u0"));
  }


  /**
   * Optional parameters are optional.
   *
   * @throws Exception On errors
   */

  @Test
  public void testOptionalParameterCheckIntegersInvalid()
    throws Exception
  {
    final var v =
      Vindication.start();
    final var p0 =
      v.addOptionalParameter("u0", Vindication.integerUnsigned());
    final var p1 =
      v.addOptionalParameter("u1", Vindication.integerUnsignedLong());
    final var p2 =
      v.addOptionalParameter("s0", Vindication.integerSigned());
    final var p3 =
      v.addOptionalParameter("s1", Vindication.integerSignedLong());
    final var p4 =
      v.addOptionalParameter("i0", Vindication.integerBig());

    final var ex =
      assertThrows(Exception.class, () -> {
        v.check(Map.ofEntries(
          Map.entry("u0", new String[]{"x"}),
          Map.entry("u1", new String[]{"y"}),
          Map.entry("s0", new String[]{"z"}),
          Map.entry("s1", new String[]{"w"}),
          Map.entry("i0", new String[]{"q"})
        ));
      });

    assertTrue(ex.getMessage().contains("u0"));
    assertTrue(ex.getMessage().contains("u1"));
    assertTrue(ex.getMessage().contains("s0"));
    assertTrue(ex.getMessage().contains("s1"));
    assertTrue(ex.getMessage().contains("i0"));
  }

  /**
   * Optional parameters are optional.
   *
   * @throws Exception On errors
   */

  @Test
  public void testOptionalParameterCheckIntegersMissing()
    throws Exception
  {
    final var v =
      Vindication.start();
    final var p0 =
      v.addOptionalParameter("u0", Vindication.integerUnsigned());
    final var p1 =
      v.addOptionalParameter("u1", Vindication.integerUnsignedLong());
    final var p2 =
      v.addOptionalParameter("s0", Vindication.integerSigned());
    final var p3 =
      v.addOptionalParameter("s1", Vindication.integerSignedLong());
    final var p4 =
      v.addOptionalParameter("i0", Vindication.integerBig());

    v.check(Map.ofEntries(

    ));

    assertEquals(Optional.empty(), p0.get());
    assertEquals(Optional.empty(), p1.get());
    assertEquals(Optional.empty(), p2.get());
    assertEquals(Optional.empty(), p3.get());
    assertEquals(Optional.empty(), p4.get());
  }
}
