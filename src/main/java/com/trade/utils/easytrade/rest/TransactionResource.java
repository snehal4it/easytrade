package com.trade.utils.easytrade.rest;

import com.trade.utils.easytrade.document.DocumentVersionFactory;
import com.trade.utils.easytrade.document.StatementDocument;
import com.trade.utils.easytrade.service.TransactionService;
import com.trade.utils.easytrade.validate.StatementDocumentValidator;
import com.trade.utils.easytrade.validate.StatementDocumentValidatorFactory;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;

@Named
@Path("/transactions")
public class TransactionResource {

    @Inject
    private DocumentVersionFactory documentVersionFactory;

    @Inject
    private StatementDocumentValidatorFactory statementDocumentValidatorFactory;

    @Inject
    private TransactionService transactionService;

    @Path("/")
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response createTransactions(@FormDataParam("file") InputStream inputStream,
                                       @FormDataParam("file") FormDataContentDisposition fileDisposition) {

        StatementDocument statementDocument = documentVersionFactory.createStatementDocument(inputStream);
        StatementDocumentValidator statementDocumentValidator = statementDocumentValidatorFactory
                .create(statementDocument);
        statementDocumentValidator.validate(statementDocument);

        transactionService.save(fileDisposition.getFileName(), statementDocument);

        return Response.created(null).build();
    }
}
