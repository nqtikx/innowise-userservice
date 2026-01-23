package com.innowise.userservice.service;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

@Service
public class UsersWithCardsCacheService {

  @CacheEvict(cacheNames = "usersWithCards", key = "#userId")
  public void evict(Long userId) {
  }

  @CacheEvict(cacheNames = "usersWithCards", allEntries = true)
  public void evictAll() {
  }

}
