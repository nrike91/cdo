/*
 * Copyright (c) 2009, 2011, 2012, 2014 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.session;

/**
 * Provides consumers with {@link CDOSession session} instances.
 *
 * @author Eike Stepper
 * @since 2.0
 * @apiviz.exclude
 */
public interface CDOSessionProvider
{
  public CDOSession getSession();
}
