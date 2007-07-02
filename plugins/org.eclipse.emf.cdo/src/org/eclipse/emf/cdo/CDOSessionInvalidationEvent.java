/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo;

import org.eclipse.emf.cdo.protocol.CDOID;

import org.eclipse.net4j.util.event.IEvent;

import java.util.Set;

/**
 * @author Eike Stepper
 */
public interface CDOSessionInvalidationEvent extends IEvent
{
  public CDOSession getSession();

  public CDOView getView();

  public long getTimeStamp();

  public Set<CDOID> getDirtyOIDs();
}
