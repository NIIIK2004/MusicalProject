package com.example.dao;

import com.example.model.Artist;
import com.example.model.Track;

import java.util.List;
import java.util.Optional;

public interface TrackDao {
    public Track add(Track track);

    public void delete(Long id);

    public List<Track> findAll();

    public Optional<Track> findById(Long id);

    public List<Track> findByName(String name);

    Track findLatestTrackByArtistId(Long artistId);

    Track editFileds(Long id,
                     String title,
                     String lyrics,
                     String genre,
                     String releaseyear,
                     String audiofilename,
                     String coverfilename,
                     Artist artists);
}
