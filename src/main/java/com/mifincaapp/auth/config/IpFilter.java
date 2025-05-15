package com.mifincaapp.auth.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;

@Component
public class IpFilter implements Filter {

    @Value("${DOMINIOSPERMITIDOS}")
    private String dominiosPermitidosStr;

    private List<String> dominiosPermitidos;

    @Value("${IP_PERMITIDAS}")
    private String ipPermitidasStr;

    private List<String> ipPermitidas;

    @Value("${CUSTOM_HEADER_NAME}")
    private String custom_header_name;

    @Value("${VALOR_CUSTOM_HEADER}")
    private String valorCustomHeader;

    @Override
    public void init(FilterConfig filterConfig) {
        this.dominiosPermitidos = Arrays.asList(dominiosPermitidosStr.split(","));
        this.ipPermitidas = Arrays.asList(ipPermitidasStr.split(","));
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String ipCliente = req.getRemoteAddr();
        String referer = req.getHeader("Referer");
        String originApp = req.getHeader(custom_header_name);

        boolean ipValida = ipEnRango(ipCliente, ipPermitidas);
        boolean dominioValido = referer != null && dominiosPermitidos.stream().anyMatch(referer::contains);
        boolean appMovilValida = valorCustomHeader.equals(originApp);

        String path = req.getRequestURI();

        boolean esAppMovil = originApp != null && originApp.equals(valorCustomHeader);

        if (path.equals("/usuarios") || path.equals("/usuarios/login") || path.equals( "/usuarios/registro")) {
            if (esAppMovil) {
                if (!appMovilValida && !ipValida) {
                    res.sendError(HttpServletResponse.SC_FORBIDDEN, "Acceso denegado: origen no permitido (app móvil)");
                    return;
                }
            } else { // es web
                if (!dominioValido && !ipValida) {
                    res.sendError(HttpServletResponse.SC_FORBIDDEN, "Acceso denegado: origen no permitido (web)");
                    return;
                }
            }
        }
        chain.doFilter(request, response);
    }

    private boolean ipEnRango(String ipCliente, List<String> rangos) {
        for (String rango : rangos) {
            if (ipEnRangoCidr(ipCliente, rango)) {
                return true;
            }
        }
        return false;
    }

    private boolean ipEnRangoCidr(String ip, String cidr) {
        try {
            String[] parts = cidr.split("/");
            String ipBase = parts[0];
            int prefix = Integer.parseInt(parts[1]);

            byte[] ipBytes = java.net.InetAddress.getByName(ip).getAddress();
            byte[] cidrBytes = java.net.InetAddress.getByName(ipBase).getAddress();

            int maskFullBytes = prefix / 8;
            int maskBits = prefix % 8;

            for (int i = 0; i < maskFullBytes; i++) {
                if (ipBytes[i] != cidrBytes[i]) {
                    return false;
                }
            }

            if (maskBits > 0) {
                int mask = ~((1 << (8 - maskBits)) - 1);
                if ((ipBytes[maskFullBytes] & mask) != (cidrBytes[maskFullBytes] & mask)) {
                    return false;
                }
            }

            return true;
        } catch (Exception e) {
            return false;
        }
    }

}

