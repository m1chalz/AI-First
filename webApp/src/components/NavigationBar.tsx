import { NavLink } from 'react-router-dom';
import { HiOutlineHome, HiOutlineSearch, HiOutlineLocationMarker, HiOutlineChatAlt2, HiOutlineUser } from 'react-icons/hi';
import { AppRoutes } from '../pages/routes';
import styles from './NavigationBar.module.css';

const NAVIGATION_ITEMS = [
  { id: 'home', label: 'Home', icon: HiOutlineHome, path: AppRoutes.home, testId: 'navigation.home.link' },
  { id: 'lostPet', label: 'Lost Pet', icon: HiOutlineSearch, path: AppRoutes.lostPets, testId: 'navigation.lostPet.link' },
  { id: 'foundPet', label: 'Found Pet', icon: HiOutlineLocationMarker, path: AppRoutes.foundPets, testId: 'navigation.foundPet.link' },
  { id: 'contact', label: 'Contact Us', icon: HiOutlineChatAlt2, path: AppRoutes.contact, testId: 'navigation.contact.link' },
  { id: 'account', label: 'Account', icon: HiOutlineUser, path: AppRoutes.account, testId: 'navigation.account.link' }
];

export function NavigationBar() {
  return (
    <nav className={styles.navigationBar} data-testid="navigation.bar">
      <NavLink to={AppRoutes.home} className={styles.logoLink} data-testid="navigation.logo.link">
        <div className={styles.logoIcon}>
          <HiOutlineSearch className={styles.logoIconImage} size={20} />
        </div>
        <span className={styles.logoText}>PetSpot</span>
      </NavLink>
      <div className={styles.navigationItems}>
        {NAVIGATION_ITEMS.map((item) => (
          <NavLink
            key={item.id}
            to={item.path}
            data-testid={item.testId}
            className={({ isActive }) => (isActive ? styles.navigationItemActive : styles.navigationItem)}
          >
            <item.icon className={styles.icon} size={20} />
            <span className={styles.label}>{item.label}</span>
          </NavLink>
        ))}
      </div>
    </nav>
  );
}
