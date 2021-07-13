package org.semanticweb.owlapi.examples;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.RDFXMLDocumentFormat;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Set;


public class JustTest {

    static InputStream input = null;

    public static void main(String[] args) throws Exception {

        try {
            input = new FileInputStream("C:/Users/gaziz/Desktop/pikaperProps.owl");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLOntology ontology = manager.loadOntologyFromOntologyDocument(input);

        Set<OWLAxiom> axioms = ontology.getAxioms();
        System.out.println("YoY!");

        manager.saveOntology(ontology, new RDFXMLDocumentFormat(), new FileOutputStream("C:\\Users\\gaziz\\Desktop\\adjika.owl"));

    }

}
