/**
 */
package org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.HibernateTestFactory
 * @model kind="package"
 * @generated
 */
public interface HibernateTestPackage extends EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "HibernateTest";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "http://org.eclipse.emf.cdo.tests.hibernate";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "hibernatetests";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	HibernateTestPackage eINSTANCE = org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.impl.HibernateTestPackageImpl.init();

	/**
	 * The meta object id for the '{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.impl.Bz356181_MainImpl <em>Bz356181 Main</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.impl.Bz356181_MainImpl
	 * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.impl.HibernateTestPackageImpl#getBz356181_Main()
	 * @generated
	 */
	int BZ356181_MAIN = 0;

	/**
	 * The feature id for the '<em><b>Transient</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BZ356181_MAIN__TRANSIENT = 0;

	/**
	 * The feature id for the '<em><b>Non Transient</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BZ356181_MAIN__NON_TRANSIENT = 1;

	/**
	 * The feature id for the '<em><b>Transient Ref</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BZ356181_MAIN__TRANSIENT_REF = 2;

	/**
	 * The feature id for the '<em><b>Transient Other Ref</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BZ356181_MAIN__TRANSIENT_OTHER_REF = 3;

	/**
	 * The number of structural features of the '<em>Bz356181 Main</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BZ356181_MAIN_FEATURE_COUNT = 4;

	/**
	 * The meta object id for the '{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.impl.Bz356181_TransientImpl <em>Bz356181 Transient</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.impl.Bz356181_TransientImpl
	 * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.impl.HibernateTestPackageImpl#getBz356181_Transient()
	 * @generated
	 */
	int BZ356181_TRANSIENT = 1;

	/**
	 * The number of structural features of the '<em>Bz356181 Transient</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BZ356181_TRANSIENT_FEATURE_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.impl.Bz356181_NonTransientImpl <em>Bz356181 Non Transient</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.impl.Bz356181_NonTransientImpl
	 * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.impl.HibernateTestPackageImpl#getBz356181_NonTransient()
	 * @generated
	 */
	int BZ356181_NON_TRANSIENT = 2;

	/**
	 * The feature id for the '<em><b>Main</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BZ356181_NON_TRANSIENT__MAIN = 0;

	/**
	 * The number of structural features of the '<em>Bz356181 Non Transient</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BZ356181_NON_TRANSIENT_FEATURE_COUNT = 1;


	/**
	 * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz356181_Main <em>Bz356181 Main</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Bz356181 Main</em>'.
	 * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz356181_Main
	 * @generated
	 */
	EClass getBz356181_Main();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz356181_Main#getTransient <em>Transient</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Transient</em>'.
	 * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz356181_Main#getTransient()
	 * @see #getBz356181_Main()
	 * @generated
	 */
	EAttribute getBz356181_Main_Transient();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz356181_Main#getNonTransient <em>Non Transient</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Non Transient</em>'.
	 * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz356181_Main#getNonTransient()
	 * @see #getBz356181_Main()
	 * @generated
	 */
	EAttribute getBz356181_Main_NonTransient();

	/**
	 * Returns the meta object for the reference '{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz356181_Main#getTransientRef <em>Transient Ref</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Transient Ref</em>'.
	 * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz356181_Main#getTransientRef()
	 * @see #getBz356181_Main()
	 * @generated
	 */
	EReference getBz356181_Main_TransientRef();

	/**
	 * Returns the meta object for the reference '{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz356181_Main#getTransientOtherRef <em>Transient Other Ref</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Transient Other Ref</em>'.
	 * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz356181_Main#getTransientOtherRef()
	 * @see #getBz356181_Main()
	 * @generated
	 */
	EReference getBz356181_Main_TransientOtherRef();

	/**
	 * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz356181_Transient <em>Bz356181 Transient</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Bz356181 Transient</em>'.
	 * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz356181_Transient
	 * @generated
	 */
	EClass getBz356181_Transient();

	/**
	 * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz356181_NonTransient <em>Bz356181 Non Transient</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Bz356181 Non Transient</em>'.
	 * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz356181_NonTransient
	 * @generated
	 */
	EClass getBz356181_NonTransient();

	/**
	 * Returns the meta object for the reference '{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz356181_NonTransient#getMain <em>Main</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Main</em>'.
	 * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz356181_NonTransient#getMain()
	 * @see #getBz356181_NonTransient()
	 * @generated
	 */
	EReference getBz356181_NonTransient_Main();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	HibernateTestFactory getHibernateTestFactory();

	/**
	 * <!-- begin-user-doc -->
	 * Defines literals for the meta objects that represent
	 * <ul>
	 *   <li>each class,</li>
	 *   <li>each feature of each class,</li>
	 *   <li>each enum,</li>
	 *   <li>and each data type</li>
	 * </ul>
	 * <!-- end-user-doc -->
	 * @generated
	 */
	interface Literals {
		/**
		 * The meta object literal for the '{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.impl.Bz356181_MainImpl <em>Bz356181 Main</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.impl.Bz356181_MainImpl
		 * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.impl.HibernateTestPackageImpl#getBz356181_Main()
		 * @generated
		 */
		EClass BZ356181_MAIN = eINSTANCE.getBz356181_Main();

		/**
		 * The meta object literal for the '<em><b>Transient</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute BZ356181_MAIN__TRANSIENT = eINSTANCE.getBz356181_Main_Transient();

		/**
		 * The meta object literal for the '<em><b>Non Transient</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute BZ356181_MAIN__NON_TRANSIENT = eINSTANCE.getBz356181_Main_NonTransient();

		/**
		 * The meta object literal for the '<em><b>Transient Ref</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference BZ356181_MAIN__TRANSIENT_REF = eINSTANCE.getBz356181_Main_TransientRef();

		/**
		 * The meta object literal for the '<em><b>Transient Other Ref</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference BZ356181_MAIN__TRANSIENT_OTHER_REF = eINSTANCE.getBz356181_Main_TransientOtherRef();

		/**
		 * The meta object literal for the '{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.impl.Bz356181_TransientImpl <em>Bz356181 Transient</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.impl.Bz356181_TransientImpl
		 * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.impl.HibernateTestPackageImpl#getBz356181_Transient()
		 * @generated
		 */
		EClass BZ356181_TRANSIENT = eINSTANCE.getBz356181_Transient();

		/**
		 * The meta object literal for the '{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.impl.Bz356181_NonTransientImpl <em>Bz356181 Non Transient</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.impl.Bz356181_NonTransientImpl
		 * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.impl.HibernateTestPackageImpl#getBz356181_NonTransient()
		 * @generated
		 */
		EClass BZ356181_NON_TRANSIENT = eINSTANCE.getBz356181_NonTransient();

		/**
		 * The meta object literal for the '<em><b>Main</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference BZ356181_NON_TRANSIENT__MAIN = eINSTANCE.getBz356181_NonTransient_Main();

	}

} //HibernateTestPackage
