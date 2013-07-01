package com.deena.docs.rs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DocumentStorageServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// Delete document
	protected void doDelete(String docId, HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		try {

			File file = new File(docId);
			if (file.exists()) {
				file.delete();
				response.setStatus(HttpServletResponse.SC_NO_CONTENT);
			} else {
				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			}

		} catch (Exception ex) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
		out.close();
	}

	// Query document
	protected void doGet(String docId, HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		OutputStream outputStream = null;
		InputStream inputStream = null;
		
		try {			
			File file = new File(docId);
			if (file.exists()) {
				outputStream = response.getOutputStream();
				inputStream = new FileInputStream(file);

				// initialize
				byte[] buffer = new byte[4096]; // tweaking this number may
												// increase performance
				int len;
				while ((len = inputStream.read(buffer)) != -1) {
					outputStream.write(buffer, 0, len);
				}

				response.setStatus(HttpServletResponse.SC_OK);

			} else {				
				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		} finally {
			if (inputStream != null) {
				inputStream.close();
			}
			if (outputStream != null) {
				outputStream.close();
			}			
		}

	}

	// Create document
	protected void doPost(String docId, HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		try {

			writeFile(request.getInputStream(), docId);
			response.setStatus(HttpServletResponse.SC_ACCEPTED);
			out.write(docId);

		} catch (Exception ex) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
		out.close();
	}

	// Update document
	protected void doPut(String docId, HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		
		try {

			File file = new File(docId);
			if (file.exists()) {
				writeFile(request.getInputStream(), docId);
				response.setStatus(HttpServletResponse.SC_NO_CONTENT);
			} else {
				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			}

		} catch (Exception ex) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
		out.close();
	}

	// Generate unique ID using
	/*Used java.util.Random class - instance with the current timestamp
	 * 
	 */
	private String generateDocId() {

		final int ID_SIZE = 20;
		final int NUM_OF_CHARS = 36;
		StringBuffer id = new StringBuffer();
		long now = new Date().getTime();

		// Set the new Seed as current timestamp
		Random r = new Random(now);

		int index = 0;
		int x = 0;

		while (x < ID_SIZE) {
			index = r.nextInt(NUM_OF_CHARS);
			
			if (index < 10) {
				id.append((char) (48 + index));
			} else if (10 <= index && index < 36) {
				index = index - 10;
				id.append((char) (97 + index));
			}
			x++;
		}

		return id.toString().toUpperCase();
	}

	/*
	 * Method to validate the pathInfo and to retrieve the document ID Return
	 * null or documentId if the pathInfo is valid Otherwise thorws
	 * ServletException
	 */
	private String getDocId(String pathInfo) throws ServletException {

		// Accommodate two requests, one for all resources, another for a
		// specific resource
		Pattern regExAllPattern = Pattern.compile("/storage/documents");
		Pattern regExIdPattern = Pattern.compile("/storage/documents/(\\w*)");

		// regex parse pathInfo
		Matcher matcher;

		// Check for ID case first, since the All pattern would also match
		matcher = regExIdPattern.matcher(pathInfo);
		if (matcher.find()) {
			return matcher.group(1);
		}

		matcher = regExAllPattern.matcher(pathInfo);
		if (matcher.find()) {
			return null;
		}

		throw new ServletException("Invalid URI");
	}

	protected void service(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		String method = request.getMethod();
		String docId = null;

		try {
			docId = getDocId(request.getPathInfo());			
			if (method.equals("GET")) {
				// Call doGet				
				doGet(docId, request, response);
			} else if (method.equals("POST")) {
				// call doPost
				doPost(generateDocId(),request, response);
			} else if (method.equals("PUT")) {
				// call doPut				
				doPut(docId, request, response);
			} else if (method.equals("DELETE")) {
				// call doPut
				doDelete(docId, request, response);
			} else {
				// Our Servlet doesn't currently support
				// other types of request.
				String errMsg = "Method Not Supported";
				response.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED,
						errMsg);
			}

		} catch (ServletException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.resetBuffer();
			e.printStackTrace();
		}

	}

	//Create file from inputstream for the give file name
	private void writeFile(InputStream is, String fileName) throws Exception {

		OutputStream os = new FileOutputStream(fileName);
		byte[] buffer = new byte[4096];
		int bytesRead;
		while ((bytesRead = is.read(buffer)) != -1) {
			os.write(buffer, 0, bytesRead);
		}
		is.close();
		os.close();

	}
}
