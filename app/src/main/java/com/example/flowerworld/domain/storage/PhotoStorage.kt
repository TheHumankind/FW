package com.example.flowerworld.domain.storage

/** Keeps plant photos in app storage; the rest of the app only deals with the returned paths. */
interface PhotoStorage {
    /**
     * Compresses the image behind [sourceUri] (e.g. a picked gallery image) into app storage.
     *
     * @return path of the stored photo, to be saved in [com.example.flowerworld.domain.model.Plant.photos].
     * @throws java.io.IOException if the source cannot be read or the photo cannot be written.
     */
    suspend fun import(sourceUri: String): String

    /** Removes a photo previously returned by [import]. Does nothing if it is already gone. */
    suspend fun delete(path: String)
}
