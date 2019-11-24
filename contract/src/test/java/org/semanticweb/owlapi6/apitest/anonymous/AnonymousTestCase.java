/* This file is part of the OWL API.
 * The contents of this file are subject to the LGPL License, Version 3.0.
 * Copyright 2014, The University of Manchester
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program.  If not, see http://www.gnu.org/licenses/.
 *
 * Alternatively, the contents of this file may be used under the terms of the Apache License, Version 2.0 in which case, the provisions of the Apache License Version 2.0 are applicable instead of those above.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License. */
package org.semanticweb.owlapi6.apitest.anonymous;

import static org.semanticweb.owlapi6.OWLFunctionalSyntaxFactory.AnonymousIndividual;
import static org.semanticweb.owlapi6.OWLFunctionalSyntaxFactory.Class;
import static org.semanticweb.owlapi6.OWLFunctionalSyntaxFactory.ClassAssertion;
import static org.semanticweb.owlapi6.OWLFunctionalSyntaxFactory.DataProperty;
import static org.semanticweb.owlapi6.OWLFunctionalSyntaxFactory.DataPropertyAssertion;
import static org.semanticweb.owlapi6.OWLFunctionalSyntaxFactory.IRI;
import static org.semanticweb.owlapi6.OWLFunctionalSyntaxFactory.Literal;
import static org.semanticweb.owlapi6.OWLFunctionalSyntaxFactory.ObjectHasValue;
import static org.semanticweb.owlapi6.OWLFunctionalSyntaxFactory.ObjectProperty;
import static org.semanticweb.owlapi6.OWLFunctionalSyntaxFactory.SubClassOf;

import org.junit.Test;
import org.semanticweb.owlapi6.apitest.baseclasses.TestBase;
import org.semanticweb.owlapi6.model.OWLClass;
import org.semanticweb.owlapi6.model.OWLDataProperty;
import org.semanticweb.owlapi6.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi6.model.OWLIndividual;
import org.semanticweb.owlapi6.model.OWLNamedIndividual;
import org.semanticweb.owlapi6.model.OWLObjectProperty;
import org.semanticweb.owlapi6.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi6.model.OWLOntology;

public class AnonymousTestCase extends TestBase {

    private static final String URN_TEST = "urn:test#";

    @Test
    public void shouldRoundTrip() throws Exception {
        OWLClass c = Class(IRI(URN_TEST, "C"));
        OWLClass d = Class(IRI(URN_TEST, "D"));
        OWLObjectProperty p = ObjectProperty(IRI(URN_TEST, "p"));
        OWLDataProperty q = DataProperty(IRI(URN_TEST, "q"));
        OWLIndividual i = AnonymousIndividual();
        OWLOntology ontology = getOWLOntology();
        ontology.add(SubClassOf(c, ObjectHasValue(p, i)), ClassAssertion(d, i),
            DataPropertyAssertion(q, i, Literal("hello")));
        OWLOntology ontologyReloaded =
            loadOntologyFromString(saveOntology(ontology), ontology.getNonnullFormat());
        equal(ontology, ontologyReloaded);
    }

    @Test
    public void testRoundTripWithAnonymousIndividuals() throws Exception {
        String ns = "http://test.com/genid#";
        OWLNamedIndividual i = df.getOWLNamedIndividual(ns, "i");
        OWLObjectProperty p = df.getOWLObjectProperty(ns, "p");
        OWLDataProperty q = df.getOWLDataProperty(ns, "q");
        OWLOntology ontology = getOWLOntology();
        OWLIndividual ind = df.getOWLAnonymousIndividual();
        OWLObjectPropertyAssertionAxiom ax1 = df.getOWLObjectPropertyAssertionAxiom(p, i, ind);
        OWLDataPropertyAssertionAxiom ax2 =
            df.getOWLDataPropertyAssertionAxiom(q, ind, df.getOWLLiteral(5));
        ontology.add(ax1, ax2);
        OWLOntology reload = roundTrip(ontology);
        equal(ontology, reload);
    }
}
