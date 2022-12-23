package netopian.network.protocol.bmp4j.controller;

import java.net.InetSocketAddress;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import netopian.network.protocol.bmp4j.integration.Message;
import netopian.network.protocol.bmp4j.integration.Messages;
import netopian.network.protocol.bmp4j.integration.Statistic;
import netopian.network.protocol.bmp4j.statistic.SerdesMetric;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen",
    date = "2022-04-13T11:37:17.861+08:00")

@Api(value = "messages", description = "the messages API")
@RequestMapping(value = "/bmp4j")
@Controller
public class MessagesController {

    private static final Logger log = LoggerFactory.getLogger(MessagesController.class);

    private final ObjectMapper objectMapper;

    private final HttpServletRequest request;

    @Autowired
    private SerdesMetric serdesMetric;

    @org.springframework.beans.factory.annotation.Autowired
    public MessagesController(ObjectMapper objectMapper, HttpServletRequest request) {
        this.objectMapper = objectMapper;
        this.request = request;
    }

    @ApiOperation(value = "show all message statistics currently.", nickname = "getAllStatistics", notes = "",
        response = Messages.class, tags = {})
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Successful Operation", response = Messages.class),
        @ApiResponse(code = 200, message = "Unexpected Error", response = Error.class)})
    @RequestMapping(value = "/messages/statistic/all", method = RequestMethod.GET)
    public ResponseEntity<Messages> getAllStatistics(
        @ApiParam(value = "The start index of paging.") @Valid @RequestParam(value = "from",
            required = false) Integer from,
        @ApiParam(value = "The start index of paging.") @Valid @RequestParam(value = "limit",
            required = false) Integer limit) {
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("application/json")) {
            try {
                Collection<SerdesMetric.MessageCounter> counters = serdesMetric.getCounters();
                Messages result = new Messages();

                counters.forEach((counter) -> result.addEntitiesItem(new Message().remote(counter.getRemote())
                    .local(counter.getLocal())
                    .addStatisticsItem(new Statistic().identity("total")
                        .receive(counter.getRxCounter().get())
                        .transmit(counter.getTxCounter().get())
                        .incomplete(counter.getIncCounter().get()))));

                return new ResponseEntity<Messages>(result, HttpStatus.OK);
            } catch (Exception e) {
                log.error("Couldn't get message counters: ", e);
                return new ResponseEntity<Messages>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        return new ResponseEntity<Messages>(HttpStatus.NOT_IMPLEMENTED);
    }

    @ApiOperation(value = "show statistics of a single connection that specificed by remote-local peer.",
        nickname = "getSpecificStatistics", notes = "", response = Message.class, tags = {})
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Successful operation", response = Message.class),
        @ApiResponse(code = 200, message = "Unexpected Error", response = Error.class)})
    @RequestMapping(value = "/messages/statistic/specific/{remote-host}/{remote-port}/{local-host}/{local-port}",
        method = RequestMethod.GET)
    public ResponseEntity<Message> getSpecificStatistics(
        @ApiParam(value = "the remote host of connection.",
            required = true) @PathVariable("remote-host") String remoteHost,
        @ApiParam(value = "the remote port of connection.",
            required = true) @PathVariable("remote-port") Integer remotePort,
        @ApiParam(value = "the local host of connection.",
            required = true) @PathVariable("local-host") String localHost,
        @ApiParam(value = "the local port of connection.",
            required = true) @PathVariable("local-port") Integer localPort) {
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("application/json")) {
            try {
                SerdesMetric.MessageCounter counter = serdesMetric.getCounter(
                    new InetSocketAddress(remoteHost, remotePort), new InetSocketAddress(localHost, localPort));
                Message result = new Message();
                if (counter == null) {
                    result.remote(counter.getRemote())
                        .local(counter.getLocal())
                        .addStatisticsItem(new Statistic().identity("total")
                            .receive(counter.getRxCounter().get())
                            .transmit(counter.getTxCounter().get())
                            .incomplete(counter.getIncCounter().get()));
                }

                return new ResponseEntity<Message>(result, HttpStatus.OK);
            } catch (Exception e) {
                log.error("Couldn't get message counter: ", e);
                return new ResponseEntity<Message>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        return new ResponseEntity<Message>(HttpStatus.NOT_IMPLEMENTED);
    }

    @ApiOperation(value = "reset statistics of a connection that specificed by remote-local peer.",
        nickname = "resetSpecificStatistics", notes = "", tags = {})
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Successful operation"),
        @ApiResponse(code = 200, message = "Unexpected Error", response = Error.class)})
    @RequestMapping(value = "/messages/statistic/specific/{remote-host}/{remote-port}/{local-host}/{local-port}",
        method = RequestMethod.DELETE)
    public ResponseEntity<Void> resetSpecificStatistics(
        @ApiParam(value = "the remote host of connection.",
            required = true) @PathVariable("remote-host") String remoteHost,
        @ApiParam(value = "the remote port of connection.",
            required = true) @PathVariable("remote-port") Integer remotePort,
        @ApiParam(value = "the local host of connection.",
            required = true) @PathVariable("local-host") String localHost,
        @ApiParam(value = "the local port of connection.",
            required = true) @PathVariable("local-port") Integer localPort) {
        String accept = request.getHeader("Accept");

        try {
            serdesMetric.removeCounter(new InetSocketAddress(remoteHost, remotePort),
                new InetSocketAddress(localHost, localPort));
        } catch (Exception e) {
            log.error("Couldn't remove serdes metric with error: ", e);
            return new ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<Void>(HttpStatus.NOT_IMPLEMENTED);
    }

}
