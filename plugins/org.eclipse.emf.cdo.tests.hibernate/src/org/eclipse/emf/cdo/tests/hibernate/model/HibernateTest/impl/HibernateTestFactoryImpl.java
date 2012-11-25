/**
 */
package org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.impl;

import org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.*;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.impl.EFactoryImpl;

import org.eclipse.emf.ecore.plugin.EcorePlugin;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class HibernateTestFactoryImpl extends EFactoryImpl implements HibernateTestFactory {
	/**
	 * Creates the default factory implementation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static HibernateTestFactory init() {
		try {
			HibernateTestFactory theHibernateTestFactory = (HibernateTestFactory)EPackage.Registry.INSTANCE.getEFactory("http://org.eclipse.emf.cdo.tests.hibernate"); 
			if (theHibernateTestFactory != null) {
				return theHibernateTestFactory;
			}
		}
		catch (Exception exception) {
			EcorePlugin.INSTANCE.log(exception);
		}
		return new HibernateTestFactoryImpl();
	}

	/**
	 * Creates an instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public HibernateTestFactoryImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EObject create(EClass eClass) {
		switch (eClass.getClassifierID()) {
			case HibernateTestPackage.BZ356181_MAIN: return createBz356181_Main();
			case HibernateTestPackage.BZ356181_TRANSIENT: return createBz356181_Transient();
			case HibernateTestPackage.BZ356181_NON_TRANSIENT: return createBz356181_NonTransient();
			case HibernateTestPackage.BZ387752_MAIN: return createBz387752_Main();
			default:
				throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object createFromString(EDataType eDataType, String initialValue) {
		switch (eDataType.getClassifierID()) {
			case HibernateTestPackage.BZ387752_ENUM:
				return createBz387752_EnumFromString(eDataType, initialValue);
			default:
				throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String convertToString(EDataType eDataType, Object instanceValue) {
		switch (eDataType.getClassifierID()) {
			case HibernateTestPackage.BZ387752_ENUM:
				return convertBz387752_EnumToString(eDataType, instanceValue);
			default:
				throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Bz356181_Main createBz356181_Main() {
		Bz356181_MainImpl bz356181_Main = new Bz356181_MainImpl();
		return bz356181_Main;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Bz356181_Transient createBz356181_Transient() {
		Bz356181_TransientImpl bz356181_Transient = new Bz356181_TransientImpl();
		return bz356181_Transient;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Bz356181_NonTransient createBz356181_NonTransient() {
		Bz356181_NonTransientImpl bz356181_NonTransient = new Bz356181_NonTransientImpl();
		return bz356181_NonTransient;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Bz387752_Main createBz387752_Main() {
		Bz387752_MainImpl bz387752_Main = new Bz387752_MainImpl();
		return bz387752_Main;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Bz387752_Enum createBz387752_EnumFromString(EDataType eDataType, String initialValue) {
		Bz387752_Enum result = Bz387752_Enum.get(initialValue);
		if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
		return result;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertBz387752_EnumToString(EDataType eDataType, Object instanceValue) {
		return instanceValue == null ? null : instanceValue.toString();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public HibernateTestPackage getHibernateTestPackage() {
		return (HibernateTestPackage)getEPackage();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @deprecated
	 * @generated
	 */
	@Deprecated
	public static HibernateTestPackage getPackage() {
		return HibernateTestPackage.eINSTANCE;
	}

} //HibernateTestFactoryImpl