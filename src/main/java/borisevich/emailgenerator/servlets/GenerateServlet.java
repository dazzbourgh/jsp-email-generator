package borisevich.emailgenerator.servlets;

import borisevich.emailgenerator.db.AddressDAO;
import borisevich.emailgenerator.model.Address;
import borisevich.emailgenerator.model.Email;
import borisevich.emailgenerator.functional.Generator;
import borisevich.emailgenerator.listeners.DbInitListener;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Leonid on 26.08.2016.
 */
@WebServlet("/generateServlet")
public class GenerateServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(GenerateServlet.class.getName());
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        AddressDAO addressDAO = (AddressDAO)req.getServletContext().getAttribute(DbInitListener.ADDRESS_DAO);
        if(req.getParameter("trackInfo") == null){
            req.setAttribute("Error", "Please, provide track info.");
            req.getRequestDispatcher("/generationFormLoader").forward(req, resp);
            return;
        }
        if(req.getParameterValues("address") == null){
            req.setAttribute("Error", "Please, choose recipients.");
            req.getRequestDispatcher("/generationFormLoader").forward(req, resp);
            return;
        }
        List<Email> emailList;
        List<Address> addressList = new ArrayList<>();
        String trackInfoString = req.getParameter("trackInfo");
        String[] addressNames = req.getParameterValues("address");

        for(String s : addressNames){
            LOGGER.debug("Address to find: " + s);
            addressList.add(addressDAO.findByName(s));
        }
        Address[] addresses = new Address[addressList.size()];
        addressList.toArray(addresses);
        LOGGER.debug("Address 0: " + addresses[0]);
        Generator generator = new Generator();
        try {
            emailList = generator.generateMails(addresses, trackInfoString);
            req.getSession().setAttribute("emailList", emailList);
            LOGGER.debug("FIRST EMAIL: " + emailList.get(0).getText());
        } catch (NullPointerException e){
            LOGGER.error("Error during generation: null pointer exception");
        }
        req.getRequestDispatcher("/generationFormLoader").forward(req, resp);
    }
}
