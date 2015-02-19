/*
 * Copyright (c) 2004-2014 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.explorer.ui;

import org.eclipse.emf.cdo.explorer.CDOExplorerUtil;
import org.eclipse.emf.cdo.explorer.checkouts.CDOCheckout;
import org.eclipse.emf.cdo.explorer.ui.bundle.OM;
import org.eclipse.emf.cdo.explorer.ui.checkouts.CDOCheckoutLabelProvider;

import org.eclipse.emf.ecore.EObject;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.DecoratingStyledCellLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class ObjectController
{
  private final List<Wrapper> wrappers = new ArrayList<Wrapper>();

  private final CDOCheckout checkout;

  public ObjectController(CDOCheckout checkout)
  {
    this.checkout = checkout;
  }

  public final CDOCheckout getCheckout()
  {
    return checkout;
  }

  public final EObject getObject(Object wrapper)
  {
    if (wrapper instanceof Wrapper)
    {
      return ((Wrapper)wrapper).getObject();
    }

    return null;
  }

  public final void addObject(EObject object, boolean delete)
  {
    wrappers.add(new Wrapper(object, delete));
  }

  public void configure(final TreeViewer treeViewer)
  {
    final LabelProvider labelProvider = new LabelProvider();

    treeViewer.setContentProvider(new ContentProvider());
    treeViewer.setLabelProvider(new DecoratingStyledCellLabelProvider(labelProvider, new LabelDecorator(), null));
    treeViewer.setInput(wrappers);

    treeViewer.getControl().getDisplay().asyncExec(new Runnable()
    {
      public void run()
      {
        configureAsync(treeViewer, labelProvider);
      }
    });
  }

  @SuppressWarnings("deprecation")
  protected void configureAsync(final TreeViewer treeViewer, final LabelProvider labelProvider)
  {
    if (treeViewer instanceof CheckboxTreeViewer)
    {
      CheckboxTreeViewer checkboxTreeViewer = (CheckboxTreeViewer)treeViewer;
      checkboxTreeViewer.setAllChecked(true);
    }

    new Job("Compute paths")
    {
      @Override
      protected IStatus run(IProgressMonitor monitor)
      {
        updateWrappers(treeViewer, labelProvider);
        return Status.OK_STATUS;
      }
    }.schedule();
  }

  protected void updateWrappers(final TreeViewer treeViewer, final LabelProvider labelProvider)
  {
    for (Wrapper wrapper : wrappers)
    {
      try
      {
        if (wrapper.computePath(checkout, labelProvider))
        {
          ViewerUtil.update(treeViewer, wrapper);
        }
      }
      catch (Exception ex)
      {
        //$FALL-THROUGH$
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class Wrapper
  {
    private final EObject object;

    private final boolean delete;

    private String path;

    public Wrapper(EObject object, boolean delete)
    {
      this.object = object;
      this.delete = delete;
    }

    public final EObject getObject()
    {
      return object;
    }

    public final boolean isDelete()
    {
      return delete;
    }

    public final String getPath()
    {
      return path;
    }

    public boolean computePath(CDOCheckout checkout, LabelProvider labelProvider)
    {
      LinkedList<EObject> nodes = CDOExplorerUtil.getPath(object);
      if (nodes != null)
      {
        nodes.removeLast();

        StringBuilder builder = new StringBuilder();
        boolean first = true;

        for (EObject node : nodes)
        {
          builder.append('/');

          if (first)
          {
            builder.append(checkout.getLabel());
            first = false;
          }
          else
          {
            builder.append(labelProvider.getText(node));
          }
        }

        path = builder.toString();
        return true;
      }

      return false;
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class ContentProvider implements ITreeContentProvider
  {
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
    {
    }

    public void dispose()
    {
    }

    public Object[] getElements(Object element)
    {
      return wrappers.toArray();
    }

    public Object getParent(Object element)
    {
      return null;
    }

    public Object[] getChildren(Object element)
    {
      return getElements(element);
    }

    public boolean hasChildren(Object element)
    {
      return element == wrappers && !wrappers.isEmpty();
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class LabelProvider extends CDOCheckoutLabelProvider
  {
    public LabelProvider()
    {
    }

    @Override
    public Image getImage(Object element)
    {
      if (element instanceof Wrapper)
      {
        Wrapper wrapper = (Wrapper)element;
        return super.getImage(wrapper.getObject());
      }

      return super.getImage(element);
    }

    @Override
    public String getText(Object element)
    {
      if (element instanceof Wrapper)
      {
        Wrapper wrapper = (Wrapper)element;
        return super.getText(wrapper.getObject());
      }

      return super.getText(element);
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class LabelDecorator extends BaseLabelDecorator
  {
    private static final Image DELETE_OVERLAY_IMAGE = OM.getImage("icons/delete_ovr.gif");

    @Override
    public Image decorateImage(Image image, Object element)
    {
      if (element instanceof Wrapper)
      {
        Wrapper wrapper = (Wrapper)element;
        if (wrapper.isDelete())
        {
          return OM.getOverlayImage(image, DELETE_OVERLAY_IMAGE, 9, 0);
        }
      }

      return super.decorateImage(image, element);
    }

    @Override
    public String decorateText(String text, Object element)
    {
      if (element instanceof Wrapper)
      {
        Wrapper wrapper = (Wrapper)element;
        String path = wrapper.getPath();
        if (path != null)
        {
          text = path + "/" + text;
        }

        return text;
      }

      return super.decorateText(text, element);
    }
  }

}