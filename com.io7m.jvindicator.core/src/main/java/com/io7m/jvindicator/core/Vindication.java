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

package com.io7m.jvindicator.core;

import java.math.BigInteger;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

/**
 * Functions to validate servlet arguments.
 */

public final class Vindication
{
  private Vindication()
  {

  }

  /**
   * @return A parameter check that simply returns the string
   */

  public static VParameterCheckType<String> strings()
  {
    return value -> value;
  }

  /**
   * @return A boolean parser
   */

  public static VParameterCheckType<Boolean> booleans()
  {
    return value -> {
      return switch (value) {
        case "true" -> Boolean.TRUE;
        case "false" -> Boolean.FALSE;
        default -> throw new IllegalArgumentException(
          "Could not parse the value %s as a boolean."
            .formatted(value)
        );
      };
    };
  }

  /**
   * @return A UUID parser
   */

  public static VParameterCheckType<UUID> uuids()
  {
    return value -> {
      try {
        return UUID.fromString(value);
      } catch (final IllegalArgumentException e) {
        throw new IllegalArgumentException(
          "Could not parse the value %s as a UUID: %s"
            .formatted(value, e.getMessage())
        );
      }
    };
  }

  /**
   * @return A timestamp parser
   */

  public static VParameterCheckType<OffsetDateTime> offsetDateTimes()
  {
    return OffsetDateTime::parse;
  }

  /**
   * @return A big integer parser
   */

  public static VParameterCheckType<BigInteger> integerBig()
  {
    return value -> {
      try {
        return new BigInteger(value);
      } catch (final NumberFormatException e) {
        throw new NumberFormatException(
          "Could not parse the value %s as an integer."
            .formatted(value)
        );
      }
    };
  }

  /**
   * @return An unsigned integer parser
   */

  public static VParameterCheckType<Integer> integerUnsigned()
  {
    return value -> {
      try {
        return Integer.valueOf(Integer.parseUnsignedInt(value));
      } catch (final NumberFormatException e) {
        throw new NumberFormatException(
          "Could not parse the value %s as an unsigned integer."
            .formatted(value)
        );
      }
    };
  }

  /**
   * @return An unsigned integer parser
   */

  public static VParameterCheckType<Long> integerUnsignedLong()
  {
    return value -> {
      try {
        return Long.valueOf(Long.parseUnsignedLong(value));
      } catch (final NumberFormatException e) {
        throw new NumberFormatException(
          "Could not parse the value %s as an unsigned integer."
            .formatted(value)
        );
      }
    };
  }

  /**
   * @return A signed integer parser
   */

  public static VParameterCheckType<Integer> integerSigned()
  {
    return value -> {
      try {
        return Integer.valueOf(Integer.parseInt(value));
      } catch (final NumberFormatException e) {
        throw new NumberFormatException(
          "Could not parse the value %s as a signed integer."
            .formatted(value)
        );
      }
    };
  }

  /**
   * @return A signed integer parser
   */

  public static VParameterCheckType<Long> integerSignedLong()
  {
    return value -> {
      try {
        return Long.valueOf(Long.parseLong(value));
      } catch (final NumberFormatException e) {
        throw new NumberFormatException(
          "Could not parse the value %s as a signed integer."
            .formatted(value)
        );
      }
    };
  }

  /**
   * @return A floating point parser
   */

  public static VParameterCheckType<Double> doubles()
  {
    return value -> {
      try {
        return Double.valueOf(Double.parseDouble(value));
      } catch (final NumberFormatException e) {
        throw new NumberFormatException(
          "Could not parse the value %s as a floating point value."
            .formatted(value)
        );
      }
    };
  }

  /**
   * Start vindication using the standard pretty formatter and throwing
   * {@code Exception} on errors.
   *
   * @return A builder
   */

  public static VindicationBuilderType<Exception> start()
  {
    return startWithFormatter(prettyFormatter(Exception::new));
  }

  /**
   * Start vindication using the standard pretty formatter and throwing
   * {@code Exception} on errors.
   *
   * @param inExceptions An exception supplier
   * @param <E>          The type of exceptions
   *
   * @return A builder
   */

  public static <E extends Exception> VindicationBuilderType<E> startWithExceptions(
    final Function<String, E> inExceptions)
  {
    return startWithFormatter(prettyFormatter(inExceptions));
  }

