package com.jeontongju.wishcart.service;

import com.jeontongju.wishcart.domain.Wish;
import com.jeontongju.wishcart.repository.WishRepository;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WishService {

  private final WishRepository wishRepository;

  @Qualifier("redisGenericTemplate")
  private final RedisTemplate redisGenericTemplate;

  @CachePut(value = "wishList", key = "#memberId")
  public Set<String> addDeleteWishItem(Long memberId, String productId) {

    Set<String> wishSet;

    if (redisGenericTemplate.hasKey(memberId + "_wish_list")) {
      wishSet = redisGenericTemplate.opsForSet().members(memberId + "_wish_list");
    } else {
      Optional<Wish> optionalWish = wishRepository.findById(memberId);
      if (optionalWish.isPresent()) {
        wishSet = optionalWish.get().getProducts();
      } else {
        wishSet = new HashSet<>();
      }
    }

    if (wishSet.contains(productId)) {
      wishSet.remove(productId);
    } else {
      wishSet.add(productId);
    }

    return wishSet;
  }

}
