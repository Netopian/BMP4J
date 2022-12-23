package netopian.network.protocol.bmp4j.controller;

import netopian.network.protocol.bmp4j.integration.Connection;
import netopian.network.protocol.bmp4j.integration.Connections;
import netopian.network.protocol.bmp4j.integration.LifeCycle;
import netopian.network.protocol.bmp4j.integration.OperState;
import netopian.network.protocol.bmp4j.statistic.UpStreamMetric;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

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

import java.net.InetSocketAddress;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen",
    date = "2022-04-13T11:37:17.861+08:00")

@Api(value = "connections", description = "the connections API")
@RequestMapping(value = "/bmp4j")
@Controller
public class ConnectionsController {

    private static final Logger log = LoggerFactory.getLogger(ConnectionsController.class);

    private final ObjectMapper objectMapper;

    private final HttpServletRequest request;

    @Autowired
    private UpStreamMetric upStreamMetric;

    @org.springframework.beans.factory.annotation.Autowired
    public ConnectionsController(ObjectMapper objectMapper, HttpServletRequest request) {
        this.objectMapper = objectMapper;
        this.request = request;
    }

    @ApiOperation(value = "show all active connections currently.", nickname = "getAllConnections", notes = "",
        response = Connections.class, tags = {})
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Successful Operation", response = Connections.class),
        @ApiResponse(code = 200, message = "Unexpected Error", response = Error.class)})
    @RequestMapping(value = "/connections/activity/all", method = RequestMethod.GET)
    public ResponseEntity<Connections> getAllConnections(
        @ApiParam(value = "The start index of paging.") @Valid @RequestParam(value = "from",
            required = false) Integer from,
        @ApiParam(value = "The start index of paging.") @Valid @RequestParam(value = "limit",
            required = false) Integer limit) {
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("application/json")) {
            try {
                Collection<UpStreamMetric.ChannelTimer> rawConns = upStreamMetric.getConnections();
                Connections result = new Connections();
                rawConns.forEach((rawConn) -> result
                    .addEntitiesItem(new Connection().remote(rawConn.getChannel().remoteAddress().toString())
                        .local(rawConn.getChannel().localAddress().toString())
                        .state(rawConn.getLastDeActive() > rawConn.getLastActive() ? OperState.INACT : OperState.ACT)
                        .timer(new LifeCycle().create(rawConn.getCreateTime())
                            .lastActive(rawConn.getLastActive())
                            .lastDeactive(rawConn.getLastDeActive())
                            .lastDuration(rawConn.getLastDeActive() > rawConn.getLastActive()
                                ? rawConn.getLastDeActive() - rawConn.getLastActive()
                                : System.currentTimeMillis() - rawConn.getLastActive()))));
                return new ResponseEntity<Connections>(result, HttpStatus.OK);
            } catch (Exception e) {
                log.error("Couldn't get up-stream metric results: ", e);
                return new ResponseEntity<Connections>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        return new ResponseEntity<Connections>(HttpStatus.NOT_IMPLEMENTED);
    }

    @ApiOperation(value = "show a single connection or null that specificed by remote-local peer.",
        nickname = "getSpecificConnection", notes = "", response = Connection.class, tags = {})
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Successful operation", response = Connection.class),
        @ApiResponse(code = 200, message = "Unexpected Error", response = Error.class)})
    @RequestMapping(value = "/connections/activity/specific/{remote-host}/{remote-port}/{local-host}/{local-port}",
        method = RequestMethod.GET)
    public ResponseEntity<Connection> getSpecificConnection(
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
                UpStreamMetric.ChannelTimer rawConn = upStreamMetric.getConnection(
                    new InetSocketAddress(remoteHost, remotePort), new InetSocketAddress(localHost, localPort));
                Connection result = new Connection();
                if (rawConn != null) {
                    result.remote(rawConn.getChannel().remoteAddress().toString())
                        .local(rawConn.getChannel().localAddress().toString())
                        .state(rawConn.getLastDeActive() > rawConn.getLastActive() ? OperState.INACT : OperState.ACT)
                        .timer(new LifeCycle().create(rawConn.getCreateTime())
                            .lastActive(rawConn.getLastActive())
                            .lastDeactive(rawConn.getLastDeActive())
                            .lastDuration(rawConn.getLastDeActive() > rawConn.getLastActive()
                                ? rawConn.getLastDeActive() - rawConn.getLastActive()
                                : System.currentTimeMillis() - rawConn.getLastActive()));
                }
                return new ResponseEntity<Connection>(result, HttpStatus.OK);
            } catch (Exception e) {
                log.error("Couldn't get up-stream metric results: ", e);
                return new ResponseEntity<Connection>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        return new ResponseEntity<Connection>(HttpStatus.NOT_IMPLEMENTED);
    }

    @ApiOperation(value = "reboot a connection that specificed by remote-local peer.",
        nickname = "rebootSpecificConnection", notes = "", tags = {})
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Successful operation"),
        @ApiResponse(code = 200, message = "Unexpected Error", response = Error.class)})
    @RequestMapping(value = "/connections/activity/specific/{remote-host}/{remote-port}/{local-host}/{local-port}",
        method = RequestMethod.DELETE)
    public ResponseEntity<Void> rebootSpecificConnection(
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
            upStreamMetric.removeConnection(new InetSocketAddress(remoteHost, remotePort),
                new InetSocketAddress(localHost, localPort));
        } catch (Exception e) {
            log.error("Couldn't remove up-stream metric with error: ", e);
            return new ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<Void>(HttpStatus.OK);
    }

}
