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

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.springframework.data.domain.UnitTestUtils.*;

import java.util.Arrays;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.test.TestSubscriber;

/**
 * Unit test for {@link ReactivePage}.
 *
 * @author Mark Paluch
 * @soundtrack Imperio - Nostra Culpa (Extended Mix)
 */
public class ReactivePageImplUnitTests {

	/**
	 * @see DATACMNS-836
	 */
	@Test
	@Ignore("equality checks also for content which is no longer possible when staying reactive")
	public void assertEqualsForSimpleSetup() throws Exception {

		Flux<String> foo = Flux.fromStream(Arrays.asList("Foo").stream()).cache();
		Flux<String> foo2 = Flux.fromStream(Arrays.asList("Foo").stream()).cache();
		ReactivePageImpl<String> page = new ReactivePageImpl<String>(foo, null, foo.count());

		assertEqualsAndHashcode(page, page);
		assertEqualsAndHashcode(page, new ReactivePageImpl<String>(foo2, null, foo2.count()));
	}

	/**
	 * @see DATACMNS-836
	 */
	@Test
	@Ignore("equality checks also for content which is no longer possible when staying reactive")
	public void assertEqualsForComplexSetup() throws Exception {

		Pageable pageable = new PageRequest(0, 10);
		Flux<String> content = Flux.fromStream(Arrays.asList("Foo").stream());

		ReactivePageImpl<String> page = new ReactivePageImpl<String>(content, pageable, Mono.just(100L));

		assertEqualsAndHashcode(page, page);
		assertEqualsAndHashcode(page, new ReactivePageImpl<String>(content, pageable, Mono.just(100L)));
		assertNotEqualsAndHashcode(page, new ReactivePageImpl<String>(content, pageable, Mono.just(100L)));
		assertNotEqualsAndHashcode(page, new ReactivePageImpl<String>(content, new PageRequest(1, 10), Mono.just(100L)));
		assertNotEqualsAndHashcode(page, new ReactivePageImpl<String>(content, new PageRequest(0, 15), Mono.just(100L)));
	}

	/**
	 * @see DATACMNS-836
	 */
	@Test(expected = IllegalArgumentException.class)
	public void preventsNullContentForSimpleSetup() throws Exception {
		new ReactivePageImpl<Object>(null, null, null);
	}

	/**
	 * @see DATACMNS-836
	 */
	@Test(expected = IllegalArgumentException.class)
	public void preventsNullContentForAdvancedSetup() throws Exception {
		new ReactivePageImpl<Object>(null, null, Mono.just(100L));
	}

	/**
	 * @see DATACMNS-836
	 */
	@Test
	public void returnsNextPageable() {

		ReactivePage<Object> page = new ReactivePageImpl<Object>(Flux.just(new Object()), new PageRequest(0, 1),
				Mono.just(100L));

		assertThat(page.isFirst().get(), is(true));
		assertThat(page.hasPrevious().get(), is(false));
		assertThat(page.previousPageable().hasElement().get(), is(false));

		assertThat(page.isLast().get(), is(false));
		assertThat(page.hasNext().get(), is(true));
		assertThat(page.nextPageable().get(), is((Pageable) new PageRequest(1, 1)));
	}

	/**
	 * @see DATACMNS-836
	 */
	@Test
	public void returnsPreviousPageable() {

		ReactivePage<Object> page = new ReactivePageImpl<Object>(Flux.just(new Object()), new PageRequest(1, 1),
				Mono.just(2L));

		assertThat(page.isFirst().get(), is(false));
		assertThat(page.hasPrevious().get(), is(true));
		assertThat(page.previousPageable().get(), is((Pageable) new PageRequest(0, 1)));

		assertThat(page.isLast().get(), is(true));
		assertThat(page.hasNext().get(), is(false));
		assertThat(page.nextPageable().get(), is(nullValue()));
	}

	/**
	 * @see DATACMNS-836
	 */
	@Test
	public void returnsTotalCorrectlyWithCachedFlux() {

		Flux<String> content = Flux.fromStream(Arrays.asList("Foo", "Bar", "Baz").stream()).cache();

		ReactivePage<String> page = new ReactivePageImpl<>(content, null, content.count());

		assertThat(page.isFirst().get(), is(true));
		assertThat(page.hasPrevious().get(), is(false));
		assertThat(page.getTotalElements().get(), is(3L));

		TestSubscriber<String> testSubscriber = new TestSubscriber<>();
		page.subscribe(testSubscriber);

		testSubscriber.assertComplete();
		testSubscriber.assertValueCount(3L);
	}

