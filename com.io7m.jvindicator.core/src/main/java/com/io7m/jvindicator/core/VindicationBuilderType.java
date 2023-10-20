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

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * The vindication builder. Parameters must be added and then all parameters
 * checked with {@code check()}.
 *
 * @param <E> The type of thrown exceptions
 */

public interface VindicationBuilderType<E extends Exception>
{
  /**
   * Add a required parameter. The parameter, if present, will be parsed with
   * the given {@code check} function. If the parameter is not present, then
   * {@link #check(Map)} will raise an exception.
   *
   * @param name  The parameter name
   * @param check The check
   * @param <T>   The type of parameter values
   *
   * @return A parameter
   *
   * @throws IllegalArgumentException If a parameter with the given name is
   *                                  already defined
   */

  <T> VParameterType<T> addRequiredParameter(
    String name,
    VParameterCheckType<T> check)
    throws IllegalArgumentException;

  /**
   * Add an optional parameter. The parameter, if present, will be parsed with
   * the given {@code check} function.
   *
   * @param name  The parameter name
   * @param check The check
   * @param <T>   The type of parameter values
   *
   * @return A parameter
   *
   * @throws IllegalArgumentException If a parameter with the given name is
   *                                  already defined
   */

  <T> VParameterType<Optional<T>> addOptionalParameter(
    String name,
    VParameterCheckType<T> check)
    throws IllegalArgumentException;

  /**
   * Check the given parameters.
   *
   * @param parameters The parameters
   *
   * @throws E On errors
   */

  default void checkArrayTyped(
    final Map<String, String[]> parameters)
    throws E
  {
    this.check(
      parameters.entrySet()
        .stream()
        .map(e -> Map.entry(e.getKey(), List.of(e.getValue())))
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
    );
  }

  /**
   * Check the given parameters.
   *
   * @param parameters The parameters
   *
   * @throws E On errors
   */

  void check(Map<String, List<String>> parameters)
    throws E;
}
