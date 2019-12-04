package service;

import dao.UserDao;
import domain.User;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Set;

public class UserService {
    private static UserDao userDao= UserDao.getInstance();
    private static UserService userService=new UserService();
    private UserService(){}

    public static UserService getInstance(){
        return userService;
    }

    public Set<User> findAll() throws SQLException {
        return userDao.findAll();
    }

    public User find(Integer id) throws SQLException {
        return userDao.find(id);
    }

    public User findByUsername(String username) throws SQLException {
        return userDao.findByUsername(username);
    }

    public User login(String username,String password) throws SQLException {
        return userDao.login(username,password);
    }

    public boolean changePassword(User user) throws SQLException {
        return userDao.changePassword(user);
    }

    public boolean add(User user, Connection connection) throws SQLException {
        return userDao.add(user,connection);
    }

    public boolean delete(Integer id,Connection connection) throws SQLException {
        return userDao.delete(id,connection);
    }
}
