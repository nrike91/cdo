/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - maintenance
 **************************************************************************/
package org.eclipse.emf.internal.cdo;

import org.eclipse.emf.cdo.CDOLock;
import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.cdo.CDOView;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.model.CDOClass;
import org.eclipse.emf.cdo.common.model.CDOFeature;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.eresource.impl.CDOResourceImpl;
import org.eclipse.emf.cdo.spi.common.InternalCDORevision;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.emf.internal.cdo.bundle.OM;
import org.eclipse.emf.internal.cdo.util.FSMUtil;
import org.eclipse.emf.internal.cdo.util.ModelUtil;

import org.eclipse.net4j.util.ImplementationError;
import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.concurrent.RWLockManager;
import org.eclipse.net4j.util.concurrent.TimeoutRuntimeException;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.BasicEMap;
import org.eclipse.emf.common.util.ECollections;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.BasicEObjectImpl;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EStoreEObjectImpl;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.Resource.Internal;
import org.eclipse.emf.ecore.util.DelegatingFeatureMap;
import org.eclipse.emf.ecore.util.EcoreEList;
import org.eclipse.emf.ecore.util.EcoreEMap;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.eclipse.emf.ecore.util.InternalEList;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;

/**
 * @author Eike Stepper
 */
public class CDOObjectImpl extends EStoreEObjectImpl implements InternalCDOObject
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_OBJECT, CDOObjectImpl.class);

  private CDOID id;

  private CDOState state;

  private InternalCDOView view;

  private InternalCDORevision revision;

  /**
   * CDO used this list instead of eSettings for transient objects. EMF used eSettings as cache. CDO deactivates the
   * cache but EMF still used eSettings to store list wrappers. CDO needs another place to store the real list with the
   * actual data (transient mode) and accessible through EStore. This allows CDO to always use the same instance of the
   * list wrapper.
   */
  private transient Object cdoSettings[];

  public CDOObjectImpl()
  {
    state = CDOState.TRANSIENT;
    eContainer = null;
    cdoSettings = null;
  }

  public CDOID cdoID()
  {
    return id;
  }

  public CDOState cdoState()
  {
    return state;
  }

  public InternalCDORevision cdoRevision()
  {
    return revision;
  }

  /**
   * @since 2.0
   */
  protected Object[] cdoSettings()
  {
    if (cdoSettings == null)
    {
      int size = eClass().getFeatureCount() - eStaticFeatureCount();
      cdoSettings = size == 0 ? ENO_SETTINGS : new Object[size];
    }

    return cdoSettings;
  }

  /**
   * @since 2.0
   */
  protected Object[] cdoBasicSettings()
  {
    return cdoSettings;
  }

  public CDOClass cdoClass()
  {
    return getCDOClass(this);
  }

  /**
   * @since 2.0
   */
  public InternalCDOView cdoView()
  {
    return view;
  }

  public CDOResourceImpl cdoResource()
  {
    Resource resource = eResource();
    if (resource instanceof CDOResourceImpl)
    {
      return (CDOResourceImpl)resource;
    }

    return null;
  }

  /**
   * @since 2.0
   */
  public CDOResourceImpl cdoDirectResource()
  {
    Resource.Internal resource = eDirectResource();
    if (resource instanceof CDOResourceImpl)
    {
      return (CDOResourceImpl)resource;
    }

    return null;
  }

  public void cdoReload()
  {
    CDOStateMachine.INSTANCE.reload(this);
  }

  /**
   * @since 2.0
   */
  public boolean cdoConflict()
  {
    return FSMUtil.isConflict(this);
  }

  /**
   * @since 2.0
   */
  public boolean cdoInvalid()
  {
    return FSMUtil.isInvalid(this);
  }

  public void cdoInternalSetID(CDOID id)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Setting ID: {0}", id);
    }

    this.id = id;
  }

  public CDOState cdoInternalSetState(CDOState state)
  {
    if (this.state != state)
    {
      if (TRACER.isEnabled())
      {
        TRACER.format("Setting state {0} for {1}", state, this);
      }

      try
      {
        return this.state;
      }
      finally
      {
        this.state = state;
      }
    }

    // TODO Detect duplicate cdoInternalSetState() calls
    return null;
  }

  public void cdoInternalSetRevision(CDORevision revision)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Setting revision: {0}", revision);
    }

    this.revision = (InternalCDORevision)revision;
  }

  public void cdoInternalSetView(CDOView view)
  {
    this.view = (InternalCDOView)view;
    if (this.view != null)
    {
      eSetStore(this.view.getStore());
    }
    else
    {
      eSetStore(null);
    }
  }

  public void cdoInternalSetResource(CDOResource resource)
  {
    throw new UnsupportedOperationException();
  }

  public void cdoInternalPostLoad()
  {
    // Reset EMAP objects
    if (eSettings != null)
    {
      // Make sure transient features are kept but persisted values are not cached.
      EClass eClass = eClass();
      for (int i = 0; i < eClass.getFeatureCount(); i++)
      {
        EStructuralFeature eFeature = cdoInternalDynamicFeature(i);

        // We need to keep the existing list if possible.
        if (!eFeature.isTransient() && eSettings[i] instanceof InternalCDOLoadable)
        {
          ((InternalCDOLoadable)eSettings[i]).cdoInternalPostLoad();
        }
      }
    }
  }

  /**
   * @since 2.0
   */
  public void cdoInternalPostInvalid()
  {
    // Do nothing
  }

  /**
   * @since 2.0
   */
  public void cdoInternalCleanup()
  {
    // Do nothing
  }

  public void cdoInternalPostAttach()
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Populating revision for {0}", this);
    }

    InternalCDOView view = cdoView();
    revision.setContainerID(eContainer == null ? CDOID.NULL : cdoView().convertObjectToID(eContainer, true));
    revision.setContainingFeatureID(eContainerFeatureID);

    Resource directResource = eDirectResource();
    if (directResource instanceof CDOResource)
    {
      CDOResource cdoResource = (CDOResource)directResource;
      revision.setResourceID(cdoResource.cdoID());
    }

    EClass eClass = eClass();
    for (int i = 0; i < eClass.getFeatureCount(); i++)
    {
      EStructuralFeature eFeature = cdoInternalDynamicFeature(i);
      if (!eFeature.isTransient())
      {
        populateRevisionFeature(view, revision, eFeature, eSettings, i);
      }
    }

    cdoSettings = null;
  }

  /**
   * @since 2.0
   */
  public CDOLock cdoReadLock()
  {
    if (FSMUtil.isTransient(this) || FSMUtil.isNew(this))
    {
      return NOOPLockImpl.INSTANCE;
    }

    // Should we cache the locks ?
    return new CDOLockImpl(RWLockManager.LockType.READ);
  }

  /**
   * @since 2.0
   */
  public CDOLock cdoWriteLock()
  {
    if (FSMUtil.isTransient(this) || FSMUtil.isNew(this))
    {
      return NOOPLockImpl.INSTANCE;
    }

    // Should we cache the locks ?
    return new CDOLockImpl(RWLockManager.LockType.WRITE);
  }

  @SuppressWarnings("unchecked")
  private void populateRevisionFeature(InternalCDOView view, InternalCDORevision revision, EStructuralFeature eFeature,
      Object[] eSettings, int i)
  {
    CDOSessionPackageManagerImpl packageManager = (CDOSessionPackageManagerImpl)view.getSession().getPackageManager();
    CDOFeature cdoFeature = packageManager.getCDOFeature(eFeature);
    if (TRACER.isEnabled())
    {
      TRACER.format("Populating feature {0}", cdoFeature);
    }

    Object setting = cdoBasicSettings() != null ? cdoSettings()[i] : null;

    CDOStore cdoStore = cdoStore();

    if (cdoFeature.isMany())
    {
      if (setting != null)
      {
        int index = 0;
        EList<Object> list = (EList<Object>)setting;
        for (Object value : list)
        {
          value = cdoStore.convertToCDO(cdoView(), eFeature, cdoFeature, value);
          revision.add(cdoFeature, index++, value);
        }
      }
    }
    else
    {
      setting = cdoStore.convertToCDO(cdoView(), eFeature, cdoFeature, setting);
      revision.set(cdoFeature, 0, setting);
    }
  }

  /**
   * It is really important for accessing the data to go through {@link #cdoStore()}. {@link #eStore()} will redirect
   * you to the transient data.
   */
  public void cdoInternalPostDetach()
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Depopulating revision for {0}", this);
    }

    InternalCDOView view = cdoView();
    super.eSetDirectResource((Resource.Internal)cdoStore().getResource(this));

    CDOStore store = cdoStore();
    eContainer = store.getContainer(this);
    eContainerFeatureID = store.getContainingFeatureID(this);
    if (eContainer != null && eContainmentFeature().isResolveProxies())
    {
      adjustOppositeReference(eContainer, eContainmentFeature());
    }

    // Ensure that the internal eSettings array is initialized;
    resetSettings();

    EClass eClass = eClass();
    for (int i = 0; i < eClass.getFeatureCount(); i++)
    {
      EStructuralFeature eFeature = cdoInternalDynamicFeature(i);
      if (!eFeature.isTransient())
      {
        depopulateRevisionFeature(view, revision, eFeature, eSettings, i);
      }
    }
  }

  private void resetSettings()
  {
    cdoSettings = null;
    cdoSettings();
  }

  private void depopulateRevisionFeature(InternalCDOView view, InternalCDORevision revision,
      EStructuralFeature eFeature, Object[] eSettings, int i)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Depopulating feature {0}", eFeature);
    }

    EStructuralFeature.Internal internalFeature = (EStructuralFeature.Internal)eFeature;
    EReference oppositeReference = cdoID().isTemporary() ? null : internalFeature.getEOpposite();

    CDOStore cdoStore = cdoStore();
    EStore eStore = eStore();
    if (eFeature.isMany())
    {
      int size = cdoStore.size(this, eFeature);
      for (int index = 0; index < size; index++)
      {
        // Do not trigger events
        // Do not trigger inverse updates
        Object object = cdoStore.get(this, eFeature, index);
        eStore.add(this, eFeature, index, object);
        if (oppositeReference != null)
        {
          adjustOppositeReference((InternalEObject)object, oppositeReference);
        }
      }
    }
    else
    {
      Object object = cdoStore.get(this, eFeature, EStore.NO_INDEX);
      eStore.set(this, eFeature, EStore.NO_INDEX, object);
      if (oppositeReference != null)
      {
        adjustOppositeReference((InternalEObject)object, oppositeReference);
      }
    }
  }

  /**
   * Adjust the reference ONLY if the opposite reference used CDOID. This is true ONLY if the state of <cdo>this</code>
   * was not {@link CDOState#NEW}.
   */
  @SuppressWarnings("unchecked")
  private void adjustOppositeReference(InternalEObject object, EReference feature)
  {
    if (object != null)
    {
      InternalCDOObject cdoObject = (InternalCDOObject)CDOUtil.getCDOObject(object);
      if (cdoObject != null && !FSMUtil.isTransient(cdoObject))
      {
        if (feature.isMany())
        {
          int index = cdoObject.eStore().indexOf(cdoObject, feature, cdoID());

          // TODO Simon Log an error in the new view.getErrors() in the case we are not able to find the object.
          // Cannot throw an exception, the detach process is too far.
          if (index != -1)
          {
            cdoObject.eStore().set(cdoObject, feature, index, this);
          }
        }
        else
        {
          cdoObject.eStore().set(cdoObject, feature, 0, this);
        }
      }
      else
      {
        if (feature.isResolveProxies())
        {
          // We should not trigger events. But we have no choice :-(.
          if (feature.isMany())
          {
            InternalEList<Object> list = (InternalEList<Object>)object.eGet(feature);
            int index = list.indexOf(this);
            if (index != -1)
            {
              list.set(index, this);
            }
          }
          else
          {
            object.eSet(feature, this);
          }
        }
      }

    }
  }

  public void cdoInternalPreCommit()
  {
    // Do nothing
  }

  public InternalEObject cdoInternalInstance()
  {
    return this;
  }

  public EStructuralFeature cdoInternalDynamicFeature(int dynamicFeatureID)
  {
    return eDynamicFeature(dynamicFeatureID);
  }

  /**
   * @since 2.0
   */
  @Override
  public synchronized EList<Adapter> eAdapters()
  {
    if (eAdapters == null)
    {
      // TODO Adjust for EObjectEAdapterList (see bug #247130)
      eAdapters = new EAdapterList<Adapter>(this)
      {
        private static final long serialVersionUID = 1L;

        @Override
        protected void didAdd(int index, Adapter newObject)
        {
          super.didAdd(index, newObject);
          if (!FSMUtil.isTransient(CDOObjectImpl.this))
          {
            cdoView().handleAddAdapter(CDOObjectImpl.this, newObject);
          }
        }

        @Override
        protected void didRemove(int index, Adapter oldObject)
        {
          super.didRemove(index, oldObject);
          if (!FSMUtil.isTransient(CDOObjectImpl.this))
          {
            cdoView().handleRemoveAdapter(CDOObjectImpl.this, oldObject);
          }
        }
      };
    }

    return eAdapters;
  }

  @Override
  protected FeatureMap createFeatureMap(EStructuralFeature eStructuralFeature)
  {
    return new CDOStoreFeatureMap(eStructuralFeature);
  }

  @Override
  protected EList<?> createList(final EStructuralFeature eStructuralFeature)
  {
    final EClassifier eType = eStructuralFeature.getEType();

    // Answer from Christian Damus
    // Java ensures that string constants are interned, so this is actually
    // more efficient than .equals() and it's correct
    if (eType.getInstanceClassName() == "java.util.Map$Entry")
    {
      class EStoreEcoreEMap extends EcoreEMap<Object, Object> implements InternalCDOLoadable
      {
        private static final long serialVersionUID = 1L;

        public EStoreEcoreEMap()
        {
          super((EClass)eType, BasicEMap.Entry.class, null);
          delegateEList = new BasicEStoreEList<BasicEMap.Entry<Object, Object>>(CDOObjectImpl.this, eStructuralFeature)
          {
            private static final long serialVersionUID = 1L;

            @Override
            protected void didAdd(int index, BasicEMap.Entry<Object, Object> newObject)
            {
              EStoreEcoreEMap.this.doPut(newObject);
            }

            @Override
            protected void didSet(int index, BasicEMap.Entry<Object, Object> newObject,
                BasicEMap.Entry<Object, Object> oldObject)
            {
              didRemove(index, oldObject);
              didAdd(index, newObject);
            }

            @Override
            protected void didRemove(int index, BasicEMap.Entry<Object, Object> oldObject)
            {
              EStoreEcoreEMap.this.doRemove(oldObject);
            }

            @Override
            protected void didClear(int size, Object[] oldObjects)
            {
              EStoreEcoreEMap.this.doClear();
            }

            @Override
            protected void didMove(int index, BasicEMap.Entry<Object, Object> movedObject, int oldIndex)
            {
              EStoreEcoreEMap.this.doMove(movedObject);
            }
          };

          size = delegateEList.size();
        }

        private void checkListForReading()
        {
          if (!FSMUtil.isTransient(CDOObjectImpl.this))
          {
            CDOStateMachine.INSTANCE.read(CDOObjectImpl.this);
          }
        }

        /**
         * Ensures that the entry data is created and is populated with contents of the delegate list.
         */
        @Override
        protected synchronized void ensureEntryDataExists()
        {
          checkListForReading();
          super.ensureEntryDataExists();
        }

        @Override
        public int size()
        {
          checkListForReading();
          return size;
        }

        @Override
        public boolean isEmpty()
        {
          checkListForReading();
          return size == 0;
        }

        @Override
        public boolean contains(Object object)
        {
          checkListForReading();
          return super.contains(object);
        }

        @Override
        public boolean containsAll(Collection<?> collection)
        {
          checkListForReading();
          return super.containsAll(collection);
        }

        @Override
        public boolean containsKey(Object key)
        {
          checkListForReading();
          return super.containsKey(key);
        }

        @Override
        public boolean containsValue(Object value)
        {
          checkListForReading();
          return super.containsValue(value);
        }

        public void cdoInternalPostLoad()
        {
          entryData = null;
          size = delegateEList.size();
        }
      }

      return new EStoreEcoreEMap();
    }

    return super.createList(eStructuralFeature);
  }

  @Override
  protected void eInitializeContainer()
  {
    throw new ImplementationError();
  }

  @Override
  protected void eSetDirectResource(Internal resource)
  {
    if (FSMUtil.isTransient(this))
    {
      super.eSetDirectResource(resource);
    }
    else if (resource instanceof CDOResourceImpl || resource == null)
    {
      cdoStore().setContainer(this, (CDOResourceImpl)resource, eInternalContainer(), eContainerFeatureID());
    }
    else
    {
      throw new IllegalArgumentException("Resource needs to be an instanceof CDOResourceImpl");
    }
  }

  /**
   * @since 2.0
   */
  @Override
  public Resource.Internal eDirectResource()
  {
    if (FSMUtil.isTransient(this))
    {
      return super.eDirectResource();
    }

    return (Resource.Internal)cdoStore().getResource(this);
  }

  /**
   * @since 2.0
   */
  @Override
  protected boolean eDynamicIsSet(int dynamicFeatureID, EStructuralFeature eFeature)
  {
    return dynamicFeatureID < 0 ? eOpenIsSet(eFeature) : eSettingDelegate(eFeature).dynamicIsSet(this, eSettings(),
        dynamicFeatureID);
  }

  /**
   * TODO: TO BE REMOVED once https://bugs.eclipse.org/bugs/show_bug.cgi?id=259855 is available to downloads
   */
  @Override
  public void dynamicSet(int dynamicFeatureID, Object value)
  {
    EStructuralFeature eStructuralFeature = eDynamicFeature(dynamicFeatureID);
    if (eStructuralFeature.isTransient())
    {
      eSettings[dynamicFeatureID] = value;
    }
    else
    {
      eStore().set(this, eStructuralFeature, InternalEObject.EStore.NO_INDEX, value);
      if (eIsCaching())
      {
        eSettings[dynamicFeatureID] = value;
      }
    }
  }

  /**
   * @since 2.0
   */
  @Override
  public InternalEObject.EStore eStore()
  {
    if (FSMUtil.isTransient(this))
    {
      return CDOStoreSettingsImpl.INSTANCE;
    }

    return cdoStore();
  }

  /**
   * Don't cache non-transient features in this CDOObject's {@link #eSettings()}.
   */
  @Override
  protected boolean eIsCaching()
  {
    return false;
  }

  @Override
  public InternalEObject eInternalContainer()
  {
    InternalEObject container;
    if (FSMUtil.isTransient(this))
    {
      container = eContainer;
    }
    else
    {
      // Delegate to CDOStore
      container = cdoStore().getContainer(this);
    }

    // TODO Eike: It is still needed ?? Since we do not use container as two possibles value (container or resource)
    // I think it should be removed!
    if (container instanceof CDOResource)
    {
      return null;
    }

    return container;
  }

  @Override
  public int eContainerFeatureID()
  {
    if (FSMUtil.isTransient(this))
    {
      return eContainerFeatureID;
    }

    // Delegate to CDOStore
    return cdoStore().getContainingFeatureID(this);
  }

  /**
   * Code took from {@link BasicEObjectImpl#eBasicSetContainer} and modify it to detect when object are moved in the
   * same context. (E.g.: An object is moved from resA to resB. resA and resB belongs to the same Repository. Without
   * this special handling, a detach and newObject will be generated for the object moved)
   * 
   * @since 2.0
   */
  @Override
  public NotificationChain eBasicSetContainer(InternalEObject newContainer, int newContainerFeatureID,
      NotificationChain msgs)
  {
    boolean isResourceRoot = this instanceof CDOResource && ((CDOResource)this).isRoot();

    InternalEObject oldContainer = eInternalContainer();
    Resource.Internal oldResource = eDirectResource();
    Resource.Internal newResource = null;
    if (oldResource != null)
    {
      if (newContainer != null && !eContainmentFeature(this, newContainer, newContainerFeatureID).isResolveProxies())
      {
        msgs = ((InternalEList<?>)oldResource.getContents()).basicRemove(this, msgs);
        eSetDirectResource(null);
        newResource = newContainer.eInternalResource();
      }
      else
      {
        oldResource = null;
      }
    }
    else
    {
      if (oldContainer != null)
      {
        oldResource = oldContainer.eInternalResource();
      }

      if (newContainer != null)
      {
        newResource = newContainer.eInternalResource();
      }
    }

    CDOView oldView = view;
    CDOView newView = newResource != null && newResource instanceof CDOResource ? ((CDOResource)newResource).cdoView()
        : null;

    boolean moved = oldView != null && oldView == newView;
    if (!moved && oldResource != null && !isResourceRoot)
    {
      oldResource.detached(this);
    }

    int oldContainerFeatureID = eContainerFeatureID();
    eBasicSetContainer(newContainer, newContainerFeatureID);

    if (!moved && oldResource != newResource && newResource != null)
    {
      newResource.attached(this);
    }

    if (eNotificationRequired())
    {
      if (oldContainer != null && oldContainerFeatureID >= 0 && oldContainerFeatureID != newContainerFeatureID)
      {
        ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, oldContainerFeatureID,
            oldContainer, null);
        if (msgs == null)
        {
          msgs = notification;
        }
        else
        {
          msgs.add(notification);
        }
      }

      if (newContainerFeatureID >= 0)
      {
        ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, newContainerFeatureID,
            oldContainerFeatureID == newContainerFeatureID ? oldContainer : null, newContainer);
        if (msgs == null)
        {
          msgs = notification;
        }
        else
        {
          msgs.add(notification);
        }
      }
    }

    return msgs;
  }

  /**
   * Code took from {@link BasicEObjectImpl#eSetResource} and modify it to detect when object are moved in the same
   * context.
   * 
   * @since 2.0
   */
  @Override
  public NotificationChain eSetResource(Resource.Internal resource, NotificationChain notifications)
  {
    Resource.Internal oldResource = eDirectResource();

    CDOView oldView = view;
    CDOView newView = resource != null && resource instanceof CDOResource ? ((CDOResource)resource).cdoView() : null;

    boolean isSameView = oldView != null && oldView == newView;

    if (oldResource != null)
    {
      notifications = ((InternalEList<?>)oldResource.getContents()).basicRemove(this, notifications);

      // When setting the resource to null we assume that detach has already been called in the resource implementation
      //
      if (!isSameView && resource != null)
      {
        oldResource.detached(this);
      }
    }

    InternalEObject oldContainer = eInternalContainer();
    if (oldContainer != null && !isSameView)
    {
      if (eContainmentFeature().isResolveProxies())
      {
        Resource.Internal oldContainerResource = oldContainer.eInternalResource();
        if (oldContainerResource != null)
        {
          // If we're not setting a new resource, attach it to the old container's resource.
          if (resource == null)
          {
            oldContainerResource.attached(this);
          }
          // If we didn't detach it from an old resource already, detach it from the old container's resource.
          //
          else if (oldResource == null)
          {
            oldContainerResource.detached(this);
          }
        }
      }
      else
      {
        notifications = eBasicRemoveFromContainer(notifications);
        notifications = eBasicSetContainer(null, -1, notifications);
      }
    }

    eSetDirectResource(resource);

    return notifications;
  }

  @Override
  protected void eBasicSetContainer(InternalEObject newEContainer, int newContainerFeatureID)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Setting container: {0}, featureID={1}", newEContainer, newContainerFeatureID);
    }

    if (FSMUtil.isTransient(this))
    {
      super.eBasicSetContainer(newEContainer, newContainerFeatureID);
    }
    else
    {
      cdoStore().setContainer(this, cdoDirectResource(), newEContainer, newContainerFeatureID);
    }
  }

  /**
   * Specializing the behaviour of {@link #equals(Object)} is not permitted as per {@link EObject} specification.
   */
  @Override
  public final boolean equals(Object obj)
  {
    return super.equals(obj);
  }

  @Override
  public String toString()
  {
    if (id == null)
    {
      return eClass().getName() + "?";
    }

    return eClass().getName() + "@" + id;
  }

  static CDOClass getCDOClass(InternalCDOObject cdoObject)
  {
    InternalCDOView view = cdoObject.cdoView();
    CDOSessionPackageManagerImpl packageManager = (CDOSessionPackageManagerImpl)view.getSession().getPackageManager();
    return ModelUtil.getCDOClass(cdoObject.eClass(), packageManager);
  }

  private CDOStore cdoStore()
  {
    return cdoView().getStore();
  }

  /**
   * @author Simon McDuff
   * @since 2.0
   */
  private final class CDOLockImpl implements CDOLock
  {
    private RWLockManager.LockType type;

    public CDOLockImpl(RWLockManager.LockType type)
    {
      this.type = type;
    }

    public RWLockManager.LockType getType()
    {
      return type;
    }

    public boolean isLocked()
    {
      return cdoView().isObjectLocked(CDOObjectImpl.this, type);
    }

    public void lock()
    {
      try
      {
        cdoView().lockObjects(Collections.singletonList(CDOObjectImpl.this), type, CDOLock.WAIT);
      }
      catch (InterruptedException ex)
      {
        throw WrappedException.wrap(ex);
      }
    }

    public void lockInterruptibly() throws InterruptedException
    {
      lock();
    }

    public Condition newCondition()
    {
      throw new UnsupportedOperationException();
    }

    public boolean tryLock()
    {
      try
      {
        cdoView().lockObjects(Collections.singletonList(CDOObjectImpl.this), type, CDOLock.NO_WAIT);
        return true;
      }
      catch (TimeoutRuntimeException ex)
      {
        return false;
      }
      catch (InterruptedException ex)
      {
        return false;
      }
    }

    /**
     * @throws will
     *           throw an exception if timeout is reached.
     */
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException
    {
      try
      {
        cdoView().lockObjects(Collections.singletonList(CDOObjectImpl.this), type, unit.toMillis(time));
        return true;
      }
      catch (TimeoutRuntimeException ex)
      {
        return false;
      }
    }

    public void unlock()
    {
      cdoView().unlockObjects(Collections.singletonList(CDOObjectImpl.this), type);
    }
  }

  /**
   * @author Simon McDuff
   * @since 2.0
   */
  public static class CDOStoreSettingsImpl implements InternalEObject.EStore
  {
    public static CDOStoreSettingsImpl INSTANCE = new CDOStoreSettingsImpl();

    private CDOStoreSettingsImpl()
    {
    }

    protected Object getValue(InternalEObject eObject, int dynamicFeatureID)
    {
      return ((CDOObjectImpl)eObject).cdoSettings()[dynamicFeatureID];

    }

    protected EList<Object> getValueAsList(InternalEObject eObject, int dynamicFeatureID)
    {
      @SuppressWarnings("unchecked")
      EList<Object> result = (EList<Object>)getValue(eObject, dynamicFeatureID);
      if (result == null)
      {
        result = new BasicEList<Object>();
        ((CDOObjectImpl)eObject).cdoSettings()[dynamicFeatureID] = result;
      }

      return result;
    }

    protected Object setValue(InternalEObject eObject, int dynamicFeatureID, Object newValue)
    {
      Object eSettings[] = ((CDOObjectImpl)eObject).cdoSettings();

      try
      {
        return eSettings[dynamicFeatureID];
      }
      finally
      {
        eSettings[dynamicFeatureID] = newValue;
      }
    }

    protected int eDynamicFeatureID(InternalEObject eObject, EStructuralFeature feature)
    {
      return ((CDOObjectImpl)eObject).eDynamicFeatureID(feature);
    }

    public Object get(InternalEObject eObject, EStructuralFeature feature, int index)
    {
      int dynamicFeatureID = eDynamicFeatureID(eObject, feature);
      if (feature.isMany())
      {
        return getValueAsList(eObject, dynamicFeatureID).get(index);
      }

      return getValue(eObject, dynamicFeatureID);
    }

    public Object set(InternalEObject eObject, EStructuralFeature feature, int index, Object value)
    {
      int dynamicFeatureID = eDynamicFeatureID(eObject, feature);
      if (feature.isMany())
      {
        return getValueAsList(eObject, dynamicFeatureID).set(index, value);
      }

      return setValue(eObject, dynamicFeatureID, value);
    }

    public void add(InternalEObject eObject, EStructuralFeature feature, int index, Object value)
    {
      int dynamicFeatureID = eDynamicFeatureID(eObject, feature);
      getValueAsList(eObject, dynamicFeatureID).add(index, value);
    }

    public Object remove(InternalEObject eObject, EStructuralFeature feature, int index)
    {
      int dynamicFeatureID = eDynamicFeatureID(eObject, feature);
      return getValueAsList(eObject, dynamicFeatureID).remove(index);
    }

    public Object move(InternalEObject eObject, EStructuralFeature feature, int targetIndex, int sourceIndex)
    {
      int dynamicFeatureID = eDynamicFeatureID(eObject, feature);
      return getValueAsList(eObject, dynamicFeatureID).move(targetIndex, sourceIndex);
    }

    public void clear(InternalEObject eObject, EStructuralFeature feature)
    {
      int dynamicFeatureID = eDynamicFeatureID(eObject, feature);
      if (feature.isMany())
      {
        getValueAsList(eObject, dynamicFeatureID).clear();
      }

      setValue(eObject, dynamicFeatureID, null);
    }

    public int size(InternalEObject eObject, EStructuralFeature feature)
    {
      int dynamicFeatureID = eDynamicFeatureID(eObject, feature);
      return getValueAsList(eObject, dynamicFeatureID).size();
    }

    public int indexOf(InternalEObject eObject, EStructuralFeature feature, Object value)
    {
      int dynamicFeatureID = eDynamicFeatureID(eObject, feature);
      return getValueAsList(eObject, dynamicFeatureID).indexOf(value);
    }

    public int lastIndexOf(InternalEObject eObject, EStructuralFeature feature, Object value)
    {
      int dynamicFeatureID = eDynamicFeatureID(eObject, feature);
      return getValueAsList(eObject, dynamicFeatureID).lastIndexOf(value);
    }

    public Object[] toArray(InternalEObject eObject, EStructuralFeature feature)
    {
      int dynamicFeatureID = eDynamicFeatureID(eObject, feature);
      return getValueAsList(eObject, dynamicFeatureID).toArray();
    }

    public <T> T[] toArray(InternalEObject eObject, EStructuralFeature feature, T[] array)
    {
      int dynamicFeatureID = eDynamicFeatureID(eObject, feature);
      return getValueAsList(eObject, dynamicFeatureID).toArray(array);
    }

    public boolean isEmpty(InternalEObject eObject, EStructuralFeature feature)
    {
      int dynamicFeatureID = eDynamicFeatureID(eObject, feature);
      return getValueAsList(eObject, dynamicFeatureID).isEmpty();
    }

    public boolean contains(InternalEObject eObject, EStructuralFeature feature, Object value)
    {
      int dynamicFeatureID = eDynamicFeatureID(eObject, feature);
      return getValueAsList(eObject, dynamicFeatureID).contains(value);
    }

    public int hashCode(InternalEObject eObject, EStructuralFeature feature)
    {
      int dynamicFeatureID = eDynamicFeatureID(eObject, feature);
      return getValueAsList(eObject, dynamicFeatureID).hashCode();
    }

    public InternalEObject getContainer(InternalEObject eObject)
    {
      return null;
    }

    public EStructuralFeature getContainingFeature(InternalEObject eObject)
    {
      // This should never be called.
      throw new UnsupportedOperationException();
    }

    public EObject create(EClass eClass)
    {
      return new EStoreEObjectImpl(eClass, this);
    }

    public boolean isSet(InternalEObject eObject, EStructuralFeature feature)
    {
      int dynamicFeatureID = eDynamicFeatureID(eObject, feature);
      return getValue(eObject, dynamicFeatureID) != null;
    }

    public void unset(InternalEObject eObject, EStructuralFeature feature)
    {
      int dynamicFeatureID = eDynamicFeatureID(eObject, feature);
      setValue(eObject, dynamicFeatureID, null);
    }
  }

  /**
   * TODO Remove this when EMF has fixed http://bugs.eclipse.org/197487
   * 
   * @author Eike Stepper
   */
  public class CDOStoreFeatureMap extends DelegatingFeatureMap
  {
    private static final long serialVersionUID = 1L;

    public CDOStoreFeatureMap(EStructuralFeature eStructuralFeature)
    {
      super(CDOObjectImpl.this, eStructuralFeature);
    }

    @Override
    protected List<FeatureMap.Entry> delegateList()
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public EStructuralFeature getEStructuralFeature()
    {
      return eStructuralFeature;
    }

    @Override
    protected void delegateAdd(int index, Entry object)
    {
      eStore().add(owner, eStructuralFeature, index, object);
    }

    @Override
    protected void delegateAdd(Entry object)
    {
      delegateAdd(delegateSize(), object);
    }

    @Override
    protected List<FeatureMap.Entry> delegateBasicList()
    {
      int size = delegateSize();
      if (size == 0)
      {
        return ECollections.emptyEList();
      }

      Object[] data = cdoStore().toArray(owner, eStructuralFeature);
      return new EcoreEList.UnmodifiableEList<FeatureMap.Entry>(owner, eStructuralFeature, data.length, data);
    }

    @Override
    protected void delegateClear()
    {
      eStore().clear(owner, eStructuralFeature);
    }

    @Override
    protected boolean delegateContains(Object object)
    {
      return eStore().contains(owner, eStructuralFeature, object);
    }

    @Override
    protected boolean delegateContainsAll(Collection<?> collection)
    {
      for (Object o : collection)
      {
        if (!delegateContains(o))
        {
          return false;
        }
      }

      return true;
    }

    @Override
    protected Entry delegateGet(int index)
    {
      return (Entry)eStore().get(owner, eStructuralFeature, index);
    }

    @Override
    protected int delegateHashCode()
    {
      return eStore().hashCode(owner, eStructuralFeature);
    }

    @Override
    protected int delegateIndexOf(Object object)
    {
      return eStore().indexOf(owner, eStructuralFeature, object);
    }

    @Override
    protected boolean delegateIsEmpty()
    {
      return eStore().isEmpty(owner, eStructuralFeature);
    }

    @Override
    protected Iterator<FeatureMap.Entry> delegateIterator()
    {
      return iterator();
    }

    @Override
    protected int delegateLastIndexOf(Object object)
    {
      return eStore().lastIndexOf(owner, eStructuralFeature, object);
    }

    @Override
    protected ListIterator<FeatureMap.Entry> delegateListIterator()
    {
      return listIterator();
    }

    @Override
    protected Entry delegateRemove(int index)
    {
      return (Entry)eStore().remove(owner, eStructuralFeature, index);
    }

    @Override
    protected Entry delegateSet(int index, Entry object)
    {
      return (Entry)eStore().set(owner, eStructuralFeature, index, object);
    }

    @Override
    protected int delegateSize()
    {
      return eStore().size(owner, eStructuralFeature);
    }

    @Override
    protected Object[] delegateToArray()
    {
      return eStore().toArray(owner, eStructuralFeature);
    }

    @Override
    protected <T> T[] delegateToArray(T[] array)
    {
      return eStore().toArray(owner, eStructuralFeature, array);
    }

    @Override
    protected Entry delegateMove(int targetIndex, int sourceIndex)
    {
      return (Entry)eStore().move(owner, eStructuralFeature, targetIndex, sourceIndex);
    }

    @Override
    protected String delegateToString()
    {
      StringBuffer stringBuffer = new StringBuffer();
      stringBuffer.append("[");
      for (int i = 0, size = size(); i < size;)
      {
        Object value = delegateGet(i);
        stringBuffer.append(String.valueOf(value));
        if (++i < size)
        {
          stringBuffer.append(", ");
        }
      }

      stringBuffer.append("]");
      return stringBuffer.toString();
    }
  }
}
