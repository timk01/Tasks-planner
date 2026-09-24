import { useNavigate } from "react-router-dom";
import styles from "./NotFound.module.css";

export default function NotFound() {
  const navigate = useNavigate();

  return (
    <main className={styles.page}>
      <div className={styles.card}>
        <div className={styles.code}>
          404
        </div>

        <h1>
          Page not found
        </h1>

        <p>
          The page you are looking for does not exist
          or has been moved.
        </p>

        <button
          className={styles.button}
          onClick={() => navigate("/tasks")}
        >
          Back to tasks
        </button>
      </div>
    </main>
  );
}