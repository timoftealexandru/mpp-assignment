package util;

import domain.BaseEntity;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.File;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nicu on 3/11/2017.
 */
public class XmlReader<ID, T extends BaseEntity<ID>> {
    private String fileName;

    public XmlReader(String fileName) {
        this.fileName = fileName;
    }

    public List<T> loadEntities() {
        List<T> entities = new ArrayList<>();
            try {
                Document doc = XmlHelper.loadDocument(fileName);

                //optional, but recommended
                //read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
                doc.getDocumentElement().normalize();

                System.out.println("Root element :" + doc.getDocumentElement().getNodeName());

                NodeList nList = doc.getElementsByTagName("staff");

                System.out.println("----------------------------");

                for (int temp = 0; temp < nList.getLength(); temp++) {

                    Node nNode = nList.item(temp);

                    System.out.println("\nCurrent Element :" + nNode.getNodeName());

                    if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                        Element eElement = (Element) nNode;

                        System.out.println("Staff id : " + eElement.getAttribute("id"));
                        System.out.println("First Name : " + eElement.getElementsByTagName("name").item(0).getTextContent());
                        System.out.println("Last Name : " + eElement.getElementsByTagName("director").item(0).getTextContent());
                        System.out.println("Nick Name : " + eElement.getElementsByTagName("genre").item(0).getTextContent());
                        System.out.println("Salary : " + eElement.getElementsByTagName("availableCopies").item(0).getTextContent());

                        int Id = Integer.parseInt(eElement.getAttribute("id"));

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


        return entities;
    }


}
