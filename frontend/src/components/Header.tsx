import { useNavigate } from "react-router-dom";
import styles from "./Header.module.css";

export default function Header() {
  const navigate = useNavigate();

  function handleLogout() {
    localStorage.removeItem("token");
    navigate("/login", { replace: true });
  }

  return (
    <header className={styles.header}>
      <div className={styles.container}>
        <button
          className={styles.logo}
          onClick={() => navigate("/tasks")}
          type="button"
        >
          <span className={styles.logoMark}>T</span>
          <span>Task Tracker</span>
        </button>

        <button
          className={styles.logoutButton}
          onClick={handleLogout}
          type="button"
        >
          <svg
            className={styles.logoutIcon}
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            strokeWidth="2"
            strokeLinecap="round"
            strokeLinejoin="round"
            aria-hidden="true"
          >
            <path d="M10 17l5-5-5-5" />
            <path d="M15 12H3" />
            <path d="M21 3v18" />
          </svg>

          <span>Logout</span>
        </button>
      </div>
    </header>
  );
}