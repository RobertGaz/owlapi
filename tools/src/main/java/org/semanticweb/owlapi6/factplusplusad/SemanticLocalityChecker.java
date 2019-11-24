package org.semanticweb.owlapi6.factplusplusad;

import static org.semanticweb.owlapi6.utilities.OWLAPIStreamUtils.asList;
import static org.semanticweb.owlapi6.utilities.OWLAPIStreamUtils.pairs;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

import org.semanticweb.owlapi6.atomicdecomposition.AxiomWrapper;
import org.semanticweb.owlapi6.model.OWLAsymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi6.model.OWLAxiom;
import org.semanticweb.owlapi6.model.OWLAxiomVisitorEx;
import org.semanticweb.owlapi6.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi6.model.OWLClassExpression;
import org.semanticweb.owlapi6.model.OWLDataFactory;
import org.semanticweb.owlapi6.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi6.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owlapi6.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owlapi6.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi6.model.OWLDifferentIndividualsAxiom;
import org.semanticweb.owlapi6.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi6.model.OWLDisjointDataPropertiesAxiom;
import org.semanticweb.owlapi6.model.OWLDisjointObjectPropertiesAxiom;
import org.semanticweb.owlapi6.model.OWLDisjointUnionAxiom;
import org.semanticweb.owlapi6.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi6.model.OWLEquivalentDataPropertiesAxiom;
import org.semanticweb.owlapi6.model.OWLEquivalentObjectPropertiesAxiom;
import org.semanticweb.owlapi6.model.OWLFunctionalDataPropertyAxiom;
import org.semanticweb.owlapi6.model.OWLFunctionalObjectPropertyAxiom;
import org.semanticweb.owlapi6.model.OWLInverseFunctionalObjectPropertyAxiom;
import org.semanticweb.owlapi6.model.OWLInverseObjectPropertiesAxiom;
import org.semanticweb.owlapi6.model.OWLIrreflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi6.model.OWLNegativeDataPropertyAssertionAxiom;
import org.semanticweb.owlapi6.model.OWLNegativeObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi6.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi6.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi6.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi6.model.OWLOntologyCreationException;
import org.semanticweb.owlapi6.model.OWLOntologyManager;
import org.semanticweb.owlapi6.model.OWLReflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi6.model.OWLRuntimeException;
import org.semanticweb.owlapi6.model.OWLSameIndividualAxiom;
import org.semanticweb.owlapi6.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi6.model.OWLSubDataPropertyOfAxiom;
import org.semanticweb.owlapi6.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi6.model.OWLSubPropertyChainOfAxiom;
import org.semanticweb.owlapi6.model.OWLSymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi6.model.OWLTransitiveObjectPropertyAxiom;
import org.semanticweb.owlapi6.reasoner.InferenceType;
import org.semanticweb.owlapi6.reasoner.OWLReasoner;
import org.semanticweb.owlapi6.reasoner.OWLReasonerFactory;

/**
 * semantic locality checker for DL axioms
 */
class SemanticLocalityChecker extends LocalityChecker {

    /**
     * Reasoner to detect the tautology
     */
    OWLReasoner kernel;
    /**
     * map between axioms and concept expressions
     */
    Map<OWLAxiom, Collection<OWLClassExpression>> exprMap = new HashMap<>();
    private final OWLOntologyManager manager;
    private final OWLDataFactory df;
    private final OWLReasonerFactory factory;
    private final ExpressionManager expressionManager;

    /**
     * init c'tor
     *
     * @param sig signature
     * @param m ontology manager
     * @param factory factory
     */
    SemanticLocalityChecker(Signature sig, OWLOntologyManager m, OWLReasonerFactory factory) {
        super(sig);
        manager = m;
        df = manager.getOWLDataFactory();
        this.factory = factory;
        expressionManager = new ExpressionManager(df);
    }

    /**
     * @param axiom axiom to convert
     * @return expression necessary to build query for a given type of an axiom; null if none
     *         necessary
     */
    Stream<OWLClassExpression> getExpr(OWLAxiom axiom) {
        OWLClassExpression e = axiom.accept(expressionManager);
        return Stream.of(e).filter(Objects::nonNull);
    }

    /* init kernel with the ontology signature */
    @Override
    public void preprocessOntology(Collection<AxiomWrapper> axioms) {
        exprMap.clear();
        Signature s = new Signature();
        for (AxiomWrapper q : axioms) {
            if (q.isUsed()) {
                exprMap.put(q.getAxiom(), asList(getExpr(q.getAxiom())));
                s.addAll(q.getAxiom().signature());
            }
        }
        // register all the objects in the ontology signature
        Set<OWLAxiom> declarationAxioms = new HashSet<>();
        s.getSignature().map(df::getOWLDeclarationAxiom).forEach(declarationAxioms::add);
        try {
            kernel = factory.createReasoner(manager.createOntology(declarationAxioms));
        } catch (OWLOntologyCreationException e) {
            throw new OWLRuntimeException(e);
        }
        kernel.precomputeInferences(InferenceType.CLASS_HIERARCHY);
    }

