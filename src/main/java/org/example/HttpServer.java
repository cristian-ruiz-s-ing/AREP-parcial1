package org.example;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;

public class HttpServer {
    private static final String USER_AGENT = "Mozilla/5.0";
    private static final String GET_URL = "http://localhost:36000/";


    public static void main(String[] args) throws IOException, URISyntaxException {
        System.out.println("Starting server...");
        ServerSocket port = new ServerSocket(35000);

        Socket cliente = null;
        Boolean running = true;

        while (running) {
            cliente = port.accept();
            PrintWriter out = new PrintWriter(cliente.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
            String inputLine;

            Boolean firstLine = true;
            String reqURI = "";
            while ((inputLine = in.readLine()) != null) {
                System.out.println("Recibe: " + inputLine);
                if (firstLine) {
                    firstLine = false;
                    reqURI = inputLine.split(" ")[1];
                }
                if (!in.ready()) {
                    break;
                }
            }

            URI uri = new URI(reqURI);
            if (uri.getPath().startsWith("/calculadora")) {
                out.println(getClient());
            } else if (uri.getPath().startsWith("/computar")) {
                String valor = uri.getQuery().split("=")[1];
                out.println(getComputar(valor));
            }else {
                out.println("HTTP/1.1 200 OK\r\n" +
                        "\r\n" +
                        "No se encontró la página");
            }
        }
    }

    private static String getComputar(String valor) throws IOException {
        URL obj = new URL(GET_URL+valor);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", USER_AGENT);

        //The following invocation perform the connection implicitly before getting the code
        int responseCode = con.getResponseCode();
        System.out.println("GET Response Code :: " + responseCode);

        if (responseCode == HttpURLConnection.HTTP_OK) { // success
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // print result
            System.out.println(response.toString());
            return "HTTP/1.1 200 OK\r\n" +
                    "\r\n"+
                    response.toString();
        } else {
            return "HTTP/1.1 200 OK\r\n" +
                    "\r\n"+
                    "No se encontró el resultado";

        }
    }

    private static String getClient() {
        return "HTTP/1.1 200 OK\r\n" +
                "\r\n" +
                "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <title>Calculadora</title>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "</head>\n" +
                "<body>\n" +
                "<h1>Calculadora Math</h1>\n" +
                "<form action=\"/calculadora\">\n" +
                "    <label for=\"operacion\">Operación</label><br>\n" +
                "    <input type=\"text\" id=\"operacion\" name=\"operacion\" value=\"cos(1)\"><br><br>\n" +
                "    <input type=\"button\" value=\"Submit\" onclick=\"loadGetResp()\">\n" +
                "</form>\n" +
                "<div id=\"getresp\"></div>\n" +
                "\n" +
                "<script>\n" +
                "    function loadGetResp() {\n" +
                "        let nameVar = document.getElementById(\"operacion\").value;\n" +
                "        const xhttp = new XMLHttpRequest();\n" +
                "        xhttp.onload = function() {\n" +
                "            document.getElementById(\"getresp\").innerHTML =\n" +
                "            this.responseText;\n" +
                "        }\n" +
                "        xhttp.open(\"GET\", \"/computar?name=\"+nameVar);\n" +
                "        xhttp.send();\n" +
                "    }\n" +
                "</script>";
    }
}