package lg;

import jdbm.RecordManager;
import jdbm.RecordManagerFactory;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Vector;


@WebServlet(name = "Server")
public class Server extends HttpServlet {

    private static final int TOP_K_RESULTS = 50;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        try {
            Querier querier = new Querier();
            request.setAttribute("queryResult", querier.NaiveSearch(request.getParameter("query"), TOP_K_RESULTS));
        } catch (Exception e) {
            System.err.println("Error");
            System.err.println(e.toString());
        }

        getServletContext().getRequestDispatcher("/home.jsp").forward(request, response);


    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Crawler.main(new String[] {});
        getServletContext().getRequestDispatcher("/home.jsp").forward(request, response);
    }

}
