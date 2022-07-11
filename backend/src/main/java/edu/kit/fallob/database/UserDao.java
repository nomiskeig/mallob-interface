package edu.kit.fallob.database;

import edu.kit.fallob.dataobjects.User;

public interface UserDao {

    public void save(User user);

    public void remove(String username);

    public User getUserByUsername(String username);

    public String getUsernameByJobId(int jobId);

    public String getUsernameByDescriptionId(int descriptionId);
}