    @Override
    public void visit(OWLDeclarationAxiom axiom) {
        isLocal = true;
    }

    @Override
    public void visit(OWLEquivalentClassesAxiom axiom) {
        isLocal = false;
        if (pairs(axiom.classExpressions()).map(v -> df.getOWLEquivalentClassesAxiom(v.i, v.j))
            .anyMatch(ax -> !kernel.isEntailed(ax))) {
            return;
        }
        isLocal = true;
    }

    @Override
    public void visit(OWLDisjointClassesAxiom axiom) {
        isLocal = false;
        if (pairs(axiom.classExpressions()).map(v -> df.getOWLDisjointClassesAxiom(v.i, v.j))
            .anyMatch(ax -> !kernel.isEntailed(ax))) {
            return;
        }
        isLocal = true;
    }

    @Override
    public void visit(OWLDisjointUnionAxiom axiom) {
        isLocal = false;
        // check A = (or C1... Cn)
        if (!kernel.isEntailed(df.getOWLEquivalentClassesAxiom(axiom.getOWLClass(),
            df.getOWLObjectIntersectionOf(axiom.classExpressions())))) {
            return;
        }
        // check disjoint(C1... Cn)
        if (pairs(axiom.classExpressions())
            .anyMatch(v -> !kernel.isEntailed(df.getOWLDisjointClassesAxiom(v.i, v.j)))) {
            return;
        }
        isLocal = true;
    }

    @Override
    public void visit(OWLEquivalentObjectPropertiesAxiom axiom) {
        isLocal = false;
        if (pairs(axiom.properties()).map(v -> df.getOWLEquivalentObjectPropertiesAxiom(v.i, v.j))
            .anyMatch(ax -> !kernel.isEntailed(ax))) {
            return;
        }
        isLocal = true;
    }

    // tautology if all the subsumptions Ri [= Rj holds
    @Override
    public void visit(OWLEquivalentDataPropertiesAxiom axiom) {
        isLocal = false;
        if (pairs(axiom.properties()).map(v -> df.getOWLEquivalentDataPropertiesAxiom(v.i, v.j))
            .anyMatch(ax -> !kernel.isEntailed(ax))) {
            return;
        }
        isLocal = true;
    }

    @Override
    public void visit(OWLDisjointObjectPropertiesAxiom axiom) {
        isLocal = kernel.isEntailed(axiom);
    }

    @Override
    public void visit(OWLDisjointDataPropertiesAxiom axiom) {
        isLocal = kernel.isEntailed(axiom);
    }

    // never local
    @Override
    public void visit(OWLSameIndividualAxiom axiom) {
        isLocal = false;
    }

    // never local
    @Override
    public void visit(OWLDifferentIndividualsAxiom axiom) {
        isLocal = false;
    }

    // R = inverse(S) is tautology iff R [= S- and S [= R-
    @Override
    public void visit(OWLInverseObjectPropertiesAxiom axiom) {
        isLocal = kernel
            .isEntailed(df.getOWLSubObjectPropertyOfAxiom(axiom.getFirstProperty(),
                axiom.getSecondProperty().getInverseProperty()))
            && kernel.isEntailed(df.getOWLSubObjectPropertyOfAxiom(
                axiom.getFirstProperty().getInverseProperty(), axiom.getSecondProperty()));
    }

    @Override
    public void visit(OWLSubPropertyChainOfAxiom axiom) {
        isLocal = kernel.isEntailed(axiom);
    }

    @Override
    public void visit(OWLSubObjectPropertyOfAxiom axiom) {
        isLocal = kernel.isEntailed(axiom);
    }

    @Override
    public void visit(OWLSubDataPropertyOfAxiom axiom) {
        isLocal = kernel.isEntailed(axiom);
    }

    /**
     * Domain(R) = C is tautology iff ER.Top [= C
     */
    @Override
    public void visit(OWLObjectPropertyDomainAxiom axiom) {
        isLocal = true;
        for (OWLClassExpression e : exprMap.get(axiom)) {
            isLocal &= kernel.isEntailed(df.getOWLSubClassOfAxiom(e, axiom.getDomain()));
        }
    }

    @Override
    public void visit(OWLDataPropertyDomainAxiom axiom) {
        isLocal = true;
        for (OWLClassExpression e : exprMap.get(axiom)) {
            isLocal &= kernel.isEntailed(df.getOWLSubClassOfAxiom(e, axiom.getDomain()));
        }
    }

