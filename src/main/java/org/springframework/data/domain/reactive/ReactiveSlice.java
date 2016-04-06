/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.data.domain.reactive;

import reactor.core.publisher.Mono;

import java.util.List;

import org.reactivestreams.Publisher;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * A slice of data that indicates whether there's a next or previous slice available. Allows to obtain a
 * {@link ReactivePageable} to request a previous or next {@link ReactiveSlice}.
 *
 * @author Mark Paluch
 * @since 1.13
 */
public interface ReactiveSlice<T> extends Publisher<T> {

	/**
	 * Returns the number of the current {@link ReactiveSlice}. Is always non-negative.
	 *
	 * @return the number of the current {@link ReactiveSlice}.
	 */
	int getNumber();

	/**
	 * Returns the size of the {@link ReactiveSlice}.
	 *
	 * @return the size of the {@link ReactiveSlice}.
	 */
	int getSize();

	/**
	 * Returns the number of elements currently on this {@link ReactiveSlice}.
	 *
	 * @return the number of elements currently on this {@link ReactiveSlice}.
	 */
	Mono<Long> count();

	/**
	 * Returns whether the {@link ReactiveSlice} has content at all.
	 *
	 * @return
	 */
	Mono<Boolean> hasElements();

	/**
	 * Returns the sorting parameters for the {@link ReactiveSlice}.
	 *
	 * @return
	 */
	Sort getSort();

	/**
	 * Returns whether the current {@link ReactiveSlice} is the first one.
	 *
	 * @return
	 */
	Mono<Boolean> isFirst();

	/**
	 * Returns whether the current {@link ReactiveSlice} is the last one.
	 *
	 * @return
	 */
	Mono<Boolean> isLast();

	/**
	 * Returns if there is a next {@link ReactiveSlice}.
	 *
	 * @return if there is a next {@link ReactiveSlice}.
	 */
	Mono<Boolean> hasNext();

	/**
	 * Returns if there is a previous {@link ReactiveSlice}.
	 *
	 * @return if there is a previous {@link ReactiveSlice}.
	 */
	Mono<Boolean> hasPrevious();

	/**
	 * Returns the {@link Pageable} to request the next {@link ReactiveSlice}. Can be {@literal null} in case the current
	 * {@link ReactiveSlice} is already the last one. Clients should check {@link #hasNext()} before calling this method to make
	 * sure they receive a non-{@literal null} value.
	 *
	 * @return
	 */
	Mono<Pageable> nextPageable();

	/**
	 * Returns the {@link Pageable} to request the previous {@link ReactiveSlice}. Can be {@literal null} in case the current
	 * {@link ReactiveSlice} is already the first one. Clients should check {@link #hasPrevious()} before calling this method make
	 * sure receive a non-{@literal null} value.
	 *
	 * @return
	 */
	Mono<Pageable> previousPageable();

	/**
	 * Returns a new {@link ReactiveSlice} with the content of the current one mapped by the given {@link Converter}.
	 *
	 * @param converter must not be {@literal null}.
	 * @return a new {@link ReactiveSlice} with the content of the current one mapped by the given {@link Converter}.
	 * @since 1.10
	 */
	<S> ReactiveSlice<S> map(Converter<? super T, ? extends S> converter);
}
