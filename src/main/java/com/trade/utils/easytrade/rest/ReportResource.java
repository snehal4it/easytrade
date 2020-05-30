package com.trade.utils.easytrade.rest;

import com.trade.utils.easytrade.service.ReportService;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.time.LocalDate;

@Named
@Path("/report")
public class ReportResource {

    @Inject
    private ReportService reportService;

    @Path("/mapped-transactions")
    @GET
    public Response generateMappedTransactionsReport(@QueryParam("startDate") LocalDate startDate,
                                                     @QueryParam("endDate") LocalDate endDate) {

        reportService.generateMappedTransactionsReport(startDate, endDate);

        return Response.created(URI.create("/report")).build();
    }

}
