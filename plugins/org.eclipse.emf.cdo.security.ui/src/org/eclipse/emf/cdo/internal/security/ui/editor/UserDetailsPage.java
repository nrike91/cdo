/*
 * Copyright (c) 2004-2013 Eike Stepper (Berlin, Germany), CEA LIST, and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Christian W. Damus (CEA LIST) - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.security.ui.editor;

import org.eclipse.emf.cdo.internal.security.ui.messages.Messages;
import org.eclipse.emf.cdo.security.SecurityPackage;
import org.eclipse.emf.cdo.security.User;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.edit.domain.EditingDomain;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * 
 */
public class UserDetailsPage extends AbstractDetailsPage<User>
{

  public UserDetailsPage(EditingDomain domain, AdapterFactory adapterFactory)
  {
    super(User.class, SecurityPackage.Literals.USER, domain, adapterFactory);
  }

  @Override
  protected void createContents(Composite parent, FormToolkit toolkit)
  {
    super.createContents(parent, toolkit);

    text(parent, toolkit, Messages.UserDetailsPage_0, SecurityPackage.Literals.ASSIGNEE__ID);

    text(parent, toolkit, Messages.UserDetailsPage_1, SecurityPackage.Literals.USER__FIRST_NAME);
    text(parent, toolkit, Messages.UserDetailsPage_2, SecurityPackage.Literals.USER__LAST_NAME);
    text(parent, toolkit, Messages.UserDetailsPage_3, SecurityPackage.Literals.USER__LABEL);
    text(parent, toolkit, Messages.UserDetailsPage_4, SecurityPackage.Literals.USER__EMAIL);

    space(parent, toolkit);

    checkbox(parent, toolkit, Messages.UserDetailsPage_5, SecurityPackage.Literals.USER__LOCKED);
    button(parent, toolkit, Messages.UserDetailsPage_9, new SelectionAdapter()
    {
      @Override
      public void widgetSelected(SelectionEvent e)
      {
        resetPassword(getInput());
      }
    });

    space(parent, toolkit);

    combo(parent, toolkit, Messages.UserDetailsPage_6, SecurityPackage.Literals.USER__DEFAULT_ACCESS_OVERRIDE);

    space(parent, toolkit);

    oneToMany(parent, toolkit, Messages.UserDetailsPage_7, SecurityPackage.Literals.USER__GROUPS);

    oneToMany(parent, toolkit, Messages.UserDetailsPage_8, SecurityPackage.Literals.ASSIGNEE__ROLES);
  }

  private void resetPassword(final User user)
  {
    new Job(Messages.UserDetailsPage_9)
    {

      @Override
      public IStatus run(IProgressMonitor monitor)
      {
        CDOView view = user.cdoView();
        if (!view.isClosed())
        {
          CDOSession session = view.getSession();
          if (!session.isClosed())
          {
            session.resetCredentials(user.getId());
          }
        }

        return Status.OK_STATUS;
      }
    }.schedule();
  }
}
