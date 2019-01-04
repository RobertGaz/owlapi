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
package org.semanticweb.owlapi6.utility;

import static org.semanticweb.owlapi6.utilities.OWLAPIStreamUtils.add;

import java.util.Collection;

import org.semanticweb.owlapi6.model.OWLAnnotationProperty;
import org.semanticweb.owlapi6.model.OWLClass;
import org.semanticweb.owlapi6.model.OWLDataProperty;
import org.semanticweb.owlapi6.model.OWLDatatype;
import org.semanticweb.owlapi6.model.OWLEntity;
import org.semanticweb.owlapi6.model.OWLNamedIndividual;
import org.semanticweb.owlapi6.model.OWLObjectProperty;
import org.semanticweb.owlapi6.model.OWLOntology;

/**
 * A utility class that visits axioms, class expressions etc. and accumulates the named objects that
 * are referred to in those axioms, class expressions etc. For example, if the collector visited the
 * axiom (propP some C) subClassOf (propQ some D), it would contain the objects propP, C, propQ and
 * D.
 *
 * @author Matthew Horridge, The University Of Manchester, Bio-Health Informatics Group
 * @since 2.0.0
 */
public class OWLEntityCollector extends AbstractCollectorEx<OWLEntity> {

    /**
     * @param toReturn the set that will contain the results
     */
    public OWLEntityCollector(Collection<OWLEntity> toReturn) {
        super(toReturn);
    }

    // OWLClassExpressionVisitor
    @Override
    public Collection<OWLEntity> visit(OWLClass ce) {
        objects.add(ce);
        return objects;
    }

    // Entity visitor
    @Override
    public Collection<OWLEntity> visit(OWLObjectProperty property) {
        objects.add(property);
        return objects;
    }

    @Override
    public Collection<OWLEntity> visit(OWLDataProperty property) {
        objects.add(property);
        return objects;
    }

    @Override
    public Collection<OWLEntity> visit(OWLNamedIndividual individual) {
        objects.add(individual);
        return objects;
    }

    @Override
    public Collection<OWLEntity> visit(OWLDatatype node) {
        objects.add(node);
        return objects;
    }

    @Override
    public Collection<OWLEntity> visit(OWLOntology ontology) {
        add(objects, ontology.unsortedSignature());
        return objects;
    }

    @Override
    public Collection<OWLEntity> visit(OWLAnnotationProperty property) {
        objects.add(property);
        return objects;
    }
}