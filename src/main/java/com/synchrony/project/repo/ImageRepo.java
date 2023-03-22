package com.synchrony.project.repo;

import com.synchrony.project.entity.Image;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageRepo extends CrudRepository<Image, Long> {

    Image[] findAllByUserName(String userName);

    Image findByImageDeleteHashAndUserName(String imageDeleteHash, String userName);

    Image findByImageHashAndUserName(String imageHash, String userName);
}
