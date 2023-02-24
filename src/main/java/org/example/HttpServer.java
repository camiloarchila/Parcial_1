package org.example;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.*;
import java.io.*;
public class HttpServer {
    public static void main(String[] args) throws IOException, ClassNotFoundException, InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(35000);
        } catch (IOException e) {
            System.err.println("Could not listen on port: 35000.");
            System.exit(1);
        }

        Socket clientSocket = null;
        boolean running = true;
        while(running) {
            try {
                System.out.println("Listo para recibir ...");
                clientSocket = serverSocket.accept();
            } catch (IOException e) {
                System.err.println("Accept failed.");
                System.exit(1);
            }
            PrintWriter out = new PrintWriter(
                    clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));
            String inputLine, outputLine;
            outputLine = getPage();
            String classname = "";
            String methodName = "";
            String paramType = "";
            String param ="";
            Method[] listmethods;
            Field[] listfields;
            Class[] classes;

            while ((inputLine = in.readLine()) != null) {
                System.out.println("Recibí: " + inputLine);
                if (!in.ready()) {
                    break;
                }

                if (inputLine.contains("=class")) {
                    classname = inputLine.split("=class")[1].split("HTTP")[0].replace("(", "").replace(")", "").replace(" ", "");
                    System.out.println(classname);
                    listmethods = Class.forName(classname).getDeclaredMethods();
                    listfields = Class.forName(classname).getFields();
                    String methods = " Methosd: [";
                    for(Method m: listmethods) {
                        System.out.println(m.getName());
                        methods += m.getName() + ", ";
                    }

                    String fields = "Fields: [";
                    for(Field f: listfields){
                        fields += f.getName() + ", ";
                    }
                    outputLine = getHeader() + methods + "] \n" + fields + "]";
                }
                else if(inputLine.contains("=invoke")){
                    classname = inputLine.split("=invoke")[1].split("HTTP")[0].replace("(", "").replace(")", "").replace(" ", "").split(",")[0];
                    methodName = inputLine.split("=invoke")[1].split("HTTP")[0].replace("(", "").replace(")", "").replace(" ", "").split(",")[1];
                    outputLine = getHeader() + Class.forName(classname).getDeclaredMethod(methodName, null).invoke(null);
                }else if(inputLine.contains("=unaryInvoke")){
                    classname = inputLine.split("=invoke")[1].split("HTTP")[0].replace("(", "").replace(")", "").replace(" ", "").split(",")[0];
                    methodName = inputLine.split("=invoke")[1].split("HTTP")[0].replace("(", "").replace(")", "").replace(" ", "").split(",")[1];
                    paramType = inputLine.split("=invoke")[1].split("HTTP")[0].replace("(", "").replace(")", "").replace(" ", "").split(",")[2];
                    param = inputLine.split("=invoke")[1].split("HTTP")[0].replace("(", "").replace(")", "").replace(" ", "").split(",")[3];

                }
            }
            out.println(outputLine);
            out.close();
            in.close();

        }
        clientSocket.close();
        serverSocket.close();
    }

    private Class[] getType(String paramType){
        if(paramType == "String"){
            return new
        }else if(paramType == "int"){

        }else if(paramType == "double"){

        }
    }
    private static String getHeader(){
        return "HTTP/1.1 200 OK\r\n"
                + "Content-Type: text/string\r\n"
                + "\r\n"
                ;
    }

    private static String getPage(){
        return "HTTP/1.1 200 OK\r\n"
                + "Content-Type: text/html\r\n"
                + "\r\n"
                + "<!DOCTYPE html>\n" +
                "<html>\n" +
                "    <head>\n" +
                "        <title>Reflective ChatGPT</title>\n" +
                "        <meta charset=\"UTF-8\">\n" +
                "        <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    </head>\n" +
                "    <body>\n" +
                "        <h1>Reflective ChatGPT</h1>\n" +
                "        <form action=\"/class\">\n" +
                "            <label for=\"name\">Name:</label><br>\n" +
                "            <input type=\"text\" id=\"name\" name=\"name\" value=\"John\"><br><br>\n" +
                "            <input type=\"button\" value=\"Submit\" onclick=\"loadGetMsg()\">\n" +
                "        </form> \n" +
                "        <div id=\"getrespmsg\"></div>\n" +
                "\n" +
                "        <script>\n" +
                "            function loadGetMsg() {\n" +
                "                let nameVar = document.getElementById(\"name\").value;\n" +
                "                const xhttp = new XMLHttpRequest();\n" +
                "                xhttp.onload = function() {\n" +
                "                    document.getElementById(\"getrespmsg\").innerHTML =\n" +
                "                    this.responseText;\n" +
                "                }\n" +
                "                xhttp.open(\"GET\", \"/Comand?name=\"+nameVar);\n" +
                "                xhttp.send();\n" +
                "            }\n" +
                "        </script>\n" +
                "\n" +
                "    </body>\n" +
                "</html>";
        }

}