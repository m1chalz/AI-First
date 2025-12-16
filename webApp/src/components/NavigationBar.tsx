import { NavLink } from 'react-router-dom';
import { AiOutlineHome, AiOutlineHeart, AiOutlineMail, AiOutlineUser } from 'react-icons/ai';
import { BiSearch } from 'react-icons/bi';
import styles from './NavigationBar.module.css';

const NAVIGATION_ITEMS = [
  { id: 'home', label: 'Home', icon: AiOutlineHome, path: '/', testId: 'navigation.home.link' },
  { id: 'lostPet', label: 'Lost Pet', icon: BiSearch, path: '/lost-pets', testId: 'navigation.lostPet.link' },
  { id: 'foundPet', label: 'Found Pet', icon: AiOutlineHeart, path: '/found-pets', testId: 'navigation.foundPet.link' },
  { id: 'contact', label: 'Contact Us', icon: AiOutlineMail, path: '/contact', testId: 'navigation.contact.link' },
  { id: 'account', label: 'Account', icon: AiOutlineUser, path: '/account', testId: 'navigation.account.link' },
];

export function NavigationBar() {
  return (
    <nav className={styles.navigationBar} data-testid="navigation.bar">
      <NavLink to="/" className={styles.logoLink} data-testid="navigation.logo.link">
        <img src="/logo.svg" alt="PetSpot" className={styles.logo} />
      </NavLink>
      <div className={styles.navigationItems}>
        {NAVIGATION_ITEMS.map((item) => (
          <NavLink
            key={item.id}
            to={item.path}
            data-testid={item.testId}
            className={({ isActive }) =>
              isActive ? styles.navigationItemActive : styles.navigationItem
            }
          >
            <item.icon className={styles.icon} size={20} />
            <span className={styles.label}>{item.label}</span>
          </NavLink>
        ))}
      </div>
    </nav>
  );
}

