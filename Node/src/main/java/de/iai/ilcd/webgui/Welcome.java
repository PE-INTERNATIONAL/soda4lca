/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.iai.ilcd.webgui;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.configuration.Configuration;

import de.iai.ilcd.configuration.ConfigurationService;
import de.iai.ilcd.webgui.controller.ConfigurationBean;

/**
 * 
 * @author clemens.duepmeier
 */
public class Welcome extends HttpServlet {

	/**
	 * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
	 * 
	 * @param request
	 *            servlet request
	 * @param response
	 *            servlet response
	 * @throws ServletException
	 *             if a servlet-specific error occurs
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	protected void processRequest( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {

		Configuration conf = ConfigurationService.INSTANCE.getProperties();
		String jumpPage = conf.getString( "welcomePage" );

		RequestDispatcher dispatcher = this.getServletContext().getRequestDispatcher( "/index.xhtml" );
		if ( jumpPage != null ) {
			ConfigurationBean confBean = new ConfigurationBean( request );
			String jumpPagePath = confBean.getTemplatePath() + "/" + jumpPage;
			dispatcher = this.getServletContext().getRequestDispatcher( jumpPagePath );
		}

		// OK, now forward to the welcome page: either "index.xhtml" or the jumppage
		dispatcher.forward( request, response );
	}

	// <editor-fold defaultstate="collapsed"
	// desc="HttpServlet methods. Click on the + sign on the left to edit the code.">

	/**
	 * Handles the HTTP <code>GET</code> method.
	 * 
	 * @param request
	 *            servlet request
	 * @param response
	 *            servlet response
	 * @throws ServletException
	 *             if a servlet-specific error occurs
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	@Override
	protected void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {
		this.processRequest( request, response );
	}

	/**
	 * Handles the HTTP <code>POST</code> method.
	 * 
	 * @param request
	 *            servlet request
	 * @param response
	 *            servlet response
	 * @throws ServletException
	 *             if a servlet-specific error occurs
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	@Override
	protected void doPost( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {
		this.processRequest( request, response );
	}

	/**
	 * Returns a short description of the servlet.
	 * 
	 * @return a String containing servlet description
	 */
	@Override
	public String getServletInfo() {
		return "Short description";
	}// </editor-fold>
}
