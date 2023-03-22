package com.synchrony.project.service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.synchrony.project.entity.User;
import com.synchrony.project.repo.UserRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    final static String USER_NOT_FOUND_MSG = "User with username %s not found";

    private final LoadingCache<String, User> userLoadingCache = CacheBuilder.newBuilder()
            .expireAfterAccess(10, TimeUnit.MINUTES)
            .build(new CacheLoader<>() {
                @Override
                public User load(final String userName) throws Exception {
                    LOGGER.debug("Fetching user {} from database", userName);
                    return userRepo.findByUsername(userName)
                            .orElseThrow(() -> new UsernameNotFoundException(USER_NOT_FOUND_MSG));
                }
            });


    /**
     * Encrypts user credential and saves user if valid and unique
     *
     * @param user user to persist
     */
    public void saveUser(User user) {

        LOGGER.debug("Attempting to save user {}", user.getUsername());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepo.save(user);
        LOGGER.info("User {} saved successfully", user.getUsername());
    }

    /**
     * Fetches user from cache or data store
     *
     * @param userName unique user name
     * @return user
     */
    public User fetchUser(String userName) {
        try {
            return userLoadingCache.get(userName);
        } catch (Exception ex) {
            throw new UsernameNotFoundException(USER_NOT_FOUND_MSG);
        }
    }
}
