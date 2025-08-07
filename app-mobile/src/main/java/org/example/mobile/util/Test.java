package org.example.mobile.util;

import org.springframework.data.repository.init.ResourceReader;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Test {

    public static void main(String[] args) throws IOException, URISyntaxException {

        String path = ResourceReader.class.getClassLoader().getResource("data.txt").toURI().getPath();

        String xml = new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);

        xml = xml.trim().replaceFirst("^\\uFEFF", "");
        System.out.println(XMLFilter.filterXML(xml));

    }
}
