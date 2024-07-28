import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/ExamServlet")
public class ExamServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/online_exam_system";
    private static final String JDBC_USER = "root";
    private static final String JDBC_PASS = "admin"; // Replace with your MySQL password

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        request.getRequestDispatcher("/examform.html").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String examName = request.getParameter("examName");
        String examDate = request.getParameter("examDate");
        String examTime = request.getParameter("examTime");
        String duration = request.getParameter("duration");

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("Successfull");
            conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASS);
            String sql = "INSERT INTO exams (exam_name, exam_date, exam_time, duration) VALUES (?, ?, ?, ?)";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, examName);
            stmt.setDate(2, java.sql.Date.valueOf(examDate));

            // Handle possible Time format issue
            java.sql.Time sqlTime = null;
            try {
                sqlTime = java.sql.Time.valueOf(examTime + ":00"); // Ensuring HH:MM:SS format
            } catch (IllegalArgumentException e) {
                response.setContentType("text/html");
                PrintWriter out = response.getWriter();
                out.println("<html><body>");
                out.println("<h2>Invalid time format. Please use HH:MM format.</h2>");
                out.println("<a href='ExamServlet'>Go back</a>");
                out.println("</body></html>");
                return;
            }

            stmt.setTime(3, sqlTime);
            stmt.setInt(4, Integer.parseInt(duration));
            stmt.executeUpdate();

            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            out.println("<html><body bgcolor=\"cyan\" align=\"center\">");
            out.println("<h2>Exam created successfully</h2>");
            out.println(" <a href=\"RegisterUser.html\">Click me to Register</a>");
            out.println("<br><br>");
            out.println("<a href='ExamServlet'>Go back</a>");
            out.println("</body></html>");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            out.println("<html><body>");
            out.println("<h2>Error occurred: " + e.getMessage() + "</h2>");
            out.println("<a href='ExamServlet'>Go back</a>");
            out.println("</body></html>");
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
