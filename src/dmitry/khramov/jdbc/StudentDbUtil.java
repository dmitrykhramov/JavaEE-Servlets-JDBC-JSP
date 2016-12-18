package dmitry.khramov.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

public class StudentDbUtil {

    private DataSource dataSource;

    public StudentDbUtil(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    
    public List<Student> getStudents() throws Exception {
        
        List<Student> students = new ArrayList<>();
        
        Connection conn = null;
        Statement stmt = null;
        ResultSet res = null;
        
        try {
            conn = dataSource.getConnection();
            
            String sql = "select * from student order by last_name";
            stmt = conn.createStatement();
            res = stmt.executeQuery(sql);
            
            while(res.next()) {
                
                int id = res.getInt("id");
                String lastName = res.getString("last_name");
                String firstName = res.getString("first_name");
                String email = res.getString("email");
                
                Student student = new Student(id, firstName, lastName, email);
                
                students.add(student);
            }
            
        } finally {
            close(conn, stmt, res);
        }
        
        return students;
    }


    public void addStudent(Student theStudent) throws SQLException {
        
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = dataSource.getConnection();
            
            String sql = "insert into student " 
                    + "(first_name, last_name, email) "
                    + "values (?, ?, ?)";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, theStudent.getFirstName());
            stmt.setString(2, theStudent.getLastName());
            stmt.setString(3, theStudent.getEmail());
            
            stmt.execute();
            
        } finally {
            close(conn, stmt, null);
        }
        
    }

    public Student getStudent(String id) throws Exception {
        
        Student student = null;
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet res = null;
        int studentId;
        
        try {
            studentId = Integer.parseInt(id);
            
            conn = dataSource.getConnection();
            String sql = "select * from student where id=?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, studentId);
            
            res = stmt.executeQuery();
            if (res.next()) {
                String firstName = res.getString("first_name");
                String lastName = res.getString("last_name");
                String email = res.getString("email");
                
                student = new Student(studentId, firstName, lastName, email);
            } else {
                throw new Exception("Could not find student id " + studentId);
            }
            
            return student;
            
        } finally {
            close(conn, stmt, res);
        }
    }

    public void updateStudent(Student theStudent) throws Exception {

        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = dataSource.getConnection();
            
            String sql = "update student " 
                    + "set first_name=?, last_name=?, email=? "
                    + "where id=?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, theStudent.getFirstName());
            stmt.setString(2, theStudent.getLastName());
            stmt.setString(3, theStudent.getEmail());
            stmt.setInt(4, theStudent.getId());
            stmt.execute();
            
        } finally {
            close(conn, stmt, null);
        }
    }

    public void deleteStudent(int id) throws Exception {

        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            
            conn = dataSource.getConnection();
            String sql = "delete from student where id=?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            stmt.execute();
            
        } finally {
            close(conn, stmt, null);
        }
    }
    
    private void close(Connection conn, Statement stmt, ResultSet res) {
            
        try {
            
            if (res != null) {
                res.close();
            }
            
            if (stmt != null) {
                stmt.close();
            }
            
            if (conn != null) {
                conn.close();
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    

}
