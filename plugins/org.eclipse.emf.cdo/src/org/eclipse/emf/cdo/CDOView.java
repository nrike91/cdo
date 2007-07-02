/***************************************************************************
 * Copyright (c) 2004-2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.protocol.CDOID;
import org.eclipse.emf.cdo.protocol.revision.CDORevision;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;

/**
 * @author Eike Stepper
 */
public interface CDOView
{
  public static final long UNSPECIFIED_DATE = CDORevision.UNSPECIFIED_DATE;

  public ResourceSet getResourceSet();

  public CDOSession getSession();

  public long getTimeStamp();

  public boolean isHistorical();

  public boolean isReadWrite();

  public boolean isReadOnly();

  /**
   * @see ResourceSet#createResource(URI)
   */
  public CDOResource createResource(String path);

  /**
   * @see ResourceSet#getResource(URI, boolean)
   */
  public CDOResource getResource(String path);

  public CDORevision resolve(CDOID id);

  /**
   * @see CDOTransaction#commit()
   */
  public void commit();

  /**
   * @see CDOTransaction#rollback()
   */
  public void rollback();

  public void close();
}
