package org.eclipse.emf.cdo.internal.ui.actions;

import org.eclipse.emf.cdo.CDOSession;
import org.eclipse.emf.cdo.internal.ui.bundle.OM;
import org.eclipse.emf.cdo.internal.ui.dialogs.OpenSessionDialog;
import org.eclipse.emf.cdo.internal.ui.views.CDOSessionsView;
import org.eclipse.emf.cdo.protocol.CDOProtocolConstants;

import org.eclipse.emf.internal.cdo.CDOSessionFactory;

import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.ui.actions.LongRunningAction;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchPage;

/**
 * @author Eike Stepper
 */
public final class OpenSessionAction extends LongRunningAction
{
  private String description;

  public OpenSessionAction(IWorkbenchPage page)
  {
    super(page, OpenSessionDialog.TITLE, "Open a new CDO session", CDOSessionsView.getAddImageDescriptor());
  }

  @Override
  protected void preRun() throws Exception
  {
    OpenSessionDialog dialog = new OpenSessionDialog(getPage());
    if (dialog.open() == OpenSessionDialog.OK)
    {
      description = dialog.getServerDescription() + "/" + dialog.getRepositoryName();
      if (!dialog.isLegacySupport())
      {
        description += "?disableLegacyObjects=true";
      }
    }
    else
    {
      cancel();
    }
  }

  @Override
  protected void doRun(IProgressMonitor monitor) throws Exception
  {
    CDOSession session = null;

    try
    {
      String productGroup = CDOSessionFactory.PRODUCT_GROUP;
      String type = CDOProtocolConstants.PROTOCOL_NAME;
      session = (CDOSession)IPluginContainer.INSTANCE.getElement(productGroup, type, description);
    }
    catch (RuntimeException ex)
    {
      OM.LOG.error(ex);
    }

    if (session == null)
    {
      try
      {
        getShell().getDisplay().syncExec(new Runnable()
        {
          public void run()
          {
            try
            {
              MessageDialog.openError(getShell(), getText(), "Could not open a session on the specified repository:\n"
                  + description);
            }
            catch (RuntimeException ignoe)
            {
            }
          }
        });
      }
      catch (RuntimeException ignoe)
      {
      }
    }
  }
}