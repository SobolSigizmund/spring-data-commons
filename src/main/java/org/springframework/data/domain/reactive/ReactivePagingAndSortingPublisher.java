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

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * Extension of {@link FindPublisher} to provide additional methods to retrieve entities using the pagination and
 * sorting abstraction.
 *
 * @author Mark Paluch
 * @since 1.13
 * @see Sort
 * @see Pageable
 * @see ReactivePage
 */
public interface ReactivePagingAndSortingPublisher<T> extends FindPublisher<T> {

	/**
	 * Sets the limit to apply.
	 *
	 * @param limit the limit, which may be zero
	 * @return a new {@link ReactivePagingAndSortingPublisher} with sorting applied.
	 */
	@Override
	ReactivePagingAndSortingPublisher<T> limit(int limit);

	/**
	 * Sets the number of elements to skip.
	 *
	 * @param skip the number of elements to skip
	 * @return this
	 * @return a new {@link ReactivePagingAndSortingPublisher} with sorting applied.
	 */
	@Override
	ReactivePagingAndSortingPublisher<T> skip(int skip);

	/**
	 * Applies the sorting.
	 *
	 * @param sort the sort option, must not be {@literal null}.
	 * @return a new {@link ReactivePagingAndSortingPublisher} with sorting applied.
	 * @throws IllegalArgumentException in case the given {@code sort} is {@literal null}
	 */
	ReactivePagingAndSortingPublisher<T> sort(Sort sort);

	/**
	 * Returns a {@link ReactivePage} of entities meeting the paging restriction provided in the {@code Pageable} object.
	 *
	 * @param pageable must not be {@literal null}.
	 * @return a reactive page of entities
	 * @throws IllegalArgumentException in case the given {@code pageable} is {@literal null}
	 */
	ReactivePage<T> page(Pageable pageable);
}
