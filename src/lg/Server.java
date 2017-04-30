package lg;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.DecimalFormat;
import java.util.Vector;


@WebServlet(name = "Server")
public class Server extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {}

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        if(request.getParameter("query") != null){
            long start_time = System.nanoTime();
            try {
                Querier querier = new Querier();
                Vector<PageInfo> queryResult = querier.NaiveSearch(request.getParameter("query"), Integer.parseInt(request.getParameter("topk")));
                request.setAttribute("queryResult", queryResult);
            } catch (Exception e) {
                System.err.println("Error");
                System.err.println(e.toString());
            }
            long end_time = System.nanoTime();
            double diff = (end_time - start_time)/1e6;
            DecimalFormat df = new DecimalFormat("#.###");
            request.setAttribute("time", df.format(diff));
            getServletContext().getRequestDispatcher("/home.jsp").forward(request, response);
            return;
        }
        else {
            //Crawler.main(new String[] {});
            getServletContext().getRequestDispatcher("/home.jsp").forward(request, response);
            return;
        }

    }

}
