import React from 'react';
import { Link } from 'react-router-dom';
import { HiOutlineMail, HiOutlinePhone, HiOutlineLocationMarker } from 'react-icons/hi';
import { AppRoutes } from '../../pages/routes';
import styles from './Footer.module.css';

const FOOTER_QUICK_LINKS = [
  { id: 'reportLost', label: 'Report Lost Pet', href: AppRoutes.reportMissing.microchip, isPlaceholder: false },
  { id: 'reportFound', label: 'Report Found Pet', href: '#', isPlaceholder: true },
  { id: 'search', label: 'Search Database', href: '#', isPlaceholder: true }
] as const;

const FOOTER_LEGAL_LINKS = [
  { id: 'privacy', label: 'Privacy Policy', href: '#' },
  { id: 'terms', label: 'Terms of Service', href: '#' },
  { id: 'cookies', label: 'Cookie Policy', href: '#' }
] as const;

const FOOTER_CONTACT = {
  email: 'support@petspot.com',
  phone: '+1 (555) 123-4567',
  address: '123 Pet Street, Animal City, AC 12345'
} as const;

export const Footer: React.FC = () => (
    <footer className={styles.footer} data-testid="landing.footer">
      <div className={styles.container}>
        <div className={styles.columns}>
          {/* Branding Column */}
          <div className={styles.column}>
            <div className={styles.logo} data-testid="landing.footer.logo">
              🐾 PetSpot
            </div>
            <p className={styles.tagline}>
              Reuniting pets with their families since 2025. Your trusted community platform for lost and found pets.
            </p>
          </div>

          {/* Quick Links Column */}
          <div className={styles.column}>
            <h4 className={styles.columnTitle}>Quick Links</h4>
            <ul className={styles.linkList}>
              {FOOTER_QUICK_LINKS.map((link) => (
                <li key={link.id}>
                  {link.isPlaceholder ? (
                    <span
                      className={styles.placeholderLink}
                      data-testid={`landing.footer.quickLink.${link.id}`}
                    >
                      {link.label}
                    </span>
                  ) : (
                    <Link
                      to={link.href}
                      className={styles.link}
                      data-testid={`landing.footer.quickLink.${link.id}`}
                    >
                      {link.label}
                    </Link>
                  )}
                </li>
              ))}
            </ul>
          </div>

          {/* Contact Column */}
          <div className={styles.column}>
            <h4 className={styles.columnTitle}>Contact Us</h4>
            <ul className={styles.contactList}>
              <li data-testid="landing.footer.contact.email">
                <HiOutlineMail className={styles.contactIcon} />
                <a href={`mailto:${FOOTER_CONTACT.email}`} className={styles.contactLink}>
                  {FOOTER_CONTACT.email}
                </a>
              </li>
              <li data-testid="landing.footer.contact.phone">
                <HiOutlinePhone className={styles.contactIcon} />
                <a href={`tel:${FOOTER_CONTACT.phone}`} className={styles.contactLink}>
                  {FOOTER_CONTACT.phone}
                </a>
              </li>
              <li data-testid="landing.footer.contact.address">
                <HiOutlineLocationMarker className={styles.contactIcon} />
                <span>{FOOTER_CONTACT.address}</span>
              </li>
            </ul>
          </div>
        </div>

        {/* Bottom Section */}
        <div className={styles.bottom}>
          <p className={styles.copyright} data-testid="landing.footer.copyright">
            © 2025 PetSpot. All rights reserved.
          </p>
          <div className={styles.legalLinks}>
            {FOOTER_LEGAL_LINKS.map((link) => (
              <a
                key={link.id}
                href={link.href}
                className={styles.legalLink}
                data-testid={`landing.footer.legalLink.${link.id}`}
              >
                {link.label}
              </a>
            ))}
          </div>
        </div>
      </div>
    </footer>
);

