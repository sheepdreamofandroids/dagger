/*
* Copyright (C) 2015 Google, Inc.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package test;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.SettableFuture;
import dagger.producers.Producer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static com.google.common.truth.Truth.assertThat;

@RunWith(JUnit4.class)
public class ProducerFactoryTest {
  @Test public void noArgMethod() throws Exception {
    SimpleProducerModule module = new SimpleProducerModule();
    Producer<String> producer =
        new SimpleProducerModule$$StrFactory(module, MoreExecutors.directExecutor());
    assertThat(producer.get().get()).isEqualTo("Hello, World!");
  }

  @Test public void singleArgMethod() throws Exception {
    SimpleProducerModule module = new SimpleProducerModule();
    SettableFuture<String> strFuture = SettableFuture.create();
    Producer<String> strProducer = producerOfFuture(strFuture);
    Producer<Integer> producer =
        new SimpleProducerModule$$LenFactory(module, MoreExecutors.directExecutor(), strProducer);
    assertThat(producer.get().isDone()).isFalse();
    strFuture.set("abcdef");
    assertThat(producer.get().get()).isEqualTo(6);
  }

  private static <T> Producer<T> producerOfFuture(final ListenableFuture<T> future) {
    return new Producer<T>() {
      @Override public ListenableFuture<T> get() {
        return future;
      }
    };
  }
}
