/*
 * Copyright (c) 2011, 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Victor Roldan Betancort - maintenance
 */
package org.eclipse.emf.cdo.explorer.ui.bundle;

import org.eclipse.net4j.util.om.OMBundle;
import org.eclipse.net4j.util.om.OMPlatform;
import org.eclipse.net4j.util.om.log.OMLogger;
import org.eclipse.net4j.util.om.pref.OMPreference;
import org.eclipse.net4j.util.om.pref.OMPreferences;
import org.eclipse.net4j.util.om.trace.OMTracer;
import org.eclipse.net4j.util.ui.UIActivator;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.edit.provider.ComposedImage;
import org.eclipse.emf.edit.ui.provider.ExtendedImageRegistry;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

import java.util.Arrays;
import java.util.List;

/**
 * The <em>Operations & Maintenance</em> class of this bundle.
 *
 * @author Eike Stepper
 */
public abstract class OM
{
  public static final String BUNDLE_ID = "org.eclipse.emf.cdo.explorer.ui"; //$NON-NLS-1$

  public static final OMBundle BUNDLE = OMPlatform.INSTANCE.bundle(BUNDLE_ID, OM.class);

  public static final OMTracer DEBUG = BUNDLE.tracer("debug"); //$NON-NLS-1$

  public static final OMLogger LOG = BUNDLE.logger();

  public static final OMPreferences PREFS = BUNDLE.preferences();

  public static final OMPreference<Integer> PREF_REPOSITORY_TIMEOUT_MINUTES = //
  PREFS.init("PREF_REPOSITORY_TIMEOUT_MINUTES", 5); //$NON-NLS-1$

  public static final OMPreference<Boolean> PREF_REPOSITORY_TIMEOUT_DISABLED = //
  PREFS.init("PREF_REPOSITORY_TIMEOUT_DISABLED", false); //$NON-NLS-1$

  public static Image getOverlayImage(Object image, Object overlayImage, int x, int y)
  {
    ComposedImage composedImage = new OverlayImage(image, overlayImage, x, y);
    return ExtendedImageRegistry.INSTANCE.getImage(composedImage);
  }

  public static Image getImage(String imagePath)
  {
    return ExtendedImageRegistry.INSTANCE.getImage(getBundleURI(imagePath));
  }

  public static ImageDescriptor getImageDescriptor(String imagePath)
  {
    return ExtendedImageRegistry.INSTANCE.getImageDescriptor(getBundleURI(imagePath));
  }

  private static URI getBundleURI(String path)
  {
    return URI.createPlatformPluginURI(BUNDLE_ID + "/" + path, true);
  }

  /**
   * @author Eike Stepper
   */
  public static final class Activator extends UIActivator
  {
    public static Activator INSTANCE;

    public Activator()
    {
      super(BUNDLE);
      INSTANCE = this;
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class OverlayImage extends ComposedImage
  {
    private final int x;

    private final int y;

    public OverlayImage(Object image, Object overlayImage, int x, int y)
    {
      super(Arrays.asList(image, overlayImage));
      this.x = x;
      this.y = y;
    }

    @Override
    public List<ComposedImage.Point> getDrawPoints(Size size)
    {
      List<ComposedImage.Point> result = super.getDrawPoints(size);
      Point overLayPoint = result.get(1);
      overLayPoint.x = x;
      overLayPoint.y = y;
      return result;
    }
  }
}
