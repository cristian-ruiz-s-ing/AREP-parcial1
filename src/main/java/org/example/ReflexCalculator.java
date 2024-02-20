package org.example;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;

public class ReflexCalculator {
    public static void main(String[] args) throws IOException, URISyntaxException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        System.out.println("Starting server...");
        ServerSocket port = new ServerSocket(36000);

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
            if (uri.getPath().startsWith("/")) {
                out.println(getResp(uri.getPath()));
            } else {
                out.println("HTTP/1.1 200 OK\r\n" +
                        "\r\n" +
                        "No se encontró la página");
            }
        }
    }

    private static String getResp(String query) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        int p1 = query.indexOf('(');
        String op = query.substring(1, p1);
        String num = query.substring(p1+1, query.length()-1);

        System.out.println("OP: "+op);
        System.out.println("Num: "+num);
        Class c = Math.class;
        Method m = c.getMethod(op,Double.TYPE);
        Object obj = m.invoke(null, Double.parseDouble(num));

        System.out.println("Obj: "+obj);
        return obj.toString();
    }

}