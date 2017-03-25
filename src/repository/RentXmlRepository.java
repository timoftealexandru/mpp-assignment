package repository;

import domain.Rent;
import domain.validators.RentalException;
import domain.validators.Validator;
import domain.validators.ValidatorException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Created by paul on 3/13/2017.
 */
public class RentXmlRepository extends InMemoryRepository<Long, Rent> {
    public RentXmlRepository(Validator<Rent> v) {
        super(v);

        loadData();
    }
    private void loadData() {
        try{
            List<Rent> rents = loadRents();
            rents.forEach((rn)->{
                try {
                    super.save(rn);
                } catch (ValidatorException e) {
                    e.printStackTrace();
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            throw new RentalException(e);
        }
    }

    @Override
    public Optional<Rent> save(Rent entity) throws ValidatorException {
        Optional<Rent> optional = super.save(entity);
        if (optional.isPresent()) {
            return optional;
        }
        try{
            saveRents(entity); //new XmlWriter<Long, Rent>(fileName).saveToXML(entity.getId() + ","+ entity.getClientId() + "," + entity.getMovieId()+ "," + entity.getNoCopies());

        }catch (IOException | SAXException | ParserConfigurationException | TransformerException e){
            e.printStackTrace();
        }
        return Optional.empty();
    }

    private List<Rent> loadRents() throws ParserConfigurationException, IOException, SAXException {
        List<Rent> rents = new ArrayList<>();

        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse("./data/rent1.xml");
        Element root = document.getDocumentElement();

        NodeList nodes = root.getChildNodes();
        Stream<Node> nodeStream = IntStream.range(0, nodes.getLength()).mapToObj(nodes::item);
        nodeStream.forEach((Node node)->{
            if (node instanceof Element) {
                Rent rent = createRentFromNode(node);
                rents.add(rent);
            }
        });

        return rents;
    }

    private Rent createRentFromNode(Node node) {
        Long rentId = Long.parseLong (((Element) node).getAttribute("rentid"));


       long ClientId = Long.parseLong(getTextContentByTagName((Element) node, "ClientId"));
        long MovieId = Long.parseLong(getTextContentByTagName((Element) node, "MovieId"));
        int noCopies = Integer.parseInt(getTextContentByTagName((Element) node, "noCopies"));
        LocalDateTime rentalDate = LocalDateTime.parse(getTextContentByTagName((Element) node, "rentaldate"));
        LocalDateTime returnDate = LocalDateTime.parse(getTextContentByTagName((Element) node, "returndate"));

        Rent rent = new Rent(ClientId, MovieId, noCopies,rentalDate,returnDate);
        rent.setId(rentId);
        return rent;
    }

    private String getTextContentByTagName(Element node, String tagName) {
        NodeList nodes = node.getElementsByTagName(tagName);
        Node nodeWithTextContent = nodes.item(0);
        String textContent = nodeWithTextContent.getTextContent();
        return textContent;
    }

    private void saveRents(Rent rent) throws IOException, SAXException, ParserConfigurationException, TransformerException {
        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse("./data/rent1.xml");
        Element root = document.getDocumentElement();

        Node bookNode = createRentNode(document, root, rent);
        root.appendChild(bookNode);

        //save in file
        Transformer transformer= TransformerFactory.newInstance().newTransformer();
        transformer.transform(new DOMSource(document),new StreamResult(new File("./data/rent2.xml")));
    }

    private Node createRentNode(Document document, Element root, Rent rent) {
        Node rentNode = document.createElement("rent");

        ((Element) rentNode).setAttribute("rentid", rent.getId().toString());
        appendChildNodeWithTextContent(document, rentNode, "clientid", rent.getClientId().toString());
        appendChildNodeWithTextContent(document, rentNode, "movieid", String.valueOf(rent.getMovieId()));
        appendChildNodeWithTextContent(document, rentNode, "nocopies", String.valueOf(rent.getNoCopies()));
        appendChildNodeWithTextContent(document, rentNode, "rentaldate", String.valueOf(rent.getRentalDate()));
        appendChildNodeWithTextContent(document, rentNode, "returndate", String.valueOf(rent.getReturnDate()));


        return rentNode;
    }

    private void appendChildNodeWithTextContent(Document document, Node parent, String tagName, String text) {
        Node node = document.createElement(tagName);
        node.setTextContent(text);

        parent.appendChild(node);
    }

}