	/**
	 * @see DATACMNS-836
	 */
	@Test(expected = IllegalStateException.class)
	public void uncachedFluxShouldFailIfCountIsDerivedFromFlux() {

		Flux<String> content = Flux.fromStream(Arrays.asList("Foo", "Bar", "Baz").stream());

		ReactivePage<String> page = new ReactivePageImpl<>(content, null, content.count());

		page.getTotalElements().get();
	}

	/**
	 * @see DATACMNS-836
	 */
	@Test
	public void returnsTotalAndElementsIfCalledTotalMultipleTimesCorrectly() {

		Flux<String> content = Flux.fromStream(Arrays.asList("Foo", "Bar", "Baz").stream()).cache();

		ReactivePage<String> page = new ReactivePageImpl<>(content, null, content.count());

		assertThat(page.getTotalElements().get(), is(3L));
		// second call
		assertThat(page.getTotalElements().get(), is(3L));

		TestSubscriber<String> testSubscriber = new TestSubscriber<>();
		page.subscribe(testSubscriber);

		testSubscriber.assertComplete();
		testSubscriber.assertValueCount(3L);
	}

	/**
	 * @see DATACMNS-836
	 */
	@Test
	public void createsPageForEmptyContentCorrectly() {

		ReactivePage<String> page = new ReactivePageImpl<String>(Flux.empty(), null, Mono.just(0L));

		assertThat(page.getNumber(), is(0));
		assertThat(page.count().get(), is(0L));
		assertThat(page.getSize(), is(0));
		assertThat(page.getSort(), is((Sort) null));
		assertThat(page.getTotalElements().get(), is(0L));
		assertThat(page.getTotalPages().get(), is(1));
		assertThat(page.hasNext().get(), is(false));
		assertThat(page.hasPrevious().get(), is(false));
		assertThat(page.isFirst().get(), is(true));
		assertThat(page.isLast().get(), is(true));
		assertThat(page.hasElements().get(), is(false));
	}

	/**
	 * @see DATACMNS-836
	 */
	@Test
	public void returnsCorrectTotalPages() {

		ReactivePage<String> page = new ReactivePageImpl<String>(Flux.just("a"), null, Mono.just(1L));

		assertThat(page.getTotalPages().get(), is(1));
		assertThat(page.hasNext().get(), is(false));
		assertThat(page.hasPrevious().get(), is(false));
	}

	/**
	 * @see DATACMNS-836
	 */
	@Test
	public void transformsPageCorrectly() {

		ReactivePage<Integer> transformed = new ReactivePageImpl<String>(Flux.just("foo", "bar"), new PageRequest(0, 2),
				Mono.just(10L)).map(String::length);

		TestSubscriber<Integer> testSubscriber = new TestSubscriber<>();
		transformed.subscribe(testSubscriber);
		testSubscriber.assertComplete();
		testSubscriber.assertValueCount(2);
		testSubscriber.assertValues(3, 3);
	}

	/**
	 * @see DATACMNS-836
	 */
	@Test
	public void adaptsTotalForLastPageOnIntermediateDeletion() {
		assertThat(new ReactivePageImpl<String>(Flux.just("foo", "bar"), new PageRequest(0, 5), Mono.just(3L))
				.getTotalElements().get(), is(2L));
	}

	/**
	 * @see DATACMNS-836
	 */
	@Test
	public void adaptsTotalForLastPageOnIntermediateInsertion() {
		assertThat(new ReactivePageImpl<String>(Flux.just("foo", "bar"), new PageRequest(0, 5), Mono.just(1L))
				.getTotalElements().get(), is(2L));
	}

	/**
	 * @see DATACMNS-836
	 */
	@Test
	public void adaptsTotalForLastPageOnIntermediateDeletionOnLastPate() {
		assertThat(new ReactivePageImpl<String>(Flux.just("foo", "bar"), new PageRequest(1, 10), Mono.just(13L))
				.getTotalElements().get(), is(12L));
	}

	/**
	 * @see DATACMNS-836
	 */
	@Test
	public void adaptsTotalForLastPageOnIntermediateInsertionOnLastPate() {
		assertThat(new ReactivePageImpl<String>(Flux.just("foo", "bar"), new PageRequest(1, 10), Mono.just(11L))
				.getTotalElements().get(), is(12L));
	}

	/**
	 * @see DATACMNS-836
	 */
	@Test
	public void doesNotAdapttotalIfPageIsEmpty() {

		assertThat(
				new ReactivePageImpl<String>(Flux.empty(), new PageRequest(1, 10), Mono.just(0L)).getTotalElements().get(),
				is(0L));
	}
}
