/*
 * Copyright (c) 2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.common.revision.delta;

import org.eclipse.emf.cdo.common.revision.CDOList;

/**
 * If the meaning of this type isn't clear, there really should be more of a description here...
 *
 * @author Eike Stepper
 * @since 4.2
 */
public interface CDOOriginSizeProvider
{
  public int getOriginSize();

  /**
   * @author Eike Stepper
   */
  public static abstract class Caching implements CDOOriginSizeProvider
  {
    private static final int UNKNOWN = -1;

    private int originSize = UNKNOWN;

    public int getOriginSize()
    {
      if (originSize == UNKNOWN)
      {
        CDOList list = getList();
        originSize = list.size();
      }

      return originSize;
    }

    protected abstract CDOList getList();
  }
}
