package com.redhat.newrelic.example;
    
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
   
@WebServlet("/TestServlet")
public class TestServlet extends HttpServlet {
    
    @Trace
    protected void
    processRequest(HttpServletRequest req,
    HttpServletResponse resp)
        throws ServletException, IOException {
    
        saveNewRelicInfo(req);
        doRequestWork(req);
        writeResponse(resp);
    }
    String storeId;
    String userId;
    long millisToSleep;
    
    private void saveNewRelicInfo(HttpServletRequest req) {
        storeId = req.getParameter("storeId");
        if (storeId != null) {

        NewRelic.setTransactionName(null, "/store");
    
    if (storeId.equals("betaStore")) {
        NewRelic.ignoreApdex();
        }
    }
    
    userId = req.getParameter("userId");
        if (userId != null) {
        NewRelic.setUserName(userId);
        NewRelic.addCustomParameter("userId", userId);
        }
    
    String promotionId = req.getParameter("promotionId");
        if (promotionId != null) {
        NewRelic.incrementCounter("Custom/Promotion");
        }
    }
    
    protected void
    doRequestWork(HttpServletRequest req) {
    try {
        this.millisToSleep  = new Random().nextInt(5000);
        Thread.sleep(millisToSleep);
    
    NewRelic.recordResponseTimeMetric("Custom/RandomSleep",
        millisToSleep);
   
    	  if(this.millisToSleep < 2500 && this.millisToSleep > 2000)
    		  throw new InterruptedException("Sleep Time was between 2000 & 2500");
    	
    
    	} catch (InterruptedException e) {
        NewRelic.noticeError(e);
        }
    }
    
    protected void
    writeResponse(HttpServletResponse resp)
        throws IOException {
    
    resp.setContentType("text/html;charset=UTF-8");
    PrintWriter out = resp.getWriter();
    out.println("<html>");
    out.println("<head>");

    out.println("timingHeader: " + NewRelic.getBrowserTimingHeader());
    out.println("<title>NewRelic API example servlet</title>");
    out.println("</head>");
    out.println("<body>");
    out.println("<h1>API examples</h1>");
    
    out.println("userId: " + this.userId);
    out.println("storeId: " + this.storeId);
    out.println("timingFooter: " + NewRelic.getBrowserTimingFooter());
    out.println("randomTime: " + this.millisToSleep);
    out.println("</body>");
    out.println("</html>");
    out.close();
    }
    protected void doGet(HttpServletRequest req,
    HttpServletResponse resp)
        throws ServletException, IOException {
        processRequest(req, resp);
        }
    protected void doPost(HttpServletRequest req,
    HttpServletResponse resp)
        throws ServletException, IOException {
        processRequest(req, resp);
    }
}