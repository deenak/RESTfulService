RESTfulService
==============
Simple "Document Storage REST Web Service" using Servlet without using any frameworks like Spring, Struts, CXF, Jersey, RestEasy, Restlet, Wink.


	Create - POST /storage/documents - 201 (Created)
	Query - GET /storage/documents/{docId} - 200(OK), 404(Not Found)
	Update - PUT /storage/documents/{docId} - 204(No Content), 404(Not Found)
	Delete - DELETE /storage/documents/{docId} - 204(No Content), 404(Not Found)