  /**
   * Start vindication.
   *
   * @param <E>          The type of thrown exceptions
   * @param inExceptions An exception formatter
   *
   * @return A builder
   */

  public static <E extends Exception> VindicationBuilderType<E> startWithFormatter(
    final Function<Map<String, String>, E> inExceptions)
  {
    return new Builder<>(inExceptions);
  }

  /**
   * A standard pretty formatter that lists every invalid or missing parameter.
   *
   * @param exceptions A creator of exceptions
   * @param <E>        The type of exception
   *
   * @return The formatter
   */

  public static <E extends Exception> Function<Map<String, String>, E> prettyFormatter(
    final Function<String, E> exceptions)
  {
    return errors -> {
      final var separator = System.lineSeparator();
      final var msg = new StringBuilder(128);
      msg.append("One or more parameters failed validation.");
      msg.append(separator);

      for (final var entry : errors.entrySet()) {
        final var name = entry.getKey();
        final var value = entry.getValue();
        msg.append(name);
        msg.append(": ");
        msg.append(value);
        msg.append(separator);
      }

      return exceptions.apply(msg.toString());
    };
  }

  private static final class Parameter
    implements VParameterType<Object>
  {
    private final String name;
    private final VParameterCheckType<?> check;
    private final boolean isOptional;
    private boolean checked;
    private Object parsed;

    Parameter(
      final String inName,
      final VParameterCheckType<?> inCheck,
      final boolean inIsOptional)
    {
      this.name =
        Objects.requireNonNull(inName, "name");
      this.check =
        Objects.requireNonNull(inCheck, "check");
      this.isOptional =
        inIsOptional;
    }

    @Override
    public Object get()
    {
      if (!this.checked) {
        throw new IllegalStateException(
          "Parameters have not yet been validated!");
      }
      return this.parsed;
    }
  }

  private static final class Builder<E extends Exception>
    implements VindicationBuilderType<E>
  {
    private Function<Map<String, String>, E> onFailure;
    private Map<String, Parameter> parameters;

    private Builder(
      final Function<Map<String, String>, E> inExceptions)
    {
      this.onFailure =
        Objects.requireNonNull(inExceptions, "exceptions");
      this.parameters =
        new HashMap<>();
    }

    @Override
    public <T> VParameterType<T> addRequiredParameter(
      final String name,
      final VParameterCheckType<T> check)
    {
      Objects.requireNonNull(name, "name");
      Objects.requireNonNull(check, "check");

      this.checkParameterNotRegistered(name);
      final var parameter = new Parameter(name, check, false);
      this.parameters.put(name, parameter);
      return (VParameterType<T>) parameter;
    }

    private void checkParameterNotRegistered(
      final String name)
    {
      if (this.parameters.containsKey(name)) {
        throw new IllegalArgumentException(
          "A parameter named %s has already been registered."
            .formatted(name));
      }
    }

    @Override
    public <T> VParameterType<Optional<T>> addOptionalParameter(
      final String name,
      final VParameterCheckType<T> check)
    {
      Objects.requireNonNull(name, "name");
      Objects.requireNonNull(check, "check");

      this.checkParameterNotRegistered(name);
      final var parameter =
        new Parameter(
          name,
          value -> Optional.of(check.check(value)),
          true
        );
      this.parameters.put(name, parameter);
      return (VParameterType<Optional<T>>) (Object) parameter;
    }

    @Override
    public void check(
      final Map<String, String[]> input)
      throws E
    {
      Objects.requireNonNull(input, "input");

      final var exceptions = new ArrayList<Throwable>();
      final var errors = new HashMap<String, String>();
      for (final var parameter : this.parameters.values()) {
        parameter.checked = true;

        final var inputValues = input.get(parameter.name);
        if (inputValues == null) {
          if (!parameter.isOptional) {
            errors.put(
              parameter.name,
              "The parameter is required but was missing."
            );
          }
          parameter.parsed = Optional.empty();
          continue;
        }

        for (final var value : inputValues) {
          try {
            parameter.parsed = parameter.check.check(value);
          } catch (final Exception e) {
            exceptions.add(e);
            errors.put(parameter.name, e.getMessage());
          }
        }
      }

      if (!errors.isEmpty()) {
        final var ex = this.onFailure.apply(errors);
        exceptions.forEach(ex::addSuppressed);
        throw ex;
      }
    }
  }
}
