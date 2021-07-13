//package org.semanticweb.owlapi.model;
//
//import com.google.common.collect.TreeMultimap;
//import org.semanticweb.owlapi.model.parameters.ChangeApplied;
//
//import java.sql.Timestamp;
//import java.util.HashSet;
//import java.util.Iterator;
//import java.util.Set;
//
//
//public class OWLTemporalAxioms {
//    private OWLOntology ontology;
//    private TreeMultimap<Long, OWLAxiom> all;
//
//    public OWLTemporalAxioms(OWLOntology ontology) {
//        this.ontology = ontology;
//        all = TreeMultimap.create();
//    }
//
//    public void putAxiom(OWLAxiom axiom) {
//        Long start = axiom.getPeriod().getStart();
//        Long end = axiom.getPeriod().getEnd();
//        if (start != null) all.put(start, axiom);
//        if (end != null) all.put(end, axiom);
//    }
//
//
//    private Iterator<Long> timeIter;
//    private Iterator<OWLAxiom> actionIter;
//    private Long currentTimestamp;
//    private OWLAxiom currentAxiom;
//    private boolean ready = false;
//
//    private void getReady() {
//        timeIter = all.keySet().iterator();
//        if (timeIter.hasNext()) {
//            currentTimestamp = timeIter.next();
//            actionIter = all.get(currentTimestamp).iterator();
//        }
//        ready = true;
//    }
//
//
//    public ChangeApplied makeChange() {
//        if (!ready) {
//            getReady();
//        }
//        if (actionIter.hasNext()) {
//            currentAxiom = actionIter.next();
//            Long start = currentAxiom.getPeriod().getStart();
//            Long end = currentAxiom.getPeriod().getEnd();
//            if (start != null && start.equals(currentTimestamp)) {
//                return ontology.getOWLOntologyManager().addAxiom(ontology, currentAxiom);
//            }
//            if (end != null && end.equals(currentTimestamp)) {
//                return ontology.getOWLOntologyManager().removeAxiom(ontology, currentAxiom);
//            }
//        } else if (timeIter.hasNext()) {
//            currentTimestamp = timeIter.next();
//            actionIter = all.get(currentTimestamp).iterator();
//            return makeChange();
//        }
//
//        ready = false;
//        return null;
//
//    }
//
//    public ChangeApplied rollBack() {
//        Set<OWLAxiom> axioms = new HashSet(all.values());
//        return ontology.getOWLOntologyManager().removeAxioms(ontology, axioms);
//    }
//}
//
//
//
//
///*
//    private class Action implements Comparable<Action>{
//        OWLAxiom axiom;
//        boolean toPut; //false if removing
//
//        Action(OWLAxiom axiom, boolean toPut) {
//            this.axiom = axiom;
//            this.toPut = toPut;
//        }
//
//        public void act() {
//            if (toPut) {
//                addAxiom(ontology, axiom);
//            } else {
//                removeAxiom(ontology, axiom);
//            }
//        }
//
//        public int compareTo(Action other) {
//            if (this.toPut == true) {
//                return 1;
//            } else {
//                return -1;
//            }
//        }
//    }
//*/
