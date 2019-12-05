package dao;


import domain.Teacher;
import domain.User;
import service.DegreeService;
import service.DepartmentService;
import service.ProfTitleService;
import service.UserService;
import util.JdbcHelper;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;


public final class TeacherDao {
	private static TeacherDao teacherDao=new TeacherDao();
	private TeacherDao(){}
	public static TeacherDao getInstance(){
		return teacherDao;
	}
	
	public Set<Teacher> findAll() throws SQLException {
		Set<Teacher> teachers = new HashSet<Teacher>();
		//获得连接对象
		Connection connection = JdbcHelper.getConn();
		Statement statement = connection.createStatement();
		ResultSet resultSet = statement.executeQuery("select * from Teacher");
		//若结果集仍然有下一条记录，则执行循环体
		while (resultSet.next()) {
			//以当前记录中的id,description,no,remarks值为参数，创建Teacher对象
			teachers.add(new Teacher(
					resultSet.getInt("id"),
					resultSet.getString("no"),
					resultSet.getString("name"),
					ProfTitleService.getInstance().find(resultSet.getInt("title_id")),
					DegreeService.getInstance().find(resultSet.getInt("degree_id")),
					DepartmentService.getInstance().find(resultSet.getInt("department_id"))));
		}
		//关闭资源
		JdbcHelper.close(resultSet, statement, connection);
		return teachers;
	}

	public Teacher find(Integer id) throws SQLException {
		Teacher teacher = null;
		Connection connection = JdbcHelper.getConn();
		String deleteTeacher_sql = "SELECT * FROM Teacher WHERE id=?";
		//在该连接上创建预编译语句对象
		PreparedStatement preparedStatement = connection.prepareStatement(deleteTeacher_sql);
		//为预编译参数赋值
		preparedStatement.setInt(1, id);
		ResultSet resultSet = preparedStatement.executeQuery();
		//由于id不能取重复值，故结果集中最多有一条记录
		//若结果集有一条记录，则以当前记录中的id,description,no,remarks值为参数，创建Teacher对象
		//若结果集中没有记录，则本方法返回null
		if (resultSet.next()) {
			teacher = new Teacher(
					resultSet.getInt("id"),
					resultSet.getString("no"),
					resultSet.getString("name"),
					ProfTitleService.getInstance().find(resultSet.getInt("title_id")),
					DegreeService.getInstance().find(resultSet.getInt("degree_id")),
					DepartmentService.getInstance().find(resultSet.getInt("department_id")));
		}
		//关闭资源
		JdbcHelper.close(resultSet, preparedStatement, connection);
		return teacher;
	}

	public boolean add(Teacher teacher)throws SQLException{
		Connection connection=null;
		PreparedStatement preparedStatement=null;
		int affectedRowNum=0;
	    try{
	        connection= JdbcHelper.getConn();
            //关闭自动提交
            connection.setAutoCommit(false);
            String addTeacher_sql = "INSERT INTO Teacher (no,name,title_id,degree_id,department_id) VALUES" + " (?,?,?,?,?)";
            //在该连接上创建预编译语句对象
            preparedStatement = connection.prepareStatement(addTeacher_sql,PreparedStatement.RETURN_GENERATED_KEYS);
            //为预编译参数赋值
            preparedStatement.setString(1, teacher.getNo());
            preparedStatement.setString(2, teacher.getName());
            preparedStatement.setInt(3, teacher.getTitle().getId());
            preparedStatement.setInt(4, teacher.getDegree().getId());
            preparedStatement.setInt(5, teacher.getDepartment().getId());
            //执行预编译语句，获取添加记录行数并赋值给affectedRowNum
            affectedRowNum = preparedStatement.executeUpdate();
            System.out.println("添加了 " + affectedRowNum +" 行记录");
            ResultSet resultSet=preparedStatement.getGeneratedKeys();
            resultSet.next();
            teacher.setId(resultSet.getInt(1));
//            获取当前时间
//            java.util.Date date_util = new  java.util.Date();
//            Long date_long = date_util.getTime();
//            java.sql.Date loginTime = new java.sql.Date(date_long);

            UserService.getInstance().add(new User(teacher.getNo(), teacher.getNo(),teacher),connection);
            //提交当前操作连接所做的操作
            connection.commit();
        }catch (SQLException e){
            System.out.println(e.getMessage()+"\n error code="+e.getErrorCode());
            try {
                //回滚当前连接所做的操作
                if (connection!=null){
                    connection.rollback();
                }
            }catch (SQLException e1){
                e1.printStackTrace();
            }
        }finally {
            try{
                //恢复自动提交
                if (connection!=null){
                    connection.setAutoCommit(true);
                }
            }catch (SQLException e){
                e.printStackTrace();
            }
        }

		//关闭资源
		JdbcHelper.close(preparedStatement, connection);
		return affectedRowNum > 0;
	}


	//delete方法，根据Teacher的id值，删除数据库中对应的Teacher对象
	public boolean delete(int id) throws  SQLException {
        Connection connection=null;
        PreparedStatement preparedStatement=null;
        int affectedRowNum=0;
        try{
            connection= JdbcHelper.getConn();
            //关闭自动提交
            connection.setAutoCommit(false);
            UserService.getInstance().delete(id,connection);
            //写sql语句
            String deleteTeacher_sql = "DELETE FROM Teacher WHERE id=?";
            //在该连接上创建预编译语句对象
            preparedStatement = connection.prepareStatement(deleteTeacher_sql);
            //为预编译参数赋值
            preparedStatement.setInt(1, id);
            //执行预编译语句，获取删除记录行数并赋值给affectedRowNum
            affectedRowNum = preparedStatement.executeUpdate();
            connection.commit();
        }catch (SQLException e){
            System.out.println(e.getMessage()+"\n error code="+e.getErrorCode());
            try {
                //回滚当前连接所做的操作
                if (connection!=null){
                    connection.rollback();
                }
            }catch (SQLException e1){
                e1.printStackTrace();
            }
        }finally {
            try{
                //恢复自动提交
                if (connection!=null){
                    connection.setAutoCommit(true);
                }
            }catch (SQLException e){
                e.printStackTrace();
            }
        }

        //关闭资源
        JdbcHelper.close(preparedStatement, connection);
        return affectedRowNum > 0;
	}

	public boolean update(Teacher teacher) throws  SQLException {
		Connection connection = JdbcHelper.getConn();
		//写sql语句
		String updateTeacher_sql = " update Teacher set no=?,name=?,title_id=?,department_id=?,degree_id=? where id=?";
		//在该连接上创建预编译语句对象
		PreparedStatement preparedStatement = connection.prepareStatement(updateTeacher_sql);
		//为预编译参数赋值
		preparedStatement.setString(1, teacher.getNo());
		preparedStatement.setString(2, teacher.getName());
		preparedStatement.setInt(3, teacher.getTitle().getId());
		preparedStatement.setInt(4, teacher.getDepartment().getId());
		preparedStatement.setInt(5, teacher.getDegree().getId());
		preparedStatement.setInt(6,teacher.getId());
		//执行预编译语句，获取改变记录行数并赋值给affectedRowNum
		int affectedRows = preparedStatement.executeUpdate();
		//关闭资源
		JdbcHelper.close(preparedStatement, connection);
		return affectedRows > 0;
	}
}