    /**
     * Range(R) = C is tautology iff ER.~C is unsatisfiable
     */
    @Override
    public void visit(OWLObjectPropertyRangeAxiom axiom) {
        isLocal = true;
        for (OWLClassExpression e : exprMap.get(axiom)) {
            isLocal &= !kernel.isSatisfiable(e);
        }
    }

    @Override
    public void visit(OWLDataPropertyRangeAxiom axiom) {
        isLocal = true;
        for (OWLClassExpression e : exprMap.get(axiom)) {
            isLocal &= !kernel.isSatisfiable(e);
        }
    }

    @Override
    public void visit(OWLTransitiveObjectPropertyAxiom axiom) {
        isLocal = kernel.isEntailed(axiom);
    }

    @Override
    public void visit(OWLReflexiveObjectPropertyAxiom axiom) {
        isLocal = kernel.isEntailed(axiom);
    }

    @Override
    public void visit(OWLIrreflexiveObjectPropertyAxiom axiom) {
        isLocal = kernel.isEntailed(axiom);
    }

    @Override
    public void visit(OWLSymmetricObjectPropertyAxiom axiom) {
        isLocal = kernel.isEntailed(axiom);
    }

    @Override
    public void visit(OWLAsymmetricObjectPropertyAxiom axiom) {
        isLocal = kernel.isEntailed(axiom);
    }

    @Override
    public void visit(OWLFunctionalObjectPropertyAxiom axiom) {
        isLocal = kernel.isEntailed(axiom);
    }

    @Override
    public void visit(OWLFunctionalDataPropertyAxiom axiom) {
        isLocal = kernel.isEntailed(axiom);
    }

    @Override
    public void visit(OWLInverseFunctionalObjectPropertyAxiom axiom) {
        isLocal = kernel.isEntailed(axiom);
    }

    @Override
    public void visit(OWLSubClassOfAxiom axiom) {
        isLocal = kernel.isEntailed(axiom);
    }

    /**
     * for top locality, this might be local
     */
    @Override
    public void visit(OWLClassAssertionAxiom axiom) {
        isLocal = kernel.isEntailed(axiom);
    }

    /**
     * R(i,j) holds if {i} [= \ER.{j}
     */
    @Override
    public void visit(OWLObjectPropertyAssertionAxiom axiom) {
        isLocal = kernel.isEntailed(axiom);
    }

    /**
     * !R(i,j) holds if {i} [= \AR.!{j}=!\ER.{j}
     */
    @Override
    public void visit(OWLNegativeObjectPropertyAssertionAxiom axiom) {
        isLocal = kernel.isEntailed(axiom);
    }

    /**
     * R(i,v) holds if {i} [= \ER.{v}
     */
    @Override
    public void visit(OWLDataPropertyAssertionAxiom axiom) {
        isLocal = kernel.isEntailed(axiom);
    }

    @Override
    public void visit(OWLNegativeDataPropertyAssertionAxiom axiom) {
        isLocal = kernel.isEntailed(axiom);
    }

    static class ExpressionManager implements OWLAxiomVisitorEx<OWLClassExpression> {

        private final OWLDataFactory df;

        public ExpressionManager(OWLDataFactory df) {
            this.df = df;
        }

        @Override
        public OWLClassExpression visit(OWLObjectPropertyAssertionAxiom ax) {
            return df.getOWLObjectHasValue(ax.getProperty(), ax.getObject());
        }

        @Override
        public OWLClassExpression visit(OWLDataPropertyAssertionAxiom ax) {
            return df.getOWLDataHasValue(ax.getProperty(), ax.getObject());
        }

        @Override
        public OWLClassExpression visit(OWLObjectPropertyDomainAxiom ax) {
            return df.getOWLObjectSomeValuesFrom(ax.getProperty(), df.getOWLThing());
        }

        @Override
        public OWLClassExpression visit(OWLObjectPropertyRangeAxiom ax) {
            return df.getOWLObjectSomeValuesFrom(ax.getProperty(), ax.getRange());
        }

        @Override
        public OWLClassExpression visit(OWLDataPropertyDomainAxiom ax) {
            return df.getOWLDataSomeValuesFrom(ax.getProperty(), df.getTopDatatype());
        }

        @Override
        public OWLClassExpression visit(OWLDataPropertyRangeAxiom ax) {
            return df.getOWLDataSomeValuesFrom(ax.getProperty(),
                df.getOWLDataComplementOf(ax.getRange()));
        }

        @Override
        public OWLClassExpression visit(OWLNegativeObjectPropertyAssertionAxiom ax) {
            return df.getOWLObjectComplementOf(
                df.getOWLObjectHasValue(ax.getProperty(), ax.getObject()));
        }

        @Override
        public OWLClassExpression visit(OWLNegativeDataPropertyAssertionAxiom ax) {
            return df
                .getOWLObjectComplementOf(df.getOWLDataHasValue(ax.getProperty(), ax.getObject()));
        }
    }
}
