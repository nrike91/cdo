/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.tests.model1.OrderDetail;
import org.eclipse.emf.cdo.tests.model1.Product1;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.util.CommitException;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.spi.cdo.InternalCDOTransaction;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Egidijus Vaisnora
 */
public class Bugzilla_350987_Test extends AbstractCDOTest
{
  public void testRestoringReferences() throws CommitException
  {
    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource(getResourcePath("test"));

      Category category = getModel1Factory().createCategory();
      resource.getContents().add(category);

      Product1 product = getModel1Factory().createProduct1();
      category.getProducts().add(product);

      OrderDetail orderDetail = getModel1Factory().createOrderDetail();
      orderDetail.setProduct(product);
      resource.getContents().add(orderDetail);

      transaction.commit();
      session.close();
    }

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.getResource(getResourcePath("test"));

    Category category = (Category)resource.getContents().get(0);
    OrderDetail order = (OrderDetail)resource.getContents().get(1);

    EList<Product1> products = category.getProducts();
    List<Product1> productList = new ArrayList<Product1>(products);
    products.clear(); // Detach
    products.addAll(productList); // Reattach

    CDORevision revision = CDOUtil.getCDOObject(order).cdoRevision();
    InternalCDORevision originRevision = ((InternalCDOTransaction)transaction).getCleanRevisions().get(order);
    CDORevisionDelta delta = revision.compare(originRevision);

    // Comparing with clean revision should not give changes
    assertEquals(0, delta.size());
    assertEquals(true, delta.isEmpty());

    Product1 product = products.get(0);
    resource.getContents().remove(1);
    resource.getContents().add(order);
    revision = CDOUtil.getCDOObject(product).cdoRevision();
    int previousSize = product.getOrderDetails().size();
    product.getOrderDetails().add(order);

    // Element shouldn't be added
    assertEquals(previousSize, product.getOrderDetails().size());
    originRevision = ((InternalCDOTransaction)transaction).getCleanRevisions().get(product);
    delta = revision.compare(originRevision);

    // Comparing with clean revision should not give changes
    assertEquals(true, delta.isEmpty());
  }
}