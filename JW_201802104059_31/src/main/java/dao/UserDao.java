package dao;

import domain.User;
import service.TeacherService;
import util.JdbcHelper;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;

public class UserDao {
    private static UserDao userDao=new UserDao();
    private UserDao(){}
    public static UserDao getInstance(){return userDao;}

    public Set<User> findAll() throws SQLException {
        Set<User> users = new HashSet<User>();
        //获得连接对象
        Connection connection = JdbcHelper.getConn();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("select * from User");
        //若结果集仍然有下一条记录，则执行循环体
        while (resultSet.next()) {
            //以当前记录中的id,description,no,remarks值为参数，创建Teacher对象
            users.add(new User(
                    resultSet.getInt("id"),
                    resultSet.getString("username"),
                    resultSet.getString("password"),
                    TeacherService.getInstance().find(resultSet.getInt("teacher_id"))));
        }
        //关闭资源
        JdbcHelper.close(resultSet, statement, connection);
        return users;
    }

    public User find(Integer id) throws SQLException {
        User user=null;
        Connection connection = JdbcHelper.getConn();
        String user_sql = "SELECT * FROM User WHERE id=?";
        //在该连接上创建预编译语句对象
        PreparedStatement preparedStatement = connection.prepareStatement(user_sql);
        //为预编译参数赋值
        preparedStatement.setInt(1, id);
        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()){
            user=new User(
                    resultSet.getInt("id"),
                    resultSet.getString("username"),
                    resultSet.getString("password"),
                    TeacherService.getInstance().find(resultSet.getInt("teacher_id")));
        }
        JdbcHelper.close(resultSet, preparedStatement, connection);
        return user;
    }

    public User findByUsername(String username) throws SQLException {
        User user=null;
        Connection connection = JdbcHelper.getConn();
        String user_sql = "SELECT * FROM User WHERE username=?";
        //在该连接上创建预编译语句对象
        PreparedStatement preparedStatement = connection.prepareStatement(user_sql);
        //为预编译参数赋值
        preparedStatement.setString(1, username);
        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()){
            user=new User(
                    resultSet.getInt("id"),
                    resultSet.getString("username"),
                    resultSet.getString("password"),
                    TeacherService.getInstance().find(resultSet.getInt("teacher_id")));
        }
        JdbcHelper.close(resultSet, preparedStatement, connection);
        return user;
    }

    public User login(String username,String password) throws SQLException {
        User user=null;
        Connection connection=JdbcHelper.getConn();
        String user_sql = "SELECT * FROM User WHERE username=? and password=?";
        //在该连接上创建预编译语句对象
        PreparedStatement preparedStatement = connection.prepareStatement(user_sql);
        //为预编译参数赋值
        preparedStatement.setString(1, username);
        preparedStatement.setString(2,password);
        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()){
            user=new User(
                resultSet.getInt("id"),
                resultSet.getString("username"),
                resultSet.getString("password"),
                TeacherService.getInstance().find(resultSet.getInt("teacher_id")));
        }
        //关闭资源
        JdbcHelper.close(resultSet, preparedStatement, connection);
        return user;
    }

    public boolean add(User user,Connection connection) throws SQLException {
        System.out.println(user.getTeacher());
        String addUser_sql = "INSERT INTO User (username,password,teacher_id) VALUES (?,?,?)";
        //在该连接上创建预编译语句对象
        PreparedStatement preparedStatement = connection.prepareStatement(addUser_sql);
        //为预编译参数赋值
        preparedStatement.setString(1,user.getUsername());
        preparedStatement.setString(2,user.getPassword());
        preparedStatement.setInt(3,user.getTeacher().getId());
        //执行预编译语句，获取添加记录行数并赋值给affectedRowNum
        int affectedRowNum = preparedStatement.executeUpdate();
        preparedStatement.close();
        return affectedRowNum > 0;
    }

    public boolean delete(Integer teacher_id,Connection connection) throws SQLException {
        //写sql语句
        String deleteUser_sql = "DELETE FROM User WHERE teacher_id=?";
        //在该连接上创建预编译语句对象
        PreparedStatement preparedStatement = connection.prepareStatement(deleteUser_sql);
        //为预编译参数赋值
        preparedStatement.setInt(1, teacher_id);
        //执行预编译语句，获取删除记录行数并赋值给affectedRowNum
        int affectedRows = preparedStatement.executeUpdate();
        //关闭资源
        preparedStatement.close();
        return affectedRows > 0;
    }

    public boolean changePassword(User user) throws SQLException {
        Connection connection = JdbcHelper.getConn();
        //写sql语句
        String updateUser_sql = " update User set password=? where id=?";
        //在该连接上创建预编译语句对象
        PreparedStatement preparedStatement = connection.prepareStatement(updateUser_sql);
        preparedStatement.setString(1,user.getPassword());
        preparedStatement.setInt(2, user.getId());
        //执行预编译语句，获取改变记录行数并赋值给affectedRowNum
        int affectedRows = preparedStatement.executeUpdate();
        //关闭资源
        JdbcHelper.close(preparedStatement, connection);
        return affectedRows>0;
    }

}
