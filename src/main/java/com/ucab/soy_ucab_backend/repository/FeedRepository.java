package com.ucab.soy_ucab_backend.repository;

import com.ucab.soy_ucab_backend.dto.FeedPostProjection;
import com.ucab.soy_ucab_backend.model.Miembro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedRepository extends JpaRepository<Miembro, String> {

    @Query(value = "SELECT * FROM obtener_feed_filtrado(" +
            ":email, :page, :pageSize, :search, :filterInterests, :filterFriends, :filterFollowing, :filterGroups, :filterOwnPosts, :orderAsc)", 
            nativeQuery = true)
    List<FeedPostProjection> getFeed(
            @Param("email") String email,
            @Param("page") int page,
            @Param("pageSize") int pageSize,
            @Param("search") String search,
            @Param("filterInterests") boolean filterInterests,
            @Param("filterFriends") boolean filterFriends,
            @Param("filterFollowing") boolean filterFollowing,
            @Param("filterGroups") boolean filterGroups,
            @Param("filterOwnPosts") boolean filterOwnPosts,
            @Param("orderAsc") boolean orderAsc
    );
}
