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

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Pageable;
import org.springframework.util.Assert;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Basic {@code ReactivePage} implementation.
 *
 * @param <T> the type of which the page consists.
 * @author Mark Paluch
 * @since 1.13
 */
public class ReactivePageImpl<T> extends ChunkPublisher<T> implements ReactivePage<T> {

	private static final long serialVersionUID = 867755909294344406L;

	private final Mono<Long> total;
	private final Pageable pageable;

	/**
	 * Constructor of {@code PageImpl}.
	 *
	 * @param upstream the upstream publisher of this {@link ReactivePage}, must not be {@literal null}.
	 * @param pageable the paging information, can be {@literal null}.
	 * @param total the total amount of items available, must not be {@literal null}. The total might be adapted
	 *          considering the length of the content given, if it is going to be the content of the last page. This is in
	 *          place to mitigate inconsistencies.
	 */
	public ReactivePageImpl(Flux<T> upstream, Pageable pageable, Mono<Long> total) {

		super(upstream, pageable);
		Assert.notNull(total, "Total must not be null!");

		this.pageable = pageable;

		this.total = Mono.when(upstream.count(), total).map(tuple -> {

			long elements = tuple.t1;
			long totalElements = tuple.t2;

			if (elements > 0 && pageable != null && pageable.getOffset() + pageable.getPageSize() > totalElements) {
				return pageable.getOffset() + elements;
			}
			return totalElements;
		});

	}

	/* (non-Javadoc)
	 * @see org.springframework.data.domain.reactive.ReactivePage#getTotalPages()
	 */
	@Override
	public Mono<Integer> getTotalPages() {
		if (getSize() == 0) {
			return Mono.just(1);
		}

		return getTotalElements().map(total -> (int) Math.ceil((double) total / (double) getSize()));
	}

	/* (non-Javadoc)
	 * @see org.springframework.data.domain.reactive.ReactivePage#getTotalElements()
	 */
	@Override
	public Mono<Long> getTotalElements() {
		return total;
	}

	/* (non-Javadoc)
	 * @see org.springframework.data.domain.reactive.ReactiveSlice#hasNext()
	 */
	@Override
	public Mono<Boolean> hasNext() {
		return getTotalPages().map(totalPages -> getNumber() + 1 < totalPages);
	}

	/* (non-Javadoc)
	 * @see org.springframework.data.domain.reactive.ChunkPublisher#isLast()
	 */
	public Mono<Boolean> isLast() {
		return hasNext().map(b -> !b);
	}

	/* (non-Javadoc)
	 * @see org.springframework.data.domain.reactive.ReactiveSlice#map(org.springframework.core.convert.converter.Converter)
	 */
	@Override
	public <S> ReactivePage<S> map(Converter<? super T, ? extends S> converter) {
		return new ReactivePageImpl<S>(getConvertedContent(converter), pageable, total);
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {

		if (this == obj) {
			return true;
		}

		if (!(obj instanceof ReactivePageImpl<?>)) {
			return false;
		}

		ReactivePageImpl<?> that = (ReactivePageImpl<?>) obj;

		return this.total.equals(that.total) && super.equals(obj);
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {

		int result = 17;

		result += 31 * total.hashCode();
		result += 31 * super.hashCode();

		return result;
	}
}
