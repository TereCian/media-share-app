package de.terecian.media_share.remote.users;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Component
public class UserRepository {
    private static final File DEFAULT_USER_REPOSITORY_LOCATION = new File(System.getProperty("user.home") + "/.mediarequest/users.json");
    private final JavaType REPOSITORY_DATA_TYPE;
    private final ObjectMapper objectMapper;
    private final ReadWriteLock repositoryAccessLock;

    public UserRepository(ObjectMapper objectMapper) throws IOException {
        this.objectMapper = objectMapper;
        repositoryAccessLock = new ReentrantReadWriteLock(true);
        if (!DEFAULT_USER_REPOSITORY_LOCATION.exists()) {
            if (!DEFAULT_USER_REPOSITORY_LOCATION.getParentFile().exists() && !DEFAULT_USER_REPOSITORY_LOCATION.getParentFile().mkdirs()) {
                throw new IOException("Could not create user repository directory!");
            }
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(DEFAULT_USER_REPOSITORY_LOCATION, List.of());
        }
        REPOSITORY_DATA_TYPE = objectMapper.getTypeFactory().constructCollectionType(ArrayList.class, IdNameMappings.class);
    }

    private String generateDefaultName() {
        return "GuestUser" + (Math.round(Math.random() * 1000000));
    }

    public void updateEntry(String key, String newUsername) throws IOException {
        try {
            repositoryAccessLock.writeLock().lock();
            Collection<IdNameMappings> repositoryState = loadRepository();
            repositoryState.removeIf(entry -> entry.userId.equals(key));
            repositoryState.add(new IdNameMappings(key, newUsername));
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(DEFAULT_USER_REPOSITORY_LOCATION, repositoryState);
        } finally {
            repositoryAccessLock.writeLock().unlock();
        }
    }

    private Collection<IdNameMappings> loadRepository() throws IOException {
        try {
            repositoryAccessLock.readLock().lock();
            return objectMapper.readValue(DEFAULT_USER_REPOSITORY_LOCATION, REPOSITORY_DATA_TYPE);
        } finally {
            repositoryAccessLock.readLock().unlock();
        }
    }

    public String lookUpUsername(String userId) throws IOException {
        Collection<IdNameMappings> repositoryState = loadRepository();
        Optional<String> entryMatchingId = repositoryState.stream().filter(entry -> entry.userId.equals(userId))
                .findFirst().map(IdNameMappings::username);
        if (entryMatchingId.isPresent()) {
            return entryMatchingId.get();
        }
        String newUsername = generateDefaultName();
        updateEntry(userId, newUsername);
        return newUsername;
    }

    record IdNameMappings(String userId, String username){}
}
