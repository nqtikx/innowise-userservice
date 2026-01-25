package com.innowise.userservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

@Service
public class UsersWithCardsCacheService {

  private static final Logger log = LoggerFactory.getLogger(UsersWithCardsCacheService.class);

  @CacheEvict(cacheNames = "usersWithCards", key = "#userId")
  public void evict(Long userId) {
    log.debug("Clearing cache for user id: {}", userId);
  }

  @CacheEvict(cacheNames = "usersWithCards", allEntries = true)
  public void evictAll() {
    log.debug("Clearing cache for all user");
  }
}
