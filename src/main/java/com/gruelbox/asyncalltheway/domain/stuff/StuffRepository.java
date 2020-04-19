package com.gruelbox.asyncalltheway.domain.stuff;

import io.reactivex.Flowable;
import io.reactivex.Single;

public interface StuffRepository {

  Single<Integer> clear();

  Single<Integer> count();

  Single<Integer> insert(Stuff stuff);

  Single<Integer> update(Stuff stuff);

  Flowable<Stuff> selectAll();

}
