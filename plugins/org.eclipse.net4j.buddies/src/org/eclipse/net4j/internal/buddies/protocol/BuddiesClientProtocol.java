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
package org.eclipse.net4j.internal.buddies.protocol;

import org.eclipse.net4j.buddies.internal.protocol.BuddyStateIndication;
import org.eclipse.net4j.buddies.protocol.ProtocolConstants;
import org.eclipse.net4j.buddies.protocol.IBuddy.State;
import org.eclipse.net4j.internal.buddies.Buddy;
import org.eclipse.net4j.internal.buddies.BuddySession;
import org.eclipse.net4j.signal.SignalProtocol;
import org.eclipse.net4j.signal.SignalReactor;
import org.eclipse.net4j.util.concurrent.ConcurrencyUtil;

/**
 * @author Eike Stepper
 */
public class BuddiesClientProtocol extends SignalProtocol
{
  public BuddiesClientProtocol()
  {
  }

  public String getType()
  {
    return ProtocolConstants.PROTOCOL_NAME;
  }

  @Override
  protected SignalReactor doCreateSignalReactor(short signalID)
  {
    switch (signalID)
    {
    case ProtocolConstants.SIGNAL_BUDDY_ADDED:
      return new BuddyAddedIndication();

    case ProtocolConstants.SIGNAL_BUDDY_REMOVED:
      return new BuddyRemovedIndication();

    case ProtocolConstants.SIGNAL_BUDDY_STATE:
      return new BuddyStateIndication()
      {
        @Override
        protected void stateChanged(String userID, State state)
        {
          for (int i = 0; i < 50; i++)
          {
            BuddySession session = (BuddySession)getProtocol().getInfraStructure();
            if (session == null)
            {
              ConcurrencyUtil.sleep(100);
            }
            else
            {
              Buddy buddy = (Buddy)session.getBuddies().get(userID);
              if (buddy != null)
              {
                buddy.setState(state);
              }

              break;
            }
          }
        }
      };
    }

    return null;
  }
}
