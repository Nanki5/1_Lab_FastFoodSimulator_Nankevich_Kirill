package org.example.dailymeal;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import org.example.dailymeal.DataAccessLayer.Db;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class ParserXmlToJavaObjects {
    public static Db ParseXmlToJavaObject(String pathname) throws JAXBException, FileNotFoundException{
        List<Object> products = new ArrayList<>();
        File file = new File(pathname);

        if(!file.exists()){
            throw new FileNotFoundException("Файл не найден");
        }

        JAXBContext jaxbContext = JAXBContext.newInstance(Db.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        Db db = (Db)jaxbUnmarshaller.unmarshal(file);

        return db;
    }
}
